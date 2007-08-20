package org.ontoware.rdfreactor.runtime;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.ModelAddRemoveMemoryImpl;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
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

		Node objectNode = RDFReactorRuntime.java2node(model,value);
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
		return RDFReactorRuntime.node2javatype(model, node, returnType);
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
				Node rdfnode = it.next().getObject();
				result.add(RDFReactorRuntime.node2javatype(model, rdfnode, returnType));
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
		addStatementGeneric(model, subject, property, RDFReactorRuntime.java2node(model,object));
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
		Node objectNode = RDFReactorRuntime.java2node(model,value);
		boolean found = model.contains(resource, propertyURI, objectNode);

		if (found) {
			model.removeStatement(resource, propertyURI, objectNode);
		}
		return found;
	}

	// /////////////////////////////
	// type conversion utilities

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
				result.add(RDFReactorRuntime.node2javatype(model, rdfnode, returnType));
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

	// @Patrolled
	// private static Object triplepattern2reactor_singleValue(Model model,
	// org.ontoware.rdf2go.model.impl.TriplePatternImpl triplePattern,
	// Class<?> returnType) {
	// log.debug("looking for *the* single value of " + triplePattern);
	// Object result = null;
	// synchronized (model) {
	// ClosableIterator<? extends Statement> it = model
	// .findStatements(triplePattern);
	// while (it.hasNext()) {
	// Statement statement = it.next();
	// Node rdfnode = triplePattern.getExtract(statement);
	// result = uriBlankLiteral2reactor(model, rdfnode, returnType);
	// }
	// it.close();
	// }
	//
	// return result;
	// }

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

//	/**
//	 * convert a ReactorBase or primitive Java object to a type used in the
//	 * RDF2GO model
//	 * 
//	 * @param reactorValue
//	 * @return URI, String and Blank nodes are passed through, ReactorBase
//	 *         instances have their identifier returned.
//	 * @throws ModelRuntimeException
//	 */
//	private static Object toRDF2GoType(Object reactorValue) {
//		if (reactorValue == null) {
//			throw new IllegalArgumentException("Argument may not be null");
//		}
//
//		// array in, array out
//
//		if (reactorValue.getClass().isArray()) {
//			log.debug("object is an array");
//			Object[] reactorValues = (Object[]) reactorValue;
//			Node[] nodes = new Node[reactorValues.length];
//			for (int i = 0; i < reactorValues.length; i++) {
//				nodes[i] = toRDF2GoNode(reactorValues[i]);
//			}
//			return nodes;
//		}
//
//		// if (reactorValue instanceof ResourceEntity[]) {
//		// log
//		// .debug("object is an instanceof ReactorBase[], so will add as
//		// multiple resources");
//		// ResourceEntity[] values = (ResourceEntity[]) reactorValue;
//		// Resource[] javatype = new Resource[values.length];
//		// for (int i = 0; i < values.length; i++) {
//		// javatype[i] = ((ResourceEntity) values[i]).getResource();
//		// }
//		// return javatype;
//		// }
//		else {
//			log.debug("object is simple, converting to rdf2go node...");
//			// value in, value out
//			return toRDF2GoNode(reactorValue);
//		}
//	}

}
