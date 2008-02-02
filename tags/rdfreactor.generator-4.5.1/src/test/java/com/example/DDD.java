/**
 * generated by http://RDFReactor.semweb4j.org ($Id: CodeGenerator.java 1046 2008-01-26 14:38:26Z max.at.xam.de $) on 02.02.08 13:35
 */
package com.example;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.Base;
import org.ontoware.rdfreactor.runtime.ReactorResult;

/**
 * This class manages access to these properties:
 * <ul>
 *   <li> Sss1 </li>
 *   <li> Ttt1 </li>
 *   <li> Ttt2 </li>
 * </ul>
 *
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 02.02.08 13:35
 */
public class DDD extends Thing {

    /** urn:ex:DDD */
	public static final URI RDFS_CLASS = new URIImpl("urn:ex:DDD", false);

    /** urn:ex:sss1 */
	public static final URI SSS1 = new URIImpl("urn:ex:sss1",false);

    /** urn:ex:ttt1 */
	public static final URI TTT1 = new URIImpl("urn:ex:ttt1",false);

    /** urn:ex:ttt2 */
	public static final URI TTT2 = new URIImpl("urn:ex:ttt2",false);

    /** all property-URIs with this class as domain */
    public static final URI[] MANAGED_URIS = {
      new URIImpl("urn:ex:sss1",false),
      new URIImpl("urn:ex:ttt1",false),
      new URIImpl("urn:ex:ttt2",false) 
    };


	// protected constructors needed for inheritance
	
	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.semweb4j.org
	 * @param classURI URI of RDFS class
	 * @param instanceIdentifier Resource that identifies this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c1] 
	 */
	protected DDD ( Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write ) {
		super(model, classURI, instanceIdentifier, write);
	}

	// public constructors

	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param instanceIdentifier an RDF2Go Resource identifying this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c2] 
	 */
	public DDD ( Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write ) {
		super(model, RDFS_CLASS, instanceIdentifier, write);
	}


	/**
	 * Returns a Java wrapper over an RDF object, identified by a URI, given as a String.
	 * Creating two wrappers for the same URI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param uriString a URI given as a String
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 * @throws ModelRuntimeException if URI syntax is wrong
	 *
	 * [Generated from RDFReactor template rule #c7] 
	 */
	public DDD ( Model model, String uriString, boolean write) throws ModelRuntimeException {
		super(model, RDFS_CLASS, new URIImpl(uriString,false), write);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by a blank node.
	 * Creating two wrappers for the same blank node is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param bnode BlankNode of this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c8] 
	 */
	public DDD ( Model model, BlankNode bnode, boolean write ) {
		super(model, RDFS_CLASS, bnode, write);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by 
	 * a randomly generated URI.
	 * Creating two wrappers results in different URIs.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c9] 
	 */
	public DDD ( Model model, boolean write ) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Create a new instance of this class in the model. 
	 * That is, create the statement (instanceResource, RDF.type, urn:ex:DDD).
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #class1] 
	 */
	public static void createInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.createInstance(model, RDFS_CLASS, instanceResource);
	}

	/**
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return true if instanceResource is an instance of this class in the model
	 *
	 * [Generated from RDFReactor template rule #class2] 
	 */
	public static boolean hasInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.hasInstance(model, RDFS_CLASS, instanceResource);
	}

	/**
	 * @param model an RDF2Go model
	 * @return all instances of this class in Model 'model' as RDF resources
	 *
	 * [Generated from RDFReactor template rule #class3] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllInstances(Model model) {
		return Base.getAllInstances(model, RDFS_CLASS, org.ontoware.rdf2go.model.node.Resource.class);
	}

	/**
	 * @param model an RDF2Go model
	 * @return all instances of this class in Model 'model' as a ReactorResult,
	 * which can conveniently be converted to iterator, list or array.
	 *
	 * [Generated from RDFReactor template rule #class3-as] 
	 */
	public static ReactorResult<? extends DDD> getAllInstance_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, DDD.class );
	}

    /**
	 * Delete all rdf:type from this instance. Other triples are not affected.
	 * To delete more, use deleteAllProperties
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #class4] 
	 */
	public static void deleteInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.deleteInstance(model, RDFS_CLASS, instanceResource);
	}

	/**
	 * Delete all (this, *, *), i.e. including rdf:type
	 * @param model an RDF2Go model
	 * @param resource
	 */
	public static void deleteAllProperties(Model model,	org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.deleteAllProperties(model, instanceResource);
	}

    ///////////////////////////////////////////////////////////////////
    // property access methods


    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@172fbca has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasSss1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, SSS1);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@172fbca has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasSss1() {
		return Base.has(this.model, this.getResource(), SSS1);
	}

     /**
     * Get all values of property Sss1 as an Iterator over RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static] 
     */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllSss1_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (ClosableIterator<org.ontoware.rdf2go.model.node.Node>) Base.getAll_asNode(model, instanceResource, SSS1);
	}
	
    /**
     * Get all values of property Sss1 as a ReactorResult of RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result] 
     */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllSss1_asNode_(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (ReactorResult<org.ontoware.rdf2go.model.node.Node>) Base.getAll_as(model, instanceResource, SSS1, org.ontoware.rdf2go.model.node.Node.class);
	}

    /**
     * Get all values of property Sss1 as an Iterator over RDF2Go nodes 
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic] 
     */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllSss1_asNode() {
		return (ClosableIterator<org.ontoware.rdf2go.model.node.Node>) Base.getAll_asNode(this.model, this.getResource(), SSS1);
	}

    /**
     * Get all values of property Sss1 as a ReactorResult of RDF2Go nodes 
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result] 
     */
	public ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllSss1_asNode_() {
		return (ReactorResult<org.ontoware.rdf2go.model.node.Node>) Base.getAll_as(this.model, this.getResource(), SSS1, org.ontoware.rdf2go.model.node.Node.class);
	}
     /**
     * Get all values of property Sss1     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static] 
     */
	public static ClosableIterator<Thing> getAllSss1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll(model, instanceResource, SSS1, Thing.class);
	}
	
    /**
     * Get all values of property Sss1 as a ReactorResult of Thing 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult] 
     */
	public static ReactorResult<Thing> getAllSss1_as(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, SSS1, Thing.class);
	}

    /**
     * Get all values of property Sss1     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic] 
     */
	public ClosableIterator<Thing> getAllSss1() {
		return Base.getAll(this.model, this.getResource(), SSS1, Thing.class);
	}

    /**
     * Get all values of property Sss1 as a ReactorResult of Thing 
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult] 
     */
	public ReactorResult<Thing> getAllSss1_as() {
		return Base.getAll_as(this.model, this.getResource(), SSS1, Thing.class);
	}
 
    /**
     * Adds a value to property Sss1 as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static] 
     */
	public static void addSss1( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.add(model, instanceResource, SSS1, value);
	}
	
    /**
     * Adds a value to property Sss1 as an RDF2Go node 
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic] 
     */
	public void addSss1( org.ontoware.rdf2go.model.node.Node value) {
		Base.add(this.model, this.getResource(), SSS1, value);
	}
    /**
     * Adds a value to property Sss1 from an instance of Thing 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #add3static] 
     */
	public static void addSss1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, Thing value) {
		Base.add(model, instanceResource, SSS1, value);
	}
	
    /**
     * Adds a value to property Sss1 from an instance of Thing 
	 *
	 * [Generated from RDFReactor template rule #add4dynamic] 
     */
	public void addSss1(Thing value) {
		Base.add(this.model, this.getResource(), SSS1, value);
	}
  

    /**
     * Sets a value of property Sss1 from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setSss1( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, SSS1, value);
	}
	
    /**
     * Sets a value of property Sss1 from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setSss1( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), SSS1, value);
	}
    /**
     * Sets a value of property Sss1 from an instance of Thing 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setSss1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, Thing value) {
		Base.set(model, instanceResource, SSS1, value);
	}
	
    /**
     * Sets a value of property Sss1 from an instance of Thing 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setSss1(Thing value) {
		Base.set(this.model, this.getResource(), SSS1, value);
	}
  


    /**
     * Removes a value of property Sss1 as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeSss1( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, SSS1, value);
	}
	
    /**
     * Removes a value of property Sss1 as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeSss1( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), SSS1, value);
	}
    /**
     * Removes a value of property Sss1 given as an instance of Thing 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeSss1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, Thing value) {
		Base.remove(model, instanceResource, SSS1, value);
	}
	
    /**
     * Removes a value of property Sss1 given as an instance of Thing 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeSss1(Thing value) {
		Base.remove(this.model, this.getResource(), SSS1, value);
	}
  
    /**
     * Removes all values of property Sss1     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllSss1( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, SSS1);
	}
	
    /**
     * Removes all values of property Sss1	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void addSss1() {
		Base.removeAll(this.model, this.getResource(), SSS1);
	}
     /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@d6be89 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasTtt1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, TTT1);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@d6be89 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasTtt1() {
		return Base.has(this.model, this.getResource(), TTT1);
	}

     /**
     * Get all values of property Ttt1 as an Iterator over RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static] 
     */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllTtt1_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (ClosableIterator<org.ontoware.rdf2go.model.node.Node>) Base.getAll_asNode(model, instanceResource, TTT1);
	}
	
    /**
     * Get all values of property Ttt1 as a ReactorResult of RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result] 
     */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllTtt1_asNode_(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (ReactorResult<org.ontoware.rdf2go.model.node.Node>) Base.getAll_as(model, instanceResource, TTT1, org.ontoware.rdf2go.model.node.Node.class);
	}

    /**
     * Get all values of property Ttt1 as an Iterator over RDF2Go nodes 
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic] 
     */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllTtt1_asNode() {
		return (ClosableIterator<org.ontoware.rdf2go.model.node.Node>) Base.getAll_asNode(this.model, this.getResource(), TTT1);
	}

    /**
     * Get all values of property Ttt1 as a ReactorResult of RDF2Go nodes 
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result] 
     */
	public ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllTtt1_asNode_() {
		return (ReactorResult<org.ontoware.rdf2go.model.node.Node>) Base.getAll_as(this.model, this.getResource(), TTT1, org.ontoware.rdf2go.model.node.Node.class);
	}
     /**
     * Get all values of property Ttt1     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static] 
     */
	public static ClosableIterator<FFF> getAllTtt1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll(model, instanceResource, TTT1, FFF.class);
	}
	
    /**
     * Get all values of property Ttt1 as a ReactorResult of FFF 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult] 
     */
	public static ReactorResult<FFF> getAllTtt1_as(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, TTT1, FFF.class);
	}

    /**
     * Get all values of property Ttt1     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic] 
     */
	public ClosableIterator<FFF> getAllTtt1() {
		return Base.getAll(this.model, this.getResource(), TTT1, FFF.class);
	}

    /**
     * Get all values of property Ttt1 as a ReactorResult of FFF 
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult] 
     */
	public ReactorResult<FFF> getAllTtt1_as() {
		return Base.getAll_as(this.model, this.getResource(), TTT1, FFF.class);
	}
 
    /**
     * Adds a value to property Ttt1 as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static] 
     */
	public static void addTtt1( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.add(model, instanceResource, TTT1, value);
	}
	
    /**
     * Adds a value to property Ttt1 as an RDF2Go node 
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic] 
     */
	public void addTtt1( org.ontoware.rdf2go.model.node.Node value) {
		Base.add(this.model, this.getResource(), TTT1, value);
	}
    /**
     * Adds a value to property Ttt1 from an instance of FFF 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #add3static] 
     */
	public static void addTtt1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, FFF value) {
		Base.add(model, instanceResource, TTT1, value);
	}
	
    /**
     * Adds a value to property Ttt1 from an instance of FFF 
	 *
	 * [Generated from RDFReactor template rule #add4dynamic] 
     */
	public void addTtt1(FFF value) {
		Base.add(this.model, this.getResource(), TTT1, value);
	}
  

    /**
     * Sets a value of property Ttt1 from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setTtt1( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, TTT1, value);
	}
	
    /**
     * Sets a value of property Ttt1 from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setTtt1( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), TTT1, value);
	}
    /**
     * Sets a value of property Ttt1 from an instance of FFF 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setTtt1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, FFF value) {
		Base.set(model, instanceResource, TTT1, value);
	}
	
    /**
     * Sets a value of property Ttt1 from an instance of FFF 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setTtt1(FFF value) {
		Base.set(this.model, this.getResource(), TTT1, value);
	}
  


    /**
     * Removes a value of property Ttt1 as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeTtt1( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, TTT1, value);
	}
	
    /**
     * Removes a value of property Ttt1 as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeTtt1( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), TTT1, value);
	}
    /**
     * Removes a value of property Ttt1 given as an instance of FFF 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeTtt1(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, FFF value) {
		Base.remove(model, instanceResource, TTT1, value);
	}
	
    /**
     * Removes a value of property Ttt1 given as an instance of FFF 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeTtt1(FFF value) {
		Base.remove(this.model, this.getResource(), TTT1, value);
	}
  
    /**
     * Removes all values of property Ttt1     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllTtt1( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, TTT1);
	}
	
    /**
     * Removes all values of property Ttt1	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void addTtt1() {
		Base.removeAll(this.model, this.getResource(), TTT1);
	}
     /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@8f5f75 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasTtt2(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, TTT2);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@8f5f75 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasTtt2() {
		return Base.has(this.model, this.getResource(), TTT2);
	}

     /**
     * Get all values of property Ttt2 as an Iterator over RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static] 
     */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllTtt2_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (ClosableIterator<org.ontoware.rdf2go.model.node.Node>) Base.getAll_asNode(model, instanceResource, TTT2);
	}
	
    /**
     * Get all values of property Ttt2 as a ReactorResult of RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result] 
     */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllTtt2_asNode_(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (ReactorResult<org.ontoware.rdf2go.model.node.Node>) Base.getAll_as(model, instanceResource, TTT2, org.ontoware.rdf2go.model.node.Node.class);
	}

    /**
     * Get all values of property Ttt2 as an Iterator over RDF2Go nodes 
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic] 
     */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllTtt2_asNode() {
		return (ClosableIterator<org.ontoware.rdf2go.model.node.Node>) Base.getAll_asNode(this.model, this.getResource(), TTT2);
	}

    /**
     * Get all values of property Ttt2 as a ReactorResult of RDF2Go nodes 
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result] 
     */
	public ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllTtt2_asNode_() {
		return (ReactorResult<org.ontoware.rdf2go.model.node.Node>) Base.getAll_as(this.model, this.getResource(), TTT2, org.ontoware.rdf2go.model.node.Node.class);
	}
     /**
     * Get all values of property Ttt2     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static] 
     */
	public static ClosableIterator<FFF> getAllTtt2(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll(model, instanceResource, TTT2, FFF.class);
	}
	
    /**
     * Get all values of property Ttt2 as a ReactorResult of FFF 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult] 
     */
	public static ReactorResult<FFF> getAllTtt2_as(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, TTT2, FFF.class);
	}

    /**
     * Get all values of property Ttt2     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic] 
     */
	public ClosableIterator<FFF> getAllTtt2() {
		return Base.getAll(this.model, this.getResource(), TTT2, FFF.class);
	}

    /**
     * Get all values of property Ttt2 as a ReactorResult of FFF 
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult] 
     */
	public ReactorResult<FFF> getAllTtt2_as() {
		return Base.getAll_as(this.model, this.getResource(), TTT2, FFF.class);
	}
 
    /**
     * Adds a value to property Ttt2 as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static] 
     */
	public static void addTtt2( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.add(model, instanceResource, TTT2, value);
	}
	
    /**
     * Adds a value to property Ttt2 as an RDF2Go node 
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic] 
     */
	public void addTtt2( org.ontoware.rdf2go.model.node.Node value) {
		Base.add(this.model, this.getResource(), TTT2, value);
	}
    /**
     * Adds a value to property Ttt2 from an instance of FFF 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #add3static] 
     */
	public static void addTtt2(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, FFF value) {
		Base.add(model, instanceResource, TTT2, value);
	}
	
    /**
     * Adds a value to property Ttt2 from an instance of FFF 
	 *
	 * [Generated from RDFReactor template rule #add4dynamic] 
     */
	public void addTtt2(FFF value) {
		Base.add(this.model, this.getResource(), TTT2, value);
	}
  

    /**
     * Sets a value of property Ttt2 from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setTtt2( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, TTT2, value);
	}
	
    /**
     * Sets a value of property Ttt2 from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setTtt2( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), TTT2, value);
	}
    /**
     * Sets a value of property Ttt2 from an instance of FFF 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setTtt2(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, FFF value) {
		Base.set(model, instanceResource, TTT2, value);
	}
	
    /**
     * Sets a value of property Ttt2 from an instance of FFF 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setTtt2(FFF value) {
		Base.set(this.model, this.getResource(), TTT2, value);
	}
  


    /**
     * Removes a value of property Ttt2 as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeTtt2( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, TTT2, value);
	}
	
    /**
     * Removes a value of property Ttt2 as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeTtt2( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), TTT2, value);
	}
    /**
     * Removes a value of property Ttt2 given as an instance of FFF 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeTtt2(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, FFF value) {
		Base.remove(model, instanceResource, TTT2, value);
	}
	
    /**
     * Removes a value of property Ttt2 given as an instance of FFF 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeTtt2(FFF value) {
		Base.remove(this.model, this.getResource(), TTT2, value);
	}
  
    /**
     * Removes all values of property Ttt2     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllTtt2( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, TTT2);
	}
	
    /**
     * Removes all values of property Ttt2	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void addTtt2() {
		Base.removeAll(this.model, this.getResource(), TTT2);
	}
 }