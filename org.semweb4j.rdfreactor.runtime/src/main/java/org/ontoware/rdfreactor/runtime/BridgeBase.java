package org.ontoware.rdfreactor.runtime;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.ModelAddRemoveMemoryImpl;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>BridgeBase</b> provides methods for adding, querying and deleting
 * statements from the underlying RDF2Go model.
 * 
 * TODO: be type-safe
 * 
 * 
 * 
 * <br>
 * RDF Reactor uses the following naming:
 * 
 * <b>resource</b> - instance of an RDF schema class, identified by the
 * resource ID (an URI or BlankNode), allmost all statements about the resource
 * use the resource ID as the object
 * 
 * <b>property</b> - a property belongs to a resource, represented by the
 * predicate of a statement about a resource
 * 
 * <b>value</b> - value of a property of a resource, represented by the object
 * of the statement with the property as predicate and the resource ID as the
 * subject
 * 
 * @author mvo
 */

public class BridgeBase {

	private static Logger log = LoggerFactory.getLogger(BridgeBase.class);

	// /////////////////////////
	// true implementations

	/**
	 * Check if the resource identified by resourceID, has a property
	 * identidified by propertyURI which has the given value among its values.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resource -
	 *            must be an URI or a BlankNode
	 * @param propertyURI -
	 *            URI of the property
	 * @param value -
	 *            look for this value of the property
	 * @return true if value is among values for the property
	 * @throws Exception
	 */
	@Patrolled
	public static boolean containsGivenValue(Model model, Resource resource,
			URI propertyURI, Object value) throws ModelRuntimeException {

		Node objectNode = toRDF2GoNode(value);
		return model.contains(resource, propertyURI, objectNode);
	}

	/**
	 * Return the first x with matching statement (resourceObject, propertyURI,
	 * x) from the given model. If severeal matching statements exist, only the
	 * first is returned.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resourceSubject -
	 *            the URI or BlankNode of the resource
	 * @param propertyURI -
	 *            the URI of the property
	 * @param returnType -
	 *            return the value from the statement as the given Java Type
	 * @return the first x matching (resourceObject, propertyURI, x)
	 * @throws RDFDataException
	 *             if more then one value was found
	 * @throws ModelRuntimeException
	 */
	@Patrolled
	public static Object getValue(Model model, Resource resourceSubject,
			URI propertyURI, java.lang.Class returnType)
			throws RDFDataException, ModelRuntimeException {
		Node node = ResourceUtils.getSingleValue(model, resourceSubject,
				propertyURI);
		if (node != null) {
			Object result = uriBlankLiteral2reactor(model, node, returnType);
			return result;
		} else {
			return null;
		}
	}

	/**
	 * Return a Set with all x matching (resourceObject, propertyURI, x) in the
	 * given model.
	 * 
	 * @param model -
	 *            the underlying model
	 * @param resource -
	 *            URI or BlankNode of the resource
	 * @param propertyURI -
	 *            URI of the property
	 * @param returnType -
	 *            return the found values as the given Java Type
	 * @return Set<Object> with each object of type 'returnType'
	 * @throws Exception
	 */
	@Patrolled
	public static Set<Object> getAllValues_asSet(Model model,
			Resource resource, URI propertyURI, java.lang.Class returnType) {
		synchronized (model) {
			ClosableIterator<? extends Statement> it = model.findStatements(
					resource, propertyURI, Variable.ANY);
			Set<Object> result = new HashSet<Object>();
			while (it.hasNext()) {
				Object rdfnode = it.next().getObject();
				result.add(uriBlankLiteral2reactor(model, rdfnode, returnType));
			}
			it.close();
			return result;
		}
	}

	/**
	 * Get all values for the given resource and property.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resource -
	 *            URI or BlankNode of the resource
	 * @param propertyURI -
	 *            URI of the property
	 * @param returnType -
	 *            return the values typed as returnType
	 * @return array of type 'returnType' with 0..n values. Never null.
	 * @throws Exception
	 */
	@Patrolled
	public static Object[] getAllValues(Model model, Resource resource,
			URI propertyURI, java.lang.Class returnType) {
		return triplepattern2reactor(model, TriplePatternImpl
				.createObjectPattern(resource, propertyURI), returnType);
	}

	/**
	 * Get all resources having the given property and value.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param propertyURI -
	 *            URI of the property
	 * @param objectNode -
	 *            predicate/value of the property
	 * @param returnType -
	 *            return the values typed as returnType
	 * @return array of type 'returnType' with 0..n values. Never null.
	 * @throws Exception
	 */
	@Patrolled
	public static Object[] getAllValues_Inverse(Model model, URI propertyURI,
			Node objectNode, java.lang.Class returnType)
			throws ModelRuntimeException {
		return triplepattern2reactor(model, TriplePatternImpl
				.createSubjectPattern(propertyURI, objectNode), returnType);
	}

	/**
	 * Query the model with a SPARQL query.
	 * 
	 * @param model -
	 *            the underlying RDF2GO model
	 * @param returnTypes -
	 *            cast the values to the types in returnTypes[]
	 * @param sparqlSelectQuery -
	 *            the SPARQL query
	 * @return javaobjects, typed as desired by 'returnTypes'
	 * @throws Exception
	 */
	@Patrolled
	public static OOQueryResultTable getSparqlSelect(Model model,
			Map<String, Class> returnTypes, String sparqlSelectQuery)
			throws ModelRuntimeException {
		return new OOQueryResultTableImpl(model, returnTypes, sparqlSelectQuery);
	}

	/**
	 * Add a value to a property of a resource.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param subject -
	 *            URI or BlankNode representing the resource
	 * @param property -
	 *            URI of the property
	 * @param object -
	 *            value of the property which is to be added to the resource
	 * @throws Exception
	 */
	@Patrolled
	public static void add(Model model, Resource subject, URI property,
			Object object) throws ModelRuntimeException {
		addStatementGeneric(model, subject, property, toRDF2GoType(object));
	}

	/**
	 * Get all instances of the class in the model.
	 * 
	 * @param model -
	 *            the underlying RDF2Go class
	 * @param javaClass -
	 *            the requested Java class
	 * @return Array of the given javaClass type instances in the model
	 */
	@Patrolled
	public static Object[] getAllInstances(Model model,
			java.lang.Class javaClass) {
		URI rdfsClass;
		try {
			rdfsClass = (URI) javaClass.getDeclaredField("RDFS_CLASS")
					.get(null);
			return triplepattern2reactor(model, TriplePatternImpl
					.createObjectPattern(RDF.type, rdfsClass), javaClass);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Remove all values of a property from a resource. (types don't matter
	 * here)
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resourceObject -
	 *            URI or BlankNode of the resource
	 * @param propertyURI -
	 *            URI of the property
	 * @return true if any value was present
	 * @throws Exception
	 */
	public static boolean removeAllValues(Model model, Resource r, URI p)
			throws ModelRuntimeException {
		assert model != null;
		assert r != null;
		assert p != null;

		synchronized (model) {
			ModelAddRemoveMemoryImpl toBeDeleted = new ModelAddRemoveMemoryImpl();
			toBeDeleted.addAll(model.findStatements(r, p, Variable.ANY));
			ClosableIterator<Statement> it = toBeDeleted.iterator();
			model.removeAll(it);
			it.close();
			return toBeDeleted.size() > 0;
		}
	}

	/**
	 * Remove a value of a property from a resource.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resource -
	 *            URI or BlankNode of the resource
	 * @param propertyURI -
	 *            URI of the property
	 * @param value -
	 *            value of the property which is removed
	 * @return true if value could be removed
	 * @throws Exception
	 */
	static boolean removeValue(Model model, Resource resource, URI propertyURI,
			Object value) throws ModelRuntimeException {
		Node objectNode = toRDF2GoNode(value);
		boolean found = model.contains(resource, propertyURI, objectNode);

		if (found) {
			model.removeStatement(resource, propertyURI, objectNode);
		}
		return found;
	}

	// /////////////////////////////
	// type conversion utilities

	/**
	 * Convert an URI or a BlankNode to the given target type.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param o -
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
	public static ReactorBase uriBlank2reactor(Model model, Object o,
			java.lang.Class<?> targetType) {
		if (targetType.isArray())
			throw new IllegalArgumentException("targetType may not be an array");

		if (o instanceof URI) {
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

				return (ReactorBase) constructor.newInstance(new Object[] {
						model, o, false });

			} catch (ClassCastException cce) {
				throw new RuntimeException(cce);
			} catch (NoSuchMethodException nsme) {
				throw new RuntimeException("found no constructor " + targetType
						+ "(Model, URI/Object, boolean) " + nsme);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (o instanceof BlankNode) {
			log.debug("BlankNode node");
			Constructor<?> constructor;
			try {
				try {
					constructor = targetType
							.getConstructor(new java.lang.Class[] {
									Model.class, BlankNode.class });
				} catch (NoSuchMethodException nsme) {
					log.debug("Class " + targetType
							+ " has no constructor for BlankNode");
					constructor = targetType
							.getConstructor(new java.lang.Class[] {
									Model.class, Object.class });
				}
				BlankNode bnode = (BlankNode) o;
				return (ReactorBase) constructor.newInstance(new Object[] {
						model, bnode });
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}

			// try {
			// BlankNode bnode = (BlankNode) o;
			// return (ReactorBaseBlank) targetType.getConstructor(
			// new java.lang.Class[] { Model.class, BlankNode.class })
			// .newInstance(new Object[] { model, bnode });
			// } catch (NoSuchMethodException nsme) {
			// throw new RuntimeException("found no constructor for "
			// + targetType + "(Model, BlankNode) " + nsme);
			// } catch (Exception e) {
			// throw new RuntimeException(e);
			// }

		} else
			throw new RuntimeException("cannot hanlde " + o.getClass()
					+ " from " + o);

	}

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
	 * @return object of returnType converted from n
	 * @throws ModelRuntimeException
	 */
	public static Object uriBlankLiteral2reactor(Model model, Object n,
			java.lang.Class<?> returnType) throws ModelRuntimeException {

		if (returnType.isArray())
			throw new IllegalArgumentException("targetType may not be an array");

		if (n == null) {
			// special case: booleans
			if (returnType.equals(boolean.class))
				return new Boolean(false);
			else if (returnType.equals(Integer.class))
				return new Integer(-1);
			else if (returnType.equals(Long.class))
				return new Long(-1);
			else if (returnType.equals(long.class))
				return new Long(-1);
			else if (returnType.equals(int.class))
				return new Integer(-1);
			else {
				log.debug("returning null as null, although " + returnType
						+ " was requested");
				return null;
			}
		} else if (n instanceof URI) {
			log.debug("URI node");

			if (returnType.equals(URI.class)) {
				return n;
			} else {
				return uriBlank2reactor(model, n, returnType);
			}

		} else if (n instanceof BlankNode) {
			log.debug("blank node");
			BlankNode bnode = (BlankNode) n;
			return uriBlank2reactor(model, bnode, returnType);

		} else if (n instanceof String) {
			log.debug("literal node");
			String s = (String) n;
			return string2java(s, returnType);

		} else if (n instanceof PlainLiteral) {
			log.debug("PlainLiteral node");
			PlainLiteral plainLit = (PlainLiteral) n;
			return plainLiteral2java(plainLit, returnType);

		} else if (n instanceof LanguageTagLiteral) {
			log.debug("LanguageTagLiteral node");
			LanguageTagLiteral langLit = (LanguageTagLiteral) n;
			return languageTagggedLiteral2java(langLit, returnType);

		} else if (n instanceof DatatypeLiteral) {
			log.debug("DatatypeLiteral node");
			DatatypeLiteral dataLit = (DatatypeLiteral) n;
			return datatypedLiteral2java(dataLit, returnType);

		} else {
			log.debug("unknown node");
			// report variable nodes
			throw new RuntimeException("Cannot return a Node (" + n + ","
					+ n.getClass() + ") as " + returnType.getName());
		}
	}

	/**
	 * Convert a DatatypeLiteral to a native Java type.
	 * 
	 * @param dataLit -
	 *            DatatypeLiteral to be converted
	 * @param targetType -
	 *            convert to this type. currently supported: String only.
	 * @return converted object
	 * @throws ModelRuntimeException
	 */
	@Patrolled
	public static Object datatypedLiteral2java(DatatypeLiteral dataLit,
			java.lang.Class<?> targetType) throws ModelRuntimeException {
		if (targetType.equals(DatatypeLiteral.class)) {
			return dataLit;
		}

		return string2java(dataLit.getValue(), targetType);
	}

	/**
	 * Convert a LanguageTagLiteral to a native Java type.
	 * 
	 * No conversion from language tagged literals to primitive Java types is
	 * support.
	 * 
	 * @param languageTagLiteral -
	 *            LanguageTagLiteral to be converted convert to this type.
	 *            currently supported: String only.
	 * @return converted object
	 */
	@Patrolled
	public static Object languageTagggedLiteral2java(
			LanguageTagLiteral languageTagLiteral, java.lang.Class<?> returnType) {
		if (returnType.equals(LanguageTagLiteral.class)) {
			return languageTagLiteral;
		}

		if (returnType.equals(String.class)) {
			log.debug("Ignoring language");
			return languageTagLiteral.getValue();
		}

		throw new RuntimeException("cannot convert LanguageTagLiteral to "
				+ returnType);

	}

	/**
	 * @param plainLit
	 * @param targetType
	 * @return string converted to desired java return type
	 * @throws ModelRuntimeException
	 */
	@Patrolled
	public static Object plainLiteral2java(PlainLiteral plainLit,
			java.lang.Class<?> targetType) throws ModelRuntimeException {
		if (targetType.equals(PlainLiteral.class)) {
			return plainLit;
		}

		if (targetType.equals(String.class)) {
			return plainLit.getValue();
		}

		return string2java(plainLit.getValue(), targetType);
	}

	/**
	 * Interpret s as a String, and convert it to the given targetType
	 * 
	 * @param string -
	 *            convert this string
	 * @param targetType -
	 *            convert into this type. supported: all primitive Java types
	 * @return object of type targetType with contents of String s
	 * @throws RuntimeException
	 *             if conversion is not supported or goes wrong
	 */
	@Patrolled
	public static Object string2java(String string,
			java.lang.Class<?> targetType) throws RuntimeException {

		if (targetType.equals(String.class))
			return string;

		if (targetType.equals(URI.class)) {
			return new URIImpl(string);
		}

		if (targetType.equals(int.class) || targetType.equals(Integer.class))
			return new Integer(Integer.parseInt(string));

		if (targetType.equals(boolean.class)
				|| targetType.equals(Boolean.class))
			return new Boolean(Boolean.parseBoolean(string));

		if (targetType.equals(byte.class) || targetType.equals(Byte.class))
			return new Byte(Byte.parseByte(string));

		if (targetType.equals(short.class) || targetType.equals(Short.class))
			return new Short(Short.parseShort(string));

		if (targetType.equals(long.class) || targetType.equals(Long.class))
			return new Long(Long.parseLong(string));

		if (targetType.equals(char.class) || targetType.equals(Character.class))
			return new Character(string.charAt(0));

		if (targetType.equals(float.class) || targetType.equals(Float.class))
			return new Float(Float.parseFloat(string));

		if (targetType.equals(double.class) || targetType.equals(Double.class))
			return new Double(Double.parseDouble(string));

		// IMPROVE is this ever going to be used?
		if (targetType.equals(PlainLiteral.class)) {
			return new PlainLiteralImpl(string);
		}

		if (targetType.equals(Calendar.class)) {
			return DatatypeUtils.parseXSDDateTime_toCalendar(string);
		}

		if (targetType.equals(URL.class)) {
			try {
				return new URL(string);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		if (targetType.equals(Calendar.class)) {
			Calendar cal = DatatypeUtils.parseXSDDateTime_toCalendar(string);
			log.debug("Converting '" + string + "' to java.util.Calendar");
			return cal;
		}

		if (targetType.equals(Date.class)) {
			Calendar cal = DatatypeUtils.parseXSDDateTime_toCalendar(string);
			log.debug("Converting '" + string + "' to java.util.Date");
			return cal.getTime();
		}

		throw new RuntimeException("cannot convert String to " + targetType);
	}

	/**
	 * Take a TripplePattern, apply it to the model, and return an array with
	 * the found instances typed as returnType.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param triplePattern -
	 *            TriplePattern for which matches are searched
	 * @param returnType -
	 *            convert all found instances to this type
	 * @return array with 0..n instances (never null) typed as 'returnType'
	 * @throws Exception
	 */
	@Patrolled
	private static Object[] triplepattern2reactor(Model model,
			org.ontoware.rdf2go.model.impl.TriplePatternImpl triplePattern,
			Class<?> returnType) {
		log.debug("looking for " + triplePattern);
		Set<Object> result = new HashSet<Object>();
		synchronized (model) {
			ClosableIterator<? extends Statement> it = model
					.findStatements(triplePattern);
			// eliminates duplicates
			while (it.hasNext()) {
				log.debug("got a result");
				Statement statement = it.next();
				Node rdfnode = triplePattern.getExtract(statement);
				result.add(uriBlankLiteral2reactor(model, rdfnode, returnType));
			}
			it.close();
		}
		log.debug("Found " + result.size() + " results");
		// IMPROVE: quite complicated array creation
		Object[] resultValues = result.toArray();
		Object[] resultAsArray = (Object[]) Array.newInstance(returnType,
				result.size());

		for (int i = 0; i < resultAsArray.length; i++) {
			log.debug("casting " + resultValues[i] + " to " + returnType);
			resultAsArray[i] = returnType.cast(resultValues[i]);
		}
		return resultAsArray;
	}

//	@Patrolled
//	private static Object triplepattern2reactor_singleValue(Model model,
//			org.ontoware.rdf2go.model.impl.TriplePatternImpl triplePattern,
//			Class<?> returnType) {
//		log.debug("looking for *the* single value of " + triplePattern);
//		Object result = null;
//		synchronized (model) {
//			ClosableIterator<? extends Statement> it = model
//					.findStatements(triplePattern);
//			while (it.hasNext()) {
//				Statement statement = it.next();
//				Node rdfnode = triplePattern.getExtract(statement);
//				result = uriBlankLiteral2reactor(model, rdfnode, returnType);
//			}
//			it.close();
//		}
//
//		return result;
//	}

	/**
	 * Add a statement (subject, property, o) to the given model. ("generic"
	 * because a lot of type casting is necessary if generic objects are allowed
	 * as arguments).
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param subject -
	 *            subject of the statement has to be an URI or BlankNode
	 * @param property -
	 *            predicate of the statement has to be an URI
	 * @param object -
	 *            object of the statement can be an URI, URI[], String,
	 *            DatatypeLiteral, LanguageTagLiteral or generic Object[]
	 * @throws Exception
	 */
	@Patrolled
	private static void addStatementGeneric(Model model, Resource subject,
			URI property, Object object) throws ModelRuntimeException {
		assert subject != null;
		assert property != null;
		assert object != null;
		log.debug("add (" + subject + "," + property + "," + object + ")");

		if (object.getClass().isArray()) {
			// handle each component
			log.debug("object is an instanceof some Array");
			Object[] values = (Object[]) object;
			for (int i = 0; i < values.length; i++) {
				addStatementGeneric_singleValue(model, subject, property,
						values[i]);
			}
		} else {
			// handle once
			addStatementGeneric_singleValue(model, subject, property, object);
		}
	}

	/**
	 * Add a single statement.
	 * 
	 * @param model
	 * @param subject
	 * @param property
	 * @param object -
	 *            handles rdf2go nodes and strings
	 */
	@Patrolled
	private static void addStatementGeneric_singleValue(Model model,
			Resource subject, URI property, Object object) {
		if (object instanceof Node) {
			log
					.debug("object is an instance of an Rdf2go Node (URI, Literal, ...) , so will add as single resource");
			model.addStatement(subject, property, (Node) object);
		} else if (object instanceof String) {
			model.addStatement(subject, property, (String) object);
		} else
			throw new RuntimeException("unknown object type "
					+ object.getClass());
	}

	/**
	 * convert a ReactorBase or primitive Java object to a type used in the
	 * RDF2GO model
	 * 
	 * @param reactorValue
	 * @return URI, String and Blank nodes are passed through, ReactorBase
	 *         instances have their identifier returned.
	 * @throws ModelRuntimeException
	 */
	private static Object toRDF2GoType(Object reactorValue) {
		if (reactorValue == null) {
			throw new IllegalArgumentException("Argument may not be null");
		}

		// array in, array out

		if (reactorValue.getClass().isArray()) {
			log.debug("object is an array");
			Object[] reactorValues = (Object[]) reactorValue;
			Node[] nodes = new Node[reactorValues.length];
			for (int i = 0; i < reactorValues.length; i++) {
				nodes[i] = toRDF2GoNode(reactorValues[i]);
			}
			return nodes;
		}

//		if (reactorValue instanceof ResourceEntity[]) {
//			log
//					.debug("object is an instanceof ReactorBase[], so will add as multiple resources");
//			ResourceEntity[] values = (ResourceEntity[]) reactorValue;
//			Resource[] javatype = new Resource[values.length];
//			for (int i = 0; i < values.length; i++) {
//				javatype[i] = ((ResourceEntity) values[i]).getResource();
//			}
//			return javatype;
//		} 
		else {
			log.debug("object is simple, converting to rdf2go node...");
			// value in, value out
			return toRDF2GoNode(reactorValue);
		}
	}

	@Patrolled
	private static Node toRDF2GoNode(Object reactorValue) {
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
		} else if (reactorValue instanceof Node) {
			return (Node) reactorValue;
		} else if (reactorValue instanceof String) {
			return new PlainLiteralImpl((String) reactorValue);
		} else if (reactorValue instanceof URL) {
			try {
				return new URIImpl(((URL) reactorValue).toURI() + "");
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			// TODO: enhance XSD data type mapping
		} else if (reactorValue instanceof Boolean) {
			return new DatatypeLiteralImpl(reactorValue + "", XSD._boolean);
		} else if (reactorValue instanceof Byte) {
			return new DatatypeLiteralImpl(reactorValue + "", XSD._byte);
		} else if (reactorValue instanceof Integer) {
			return new DatatypeLiteralImpl(reactorValue + "", XSD._int);
		} else if (reactorValue instanceof Long) {
			return new DatatypeLiteralImpl(reactorValue + "", XSD._long);
		} else if (reactorValue instanceof Float) {
			return new DatatypeLiteralImpl(reactorValue + "", XSD._float);
		} else if (reactorValue instanceof Double) {
			return new DatatypeLiteralImpl(reactorValue + "", XSD._double);
		} else if (reactorValue instanceof Calendar) {
			String xsdDateTime = DatatypeUtils
					.encodeCalendar_toXSDDateTime((Calendar) reactorValue);
			return new DatatypeLiteralImpl(xsdDateTime, XSD._dateTime);
		} else if (reactorValue instanceof Date) {
			// IMPORVE this is in local time zone
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) reactorValue);
			String xsdDateTime = DatatypeUtils
					.encodeCalendar_toXSDDateTime(cal);
			return new DatatypeLiteralImpl(xsdDateTime, XSD._dateTime);
		} else {
			throw new RuntimeException(
					"Cannot handle instances of "
							+ reactorValue.getClass()
							+ " which are neither instance of Reactorbase nor Reactorbase[]");
		}
	}
}
