package org.ontoware.rdfreactor.runtime;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;

/**
 * <b>ReactorBase</b> is the Interface which has to be implemented by all
 * classes used to represent the RDFS or OWL ontology.
 * 
 * @author mvo
 */
public interface ReactorBase extends ResourceEntity, Mappable<URI, Object> {

	/**
	 * @return the model for which this instance acts as a view
	 */
	public Model getModel();

	/**
	 * This method is useful for functional properties.
	 * 
	 * @param prop
	 * @return the first x in (this, prop, x) if such a statement is in the
	 *         model. Null otherwise.
	 * @throws RDFDataException
	 *             if multiple values are present
	 */
	public Object get(URI prop, java.lang.Class returnType)
			throws RDFDataException;

	/**
	 * Get all predicates x of triples matching (this, prop, x)
	 * 
	 * @param prop
	 * @return an array of x with (this, prop, x) if such statements are in the
	 *         model. Empty array otherwise.
	 */
	public Object[] getAll(URI prop, java.lang.Class returnType);

	/**
	 * Removes all statements (this, prop, x) and set one anew: (this, prop, o).
	 * 
	 * @param prop
	 * @param o
	 */
	public void set(URI prop, Object o);

	/**
	 * Removes all statements (this, prop, x) and sets anew: (this, prop, o[0]),
	 * (this, prop, o[1]), ...
	 * 
	 * @param prop
	 * @param o
	 * @throws Exception
	 */
	public void setAll(URI prop, Object[] o) throws ModelRuntimeException;

	/**
	 * Removes all statements (this, prop, x) and sets anew: (this, prop, o[0]),
	 * (this, prop, o[1]), ... But only if the number of objects in o[] is less
	 * than or equal to maxCard
	 * 
	 * 
	 * @param prop
	 * @param o
	 * @param maxCard
	 *            is the maximum number of triples allowed to match (this, prop,
	 *            x)
	 * @throws Exception
	 * @throws CardinalityException
	 */
	public void setAll(URI prop, Object[] o, int maxCard)
			throws ModelRuntimeException, CardinalityException;

	/**
	 * Looks for a statement (this, prop, oldValue) and replaces it by a new
	 * statement (this, prop, newValue). If the first cannot be found, false is
	 * returned, true otherwise.
	 * 
	 * @param prop
	 * @param oldValue
	 * @param newValue
	 * @return true, if old value was found in the model
	 * @throws Exception
	 */
	public boolean update(URI prop, Object oldValue, Object newValue)
			throws ModelRuntimeException;

	/**
	 * Adds a statement (this, prop, o).
	 * 
	 * @param prop
	 * @param o
	 * @return true if value was already in the model
	 * @throws Exception
	 */
	public boolean add(URI prop, Object o) throws ModelRuntimeException;

	/**
	 * Adds a statement (this, prop, o) if the number of statements matching
	 * (this, prop, x) is less then maxCard
	 * 
	 * @param property
	 * @param object
	 * @param maxCard,
	 *            number of occurences of (this, prop, x) allowed in the model
	 * @return true if value was already preset
	 * @throws CardinalityException
	 */

	public boolean add(URI prop, Object o, int maxCard)
			throws CardinalityException, ModelRuntimeException;

	/**
	 * Tries to remove a statement (this, prop, o).
	 * 
	 * @param prop
	 * @param o
	 * @return true if old value was found
	 */
	public boolean remove(URI prop, Object o);

	/**
	 * Tries to remove a statement (this, prop, o) if the number of statements
	 * matching (this, prop, x) in the model is less then minCard
	 * 
	 * @param prop
	 * @param o
	 * @param minCard,
	 *            number of occurences of (this, prop, x) needed in the model
	 * @return true if value was found
	 * @throws CardinalityException
	 */
	public boolean remove(URI prop, Object o, int minCard)
			throws CardinalityException;

	/**
	 * Remove all values of this property (same as remove(this, prop, *)
	 * 
	 * @param prop
	 *            the URI to be removed
	 * @return true if at least on value was found and removed
	 */
	public boolean removeAll(URI prop);

	// /**
	// * @deprecated not needed
	// * @param uri
	// * property URI
	// * @param o
	// * must be an instance of RDFBase (thus has a URI)
	// * @return a statement (this, uri, object) and thus bridges the gap
	// between
	// * the OO and RDF worlds.
	// */
	// public Statement getStatement(URI uri, ReactorBase o);
	//
	// /**
	// * @deprecated not needed
	// * @param uri
	// * property URI
	// * @param o
	// * must be a unique literal
	// * @return a statement (this, uri, object) and thus bridges the gap
	// between
	// * the OO and RDF worlds.
	// */
	// public Statement getStatement(URI uri, String o);

	/**
	 * remove all (this, rdf:type, ANY) statements
	 */
	public void delete();

	// ////////////////////////////////////////
	// queries

	/**
	 * @return the URI of the rdfs:Class, as which this resource is viewed
	 */
	public URI getRDFSClassURI();

	public boolean isInstanceof(URI classURI) throws ModelRuntimeException;

	/**
	 * @deprecated not needed
	 * @param javaClass
	 * @return true iff the given Object is of type javaClass
	 * @throws Exception
	 */
	public boolean isInstanceof(java.lang.Class javaClass)
			throws ModelRuntimeException;

	/**
	 * 
	 * @param targetType
	 *            any java class that extends ReactorBase directly or indirectly
	 * @return a reference to a new object seeing this object as a 'targetType'
	 */
	public Object castTo(java.lang.Class targetType);

	/**
	 * @param model
	 *            and RDG2GO model
	 * @return true, iff this resource is contained in any statement of the
	 *         model
	 */
	public boolean in(Model model);

}
