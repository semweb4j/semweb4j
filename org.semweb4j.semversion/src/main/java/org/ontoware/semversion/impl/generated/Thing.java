/**
 * generated by http://RDFReactor.semweb4j.org ($Id: CodeGenerator.java 785 2007-05-31 15:47:01Z voelkel $) on 16.10.07 16:48
 */
package org.ontoware.semversion.impl.generated;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.exception.ModelRuntimeException;

import org.ontoware.rdfreactor.runtime.RDFDataException;


/**
 * This class acts as a catch-all for all properties, for which no domain has specified.
 *  
 * This class manages access to these properties:
 * <ul>
 *   <li> Cardinality </li>
 *   <li> MaxCardinality </li>
 *   <li> Member </li>
 *   <li> VersionedModel </li>
 * </ul>
 *
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 16.10.07 16:48
 */
public class Thing extends org.ontoware.rdfreactor.schema.rdfschema.Class {

    /** http://www.w3.org/2000/01/rdf-schema#Class */
	public static final URI RDFS_CLASS = new URIImpl("http://www.w3.org/2000/01/rdf-schema#Class", false);

    /** http://www.w3.org/2002/07/owl#cardinality */
	public static final URI CARDINALITY = new URIImpl("http://www.w3.org/2002/07/owl#cardinality",false);

    /** http://www.w3.org/2002/07/owl#maxCardinality */
	public static final URI MAXCARDINALITY = new URIImpl("http://www.w3.org/2002/07/owl#maxCardinality",false);

    /** http://www.w3.org/2000/01/rdf-schema#member */
	public static final URI MEMBER = new URIImpl("http://www.w3.org/2000/01/rdf-schema#member",false);

    /** http://purl.org/net/semversion#hasVersionedModel */
	public static final URI VERSIONEDMODEL = new URIImpl("http://purl.org/net/semversion#hasVersionedModel",false);

    /** all property-URIs with this class as domain */
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.w3.org/2002/07/owl#cardinality",false),
      new URIImpl("http://www.w3.org/2002/07/owl#maxCardinality",false),
      new URIImpl("http://www.w3.org/2000/01/rdf-schema#member",false),
      new URIImpl("http://purl.org/net/semversion#hasVersionedModel",false) 
    };

	
	// protected constructors needed for inheritance
	
	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.semweb4j.org
	 * @param classURI URI of RDFS class
	 * @param instanceIdentifier Resource that identifies this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 */
	protected Thing ( Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write ) {
		super(model, classURI, instanceIdentifier, write);
	}

	// public constructors

	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param instanceIdentifier an RDF2Go Resource identifying this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 */
	public Thing ( Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write ) {
		super(model, RDFS_CLASS, instanceIdentifier, write);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * The statement (this, rdf:type, TYPE) is written to the model
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param uri URI of this instance
	 */
	public Thing ( Model model, URI uri ) {
		this(model, uri, true);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * The statement (this, rdf:type, TYPE) is written to the model
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param uriString A URI of this instance, represented as a String
	 * @throws ModelRuntimeException if URI syntax is wrong
	 */
	public Thing ( Model model, String uriString ) throws ModelRuntimeException {
		this(model, new URIImpl(uriString), true);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by a blank node.
	 * Creating two wrappers for the same blank node is legal.
	 * The statement (this, rdf:type, TYPE) is written to the model
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param bnode BlankNode of this instance
	 */
	public Thing ( Model model, BlankNode bnode ) {
		this(model, bnode, true);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by 
	 * a randomly generated URI.
	 * Creating two wrappers results in different URIs.
	 * The statement (this, rdf:type, TYPE) is written to the model
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 */
	public Thing ( Model model ) {
		this(model, model.newRandomUniqueURI(), true);
	}


    ///////////////////////////////////////////////////////////////////
    // getters, setters, ...

	/**
	 * @param model RDF2Go model
	 * @param uri instance identifier
	 * @return an instance of Thing or null if none existst
	 * @throws Exception if Model causes problems
	 */
	public static Thing getInstance(Model model, URI uri) throws Exception {
		return (Thing) getInstance(model, uri, Thing.class);
	}

	/**
	 * @param model
	 * @param uri
	 * @return true if uri is an instance of this class in the model
	 */
	public static boolean hasInstance(Model model, URI uri) {
		return hasInstance(model, uri, RDFS_CLASS);
	}

	/**
	 * @return all instances of this class
	 */
	public Thing[] getAllInstances() {
		return (Thing[]) getAllInstances(super.model, Thing.class);
	}

	/**
	 * @return all instances of this class in the given Model
	 * @param model an RDF2Go model
	 */
	public static Thing[] getAllInstances(Model model) {
		return (Thing[]) getAllInstances(model, Thing.class);
	}

	/**
	 * @return all A's that have a relation 'Cardinality' to this Thing instance
	 */
	public Thing[] getAllCardinality_Inverse() {
		return (Thing[]) getAll_Inverse(Thing.CARDINALITY, this.getResource(), Thing.class);
	}

	/**
	 * add 'Cardinality'-Inverse
	 * @param value
	 */
	public void addCardinality_Inverse(Thing value) {
		value.add( Thing.CARDINALITY ,this);
	}


	/**
	 * @return all A's that have a relation 'MaxCardinality' to this Thing instance
	 */
	public Thing[] getAllMaxCardinality_Inverse() {
		return (Thing[]) getAll_Inverse(Thing.MAXCARDINALITY, this.getResource(), Thing.class);
	}

	/**
	 * add 'MaxCardinality'-Inverse
	 * @param value
	 */
	public void addMaxCardinality_Inverse(Thing value) {
		value.add( Thing.MAXCARDINALITY ,this);
	}



    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Thing getCardinality() {
		return (Thing) get(CARDINALITY, Thing.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setCardinality( Thing value ) {
		set(CARDINALITY, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeCardinality() {
		removeAll(CARDINALITY);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeCardinality( Thing value  ) {
		remove(CARDINALITY, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllCardinality() {
		removeAll(CARDINALITY);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, CARDINALITY, value)
	 */
	public boolean hasCardinality( Thing value) {
		return hasValue(CARDINALITY, value);
	}

	/**
	 * @return true if the model contains a statement (this, CARDINALITY, *)
	 */
	public boolean hasCardinality() {
		return hasValue(CARDINALITY);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addCardinality( Thing value  ) {
		add(CARDINALITY, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public Thing[] getAllCardinality() {
		return (Thing[]) getAll(CARDINALITY, Thing.class);
	}
  

    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public Thing getMaxCardinality() {
		return (Thing) get(MAXCARDINALITY, Thing.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setMaxCardinality( Thing value ) {
		set(MAXCARDINALITY, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeMaxCardinality() {
		removeAll(MAXCARDINALITY);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeMaxCardinality( Thing value  ) {
		remove(MAXCARDINALITY, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllMaxCardinality() {
		removeAll(MAXCARDINALITY);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, MAXCARDINALITY, value)
	 */
	public boolean hasMaxCardinality( Thing value) {
		return hasValue(MAXCARDINALITY, value);
	}

	/**
	 * @return true if the model contains a statement (this, MAXCARDINALITY, *)
	 */
	public boolean hasMaxCardinality() {
		return hasValue(MAXCARDINALITY);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addMaxCardinality( Thing value  ) {
		add(MAXCARDINALITY, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public Thing[] getAllMaxCardinality() {
		return (Thing[]) getAll(MAXCARDINALITY, Thing.class);
	}
  

    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public org.ontoware.rdfreactor.schema.rdfschema.Resource getMember() {
		return (org.ontoware.rdfreactor.schema.rdfschema.Resource) get(MEMBER, org.ontoware.rdfreactor.schema.rdfschema.Resource.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setMember( org.ontoware.rdfreactor.schema.rdfschema.Resource value ) {
		set(MEMBER, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeMember() {
		removeAll(MEMBER);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeMember( org.ontoware.rdfreactor.schema.rdfschema.Resource value  ) {
		remove(MEMBER, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllMember() {
		removeAll(MEMBER);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, MEMBER, value)
	 */
	public boolean hasMember( org.ontoware.rdfreactor.schema.rdfschema.Resource value) {
		return hasValue(MEMBER, value);
	}

	/**
	 * @return true if the model contains a statement (this, MEMBER, *)
	 */
	public boolean hasMember() {
		return hasValue(MEMBER);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addMember( org.ontoware.rdfreactor.schema.rdfschema.Resource value  ) {
		add(MEMBER, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public org.ontoware.rdfreactor.schema.rdfschema.Resource[] getAllMember() {
		return (org.ontoware.rdfreactor.schema.rdfschema.Resource[]) getAll(MEMBER, org.ontoware.rdfreactor.schema.rdfschema.Resource.class);
	}
  

    /**
     * Schema Comment: 
     * @return the only value. null if none is found
     * @throws RDFDataException, if the property has multiple values
     */
	public VersionedModel getVersionedModel() {
		return (VersionedModel) get(VERSIONEDMODEL, VersionedModel.class);
	}

	/**
	 * removes all values and sets this one
	 * @param value the value to be set
     * Schema Comment: 
	 */
	public void setVersionedModel( VersionedModel value ) {
		set(VERSIONEDMODEL, value);
	}

	/**
	 * removes current value(s)
     * Schema Comment: 
	 */
	public void removeVersionedModel() {
		removeAll(VERSIONEDMODEL);
	}
 	/**
	 * removes a value
	 * @param value the value to be removed
     * Schema Comment: 
	 */
	public void removeVersionedModel( VersionedModel value  ) {
		remove(VERSIONEDMODEL, value);
	}

	/**
	 * removes all values
     * Schema Comment: 
	 */
	public void removeAllVersionedModel() {
		removeAll(VERSIONEDMODEL);
	}
 
	/**
	 * @param value
	 * @return true if the model contains a statement (this, VERSIONEDMODEL, value)
	 */
	public boolean hasVersionedModel( VersionedModel value) {
		return hasValue(VERSIONEDMODEL, value);
	}

	/**
	 * @return true if the model contains a statement (this, VERSIONEDMODEL, *)
	 */
	public boolean hasVersionedModel() {
		return hasValue(VERSIONEDMODEL);
	}

 
	/**
	 * adds a value
	 * @param value the value to be added
     * Schema Comment: 
	 */
	public void addVersionedModel( VersionedModel value  ) {
		add(VERSIONEDMODEL, value );
	}

	/**
	 * @return all values
     * Schema Comment: 
	 */
	public VersionedModel[] getAllVersionedModel() {
		return (VersionedModel[]) getAll(VERSIONEDMODEL, VersionedModel.class);
	}
   
}

  
  
