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
 *   <li> SubClassOf </li>
 *   <li> Tag </li>
 * </ul>
 *
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 11.11.07 18:35
 */
public class Class extends Resource {

    /** http://www.w3.org/2000/01/rdf-schema#Class */
	public static final URI RDFS_CLASS = new URIImpl("http://www.w3.org/2000/01/rdf-schema#Class", false);

    /** http://www.w3.org/2000/01/rdf-schema#subClassOf */
	public static final URI SUBCLASSOF = new URIImpl("http://www.w3.org/2000/01/rdf-schema#subClassOf",false);

    /** http://semfs.ontoware.org/ontology/tagfs#hasTag */
	public static final URI TAG = new URIImpl("http://semfs.ontoware.org/ontology/tagfs#hasTag",false);

    /** all property-URIs with this class as domain */
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.w3.org/2000/01/rdf-schema#subClassOf",false),
      new URIImpl("http://semfs.ontoware.org/ontology/tagfs#hasTag",false) 
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
	 * @return all A's that have a relation 'Type' to this Class instance
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllType_Inverse( Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Resource.TYPE, objectValue);
	}


	/**
	 * @return all A's that have a relation 'SubClassOf' to this Class instance
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllSubClassOf_Inverse( Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Class.SUBCLASSOF, objectValue);
	}


	/**
	 * @return all A's that have a relation 'Domain' to this Class instance
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllDomain_Inverse( Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Property.DOMAIN, objectValue);
	}


	/**
	 * @return all A's that have a relation 'Range' to this Class instance
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllRange_Inverse( Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Property.RANGE, objectValue);
	}



    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Class getSubClassOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return (Class) Base.get(model, resource, SUBCLASSOF, Class.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setSubClassOf( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value ) {
		Base.set(model, resource, SUBCLASSOF, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeSubClassOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll(model, resource, SUBCLASSOF);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeSubClassOf( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value  ) {
		Base.remove(model, resource, SUBCLASSOF, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllSubClassOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll( model, resource, SUBCLASSOF);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, SUBCLASSOF, value)
	 */
	public boolean hasSubClassOf( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value) {
		return Base.hasValue(model, resource, SUBCLASSOF, value);
	}

	/**
	 * @return true if the model contains a statement (this, SUBCLASSOF, *)
	 */
	public boolean hasSubClassOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.hasValue(model, resource, SUBCLASSOF);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addSubClassOf( Model model, org.ontoware.rdf2go.model.node.Resource resource, Class value  ) {
		Base.add(model, resource,SUBCLASSOF, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public ClosableIterator<Class> getAllSubClassOf(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.getAll(model, resource,SUBCLASSOF, Class.class);
	}
  

    /**
     * Schema Comment: links a resource to a tag string
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Literal getTag(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return (Literal) Base.get(model, resource, TAG, Literal.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: links a resource to a tag string
	 */
	public void setTag( Model model, org.ontoware.rdf2go.model.node.Resource resource, Literal value ) {
		Base.set(model, resource, TAG, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: links a resource to a tag string
	 */
	public void removeTag(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll(model, resource, TAG);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: links a resource to a tag string
	 */
	public void removeTag( Model model, org.ontoware.rdf2go.model.node.Resource resource, Literal value  ) {
		Base.remove(model, resource, TAG, value);
	}

	/**
	 * removes all values
     * Schema Comment: links a resource to a tag string
	 */
	public void removeAllTag(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		Base.removeAll( model, resource, TAG);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, TAG, value)
	 */
	public boolean hasTag( Model model, org.ontoware.rdf2go.model.node.Resource resource, Literal value) {
		return Base.hasValue(model, resource, TAG, value);
	}

	/**
	 * @return true if the model contains a statement (this, TAG, *)
	 */
	public boolean hasTag(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.hasValue(model, resource, TAG);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: links a resource to a tag string
	 */
	public void addTag( Model model, org.ontoware.rdf2go.model.node.Resource resource, Literal value  ) {
		Base.add(model, resource,TAG, value );
	}

	/**
	 * @return all values
     * Schema Comment: links a resource to a tag string
	 */
	public ClosableIterator<Literal> getAllTag(Model model, org.ontoware.rdf2go.model.node.Resource resource) {
		return Base.getAll(model, resource,TAG, Literal.class);
	}
   
}

  
  
