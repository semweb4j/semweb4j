package org.ontoware.rdfreactor.runtime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base {

	static Logger log = LoggerFactory.getLogger(Base.class);

	public static void add(Model model, Resource resourceSubject,
			URI propertyURI, Object value) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( value == null)
			throw new IllegalArgumentException("value may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		Node node = RDFReactorRuntime.java2node(model, value);
		model.addStatement(rdfResource, propertyURI, node);
	}

	public static void add(Model model, Resource resourceSubject,
			URI propertyURI, Object value, int maxCardinality)
			throws CardinalityException {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( value == null)
			throw new IllegalArgumentException("value may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		
		Node node = RDFReactorRuntime.java2node(model, value);
		if( model.contains(rdfResource, propertyURI, node) ) {
			return;
		}

		long count = countPropertyValues(model, rdfResource, propertyURI);
		if (count < maxCardinality) {
			model.addStatement(rdfResource, propertyURI, node);
		} else
			throw new CardinalityException(
					"Adding this value would violate maxCardinality = "
							+ maxCardinality + " for property " + propertyURI);
	}

	private static void assertOpen(Model model) {
		if (!model.isOpen()) {
			throw new RuntimeException("Model is not open");
		}
	}

	public static long countPropertyValues(Model model,
			Resource resourceSubject, URI propertyURI) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		assertOpen(model);
		ClosableIterator<Statement> it = model.findStatements(resourceSubject,
				propertyURI, Variable.ANY);
		long count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		it.close();
		return count;
	}

	public static void createInstance(Model model, URI classURI,
			Resource resourceSubject) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( classURI == null)
			throw new IllegalArgumentException("classURI may not be null");
		assertOpen(model);
		model.addStatement(resourceSubject, RDF.type, classURI);
	}

	/**
	 * @param model
	 *            RDF2Go model
	 * @param uri
	 *            instance identifier
	 * @return an instance of Restriction or null if none existst
	 * @throws Exception
	 *             if Model causes problems
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Model model, Resource resource,
			Class<?> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resource == null)
			throw new IllegalArgumentException("resource may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		return (T) RDFReactorRuntime.node2javatype(model, resource, returnType);
	}

	/**
	 * Removes rdf:type rdfsClass
	 * 
	 * @param model
	 * @param rdfsClass
	 * @param resource
	 */
	public static void deleteInstance(Model model, URI rdfsClass,
			Resource resource) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( rdfsClass == null)
			throw new IllegalArgumentException("rdfsClass may not be null");
		if( resource == null)
			throw new IllegalArgumentException("resource may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resource);
		model.removeStatement(rdfResource, RDF.type, rdfsClass);
	}

	public static Object get(Model model, Resource resourceSubject,
			URI propertyURI, java.lang.Class<?> returnType)
			throws RDFDataException, ModelRuntimeException {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		Node node = ResourceUtils.getSingleValue(model, rdfResource,
				propertyURI);
		return RDFReactorRuntime.node2javatype(model, node, returnType);
	}

	public static Node get_asNode(Model model, Resource instanceResource,
			URI propertyURI) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( instanceResource == null)
			throw new IllegalArgumentException("instanceResource  may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, instanceResource);
		ClosableIterator<Node> it = getAll(model, rdfResource, propertyURI,
				Node.class);
		if (it.hasNext()) {
			Node result = it.next();
			return result;
		} else {
			return null;
		}
	}

	public static <T> ClosableIterator<T> getAll(Model model,
			Resource resourceSubject, URI propertyURI, Class<T> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		ClosableIterator<Statement> it = model.findStatements(rdfResource,
				propertyURI, Variable.ANY);
		return new ConvertingClosableIterator<T>(new ProjectingIterator<Node>(
				it, ProjectingIterator.projection.Object), model, returnType);
	}

	public static <T> ReactorResult<T> getAll_as(Model model,
			Resource resourceSubject, URI propertyURI, Class<T> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		return new ReactorResult<T>(model, new TriplePatternImpl(rdfResource,
				propertyURI, Variable.ANY, TriplePatternImpl.SPO.OBJECT),
				returnType);
	}

	/**
	 * Convenience method for ClosableIterator<T> getAll.
	 * 
	 * @param <T>
	 * @param model
	 * @param resourceSubject
	 * @param propertyURI
	 * @param returnType
	 * @return
	 */
	public static <T> List<T> getAll_asList(Model model,
			Resource resourceSubject, URI propertyURI, Class<T> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		ClosableIterator<T> it = getAll(model, rdfResource, propertyURI,
				returnType);
		return asList(it);
	}

	public static <T> T[] getAll_asArray(Model model, Resource resourceSubject,
			URI propertyURI, Class<T> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		ClosableIterator<T> it = getAll(model, rdfResource, propertyURI,
				returnType);
		return asArray(it, returnType);
	}

	public static ClosableIterator<Node> getAll_asNode(Model model,
			Resource resourceSubject, URI propertyURI) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		ClosableIterator<Statement> it = model.findStatements(rdfResource,
				propertyURI, Variable.ANY);
		return new ProjectingIterator<Node>(it,
				ProjectingIterator.projection.Object);
	}

	public static List<Node> getAll_asNodeList(Model model,
			Resource resourceSubject, URI propertyURI) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		ClosableIterator<Node> it = getAll_asNode(model, rdfResource,
				propertyURI);
		return asList(it);
	}

	public static ClosableIterator<Resource> getAll_Inverse(Model model,
			URI propertyURI, Object value) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( value == null)
			throw new IllegalArgumentException("value may not be null");
		assertOpen(model);
		Node node = RDFReactorRuntime.java2node(model, value);
		ClosableIterator<Statement> it = model.findStatements(Variable.ANY,
				propertyURI, node);
		return new ProjectingIterator<Resource>(it,
				ProjectingIterator.projection.Subject);
	}

	public static <T> ReactorResult<T> getAll_Inverse_as(Model model,
			URI propertyURI, Object value, Class<T> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( value == null)
			throw new IllegalArgumentException("value may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		Node node = RDFReactorRuntime.java2node(model, value);
		return new ReactorResult<T>(model, new TriplePatternImpl(Variable.ANY,
				propertyURI, node, TriplePatternImpl.SPO.SUBJECT), returnType);
	}

	// public static <T> ClosableIterator<T> getAllAs(Model model,
	// Resource instanceResource, URI propertyURI, Class<?> returnType) {
	// assertOpen(model);
	// ClosableIterator<Statement> it = model.findStatements(instanceResource,
	// propertyURI, Variable.ANY);
	// return new ProjectingIterator<T>(it,
	// ProjectingIterator.projection.Object);
	// }

	/**
	 * Return all instances of the given class.
	 * 
	 * @param model -
	 *            underlying RDF2Go model
	 * @param classURI -
	 *            URI of the (RDFS/OWL) class.
	 * @return all instances in the model
	 */
	public static <T> ClosableIterator<T> getAllInstances(Model model,
			URI classURI, Class<T> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( classURI == null)
			throw new IllegalArgumentException("classURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		ClosableIterator<Statement> it = model.findStatements(Variable.ANY,
				RDF.type, classURI);
		return new ConvertingClosableIterator<T>(new ProjectingIterator<Node>(
				it, ProjectingIterator.projection.Subject), model, returnType);
	}

	public static <T> java.util.List<T> getAllInstances_asList(Model model,
			URI classURI, Class<T> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( classURI == null)
			throw new IllegalArgumentException("classURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		ClosableIterator<T> it = getAllInstances(model, classURI, returnType);
		return asList(it);
	}

	public static <T> T[] getAllInstances_asArray(Model model, URI classURI,
			Class<T> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( classURI == null)
			throw new IllegalArgumentException("classURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		ClosableIterator<T> it = getAllInstances(model, classURI, returnType);
		return asArray(it, returnType);
	}

	public static <T> ReactorResult<T> getAllInstances_as(Model model,
			URI classURI, Class<T> returnType) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( classURI == null)
			throw new IllegalArgumentException("classURI may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		assertOpen(model);
		return new ReactorResult<T>(model, new TriplePatternImpl(Variable.ANY,
				RDF.type, classURI, TriplePatternImpl.SPO.SUBJECT), returnType);

	}

	public static Resource getInverse(Model model, URI propertyURI, Object value) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( value == null)
			throw new IllegalArgumentException("value may not be null");
		assertOpen(model);
		Node valueNode = RDFReactorRuntime.java2node(model, value);
		ClosableIterator<Statement> it = model.findStatements(Variable.ANY,
				propertyURI, valueNode);
		Resource result = null;
		if (it.hasNext()) {
			result = it.next().getSubject();
		}
		if (it.hasNext()) {
			throw new RDFDataException("Found more than one inverse of "
					+ propertyURI + " i.e. mroe than one match for (*,"
					+ propertyURI + "," + valueNode);
		}
		it.close();
		return result;
	}

	public static boolean hasInstance(Model model, Resource classURI,
			Resource resourceSubject) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( classURI == null)
			throw new IllegalArgumentException("classURI may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		return model.contains(rdfResource, RDF.type, classURI);
	}

	public static boolean hasValue(Model model, Resource resourceSubject,
			URI propertyURI) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		return model.contains(rdfResource, propertyURI, Variable.ANY);
	}

	public static boolean hasValue(Model model, Resource resourceSubject,
			URI propertyURI, Object value) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( value == null)
			throw new IllegalArgumentException("value may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		Node node = RDFReactorRuntime.java2node(model, value);
		return model.contains(rdfResource, propertyURI, node);
	}

	public static void remove(Model model, Resource resourceSubject,
			URI propertyURI, Object value) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( value == null)
			throw new IllegalArgumentException("value may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		Node node = RDFReactorRuntime.java2node(model, value);
		model.removeStatement(rdfResource, propertyURI, node);
	}

	public static void remove(Model model, Resource resourceSubject,
			URI propertyURI, Object value, int minCardinality)
			throws CardinalityException {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( value == null)
			throw new IllegalArgumentException("value may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		long count = countPropertyValues(model, rdfResource, propertyURI);
		if (count > minCardinality)
			remove(model, rdfResource, propertyURI, value);
		else
			throw new CardinalityException("Must have at least "
					+ minCardinality + " values for property " + propertyURI);
	}

	public static void removeAll(Model model, Resource resourceSubject,
			URI propertyURI) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		assertOpen(model);
		synchronized (model) {
			removeAll_unsynchronized(model, resourceSubject, propertyURI);
		}
	}

	private static void removeAll_unsynchronized(Model model,
			Resource resourceSubject, URI propertyURI) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		assertOpen(model);
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		model.removeStatements(rdfResource, propertyURI, Variable.ANY);
	}

	public static void set(Model model, Resource resourceSubject,
			URI propertyURI, Object value) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		if( value == null)
			throw new IllegalArgumentException("value may not be null");
		assertOpen(model);
		synchronized (model) {
			Resource rdfResource = RDFReactorRuntime
					.genericResource2RDF2Goresource(model, resourceSubject);
			removeAll_unsynchronized(model, rdfResource, propertyURI);
			Node node = RDFReactorRuntime.java2node(model, value);
			model.addStatement(rdfResource, propertyURI, node);
		}
	}

	public static <T> List<T> asList(ClosableIterator<T> it) {
		if( it == null)
			throw new IllegalArgumentException("it may not be null");
		ArrayList<T> list = new ArrayList<T>();
		while (it.hasNext()) {
			list.add(it.next());
		}
		it.close();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] asArray(ClosableIterator<T> it, Class<T> returnType) {
		if( it == null)
			throw new IllegalArgumentException("it may not be null");
		if( returnType == null)
			throw new IllegalArgumentException("returnType may not be null");
		Object[] resultAsArray = (Object[]) Array.newInstance(returnType, 0);
		return (T[]) asList(it).toArray(resultAsArray);
	}

	/**
	 * Cast .this object to the given target Java type.
	 * 
	 * @param targetType -
	 *            Java type to which to cast this object
	 * @return converted object
	 */
	public static Object castTo(Model model, Resource resource,
			Class<?> targetType) {
			if( model == null)
				throw new IllegalArgumentException("model may not be null");
			if( resource == null)
				throw new IllegalArgumentException("resource may not be null");
			if( targetType == null)
				throw new IllegalArgumentException("targetType may not be null");
		return RDFReactorRuntime.node2javatype(model, resource, targetType);
	}

	public static boolean has(Model model, Resource resourceSubject,
			URI propertyURI) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( resourceSubject == null)
			throw new IllegalArgumentException("resourceSubject may not be null");
		if( propertyURI == null)
			throw new IllegalArgumentException("propertyURI may not be null");
		Resource rdfResource = RDFReactorRuntime
				.genericResource2RDF2Goresource(model, resourceSubject);
		ClosableIterator<Statement> it = model.findStatements(rdfResource,
				propertyURI, Variable.ANY);
		boolean result = it.hasNext();
		it.close();
		return result;
	}

	/**
	 * Delete all (this, *, *)
	 * 
	 * @param model
	 * @param instanceResource
	 */
	public static void deleteAllProperties(Model model,
			Resource instanceResource) {
		if( model == null)
			throw new IllegalArgumentException("model may not be null");
		if( instanceResource == null)
			throw new IllegalArgumentException("instanceResource may not be null");
		model.removeStatements(instanceResource, Variable.ANY, Variable.ANY);
	}

}
