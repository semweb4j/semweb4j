/*
 * Created on 13.02.2005
 */
package org.ontoware.rdfreactor.runtime;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime model for a java bean property
 * 
 * For all getters and setters (and all other accessors): - if the object
 * implements URIEntity it's mapped to rdf:Resource, - otherwise it's mapped to
 * rdf:Literal by calling "toString" IMPROVE: better Literal handling?
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
public class Bridge extends BridgeBase {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(Bridge.class);

	/**
	 * Add a value to the property of an object after checking if the object
	 * already had the property with the same value.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param o -
	 *            URI or BlankNode identifying the resource
	 * @param propertyURI -
	 *            URI of the property
	 * @param value -
	 *            value of the property
	 * @return true if value was already in the model
	 */
	public static boolean addValue(Model model, Resource o, URI propertyURI, Object value)
			throws ModelRuntimeException {
		assert model != null;
		assert o != null;
		assert o instanceof URI || o instanceof BlankNode;
		assert propertyURI != null;
		assert value != null;

		boolean contains = containsGivenValue(model, o, propertyURI, value);
		add(model, o, propertyURI, value);
		return contains;
	}

	/**
	 * Set the value(s) of a property of an object, after removing all values of
	 * the property from the object
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resourceObject -
	 *            URI or BlankNode identifying the object
	 * @param propertyURI -
	 *            URI of the resource
	 * @param value -
	 *            value(s) of the property, may be an array
	 * @throws ModelRuntimeException 
	 */
	public static void setValue(Model model, Resource resourceObject, URI propertyURI, Object value) {
		assert value != null;
		// remove all current values
		removeAllValues(model, resourceObject, propertyURI);
		if (value.getClass().isArray()) {
			Object[] valuearray = (Object[]) value;
			for (int i = 0; i < valuearray.length; i++) {
				addValue(model, resourceObject, propertyURI, valuearray[i]);
			}

		} else
			addValue(model, resourceObject, propertyURI, value);
	}

	/**
	 * Set the value of a property from a resource after removing all existing
	 * values of the property from that resource.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param subject -
	 *            URI or BlankNode identifying the resource
	 * @param prop -
	 *            URI of the property
	 * @param o -
	 *            value of the property
	 * @throws Exception
	 */
	public static void setAllValue(Model model, Resource subject, URI prop, Object[] o)
			throws ModelRuntimeException {
		removeAllValues(model, subject, prop);
		add(model, subject, prop, o);

	}

	/**
	 * Update the value of a property from a resource. (Remove old value, add
	 * new value.)
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resourceIdentifier -
	 *            URI or BlankNode identifying the resource
	 * @param propertyURI -
	 *            URI of the property
	 * @param _old -
	 *            old value of the property
	 * @param _new -
	 *            new value of the property
	 * @return true if the property really had the old value
	 * @throws Exception
	 */
	public static Boolean updateValue(Model model, Resource resourceIdentifier, URI propertyURI,
			Object _old, Object _new) throws ModelRuntimeException {
		Boolean result = new Boolean(removeValue(model, resourceIdentifier, propertyURI, _old));
		addValue(model, resourceIdentifier, propertyURI, _new);
		return result;
	}

	/**
	 * Update the value of a property from a resource. (Remove old value, add
	 * new value.)
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resourceIdentifier -
	 *            BlankNode identifying the resource
	 * @param propertyURI -
	 *            URI of the property
	 * @param _old -
	 *            old value of the property
	 * @param _new -
	 *            new value of the property
	 * @return true if the property really had the old value
	 * @throws Exception
	 */
	public static boolean updateValue(Model model, BlankNode blankNode, URI propertyURI,
			Object _old, Object _new) throws ModelRuntimeException {
		Boolean result = new Boolean(removeValue(model, blankNode, propertyURI, _old));
		addValue(model, blankNode, propertyURI, _new);
		return result;
	}

	// /////////////////////
	// utility functions

	/**
	 * Check if the model contains an instance of the given RDFS/OWL schema
	 * class. It is assumed that every instance of a class has an accompanying
	 * triple of the form (instanceID, rdf:type, classURI) in the model.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param classURI -
	 *            URI of the RDFS/OWL schema class
	 * @return true if the model contains an instance of the class URI
	 */
	public static boolean containsInstance(Model model, URI classURI) {
		try {
			return (model.contains(Variable.ANY, RDF.type, classURI));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Check if a resource is an instance of a given RDFS/OWL class.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param instanceID -
	 *            URI or BlankNode of the instance
	 * @param classURI -
	 *            URI of the RDFS/OWL class
	 * @return true if the model contains the triple (instanceID; rdf:type,
	 *         classURI)
	 * @throws Exception
	 */
	public static boolean isInstanceOf(Model model, Resource instanceID, URI classURI)
			throws ModelRuntimeException {
		return model.contains(instanceID, RDF.type, classURI);
	}

	/**
	 * @return java object, typed as desired by 'returnType'
	 * @throws Exception
	 */

	/**
	 * Use a SPARQL query on the model and wrap the result in a
	 * SparlSingleVariableIterator, to ensure that only single elements are
	 * included in the result. Assume 'x' as variable name.
	 * 
	 * @param m -
	 *            the underlying RDF2Go model
	 * @param returnType -
	 *            the desired Java return type
	 * @param sparqlSelectQuery -
	 *            the SPARQL query string
	 * @return SparqlSingleVariableIterator wrapper around the SPARQL query
	 *         result
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<? extends Object> getSparqlSelectSingleVariable(Model m, Class returnType,
			String sparqlSelectQuery) throws ModelRuntimeException {

		ClosableIterator<QueryRow> it = m.sparqlSelect(sparqlSelectQuery).iterator();

		return new ObjectResultIterator(m, new ExtractingIterator(m, it, "x"), returnType);

	}

	public static Statement getStatement(Model model, Resource resource, URI p, Object o) {
		return new StatementImpl(model.getContextURI(), resource, p, FORD.toNode(o));
	}



}
