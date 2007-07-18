/*
 * Created on Dez 03, 2005
 */
package org.ontoware.rdf2go.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * OWL vocabulary items as URIs
 * 
 * there are all vocabulary items (OWL Full), some have usage-restrictions
 * in other owl sublanguages (OWL DL and especially OWL Lite). They are not
 * all stated here.
 * 
 * The comments provided in this class are (almost) all derived form one of the
 * following urls
 * 
 * - http://www.w3.org/TR/owl-ref/
 * - http://www.w3.org/TR/owl-semantics/
 * 
 * @author wth (thiemann@blue-age.de), 2005
 */
public class OWL {

	/**
	 * The OWL Namespace
	 */
    public static final String OWL_NS="http://www.w3.org/2002/07/owl#";

    protected static final URI toURI( String local ) { 
    	return new URIImpl( OWL_NS + local, false ); 
    }

    //ontology header
    /**
     * An OWL ontology contains a sequence of annotations, axioms, and facts.
     */
    public static final URI Ontology = toURI("Ontology");
    /**
     * OWL has a built-in class owl:OntologyProperty. 
     * Instances of owl:OntologyProperty must have the class owl:Ontology as 
     * their domain and range. It is permitted to define other instances of 
     * owl:OntologyProperty.
     */
    public static final URI OntologyProperty = toURI("OntologyProperty");
    /**
     * OWL Full does not put any constraints on annotations in an ontology. 
     * OWL DL allows annotations on classes, properties, individuals and 
     * ontology headers, but only under the following conditions:
     *
	 * - The sets of object properties, datatype properties, annotation properties 
	 * and ontology properties must be mutually disjoint. Thus, in OWL DL 
	 * dc:creator cannot be at the same time a datatype property and an 
	 * annotation property.
	 * - Annotation properties must have an explicit typing triple of the form:
	 * -- AnnotationPropertyID rdf:type owl:AnnotationProperty .  
	 * - Annotation properties must not be used in property axioms. Thus, in 
	 * OWL DL one cannot define subproperties or domain/range constraints for 
	 * annotation properties.
	 * - The object of an annotation property must be either a data literal, a 
	 * URI reference, or an individual.
     */
    public static final URI AnnotationProperty = toURI("AnnotationProperty");
    /**
     * An owl:imports statement references another OWL ontology containing 
     * definitions, whose meaning is considered to be part of the meaning of 
     * the importing ontology. Each reference consists of a URI specifying from 
     * where the ontology is to be imported. Syntactically, owl:imports is a 
     * property with the class owl:Ontology as its domain and range.
     *
     * The owl:imports statements are transitive, that is, if ontology A 
     * imports B, and B imports C, then A imports both B and C.
     */
    public static final URI imports = toURI("imports");
    
    //classes
    /**
     * OWL distinguishes six types of class descriptions:
     *
     * 1. a class identifier (a URI reference)
     * 2. an exhaustive enumeration of individuals that together form the 
     * instances of a class
     * 3. a property restriction
     * 4. the intersection of two or more class descriptions
     * 5. the union of two or more class descriptions
     * 6. the complement of a class description
     */
    public static final URI Class = toURI("Class");
    
    /**
     * A property restriction is a special kind of class description. It 
     * describes an anonymous class, namely a class of all individuals that 
     * satisfy the restriction. OWL distinguishes two kinds of property 
     * restrictions: value constraints and cardinality constraints.
     */
    public static final URI Restriction = toURI("Restriction");
    /**
     * A restriction class should have exactly one triple linking the 
     * restriction to a particular property, using the  owl:onProperty property.
     */
    public static final URI onProperty = toURI("onProperty");
    
    //cardinality restrictions
    /**
     * The cardinality constraint owl:cardinality is a built-in OWL property 
     * that links a restriction class to a data value belonging to the range of 
     * the XML Schema datatype nonNegativeInteger. A restriction containing an 
     * owl:cardinality constraint describes a class of all individuals that 
     * have  exactly N semantically distinct values (individuals or data values) 
     * for the property concerned, where N is the value of the cardinality 
     * constraint. Syntactically, the cardinality constraint is represented as 
     * an RDF property element with the corresponding rdf:datatype attribute.
     */
    public static final URI cardinality = toURI("cardinality");
    /**
     * A restriction containing an owl:maxCardinality constraint describes a 
     * class of all individuals that have at most N semantically distinct values 
     * (individuals or data values) for the property concerned, where N is the 
     * value of the cardinality constraint.
     */
    public static final URI maxCardinality = toURI("maxCardinality");
    /**
     * A restriction containing an owl:minCardinality constraint describes a 
     * class of all individuals that have at least N semantically distinct 
     * values (individuals or data values) for the property concerned, where N 
     * is the value of the cardinality constraint.
     */
    public static final URI minCardinality = toURI("minCardinality");

    //value restricitons
    public static final URI someValuesFrom = toURI("someValuesFrom");
    public static final URI allValuesFrom = toURI("allValuesFrom");
    public static final URI hasValue = toURI("hasValue");

    //intersection + union
    public static final URI intersectionOf = toURI("intersectionOf");
    public static final URI unionOf = toURI("unionOf");
    public static final URI complementOf = toURI("complementOf");

    public static final URI equivalentClass = toURI("equivalentClass");
    public static final URI disjointWith = toURI("disjointWith");
    
    //properties
    public static final URI ObjectProperty = toURI("ObjectProperty");
    public static final URI DatatypeProperty = toURI("DatatypeProperty");
    public static final URI SymmetricProperty = toURI("SymmetricProperty");
    public static final URI TransitiveProperty = toURI("TransitiveProperty");
    public static final URI FunctionalProperty = toURI("FunctionalProperty");
    public static final URI InverseFunctionalProperty = toURI("InverseFunctionalProperty");

    public static final URI equivalentProperty = toURI("equivalentProperty");
    public static final URI inverseOf = toURI("inverseOf");

    
    //individuals
    /**
     * The class with identifier owl:Thing is the class of all individuals.
     */
    public static final URI Thing = toURI("Thing");
    /**
     * The class with identifier owl:Nothing is the empty class.
     */
    public static final URI Nothing = toURI("Nothing");
    
    public static final URI sameAs = toURI("sameAs");
    public static final URI differentFrom = toURI("differentFrom");
    public static final URI AllDifferent = toURI("AllDifferent");
    public static final URI distinctMembers = toURI("distinctMembers");

    
    //Datatype enumeration
    public static final URI DataRange = toURI("DataRange");
    public static final URI oneOf = toURI("oneOf");
    

    //Version information
    /**
     * An owl:versionInfo statement generally has as its object a string giving 
     * information about this version, for example RCS/CVS keywords. This 
     * statement does not contribute to the logical meaning of the ontology 
     * other than that given by the RDF(S) model theory.
     *
	 * Although this property is typically used to make statements about 
	 * ontologies, it may be applied to any OWL construct. For example, one 
	 * could attach a owl:versionInfo statement to an OWL class.
	 *
	 * NOTE: owl:versionInfo is an instance of owl:AnnotationProperty. 
     */
    public static final URI versionInfo = toURI("versionInfo");
    /**
     * An owl:priorVersion statement contains a reference to another ontology. 
     * This identifies the specified ontology as a prior version of the 
     * containing ontology. This has no meaning in the model-theoretic 
     * semantics other than that given by the RDF(S) model theory. However, it 
     * may be used by software to organize ontologies by versions.
     *
     * owl:priorVersion is a built-in OWL property with the class owl:Ontology 
     * as its domain and range.
     *
     * NOTE: owl:priorVersion is an instance of owl:OntologyProperty. 
     */
    public static final URI priorVersion = toURI("priorVersion");
    /**
     * An owl:backwardCompatibleWith statement contains a reference to another 
     * ontology. This identifies the specified ontology as a prior version of 
     * the containing ontology, and further indicates that it is backward 
     * compatible with it. In particular, this indicates that all identifiers 
     * from the previous version have the same intended interpretations in the 
     * new version. Thus, it is a hint to document authors that they can safely 
     * change their documents to commit to the new version (by simply updating 
     * namespace declarations and owl:imports statements to refer to the URL of 
     * the new version). If owl:backwardCompatibleWith is not declared for two 
     * versions, then compatibility should not be assumed.
     *
     * owl:backwardCompatibleWith has no meaning in the model theoretic 
     * semantics other than that given by the RDF(S) model theory.
     * 
     * owl:backwardCompatibleWith is a built-in OWL property with the class 
     * owl:Ontology as its domain and range.
     *
     * NOTE: owl:backwardCompatibleWith is an instance of owl:OntologyProperty. 
     */
    public static final URI backwardCompatibleWith = toURI("backwardCompatibleWith");
    /**
     * An owl:incompatibleWith statement contains a reference to another 
     * ontology. This indicates that the containing ontology is a later version 
     * of the referenced ontology, but is not backward compatible with it. 
     * Essentially, this is for use by ontology authors who want to be explicit 
     * that documents cannot upgrade to use the new version without checking 
     * whether changes are required.
     *
     * owl:incompatibleWith has no meaning in the model theoretic semantics 
     * other than that given by the RDF(S) model theory.
     *
     * owl:incompatibleWith is a built-in OWL property with the class 
     * owl:Ontology as its domain and range.
     *
     * NOTE: owl:backwardCompatibleWith is an instance of owl:OntologyProperty. 
     */
    public static final URI incompatibleWith = toURI("incompatibleWith");
    /**
     * Here, a specific identifier is said to be of type owl:DeprecatedClass, 
     * which is a subclass of rdfs:Class. By deprecating a term, it means that 
     * the term should not be used in new documents that commit to the ontology.
     * This allows an ontology to maintain backward-compatibility while phasing 
     * out an old vocabulary (thus, it only makes sense to use deprecation in 
     * combination with backward compatibility). 
     */
    public static final URI DeprecatedClass = toURI("DeprecatedClass");
    /**
     * Here, a specific identifier is said to be of type or 
     * owl:DeprecatedProperty, which is a subclass of rdf:Property.
     * @see OWL#DeprecatedClass
     */
    public static final URI DeprecatedProperty = toURI("DeprecatedProperty");

    
}