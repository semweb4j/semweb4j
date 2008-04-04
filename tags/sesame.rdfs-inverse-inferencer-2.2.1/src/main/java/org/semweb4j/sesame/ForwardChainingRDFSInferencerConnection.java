/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.semweb4j.sesame;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.Iterations;
import info.aduna.text.ASCIIUtil;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnectionListener;
import org.openrdf.sail.SailException;
import org.openrdf.sail.inferencer.InferencerConnection;
import org.openrdf.sail.inferencer.InferencerConnectionWrapper;

/**
 * Forward-chaining RDF Schema inferencer, using the rules from the <a
 * href="http://www.w3.org/TR/2004/REC-rdf-mt-20040210/">RDF Semantics
 * Recommendation (10 February 2004)</a>. This inferencer can be used to add
 * RDF Schema semantics to any Sail that returns {@link InferencerConnection}s
 * from their {@link Sail#getConnection()} method.
 */
class ForwardChainingRDFSPlusInverseInferencerConnection extends InferencerConnectionWrapper implements
		SailConnectionListener
{

	/*-----------*
	 * Constants *
	 *-----------*/

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/*-----------*
	 * Variables *
	 *-----------*/

	/**
	 * true if the base Sail reported removed statements.
	 */
	private boolean statementsRemoved;

	/**
	 * Contains the statements that have been reported by the base Sail as
	 */
	private Graph newStatements;

	private Graph newThisIteration;

	/**
	 * Flags indicating which rules should be evaluated.
	 */
	private boolean[] checkRule = new boolean[RDFSPlusInversesRules.RULECOUNT];

	/**
	 * Flags indicating which rules should be evaluated next iteration.
	 */
	private boolean[] checkRuleNextIter = new boolean[RDFSPlusInversesRules.RULECOUNT];

	private int totalInferred = 0;

	/**
	 * The number of inferred statements per rule.
	 */
	private int[] ruleCount = new int[RDFSPlusInversesRules.RULECOUNT];

	/*--------------*
	 * Constructors *
	 *--------------*/

	public ForwardChainingRDFSPlusInverseInferencerConnection(InferencerConnection con) {
		super(con);
		con.addConnectionListener(this);
	}

	/*---------*
	 * Methods *
	 *---------*/

	// Called by base sail
	public void statementAdded(Statement st) {
		if (statementsRemoved) {
			// No need to record, starting from scratch anyway
			return;
		}

		if (newStatements == null) {
			newStatements = new GraphImpl();
		}
		newStatements.add(st);
	}

	// Called by base sail
	public void statementRemoved(Statement st) {
		statementsRemoved = true;
		newStatements = null;
	}

	@Override
	public void flushUpdates()
		throws SailException
	{
		super.flushUpdates();

		if (statementsRemoved) {
			logger.debug("statements removed, starting inferencing from scratch");
			clearInferred();
			addAxiomStatements();

			newStatements = new GraphImpl();
			Iterations.addAll(getWrappedConnection().getStatements(null, null, null, true), newStatements);

			statementsRemoved = false;
		}

		doInferencing();
	}

	@Override
	public void rollback()
		throws SailException
	{
		statementsRemoved = false;
		newStatements = null;

		super.rollback();
	}
	
	
	public static final String NRL_NS = "http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#";
	
	public static final URI NRL_InverseProperty = new URIImpl(NRL_NS+"inverseProperty");

	/**
	 * Adds all basic set of axiom statements from which the complete set can be
	 * inferred to the underlying Sail.
	 */
	protected void addAxiomStatements()
		throws SailException
	{
		logger.debug("Inserting axiom statements");

		// RDF axiomatic triples (from RDF Semantics, section 3.1):

		addInferredStatement(RDF.TYPE, RDF.TYPE, RDF.PROPERTY);
		addInferredStatement(RDF.SUBJECT, RDF.TYPE, RDF.PROPERTY);
		addInferredStatement(RDF.PREDICATE, RDF.TYPE, RDF.PROPERTY);
		addInferredStatement(RDF.OBJECT, RDF.TYPE, RDF.PROPERTY);

		addInferredStatement(RDF.FIRST, RDF.TYPE, RDF.PROPERTY);
		addInferredStatement(RDF.REST, RDF.TYPE, RDF.PROPERTY);
		addInferredStatement(RDF.VALUE, RDF.TYPE, RDF.PROPERTY);

		addInferredStatement(RDF.NIL, RDF.TYPE, RDF.LIST);

		// RDFS axiomatic triples (from RDF Semantics, section 4.1):

		addInferredStatement(RDF.TYPE, RDFS.DOMAIN, RDFS.RESOURCE);
		addInferredStatement(RDFS.DOMAIN, RDFS.DOMAIN, RDF.PROPERTY);
		addInferredStatement(RDFS.RANGE, RDFS.DOMAIN, RDF.PROPERTY);
		addInferredStatement(RDFS.SUBPROPERTYOF, RDFS.DOMAIN, RDF.PROPERTY);
		addInferredStatement(RDFS.SUBCLASSOF, RDFS.DOMAIN, RDFS.CLASS);
		addInferredStatement(RDF.SUBJECT, RDFS.DOMAIN, RDF.STATEMENT);
		addInferredStatement(RDF.PREDICATE, RDFS.DOMAIN, RDF.STATEMENT);
		addInferredStatement(RDF.OBJECT, RDFS.DOMAIN, RDF.STATEMENT);
		addInferredStatement(RDFS.MEMBER, RDFS.DOMAIN, RDFS.RESOURCE);
		addInferredStatement(RDF.FIRST, RDFS.DOMAIN, RDF.LIST);
		addInferredStatement(RDF.REST, RDFS.DOMAIN, RDF.LIST);
		addInferredStatement(RDFS.SEEALSO, RDFS.DOMAIN, RDFS.RESOURCE);
		addInferredStatement(RDFS.ISDEFINEDBY, RDFS.DOMAIN, RDFS.RESOURCE);
		addInferredStatement(RDFS.COMMENT, RDFS.DOMAIN, RDFS.RESOURCE);
		addInferredStatement(RDFS.LABEL, RDFS.DOMAIN, RDFS.RESOURCE);
		addInferredStatement(RDF.VALUE, RDFS.DOMAIN, RDFS.RESOURCE);

		addInferredStatement(RDF.TYPE, RDFS.RANGE, RDFS.CLASS);
		addInferredStatement(RDFS.DOMAIN, RDFS.RANGE, RDFS.CLASS);
		addInferredStatement(RDFS.RANGE, RDFS.RANGE, RDFS.CLASS);
		addInferredStatement(RDFS.SUBPROPERTYOF, RDFS.RANGE, RDF.PROPERTY);
		addInferredStatement(RDFS.SUBCLASSOF, RDFS.RANGE, RDFS.CLASS);
		addInferredStatement(RDF.SUBJECT, RDFS.RANGE, RDFS.RESOURCE);
		addInferredStatement(RDF.PREDICATE, RDFS.RANGE, RDFS.RESOURCE);
		addInferredStatement(RDF.OBJECT, RDFS.RANGE, RDFS.RESOURCE);
		addInferredStatement(RDFS.MEMBER, RDFS.RANGE, RDFS.RESOURCE);
		addInferredStatement(RDF.FIRST, RDFS.RANGE, RDFS.RESOURCE);
		addInferredStatement(RDF.REST, RDFS.RANGE, RDF.LIST);
		addInferredStatement(RDFS.SEEALSO, RDFS.RANGE, RDFS.RESOURCE);
		addInferredStatement(RDFS.ISDEFINEDBY, RDFS.RANGE, RDFS.RESOURCE);
		addInferredStatement(RDFS.COMMENT, RDFS.RANGE, RDFS.LITERAL);
		addInferredStatement(RDFS.LABEL, RDFS.RANGE, RDFS.LITERAL);
		addInferredStatement(RDF.VALUE, RDFS.RANGE, RDFS.RESOURCE);

		addInferredStatement(RDF.ALT, RDFS.SUBCLASSOF, RDFS.CONTAINER);
		addInferredStatement(RDF.BAG, RDFS.SUBCLASSOF, RDFS.CONTAINER);
		addInferredStatement(RDF.SEQ, RDFS.SUBCLASSOF, RDFS.CONTAINER);
		addInferredStatement(RDFS.CONTAINERMEMBERSHIPPROPERTY, RDFS.SUBCLASSOF, RDF.PROPERTY);

		addInferredStatement(RDFS.ISDEFINEDBY, RDFS.SUBPROPERTYOF, RDFS.SEEALSO);

		addInferredStatement(RDF.XMLLITERAL, RDF.TYPE, RDFS.DATATYPE);
		addInferredStatement(RDF.XMLLITERAL, RDFS.SUBCLASSOF, RDFS.LITERAL);
		addInferredStatement(RDFS.DATATYPE, RDFS.SUBCLASSOF, RDFS.CLASS);
		
//		// NRL inverse
//		// FIXME made up a property for inverse-of rdf:type
//		addInferredStatement(RDF.TYPE, NRL_InverseProperty, new URIImpl(NRL_NS+"hasInstance"));
		

	}

	protected void doInferencing()
		throws SailException
	{
		if (!hasNewStatements()) {
			// There's nothing to do
			return;
		}

		// initialize some vars
		totalInferred = 0;
		int iteration = 0;
		int nofInferred = 1;

		// All rules need to be checked:
		for (int i = 0; i < RDFSPlusInversesRules.RULECOUNT; i++) {
			ruleCount[i] = 0;
			checkRuleNextIter[i] = true;
		}

		while (hasNewStatements()) {
			iteration++;
			logger.debug("starting iteration " + iteration);
			prepareIteration();

			nofInferred = 0;
			nofInferred += applyRule(RDFSPlusInversesRules.Rdf1);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs2_1);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs2_2);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs3_1);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs3_2);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs4a);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs4b);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs5_1);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs5_2);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs6);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs7_1);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs7_2);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs8);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs9_1);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs9_2);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs10);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs11_1);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs11_2);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs12);
			nofInferred += applyRule(RDFSPlusInversesRules.Rdfs13);
			nofInferred += applyRule(RDFSPlusInversesRules.RX1);
			nofInferred += applyRule(RDFSPlusInversesRules.N1a);
			nofInferred += applyRule(RDFSPlusInversesRules.N1b);
			nofInferred += applyRule(RDFSPlusInversesRules.N2a);
			nofInferred += applyRule(RDFSPlusInversesRules.N2b);
			nofInferred += applyRule(RDFSPlusInversesRules.N3);

			logger.debug("iteration " + iteration + " done; inferred " + nofInferred + " new statements");
			totalInferred += nofInferred;
		}

		// Print some statistics
		logger.debug("---RdfMTInferencer statistics:---");
		logger.debug("total statements inferred = " + totalInferred);
		for (int i = 0; i < RDFSPlusInversesRules.RULECOUNT; i++) {
			logger.debug("rule " + RDFSPlusInversesRules.RULENAMES[i] + ":\t#inferred=" + ruleCount[i]);
		}
		logger.debug("---end of statistics:---");
	}

	protected void prepareIteration() {
		for (int i = 0; i < RDFSPlusInversesRules.RULECOUNT; i++) {
			checkRule[i] = checkRuleNextIter[i];

			// reset for next iteration:
			checkRuleNextIter[i] = false;
		}

		newThisIteration = newStatements;
		newStatements = new GraphImpl();
	}

	protected boolean hasNewStatements() {
		return newStatements != null && !newStatements.isEmpty();
	}

	protected void updateTriggers(int ruleNo, int nofInferred) {
		if (nofInferred > 0) {
			ruleCount[ruleNo] += nofInferred;

			// Check which rules are triggered by this one.
			boolean[] triggers = RDFSPlusInversesRules.TRIGGERS[ruleNo];

			for (int i = 0; i < RDFSPlusInversesRules.RULECOUNT; i++) {
				if (triggers[i] == true) {
					checkRuleNextIter[i] = true;
				}
			}
		}
	}

	protected int applyRule(int rule)
		throws SailException
	{
		if (!checkRule[rule]) {
			return 0;
		}
		int nofInferred = 0;

		nofInferred = applyRuleInternal(rule);

		updateTriggers(rule, nofInferred);

		return nofInferred;
	}

	protected int applyRuleInternal(int rule)
		throws SailException
	{
		int result = 0;

		switch (rule) {
			case RDFSPlusInversesRules.Rdf1:
				result = applyRuleRdf1();
				break;
			case RDFSPlusInversesRules.Rdfs2_1:
				result = applyRuleRdfs2_1();
				break;
			case RDFSPlusInversesRules.Rdfs2_2:
				result = applyRuleRdfs2_2();
				break;
			case RDFSPlusInversesRules.Rdfs3_1:
				result = applyRuleRdfs3_1();
				break;
			case RDFSPlusInversesRules.Rdfs3_2:
				result = applyRuleRdfs3_2();
				break;
			case RDFSPlusInversesRules.Rdfs4a:
				result = applyRuleRdfs4a();
				break;
			case RDFSPlusInversesRules.Rdfs4b:
				result = applyRuleRdfs4b();
				break;
			case RDFSPlusInversesRules.Rdfs5_1:
				result = applyRuleRdfs5_1();
				break;
			case RDFSPlusInversesRules.Rdfs5_2:
				result = applyRuleRdfs5_2();
				break;
			case RDFSPlusInversesRules.Rdfs6:
				result = applyRuleRdfs6();
				break;
			case RDFSPlusInversesRules.Rdfs7_1:
				result = applyRuleRdfs7_1();
				break;
			case RDFSPlusInversesRules.Rdfs7_2:
				result = applyRuleRdfs7_2();
				break;
			case RDFSPlusInversesRules.Rdfs8:
				result = applyRuleRdfs8();
				break;
			case RDFSPlusInversesRules.Rdfs9_1:
				result = applyRuleRdfs9_1();
				break;
			case RDFSPlusInversesRules.Rdfs9_2:
				result = applyRuleRdfs9_2();
				break;
			case RDFSPlusInversesRules.Rdfs10:
				result = applyRuleRdfs10();
				break;
			case RDFSPlusInversesRules.Rdfs11_1:
				result = applyRuleRdfs11_1();
				break;
			case RDFSPlusInversesRules.Rdfs11_2:
				result = applyRuleRdfs11_2();
				break;
			case RDFSPlusInversesRules.Rdfs12:
				result = applyRuleRdfs12();
				break;
			case RDFSPlusInversesRules.Rdfs13:
				result = applyRuleRdfs13();
				break;
			case RDFSPlusInversesRules.RX1:
				result = applyRuleX1();
				break;
			case RDFSPlusInversesRules.N1a:
				result = applyRuleN1a();
				break;
			case RDFSPlusInversesRules.N1b:
				result = applyRuleN1b();
				break;
			case RDFSPlusInversesRules.N2a:
				result = applyRuleN2a();
				break;
			case RDFSPlusInversesRules.N2b:
				result = applyRuleN2b();
				break;
			case RDFSPlusInversesRules.N3:
				result = applyRuleN3();
				break;
			default:
				throw new AssertionError("Should be unreachable code");
		}
		// ThreadLog.trace("Rule " + RDFSRules.RULENAMES[rule] + " inferred " +
		// result + " new triples.");
		return result;
	}

	/*
	 * rdf1. xxx aaa yyy --> aaa rdf:type rdf:Property
	 */
	private int applyRuleRdf1()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> iter = newThisIteration.match(null, null, null);

		while (iter.hasNext()) {
			Statement st = iter.next();

			boolean added = addInferredStatement(st.getPredicate(), RDF.TYPE, RDF.PROPERTY);

			if (added) {
				nofInferred++;
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs2. 2_1. xxx aaa yyy && (nt) aaa rdfs:domain zzz --> (t1) xxx rdf:type
	 * zzz (t2)
	 */
	private int applyRuleRdfs2_1()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, null, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource xxx = nt.getSubject();
			URI aaa = nt.getPredicate();

			CloseableIteration<? extends Statement, SailException> t1Iter;
			t1Iter = getWrappedConnection().getStatements(aaa, RDFS.DOMAIN, null, true);

			while (t1Iter.hasNext()) {
				Statement t1 = t1Iter.next();

				Value zzz = t1.getObject();
				if (zzz instanceof Resource) {
					boolean added = addInferredStatement(xxx, RDF.TYPE, zzz);
					if (added) {
						nofInferred++;
					}
				}
			}
			t1Iter.close();
		}

		return nofInferred;
	}

	/*
	 * rdfs2. 2_2. aaa rdfs:domain zzz && (nt) xxx aaa yyy --> (t1) xxx rdf:type
	 * zzz (t2)
	 */
	private int applyRuleRdfs2_2()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, RDFS.DOMAIN, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource aaa = nt.getSubject();
			Value zzz = nt.getObject();

			if (aaa instanceof URI && zzz instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements(null, (URI)aaa, null, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Resource xxx = t1.getSubject();
					boolean added = addInferredStatement(xxx, RDF.TYPE, zzz);
					if (added) {
						nofInferred++;
					}
				}
				t1Iter.close();
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs3. 3_1. xxx aaa uuu && (nt) aaa rdfs:range zzz --> (t1) uuu rdf:type
	 * zzz (t2)
	 */
	private int applyRuleRdfs3_1()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, null, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			URI aaa = nt.getPredicate();
			Value uuu = nt.getObject();

			if (uuu instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements(aaa, RDFS.RANGE, null, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Value zzz = t1.getObject();
					if (zzz instanceof Resource) {
						boolean added = addInferredStatement((Resource)uuu, RDF.TYPE, zzz);
						if (added) {
							nofInferred++;
						}
					}
				}
				t1Iter.close();
			}
		}
		return nofInferred;
	}

	/*
	 * rdfs3. 3_2. aaa rdfs:range zzz && (nt) xxx aaa uuu --> (t1) uuu rdf:type
	 * zzz (t2)
	 */
	private int applyRuleRdfs3_2()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, RDFS.RANGE, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource aaa = nt.getSubject();
			Value zzz = nt.getObject();

			if (aaa instanceof URI && zzz instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements(null, (URI)aaa, null, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Value uuu = t1.getObject();
					if (uuu instanceof Resource) {
						boolean added = addInferredStatement((Resource)uuu, RDF.TYPE, zzz);
						if (added) {
							nofInferred++;
						}
					}
				}
				t1Iter.close();
			}
		}

		return nofInferred;

	}

	/*
	 * rdfs4a. xxx aaa yyy --> xxx rdf:type rdfs:Resource
	 */
	private int applyRuleRdfs4a()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> iter = newThisIteration.match(null, null, null);

		while (iter.hasNext()) {
			Statement st = iter.next();

			boolean added = addInferredStatement(st.getSubject(), RDF.TYPE, RDFS.RESOURCE);
			if (added) {
				nofInferred++;
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs4b. xxx aaa uuu --> uuu rdf:type rdfs:Resource
	 */
	private int applyRuleRdfs4b()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> iter = newThisIteration.match(null, null, null);

		while (iter.hasNext()) {
			Statement st = iter.next();

			Value uuu = st.getObject();
			if (uuu instanceof Resource) {
				boolean added = addInferredStatement((Resource)uuu, RDF.TYPE, RDFS.RESOURCE);
				if (added) {
					nofInferred++;
				}
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs5. 5_1. aaa rdfs:subPropertyOf bbb && (nt) bbb rdfs:subPropertyOf ccc
	 * --> (t1) aaa rdfs:subPropertyOf ccc (t2)
	 */
	private int applyRuleRdfs5_1()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, RDFS.SUBPROPERTYOF, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource aaa = nt.getSubject();
			Value bbb = nt.getObject();

			if (bbb instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements((Resource)bbb, RDFS.SUBPROPERTYOF, null, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Value ccc = t1.getObject();
					if (ccc instanceof Resource) {
						boolean added = addInferredStatement(aaa, RDFS.SUBPROPERTYOF, ccc);
						if (added) {
							nofInferred++;
						}
					}
				}
				t1Iter.close();

			}
		}

		return nofInferred;
	}

	/*
	 * rdfs5. 5_2. bbb rdfs:subPropertyOf ccc && (nt) aaa rdfs:subPropertyOf bbb
	 * --> (t1) aaa rdfs:subPropertyOf ccc (t2)
	 */
	private int applyRuleRdfs5_2()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, RDFS.SUBPROPERTYOF, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource bbb = nt.getSubject();
			Value ccc = nt.getObject();

			if (ccc instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements(null, RDFS.SUBPROPERTYOF, bbb, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Resource aaa = t1.getSubject();
					boolean added = addInferredStatement(aaa, RDFS.SUBPROPERTYOF, ccc);
					if (added) {
						nofInferred++;
					}
				}
				t1Iter.close();
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs6. xxx rdf:type rdf:Property --> xxx rdfs:subPropertyOf xxx
	 * reflexivity of rdfs:subPropertyOf
	 */
	private int applyRuleRdfs6()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> iter = newThisIteration.match(null, RDF.TYPE, RDF.PROPERTY);

		while (iter.hasNext()) {
			Statement st = iter.next();

			Resource xxx = st.getSubject();
			boolean added = addInferredStatement(xxx, RDFS.SUBPROPERTYOF, xxx);
			if (added) {
				nofInferred++;
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs7. 7_1. xxx aaa yyy && (nt) aaa rdfs:subPropertyOf bbb --> (t1) xxx
	 * bbb yyy (t2)
	 */
	private int applyRuleRdfs7_1()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, null, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource xxx = nt.getSubject();
			URI aaa = nt.getPredicate();
			Value yyy = nt.getObject();

			CloseableIteration<? extends Statement, SailException> t1Iter;
			t1Iter = getWrappedConnection().getStatements(aaa, RDFS.SUBPROPERTYOF, null, true);

			while (t1Iter.hasNext()) {
				Statement t1 = t1Iter.next();

				Value bbb = t1.getObject();
				if (bbb instanceof URI) {
					boolean added = addInferredStatement(xxx, (URI)bbb, yyy);
					if (added) {
						nofInferred++;
					}
				}
			}
			t1Iter.close();
		}

		return nofInferred;
	}

	/*
	 * rdfs7. 7_2. aaa rdfs:subPropertyOf bbb && (nt) xxx aaa yyy --> (t1) xxx
	 * bbb yyy (t2)
	 */
	private int applyRuleRdfs7_2()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, RDFS.SUBPROPERTYOF, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource aaa = nt.getSubject();
			Value bbb = nt.getObject();

			if (aaa instanceof URI && bbb instanceof URI) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements(null, (URI)aaa, null, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Resource xxx = t1.getSubject();
					Value yyy = t1.getObject();

					boolean added = addInferredStatement(xxx, (URI)bbb, yyy);
					if (added) {
						nofInferred++;
					}
				}
				t1Iter.close();
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs8. xxx rdf:type rdfs:Class --> xxx rdfs:subClassOf rdfs:Resource
	 */
	private int applyRuleRdfs8()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> iter = newThisIteration.match(null, RDF.TYPE, RDFS.CLASS);

		while (iter.hasNext()) {
			Statement st = iter.next();

			Resource xxx = st.getSubject();

			boolean added = addInferredStatement(xxx, RDFS.SUBCLASSOF, RDFS.RESOURCE);
			if (added) {
				nofInferred++;
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs9. 9_1. xxx rdfs:subClassOf yyy && (nt) aaa rdf:type xxx --> (t1) aaa
	 * rdf:type yyy (t2)
	 */
	private int applyRuleRdfs9_1()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, RDFS.SUBCLASSOF, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource xxx = nt.getSubject();
			Value yyy = nt.getObject();

			if (yyy instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements(null, RDF.TYPE, xxx, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Resource aaa = t1.getSubject();

					boolean added = addInferredStatement(aaa, RDF.TYPE, yyy);
					if (added) {
						nofInferred++;
					}
				}
				t1Iter.close();
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs9. 9_2. aaa rdf:type xxx && (nt) xxx rdfs:subClassOf yyy --> (t1) aaa
	 * rdf:type yyy (t2)
	 */
	private int applyRuleRdfs9_2()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, RDF.TYPE, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource aaa = nt.getSubject();
			Value xxx = nt.getObject();

			if (xxx instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements((Resource)xxx, RDFS.SUBCLASSOF, null, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Value yyy = t1.getObject();

					if (yyy instanceof Resource) {
						boolean added = addInferredStatement(aaa, RDF.TYPE, yyy);
						if (added) {
							nofInferred++;
						}
					}
				}
				t1Iter.close();
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs10. xxx rdf:type rdfs:Class --> xxx rdfs:subClassOf xxx reflexivity of
	 * rdfs:subClassOf
	 */
	private int applyRuleRdfs10()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> iter = newThisIteration.match(null, RDF.TYPE, RDFS.CLASS);

		while (iter.hasNext()) {
			Statement st = iter.next();

			Resource xxx = st.getSubject();

			boolean added = addInferredStatement(xxx, RDFS.SUBCLASSOF, xxx);
			if (added) {
				nofInferred++;
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs11. 11_1. xxx rdfs:subClassOf yyy && (nt) yyy rdfs:subClassOf zzz -->
	 * (t1) xxx rdfs:subClassOf zzz (t2)
	 */
	private int applyRuleRdfs11_1()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, RDFS.SUBCLASSOF, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource xxx = nt.getSubject();
			Value yyy = nt.getObject();

			if (yyy instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements((Resource)yyy, RDFS.SUBCLASSOF, null, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Value zzz = t1.getObject();

					if (zzz instanceof Resource) {
						boolean added = addInferredStatement(xxx, RDFS.SUBCLASSOF, zzz);
						if (added) {
							nofInferred++;
						}
					}
				}
				t1Iter.close();
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs11. 11_2. yyy rdfs:subClassOf zzz && (nt) xxx rdfs:subClassOf yyy -->
	 * (t1) xxx rdfs:subClassOf zzz (t2)
	 */
	private int applyRuleRdfs11_2()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> ntIter = newThisIteration.match(null, RDFS.SUBCLASSOF, null);

		while (ntIter.hasNext()) {
			Statement nt = ntIter.next();

			Resource yyy = nt.getSubject();
			Value zzz = nt.getObject();

			if (zzz instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> t1Iter;
				t1Iter = getWrappedConnection().getStatements(null, RDFS.SUBCLASSOF, yyy, true);

				while (t1Iter.hasNext()) {
					Statement t1 = t1Iter.next();

					Resource xxx = t1.getSubject();

					boolean added = addInferredStatement(xxx, RDFS.SUBCLASSOF, zzz);
					if (added) {
						nofInferred++;
					}
				}
				t1Iter.close();
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs12. xxx rdf:type rdfs:ContainerMembershipProperty --> xxx
	 * rdfs:subPropertyOf rdfs:member
	 */
	private int applyRuleRdfs12()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> iter = newThisIteration.match(null, RDF.TYPE, RDFS.CONTAINERMEMBERSHIPPROPERTY);

		while (iter.hasNext()) {
			Statement st = iter.next();

			Resource xxx = st.getSubject();

			boolean added = addInferredStatement(xxx, RDFS.SUBPROPERTYOF, RDFS.MEMBER);
			if (added) {
				nofInferred++;
			}
		}

		return nofInferred;
	}

	/*
	 * rdfs13. xxx rdf:type rdfs:Datatype --> xxx rdfs:subClassOf rdfs:Literal
	 */
	private int applyRuleRdfs13()
		throws SailException
	{
		int nofInferred = 0;

		Iterator<Statement> iter = newThisIteration.match(null, RDF.TYPE, RDFS.DATATYPE);

		while (iter.hasNext()) {
			Statement st = iter.next();

			Resource xxx = st.getSubject();

			boolean added = addInferredStatement(xxx, RDFS.SUBCLASSOF, RDFS.LITERAL);
			if (added) {
				nofInferred++;
			}
		}

		return nofInferred;
	}

	/*
	 * X1. xxx rdf:_* yyy --> rdf:_* rdf:type rdfs:ContainerMembershipProperty
	 * This is an extra rule for list membership properties (_1, _2, _3, ...).
	 * The RDF MT does not specificy a production for this.
	 */
	private int applyRuleX1()
		throws SailException
	{
		int nofInferred = 0;

		String prefix = RDF.NAMESPACE + "_";
		Iterator<Statement> iter = newThisIteration.match(null, null, null);

		while (iter.hasNext()) {
			Statement st = iter.next();

			URI predNode = st.getPredicate();
			String predURI = predNode.toString();

			if (predURI.startsWith(prefix) && isValidPredicateNumber(predURI.substring(prefix.length()))) {
				boolean added = addInferredStatement(predNode, RDF.TYPE, RDFS.CONTAINERMEMBERSHIPPROPERTY);
				if (added) {
					nofInferred++;
				}
			}
		}

		return nofInferred;
	}

	/**
	 * xxx nrl:inverseProperty yyy
	 * aaa xxx bbb 
	 * -->
	 * bbb yyy aaa
	 * @return
	 * @throws SailException
	 */
	private int applyRuleN1a()
	throws SailException
{
	int nofInferred = 0;
	
	Iterator<Statement> ntIter = newThisIteration.match(null, NRL_InverseProperty, null);

	while (ntIter.hasNext()) {
		Statement nt = ntIter.next();

		Resource xxx = nt.getSubject();
		Value yyy = nt.getObject();

		if (xxx instanceof URI && yyy instanceof URI) {
			// apply to triples using the property
			CloseableIteration<? extends Statement, SailException> t1Iter;
			t1Iter = getWrappedConnection().getStatements(null, (URI)xxx, null, true);

			while (t1Iter.hasNext()) {
				Statement t1 = t1Iter.next();

				Value aaa = t1.getSubject();
				Value bbb = t1.getObject();
				if (bbb instanceof Resource) {
					boolean added = addInferredStatement((Resource)bbb, (URI) yyy, aaa);
					if (added) {
						nofInferred++;
					}
				}
			}
			t1Iter.close();
		}
	}

	return nofInferred;
}

	/**
	 * aaa xxx bbb 
	 * xxx nrl:inverseProperty yyy
	 * -->
	 * bbb yyy aaa
	 * @return
	 * @throws SailException
	 */
	private int applyRuleN1b()
	throws SailException
{
	int nofInferred = 0;
	
	Iterator<Statement> ntIter = newThisIteration.match(null, null, null);

	while (ntIter.hasNext()) {
		Statement nt = ntIter.next();

		Resource xxx = nt.getPredicate();

		CloseableIteration<? extends Statement, SailException> t1Iter;
		t1Iter = getWrappedConnection().getStatements(xxx,NRL_InverseProperty, null, true);

		while (t1Iter.hasNext()) {
			Statement t1 = t1Iter.next();

			Value yyy = t1.getObject();
			if (yyy instanceof URI) {
				Resource aaa = 	nt.getSubject();
				Value bbb = nt.getObject();
				boolean added = addInferredStatement((Resource)bbb, (URI) yyy, aaa);
				if (added) {
					nofInferred++;
				}
			}
		}
		t1Iter.close();
	}

	return nofInferred;
}

	/**
	 * New: ppp nrl:inverseProperty qqq
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
	 * 
	 * @return
	 * @throws SailException
	 */
	private int applyRuleN2a()
	throws SailException
	{
		int nofInferred = 0;
		Iterator<Statement> it1 = newThisIteration.match(null, NRL_InverseProperty, null);
		while (it1.hasNext()) {
			Statement stmt1 = it1.next();
			Resource ppp = stmt1.getSubject();
			Value qqq = stmt1.getObject();
			if(qqq instanceof Resource) {
				// case 1
				CloseableIteration<? extends Statement, SailException> it2;
				it2 = getWrappedConnection().getStatements(null,RDFS.SUBPROPERTYOF, ppp, true);
				while (it2.hasNext()) {
					Statement stmt2 = it2.next();
					Resource rrr = stmt2.getSubject();
					CloseableIteration<? extends Statement, SailException> it3;
					it3 = getWrappedConnection().getStatements(rrr,NRL_InverseProperty, null, true);
					while (it3.hasNext()) {
						Statement stmt3 = it3.next();
						Value sss = stmt3.getObject();
						if( sss instanceof Resource) {
							boolean added = addInferredStatement((Resource)sss, RDFS.SUBPROPERTYOF, qqq);
							if (added) {
								nofInferred++;
							}
						}
					}
					it3.close();
				}
				it2.close();
				// case 2
				it2 = getWrappedConnection().getStatements(ppp,RDFS.SUBPROPERTYOF, null, true);
				while (it2.hasNext()) {
					Statement stmt2 = it2.next();
					Value ttt = stmt2.getObject();
					if( ttt instanceof Resource) {
						CloseableIteration<? extends Statement, SailException> it3;
						it3 = getWrappedConnection().getStatements( (Resource) ttt,NRL_InverseProperty, null, true);
						while (it3.hasNext()) {
							Statement stmt3 = it3.next();
							Value uuu = stmt3.getObject();
							if( uuu instanceof Resource) {
								boolean added = addInferredStatement((Resource)qqq, RDFS.SUBPROPERTYOF, uuu);
								if (added) {
									nofInferred++;
								}
							}
						}
						it3.close();
					}
				}
				it2.close();
			}
			
		}
		return nofInferred;
	}	
	
	/**
	 * rrr rdfs:subPropertyOf  ppp 
	 * rrr nrl:inverseProperty sss 
	 * ppp nrl:inverseProperty qqq 
	 * -->
	 * sss rdfs:subPropertyOf  qqq
	 * @return
	 * @throws SailException
	 */
	private int applyRuleN2b()
	throws SailException
	{
		int nofInferred = 0;
		Iterator<Statement> it1 = newThisIteration.match(null, RDFS.SUBPROPERTYOF, null);
		while (it1.hasNext()) {
			Statement stmt1 = it1.next();
			Resource rrr = stmt1.getSubject();
			Value ppp = stmt1.getObject();
			if(ppp instanceof Resource) {
				CloseableIteration<? extends Statement, SailException> it2;
				it2 = getWrappedConnection().getStatements(rrr,NRL_InverseProperty, null, true);
				while (it2.hasNext()) {
					Statement stmt2 = it2.next();
					Value sss = stmt2.getObject();
					if(sss instanceof Resource) {
						CloseableIteration<? extends Statement, SailException> it3;
						it3 = getWrappedConnection().getStatements( (Resource) ppp,NRL_InverseProperty, null, true);
						while (it3.hasNext()) {
							Statement stmt3 = it3.next();
							Value qqq = stmt3.getObject();
							if( qqq instanceof Resource) {
								boolean added = addInferredStatement((Resource)sss, RDFS.SUBPROPERTYOF, qqq);
								if (added) {
									nofInferred++;
								}
							}
						}
						it3.close();
					}
				}
				it2.close();
			}
		}
		return nofInferred;
	}	
	
	/**
	 * ppp nrl:inverseProperty qqq 
	 * -->
	 * qqq nrl:inverseProperty ppp 
	 * ppp a rdf:Property
	 * qqq a rdf:Property
	 * 
	 * @return
	 * @throws SailException
	 */
	private int applyRuleN3()
	throws SailException
	{
		int nofInferred = 0;
		
		Iterator<Statement> it1 = newThisIteration.match(null, NRL_InverseProperty, null);
		while (it1.hasNext()) {
			Statement stmt1 = it1.next();
			Resource ppp = stmt1.getSubject();
			if(ppp instanceof URI) {
				// infer: ppp is a property
				boolean addedPPP = addInferredStatement((URI)ppp, RDF.TYPE, RDF.PROPERTY);
				if (addedPPP) {
					nofInferred++;
				}
			}
			Value qqq = stmt1.getObject();
			if(qqq instanceof Resource) {
				if(qqq instanceof URI) {
					// infer: qqq is a property
					boolean addedQQQ = addInferredStatement((URI)qqq, RDF.TYPE, RDF.PROPERTY);
					if (addedQQQ) {
						nofInferred++;
					}
				}
				if(! qqq.equals(ppp)) {
					// infer: qqq inverse ppp
					boolean added = addInferredStatement((Resource)qqq, NRL_InverseProperty, ppp);
					if (added) {
						nofInferred++;
					}
				}
			}
		}
		return nofInferred;
	}	

	/**
	 * Util method for {@link #applyRuleX1}.
	 */
	private boolean isValidPredicateNumber(String str) {
		int strLength = str.length();

		if (strLength == 0) {
			return false;
		}

		for (int i = 0; i < strLength; i++) {
			if (!ASCIIUtil.isNumber(str.charAt(i))) {
				return false;
			}
		}

		// No leading zeros
		if (str.charAt(0) == '0') {
			return false;
		}

		return true;
	}
}
