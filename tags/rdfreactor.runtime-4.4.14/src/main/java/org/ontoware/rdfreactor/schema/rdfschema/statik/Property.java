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
 *   <li> Domain </li>
 *   <li> Range </li>
 *   <li> SubPropertyOf </li>
 * </ul>
 *
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 11.11.07 18:35
 */
public class Property extends Resource {

    /** http://www.w3.org/1999/02/22-rdf-syntax-ns#Property */
	public static final URI RDFS_CLASS = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property", false);

    /** http://www.w3.org/2000/01/rdf-schema#domain */
	public static final URI DOMAIN = new URIImpl("http://www.w3.org/2000/01/rdf-schema#domain",false);

    /** http://www.w3.org/2000/01/rdf-schema#range */
	public static final URI RANGE = new URIImpl("http://www.w3.org/2000/01/rdf-schema#range",false);

    /** http://www.w3.org/2000/01/rdf-schema#subPropertyOf */
	public static final URI SUBPROPERTYOF = new URIImpl("http://www.w3.org/2000/01/rdf-schema#subPropertyOf",false);

    /** all property-URIs with this class as domain */
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.w3.org/2000/01/rdf-schema#domain",false),
      new URIImpl("http://www.w3.org/2000/01/rdf-schema#range",false),
      new URIImpl("http://www.w3.org/2000/01/rdf-schema#subPropertyOf",false) 
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
	 * @return all A's that have a relation 'SubPropertyOf' to this Property instance
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllSubPropertyOf_Inverse( Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Property.SUBPROPERTYOF, objectValue);
	}



    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Class getDomain(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return (Class) Base.get(model, resource, DOMAIN, Class.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setDomain( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value ) {
		Base.set(model, resource, DOMAIN, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeDomain(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll(model, resource, DOMAIN);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeDomain( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value  ) {
		Base.remove(model, resource, DOMAIN, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllDomain(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll( model, resource, DOMAIN);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, DOMAIN, value)
	 */
	public boolean hasDomain( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value) {
		return Base.hasValue(model, resource, DOMAIN, value);
	}

	/**
	 * @return true if the model contains a statement (this, DOMAIN, *)
	 */
	public boolean hasDomain(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.hasValue(model, resource, DOMAIN);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addDomain( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value  ) {
		Base.add(model, resource,DOMAIN, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public ClosableIterator<Class> getAllDomain(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.getAll(model, resource,DOMAIN, Class.class);
	}
  

    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Class getRange(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return (Class) Base.get(model, resource, RANGE, Class.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setRange( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value ) {
		Base.set(model, resource, RANGE, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeRange(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll(model, resource, RANGE);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeRange( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value  ) {
		Base.remove(model, resource, RANGE, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllRange(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll( model, resource, RANGE);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, RANGE, value)
	 */
	public boolean hasRange( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value) {
		return Base.hasValue(model, resource, RANGE, value);
	}

	/**
	 * @return true if the model contains a statement (this, RANGE, *)
	 */
	public boolean hasRange(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.hasValue(model, resource, RANGE);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addRange( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value  ) {
		Base.add(model, resource,RANGE, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public ClosableIterator<Class> getAllRange(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.getAll(model, resource,RANGE, Class.class);
	}
  

    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Property getSubPropertyOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return (Property) Base.get(model, resource, SUBPROPERTYOF, Property.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setSubPropertyOf( Model model, org.ontoware.rdf2go.model.node.Resource resource, Property value ) {
		Base.set(model, resource, SUBPROPERTYOF, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeSubPropertyOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll(model, resource, SUBPROPERTYOF);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeSubPropertyOf( Model model, org.ontoware.rdf2go.model.node.Resource resource, Property value  ) {
		Base.remove(model, resource, SUBPROPERTYOF, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllSubPropertyOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll( model, resource, SUBPROPERTYOF);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, SUBPROPERTYOF, value)
	 */
	public boolean hasSubPropertyOf( Model model, org.ontoware.rdf2go.model.node.Resource resource, Property value) {
		return Base.hasValue(model, resource, SUBPROPERTYOF, value);
	}

	/**
	 * @return true if the model contains a statement (this, SUBPROPERTYOF, *)
	 */
	public boolean hasSubPropertyOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.hasValue(model, resource, SUBPROPERTYOF);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addSubPropertyOf( Model model, org.ontoware.rdf2go.model.node.Resource resource, Property value  ) {
		Base.add(model, resource,SUBPROPERTYOF, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public ClosableIterator<Property> getAllSubPropertyOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.getAll(model, resource,SUBPROPERTYOF, Property.class);
	}
   
}

  
  