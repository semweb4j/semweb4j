package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base {

	public static void add(Model model, Resource resourceSubject,
			URI propertyURI, Object value) {
		Node node = RDFReactorRuntime.java2node(model, value);
		model.addStatement(resourceSubject, propertyURI, node);
	}

	private static void assertOpen(Model model) {
		if (!model.isOpen()) {
			throw new RuntimeException("Model is not open");
		}
	}

	public static Object get(Model model, Resource resourceSubject,
			URI propertyURI, java.lang.Class<?> returnType)
			throws RDFDataException, ModelRuntimeException {
		Node node = ResourceUtils.getSingleValue(model, resourceSubject,
				propertyURI);
		return RDFReactorRuntime.node2javatype(model, node, returnType);
	}

	public static <T> ClosableIterator<T> getAll(Model model,
			Resource resourceSubject, URI propertyURI, Class<?> returnType) {
		ClosableIterator<Statement> it = model.findStatements(resourceSubject,
				propertyURI, Variable.ANY);
		return new ProjectingIterator<T>(it,
				ProjectingIterator.projection.Object);
	}

	public static ClosableIterator<Resource> getAll_Inverse(Model model,
			URI propertyURI, Object value) {
		Node node = RDFReactorRuntime.java2node(model, value);
		ClosableIterator<Statement> it = model.findStatements(Variable.ANY,
				propertyURI, node);
		return new ProjectingIterator<Resource>(it,
				ProjectingIterator.projection.Object);
	}

	public static Resource getInverse(Model model, URI propertyURI, Object value) {
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

	/**
	 * Return all instances of the given class.
	 * 
	 * @param model -
	 *            underlying RDF2Go model
	 * @param classURI -
	 *            URI of the (RDFS/OWL) class.
	 * @return all instances in the model
	 */
	public static ClosableIterator<Resource> getAllInstances(Model model,
			URI classURI) {
		assertOpen(model);
		ClosableIterator<Statement> it = model.findStatements(Variable.ANY,
				RDF.type, classURI);
		return new ProjectingIterator<Resource>(it,
				ProjectingIterator.projection.Subject);
	}

	public static boolean hasInstance(Model model, Resource classURI,
			Resource resourceURI) {
		assertOpen(model);
		return model.contains(resourceURI, RDF.type, classURI);
	}

	public static void createInstance(Model model, URI classURI,
			Resource resource) {
		model.addStatement(resource, RDF.type, classURI);
	}

	/**
	 * Removes all types
	 * 
	 * @param model
	 * @param rdfsClass
	 * @param resource
	 */
	public static void deleteInstance(Model model, URI rdfsClass,
			Resource resource) {
		model.removeStatements(resource, RDF.type, Variable.ANY);
	}

	public static boolean hasValue(Model model, Resource resourceSubject,
			URI propertyURI) {
		return model.contains(resourceSubject, propertyURI, Variable.ANY);
	}

	public static boolean hasValue(Model model, Resource resourceSubject,
			URI propertyURI, Object value) {
		Node node = RDFReactorRuntime.java2node(model, value);
		return model.contains(resourceSubject, propertyURI, node);
	}

	public static void remove(Model model, Resource resource, URI propertyURI,
			Object value, int minCardinality) throws CardinalityException {
		ClosableIterator<Statement> it = model.findStatements(resource,
				propertyURI, Variable.ANY);
		long count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		it.close();

		if (count > minCardinality)
			remove(model, resource, propertyURI, value);
		else
			throw new CardinalityException("Must have at least "
					+ minCardinality + " values for property " + propertyURI);
	}

	public static void remove(Model model, Resource resourceSubject,
			URI propertyURI, Object value) {
		Node node = RDFReactorRuntime.java2node(model, value);
		model.removeStatement(resourceSubject, propertyURI, node);
	}

	public static void removeAll(Model model, Resource resourceSubject,
			URI propertyURI) {
		synchronized (model) {
			removeAll_unsynchronized(model, resourceSubject, propertyURI);
		}
	}

	private static void removeAll_unsynchronized(Model model,
			Resource resourceSubject, URI propertyURI) {
		model.removeStatements(resourceSubject, propertyURI, Variable.ANY);
	}

	static Logger log = LoggerFactory.getLogger(Base.class);

	public static void set(Model model, Resource resourceSubject,
			URI propertyURI, Object object) {
		synchronized (model) {
			removeAll_unsynchronized(model, resourceSubject, propertyURI);
			Node node = RDFReactorRuntime.java2node(model, object);
			model.addStatement(resourceSubject, propertyURI, node);
		}
	}

}
