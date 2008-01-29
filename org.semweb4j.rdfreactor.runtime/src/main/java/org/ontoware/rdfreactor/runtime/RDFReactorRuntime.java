package org.ontoware.rdfreactor.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.runtime.converter.BooleanConverter;
import org.ontoware.rdfreactor.runtime.converter.ByteConverter;
import org.ontoware.rdfreactor.runtime.converter.CalendarConverter;
import org.ontoware.rdfreactor.runtime.converter.DoubleConverter;
import org.ontoware.rdfreactor.runtime.converter.FloatConverter;
import org.ontoware.rdfreactor.runtime.converter.IntegerConverter;
import org.ontoware.rdfreactor.runtime.converter.JavaNetUriConverter;
import org.ontoware.rdfreactor.runtime.converter.LongConverter;
import org.ontoware.rdfreactor.runtime.converter.NodeConverter;
import org.ontoware.rdfreactor.runtime.converter.ResourceConverter;
import org.ontoware.rdfreactor.runtime.converter.ShortConverter;
import org.ontoware.rdfreactor.runtime.converter.StringConverter;
import org.ontoware.rdfreactor.runtime.converter.UriConverter;
import org.ontoware.rdfreactor.runtime.converter.UrlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDFReactorRuntime {

	static Logger log = LoggerFactory.getLogger(RDFReactorRuntime.class);

	private static Map<Class<?>, INodeConverter<?>> map = new HashMap<Class<?>, INodeConverter<?>>();

	static {
		// rdf2go types
		registerConverter(org.ontoware.rdf2go.model.node.Node.class,
				new NodeConverter());
		registerConverter(org.ontoware.rdf2go.model.node.Resource.class,
				new ResourceConverter());
		registerConverter(org.ontoware.rdf2go.model.node.URI.class,
				new UriConverter());

		// primitive data types
		registerConverter(String.class, new StringConverter());
		registerConverter(Boolean.class, new BooleanConverter());
		registerConverter(Byte.class, new ByteConverter());
		registerConverter(Short.class, new ShortConverter());
		registerConverter(Integer.class, new IntegerConverter());
		registerConverter(Long.class, new LongConverter());
		registerConverter(Float.class, new FloatConverter());
		registerConverter(Double.class, new DoubleConverter());
		registerConverter(java.net.URI.class, new JavaNetUriConverter());
		registerConverter(java.net.URL.class, new UrlConverter());
		registerConverter(java.util.Calendar.class, new CalendarConverter());
	}

	public static void registerConverter(Class<?> type, INodeConverter<?> converter) {
		map.put(type, converter);
	}

	public static INodeConverter<?> getConverter(Class<?> type) {
		return map.get(type);
	}

	// /////////////////////
	// type conversion

	/**
	 * Convert RDF entities to ReactorBaseNamed and primitive java values. This
	 * is the main method for calling other converter methods.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param n -
	 *            convert this object. type may be: URI, BlankNode,
	 *            LanguageTagLiteral, DatatypeLiteral, String
	 * @param returnType -
	 *            n is converted to this type, if it implements the needed
	 *            constructor
	 * @return a single object of returnType converted from n
	 * @throws ModelRuntimeException
	 */
	@Patrolled
	public static Object node2javatype(Model model, Node n,
			java.lang.Class<?> returnType) throws ModelRuntimeException {
		if (returnType.isArray())
			// TODO call this method for each array member
			throw new IllegalArgumentException("targetType may not be an array");

		INodeConverter<?> nodeConverter = RDFReactorRuntime
				.getConverter(returnType);
		if (nodeConverter == null) {
	//		// requested an RDFReactor generated subtype?
	//		if (ReflectionUtils.hasSuperClass(returnType, ReactorBase.class)) {
				return RDFReactorRuntime.resource2reactorbase(model, n,
						returnType);
	//		} else {
	//			throw new RuntimeException(
	//					"RDFReactor cannot convert any RDF node to "
	//							+ returnType + ". RDF node: " + n + " of type "
	//							+ n.getClass());
	//		}
		} else {
			return nodeConverter.toJava(n);
		}

	}

	/**
	 * Convert an RDF2Go resource to the target type, which must be a subclass
	 * of ReactorBase.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param node -
	 *            convert this object, can be a URI or a BlankNode
	 * @param targetType -
	 *            used to find constructor for creating the returned object. has
	 *            to implement the following constructor c: <br>
	 *            o is URI: c(Model, URI, boolean) or c(Model, Object, boolean)
	 *            <br>
	 *            o is BlankNode: c(Model, BlankNode) or c(Model, BlankNode)
	 *            <br>
	 * @return object of the given target type with contents of given object
	 */
	public static Object resource2reactorbase(Model model, Node node,
			Class<?> targetType) {
		if (targetType.isArray())
			throw new IllegalArgumentException("targetType may not be an array");

		if (node instanceof URI) {
			log.debug("URI node");
			try {

				// // TODO: experimental
				// URI classURI = (URI)
				// targetType.getDeclaredField("RDFS_CLASS")
				// .get(null);

				Constructor<?> constructor;
				try {
					constructor = targetType
							.getConstructor(new java.lang.Class[] {
									Model.class, URI.class, boolean.class });
				} catch (NoSuchMethodException nsme) {
					constructor = targetType
							.getConstructor(new java.lang.Class[] {
									Model.class, Resource.class, boolean.class });
				}

				return (Object) constructor.newInstance(new Object[] {
						model, node, false });

			} catch (ClassCastException cce) {
				throw new RuntimeException(cce);
			} catch (NoSuchMethodException nsme) {
				throw new RuntimeException("found no constructor " + targetType
						+ "(Model, URI/Resource, boolean) " + nsme);
			} catch (Exception e) {
				throw new ConversionException(e);
			}
		} else if (node instanceof BlankNode) {
			log.debug("BlankNode node");
			Constructor<?> constructor;
			try {
				try {
					constructor = targetType
							.getConstructor(new java.lang.Class[] {
									Model.class, BlankNode.class, boolean.class });
				} catch (NoSuchMethodException nsme) {
					log.debug("Class " + targetType
							+ " has no constructor for BlankNode");
					constructor = targetType
							.getConstructor(new java.lang.Class[] {
									Model.class, Resource.class, boolean.class });
				}
				BlankNode bnode = (BlankNode) node;
				return (Object) constructor.newInstance(new Object[] {
						model, bnode, false });
			} catch (ClassCastException cce) {
				throw new RuntimeException(cce);
			} catch (NoSuchMethodException nsme) {
				throw new RuntimeException("found no constructor " + targetType
						+ "(Model, BlankNode/Resource, boolean) " + nsme);
			} catch (Exception e) {
				throw new ConversionException(e);
			}

		} else if (node == null) {
			return null;
		} else {
			throw new RuntimeException("cannot convert " + node+ " of class <"+node.getClass()
					+ "> from " + node + " and convert it to " + targetType);
		}

	}

	/**
	 * @param model
	 * @param reactorValue
	 * @return a single RDF2Go Node from a Java object
	 */
	protected static Node java2node(Model model, Object reactorValue) {
		if (reactorValue == null) {
			throw new IllegalArgumentException("Argument may not be null");
		}

		// convert value to rdfnode
		log.debug("value is instance of " + reactorValue.getClass().getName());
		if (reactorValue instanceof ResourceEntity) {
			log
					.debug("object is an instanceof ReactorBase, so will add as single resource");
			// add as resource
			Resource objectID = ((ResourceEntity) reactorValue).getResource();
			return objectID;
		} else {
			for (Class<?> clazz : map.keySet()) {
				log.debug("Can a " + reactorValue.getClass()
						+ " be converted as " + clazz + " ?");
				if (clazz.isInstance(reactorValue)) {
					log.debug("Yes");
					return getConverter(clazz).toNode(model, reactorValue);
				}
			}
		}

		throw new ConversionException(
				"Cannot handle instances of "
						+ reactorValue.getClass()
						+ " which are neither instance of Reactorbase nor Reactorbase[]");
	}

}