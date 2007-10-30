
/**
 * generated by http://RDFReactor.semweb4j.org ($Id: CodeGenerator.java 785 2007-05-31 15:47:01Z voelkel $) on 01.06.07 17:33
 */
package org.ontoware.rdfreactor.schema.rdfschema;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;



/**
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 01.06.07 17:33
 * Schema Comment: The class of container membership properties, rdf:_1, rdf:_2, ...,
                    all of which are sub-properties of 'member'.
 */
public class ContainerMembershipProperty extends Property {

    /** http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty */
	public static final URI RDFS_CLASS = new URIImpl("http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty", false);

    /** all property-URIs with this class as domain */
    public static final URI[] MANAGED_URIS = {
 
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
	protected ContainerMembershipProperty ( Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write ) {
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
	public ContainerMembershipProperty ( Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write ) {
		super(model, RDFS_CLASS, instanceIdentifier, write);
	}


	/**
	 * Returns a Java wrapper over an RDF object, identified by a URI, given as a String.
	 * Creating two wrappers for the same URI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param uriString a URI given as a String
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 */
	public ContainerMembershipProperty ( Model model, String uriString, boolean write) {
		super(model, RDFS_CLASS, new URIImpl(uriString,false), write);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by a blank node.
	 * Creating two wrappers for the same blank node is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param bnode BlankNode of this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 */
	public ContainerMembershipProperty ( Model model, BlankNode bnode, boolean write ) {
		super(model, RDFS_CLASS, bnode, write);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by 
	 * a randomly generated URI.
	 * Creating two wrappers results in different URIs.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 */
	public ContainerMembershipProperty ( Model model, boolean write ) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // getters, setters, ...

	/**
	 * @param model RDF2Go model
	 * @param uri instance identifier
	 * @return an instance of ContainerMembershipProperty or null if none existst
	 * @throws Exception if Model causes problems
	 */
	public static ContainerMembershipProperty getInstance(Model model, URI uri) throws Exception {
		return (ContainerMembershipProperty) getInstance(model, uri, ContainerMembershipProperty.class);
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
	public ContainerMembershipProperty[] getAllInstances() {
		return (ContainerMembershipProperty[]) getAllInstances(super.model, ContainerMembershipProperty.class);
	}

	/**
	 * @return all instances of this class in the given Model
	 * @param model an RDF2Go model
	 */
	public static ContainerMembershipProperty[] getAllInstances(Model model) {
		return (ContainerMembershipProperty[]) getAllInstances(model, ContainerMembershipProperty.class);
	}
 
}

  
  