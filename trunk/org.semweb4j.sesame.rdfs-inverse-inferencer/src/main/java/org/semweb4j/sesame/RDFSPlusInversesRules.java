package org.semweb4j.sesame;

/**
 * Constants representing the RDF+RDFS entailment rules from the RDF Semantics
 * W3C Recommendation (10 February 2004). 
 * See http://www.w3.org/TR/2004/REC-rdf-mt-20040210/
 * 
 * Each entailment rule in the specification has either one or two premises. To allow
 * the inferencer to distinguish triggering of rules for both premises, entailment rules
 * that have two premises are represented by two separate constants, one for each premise.
 * 
 * An additional entailment rule, X1, is added to capture list membership property assertions. 
 */
class RDFSPlusInversesRules {

	/** rule rdf1:  (*,p,*)
	 * xxx aaa yyy --> aaa rdf:type rdf:Property
	 */
	public static final int Rdf1 = 0;

// domain & range	
	
	/** rule rdfs2_1: (s,*,*)
	 * xxx aaa yyy &&           (nt)
	 * aaa rdfs:domain zzz -->  (t1)
	 * xxx rdf:type zzz         (t2)
	 */
	public static final int Rdfs2_1 = 1;

	/** rule rdfs2_2: (*,rdfs:domain,o)
	 * aaa rdfs:domain zzz &&  (nt)
	 * xxx aaa yyy -->         (t1)
	 * xxx rdf:type zzz        (t2)
	 */
	public static final int Rdfs2_2 = 2;

	/** rule rdfs3_1: (*,*,o)
	 * xxx aaa uuu &&          (nt)
	 * aaa rdfs:range zzz -->  (t1)
	 * uuu rdf:type zzz        (t2)
	 */
	public static final int Rdfs3_1 = 3;

	/** rule rdfs3_2: (*,rdfs:range,o)
	 * aaa rdfs:range zzz &&  (nt)
	 * xxx aaa uuu -->        (t1)
	 * uuu rdf:type zzz       (t2)
	 */
	public static final int Rdfs3_2 = 4;
	
// ---	

// TODO these two rules can become one rule with two consequences, as both consequences match the same pattern	
	
	/** rule rdfs4a: (s,*,*)
	 * xxx aaa yyy --> xxx rdf:type rdfs:Resource
	 */
	public static final int Rdfs4a = 5;

	/** rule rdfs4b: (*,*o)
	 * xxx aaa uuu --> uuu rdf:type rdfs:Resource
	 */
	public static final int Rdfs4b = 6;

// subProperty	

	/** rule rdfs5_1: (s,rdfs:subPropertyOf,*)
	 * aaa rdfs:subPropertyOf bbb &&   (nt)
	 * bbb rdfs:subPropertyOf ccc -->  (t1)
	 * aaa rdfs:subPropertyOf ccc      (t2)
	 * 
	 * transitivity of rdfs:subPropertyOf
	 */
	public static final int Rdfs5_1 = 7;

	/** rule rdfs5_2: (*,rdfs:subPropertyOf,o)
	 * bbb rdfs:subPropertyOf ccc && (nt)
	 * aaa rdfs:subPropertyOf bbb -->  (t1)
	 * aaa rdfs:subPropertyOf ccc      (t2)
	 * 
	 * transitivity of rdfs:subPropertyOf
	 */
	public static final int Rdfs5_2 = 8;

	/** rule rdfs6: (s, rdf:type rdf:Property)
	 * xxx rdf:type rdf:Property --> xxx rdfs:subPropertyOf xxx
	 * 
	 * reflexivity of rdfs:subPropertyOf
	 */
	public static final int Rdfs6 = 9;

	/** rule rdfs7_1: (s,*,o)
	 * xxx aaa yyy &&                  (nt)
	 * aaa rdfs:subPropertyOf bbb -->  (t1)
	 * xxx bbb yyy                     (t2)
	 */
	public static final int Rdfs7_1 = 10;

	/** rule rdfs7_2: (*,rdfs:subPropertyOf,o)
	 * aaa rdfs:subPropertyOf bbb &&  (nt)
	 * xxx aaa yyy -->                (t1)
	 * xxx bbb yyy                    (t2)
	 */
	public static final int Rdfs7_2 = 11;

// subClassOf	
	
	/** rule rdfs8: (*,rdf:type rdfs:Class)
	 *  xxx rdf:type rdfs:Class --> xxx rdfs:subClassOf rdfs:Resource 
	 */
	public static final int Rdfs8 = 12;

	/** rule rdfs9_1: (*,rdfs:subClassOf,o)
	 * xxx rdfs:subClassOf yyy &&  (nt)
	 * aaa rdf:type xxx -->        (t1)
	 * aaa rdf:type yyy            (t2)
	 */
	public static final int Rdfs9_1 = 13;

	/** rule rdfs9_2: (s,rdf:type,*)
	 * aaa rdf:type xxx &&          (nt)
	 * xxx rdfs:subClassOf yyy -->  (t1)
	 * aaa rdf:type yyy             (t2)
	 */
	public static final int Rdfs9_2 = 14;

	/** rule rdfs10: (s,rdf:type,rdfs:Class)
	 * xxx rdf:type rdfs:Class --> xxx rdfs:subClassOf xxx
	 * 
	 * reflexivity of rdfs:subClassOf
	 */
	public static final int Rdfs10 = 15;

	/** rule rdfs11_1: (s,rdfs:subClassOf,*)
	 * xxx rdfs:subClassOf yyy &&  (nt)
	 * yyy rdfs:subClassOf zzz -->  (t1)
	 * xxx rdfs:subClassOf zzz      (t2)
	 * 
	 * transitivity of rdfs:subClassOf
	 */
	public static final int Rdfs11_1 = 16;

	/** rule rdfs11_2:   (*,rdfs:subClassOf,o)
	 * yyy rdfs:subClassOf zzz &&  (nt)
	 * xxx rdfs:subClassOf yyy -->  (t1)
	 * xxx rdfs:subClassOf zzz      (t2)
	 * 
	 * transitivity of rdfs:subClassOf
	 */
	public static final int Rdfs11_2 = 17;

// container 12,13,X1	
	
	/** rule rdfs12: ---
	 * xxx rdf:type rdfs:ContainerMembershipProperty -->
	 *     xxx rdfs:subPropertyOf rdfs:member
	 */
	public static final int Rdfs12 = 18;

	/** rule rdfs13:  ---
	 * xxx rdf:type rdfs:Datatype --> xxx rdfs:subClassOf rdfs:Literal
	 */
	public static final int Rdfs13 = 19;

	/** rule X1: ---
	 * xxx rdf:_* yyy -->
	 * rdf:_* rdf:type rdfs:ContainerMembershipProperty
	 *
	 * This is an extra rule for list membership properties (_1, _2, _3,
	 * ...). The RDF Semantics Recommendation does not specificy a production for this, instead
	 * these statements are considered axiomatic. Since it is an infinite set it can not, in practice,
	 * be asserted axiomatically. 
	 */
	public static final int RX1 = 20;

// inverses	
	
	/**
	 * rule N1a:  (*,nrl:inverseProperty,o)
	 * xxx nrl:inverseProperty yyy
	 * 
	 * ... -->
	 * xxx rdf:type rdf:Property
	 * yyy rdf:type rdf:Property
	 * 
	 * ... AND aaa xxx bbb 
	 * --> bbb yyy aaa
	 * 
	 * NEPOMUK NRL inverse, nrl = http://www.semanticdesktop.org/ontologies/nrl/#
	 */
	public static final int N1a = 21;

	/**
	 * rule N1b: (s,*,o)
	 * aaa xxx bbb 
	 * xxx nrl:inverseProperty yyy   
	 * -->
	 * bbb yyy aaa
	 * 
	 * NEPOMUK NRL inverse, nrl = http://www.semanticdesktop.org/ontologies/nrl/#
	 */
	public static final int N1b = 22;

	/**
	 * rule N2a: 
	 * 
	 * ppp nrl:inverseProperty qqq
	 * 
	 * ... AND (case 1) 
	 * rrr rdfs:subPropertyOf  ppp /\ rrr nrl:inverseProperty sss 
	 * -->
	 * sss rdfs:subPropertyOf  qqq
	 * 
	 * 
	 * ... AND (case 2)
	 * ppp rdfs:subPropertyOf  ttt (2)
	 * ttt nrl:inverseProperty uuu (1)
	 * -->
	 * qqq rdfs:subPropertyOf  uuu
	 */
	public static final int N2a = 23;
	
	/**
	 * rrr rdfs:subPropertyOf  ppp 
	 * rrr nrl:inverseProperty sss 
	 * ppp nrl:inverseProperty qqq 
	 * -->
	 * sss rdfs:subPropertyOf  qqq
	 */
	public static final int N2b = 24;
	
	/**
	 * ppp nrl:inverseProperty qqq 
	 * -->
	 * qqq nrl:inverseProperty ppp 
	 */
	public static final int N3 = 25;

/* TODO: missing rules

 	ppp nrl:inverseProperty qqq
    ppp rdfs:domain         ddd
    -->
    qqq rdfs:range          ddd
    
    TODO same rule with conditions swapped
---    
Note: This could lead to properties having domain xsd:string or rdfs:Literal.
Those would just never be instantiated.    
    
 	ppp nrl:inverseProperty qqq
    ppp rdfs:range          rrr
    -->
    qqq rdfs:domain         rrr
    
    TODO same rule with conditions swapped
---
    ppp nrl:inverseProperty qqq  
    -->
    ppp a rdf:Property
    qqq a rdf:Property

 */	
	
	
	public static final int RULECOUNT = 26;

	public static final String[] RULENAMES = {
			"   Rdf1", " Rdfs2_1", " Rdfs2_2", " Rdfs3_1", " Rdfs3_2", "  Rdfs4a",
			"  Rdfs4b", "Rdfs5_1", "Rdfs5_2", "  Rdfs6", " Rdfs7_1", " Rdfs7_2",
			"  Rdfs8", " Rdfs9_1", " Rdfs9_2", "  Rdfs10", " Rdfs11_1",
			" Rdfs11_2", "  Rdfs12", "  Rdfs13", "    RX1","    N1a","    N1b", "    N2a","    N2b","     N3" };

	private static final boolean _ = false;

	private static final boolean X = true;

	/**
	 * Table of triggers for entailment rules. Each column represents the triggers for 
	 * an entailment rule R, that is, it encodes which entailment rules produces statements
	 * that can be used as a premise in rule R.
	 * 
	 * Example: the conclusion of rule rdfs2_1 is a statement of the form: (xxx rdf:type yyy). 
	 * The premise of rule rdfs9_2 is (xxx rdf:type yyy). Hence, rule rdfs2_1 triggers rule
	 * rdfs9_2.  
	 */
	public static final boolean[][] TRIGGERS = {
			//	    1    2_2   3_2    4b   5_2   7_1    8    9_2   11_1   12   X1    N1b   N2b
			//	      2_1   3_1    4a   5_1    6    7_2   9_1   10   11_2   13    N1a   N2a   N3
			{
					_, X, _, X, _, X, _, _, _, X, X, _, _, _, X, _, _, _, _, _, _, _, X, _, _, _ },// 1 triggers...
			{
					_, X, _, X, _, _, _, _, _, X, X, _, X, _, X, X, _, _, X, X, _, _, X, _, _, _ },// 2_1
			{
					_, X, _, X, _, _, _, _, _, X, X, _, X, _, X, X, _, _, X, X, _, _, X, _, _, _ },// 2_2
			{
					_, X, _, X, _, _, _, _, _, X, X, _, X, _, X, X, _, _, X, X, _, _, X, _, _, _ },// 3_1
			{
					_, X, _, X, _, _, _, _, _, X, X, _, X, _, X, X, _, _, X, X, _, _, X, _, _, _ },// 3_2
			{
					_, X, _, X, _, _, _, _, _, _, X, _, _, _, X, _, _, _, _, _, _, _, X, _, _, _ },// 4a
			{
					_, X, _, X, _, _, _, _, _, _, X, _, _, _, X, _, _, _, _, _, _, _, X, _, _, _ },// 4b
			{
					_, _, _, _, _, _, _, X, X, _, X, X, _, _, _, _, _, _, _, _, _, _, X, _, X, _ },// 51
			{
					_, _, _, _, _, _, _, X, X, _, X, X, _, _, _, _, _, _, _, _, _, _, X, _, X, _ },// 52
			{
					_, X, _, X, _, _, _, _, _, _, X, _, _, _, _, _, _, _, _, _, _, _, X, _, X, _ },// 6
			{
					_, X, X, X, X, _, _, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X },// 7_1
			{
					_, X, X, X, X, _, _, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X },// 7_2
			{
					_, X, _, X, _, _, _, _, _, _, X, _, _, X, _, _, X, X, _, _, _, _, X, _, _, _},// 8
			{
					_, _, _, X, _, _, _, _, _, X, X, _, X, _, X, X, _, _, X, X, _, _, X, _, _, _ },// 9_1
			{
					_, _, _, X, _, _, _, _, _, X, X, _, X, _, X, X, _, _, X, X, _, _, X, _, _, _ },// 9_2
			{
					_, X, _, X, _, _, _, _, _, _, X, _, _, _, _, _, _, _, _, _, _, _, X, _, _, _ },// 10
			{
					_, _, _, _, _, _, _, _, _, _, X, _, _, X, _, _, X, X, _, _, _, _, X, _, _, _ },// 11_1
			{
					_, _, _, _, _, _, _, _, _, _, X, _, _, X, _, _, X, X, _, _, _, _, X, _, _, _ },// 11_2
			{
					_, X, _, X, _, _, X, X, X, _, X, X, _, _, _, _, _, _, _, _, _, _, X, _, _, _ },// 12
			{
					_, X, _, X, _, _, _, _, _, _, X, _, _, X, _, _, X, X, _, _, _, _, X, _, _, _},// 13
			{
					_, X, _, X, _, _, _, _, _, _, X, _, _, _, X, _, _, _, X, _, _, _, X, _, _, _},// X1
					// FIXME optimize
			{
					_, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, _, X, X, X, X, X},// N1a
			{
					_, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, _, X, X, X, X, X},// N1b
			// Trigger only rules listening for (*, rdfs:subClassOf, *)
			{
					_, X, _, X, _, X, X, X, X, _, X, X, _, _, _, _, _, _, _, _, _, _, X, _, X, _},// N2a
			{
					_, X, _, X, _, X, X, X, X, _, X, X, _, _, _, _, _, _, _, _, _, _, X, _, X, _},// N2b
			{
					_, X, _, X, _, X, X, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, X, _, X, _},// N3
			//	    1    2_2   3_2    4b   5_2   7_1    8    9_2   11_1   12   X1    N1b   N2b
			//	      2_1   3_1    4a   5_1    6    7_2   9_1   10   11_2   13    N1a   N2a   N2c

	};
}
