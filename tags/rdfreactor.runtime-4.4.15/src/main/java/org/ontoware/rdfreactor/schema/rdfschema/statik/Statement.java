/**
 * generated by http://RDFReactor.semweb4j.org ($Id: CodeGenerator.java 870 2007-11-07 17:30:59Z max.at.xam.de $) on 11.11.07 18:35
 */
package org.ontoware.rdfreactor.schema.rdfschema.statik;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.Base;
import org.ontoware.rdfreactor.runtime.RDFDataException;


/**
 * 
 *  
 * This class manages access to these properties:
 * <ul>
 *   <li> Object </li>
 *   <li> Predicate </li>
 *   <li> Subject </li>
 * </ul>
 *
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 11.11.07 18:35
 */
public class Statement extends Resource {

    /** http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement */
	public static final URI RDFS_CLASS = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement", false);

    /** http://www.w3.org/1999/02/22-rdf-syntax-ns#object */
	public static final URI OBJECT = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#object",false);

    /** http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate */
	public static final URI PREDICATE = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate",false);

    /** http://www.w3.org/1999/02/22-rdf-syntax-ns#subject */
	public static final URI SUBJECT = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#subject",false);

    /** all property-URIs with this class as domain */
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#object",false),
      new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate",false),
      new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#subject",false) 
    };

    ///////////////////////////////////////////////////////////////////
    // getters, setters, ...

	/**
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return true if instanceResource is an instance of classResource in the model
	 */
	public static boolean hasInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.hasInstance(model, RDFS_CLASS, instanceResource);
	}



    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Resource getObject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return (Resource) Base.get(model, resource, OBJECT, Resource.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setObject( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value ) {
		Base.set(model, resource, OBJECT, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeObject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll(model, resource, OBJECT);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeObject( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value  ) {
		Base.remove(model, resource, OBJECT, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllObject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll( model, resource, OBJECT);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, OBJECT, value)
	 */
	public boolean hasObject( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value) {
		return Base.hasValue(model, resource, OBJECT, value);
	}

	/**
	 * @return true if the model contains a statement (this, OBJECT, *)
	 */
	public boolean hasObject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.hasValue(model, resource, OBJECT);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addObject( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value  ) {
		Base.add(model, resource,OBJECT, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public ClosableIterator<Resource> getAllObject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.getAll(model, resource,OBJECT, Resource.class);
	}
  

    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Resource getPredicate(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return (Resource) Base.get(model, resource, PREDICATE, Resource.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setPredicate( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value ) {
		Base.set(model, resource, PREDICATE, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removePredicate(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll(model, resource, PREDICATE);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removePredicate( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value  ) {
		Base.remove(model, resource, PREDICATE, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllPredicate(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll( model, resource, PREDICATE);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, PREDICATE, value)
	 */
	public boolean hasPredicate( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value) {
		return Base.hasValue(model, resource, PREDICATE, value);
	}

	/**
	 * @return true if the model contains a statement (this, PREDICATE, *)
	 */
	public boolean hasPredicate(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.hasValue(model, resource, PREDICATE);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addPredicate( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value  ) {
		Base.add(model, resource,PREDICATE, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public ClosableIterator<Resource> getAllPredicate(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.getAll(model, resource,PREDICATE, Resource.class);
	}
  

    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Resource getSubject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return (Resource) Base.get(model, resource, SUBJECT, Resource.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setSubject( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value ) {
		Base.set(model, resource, SUBJECT, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeSubject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll(model, resource, SUBJECT);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeSubject( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value  ) {
		Base.remove(model, resource, SUBJECT, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllSubject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll( model, resource, SUBJECT);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, SUBJECT, value)
	 */
	public boolean hasSubject( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value) {
		return Base.hasValue(model, resource, SUBJECT, value);
	}

	/**
	 * @return true if the model contains a statement (this, SUBJECT, *)
	 */
	public boolean hasSubject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.hasValue(model, resource, SUBJECT);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addSubject( Model model, org.ontoware.rdf2go.model.node.Resource resource, Resource value  ) {
		Base.add(model, resource,SUBJECT, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public ClosableIterator<Resource> getAllSubject(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.getAll(model, resource,SUBJECT, Resource.class);
	}
   
}

  
  