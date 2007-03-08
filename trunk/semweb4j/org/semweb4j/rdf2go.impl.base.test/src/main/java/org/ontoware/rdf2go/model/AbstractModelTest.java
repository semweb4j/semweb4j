/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.ReasoningNotSupportedException;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.testdata.TestData;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

public abstract class AbstractModelTest extends TestCase {

	private static final Log log = LogFactory.getLog(AbstractModelTest.class);

	public static URI a = new URIImpl("test://test/a", false);

	public static URI b = new URIImpl("test://test/b", false);

	public static URI c = new URIImpl("test://test/c", false);

	public static URI subject = new URIImpl("test://test/a", false);

	public static URI predicate = new URIImpl("test://test/b", false);

	public static URI object = new URIImpl("test://test/c", false);

	public static URI dt = new URIImpl("test://somedata/dt", false);

	/** @return a Model to be used in the test. It must be fresh, e.g. unused */
	public abstract ModelFactory getModelFactory();

	public void setUp() throws ModelRuntimeException {
	}

	public void testIsOpen() {
		Model model = getModelFactory().createModel();
		model.open();
		assertNotNull(model);
		assertTrue(model.isOpen());
	}

	/*
	 * Class under test for void addStatement(URI, URI, URI)
	 */
	public void testAddStatementSimple() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, object);
		ClosableIterator<? extends Statement> sit = model.findStatements(
				subject, predicate, object);
		assertNotNull(sit);
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals(subject, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(object, s.getObject());
		}
		sit.close();
	}

	/*
	 * Class under test for void addStatement(URI, URI, URI)
	 */
	public void testAddTwoStatements() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model.addStatement(a, b, "Jim");
		model.addStatement(a, c, "Jon");
		Iterator<Statement> sit = model.iterator();
		assertTrue(sit.hasNext());
		int count = 0;
		while (sit.hasNext()) {
			count++;
			@SuppressWarnings("unused")
			Statement s;
			s = sit.next();
		}
		assertEquals(2, count);
		// this.model.dump(null);
	}

	/*
	 * Class under test for void removeStatement(URI, URI, URI) and void
	 * addStatement(URI, URI, URI)
	 */
	public void testRemoveStatementSimple() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, object);
		Iterator iter = model.findStatements(subject, predicate, object);
		assertTrue(iter.hasNext());
		while (iter.hasNext())
			iter.next();

		model.removeStatement(subject, predicate, object);
		ClosableIterator it = model.findStatements(subject, predicate, object);
		assertFalse(it.hasNext());
		it.close();
	}

	/*
	 * Class under test for void addStatement(URI, URI, String) with a Literal
	 * without Language Tag or Data Type
	 */
	public void testAddStatementURIURIString() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, "Test");
		ClosableIterator<? extends Statement> sit = model.findStatements(
				subject, predicate, model.createPlainLiteral("Test"));
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals(subject, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals((PlainLiteral) s.getObject(), "Test");
		}
		sit.close();
	}

	/*
	 * Class under test for void removeStatement(URI, URI, String)
	 */
	public void testRemoveStatementURIURIString() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, "Test");

		Iterator iter = model.findStatements(subject, predicate, model
				.createPlainLiteral("Test"));
		assertTrue(iter.hasNext());
		while (iter.hasNext())
			iter.next();

		model.removeStatement(subject, predicate, "Test");
		ClosableIterator it = model.findStatements(subject, predicate, model
				.createPlainLiteral("Test"));
		assertFalse(it.hasNext());
		it.close();
	}

	/*
	 * Class under test for void addStatement(URI, URI, String, String)
	 * 
	 * PENDING wait for yars to implement languageTags correctly
	 */
	public void testAddStatementLanguageTaggedLiteral() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, "Test", "DE");
		Iterator<? extends Statement> sit = model.findStatements(subject,
				predicate, Variable.ANY);
		assertTrue(sit.hasNext());
		Statement s = sit.next();
		assertEquals(subject, s.getSubject());
		assertEquals(predicate, s.getPredicate());

		assertTrue("Object is " + s.getObject().getClass()
				+ ", expected was LanguageTagLiteral",
				s.getObject() instanceof LanguageTagLiteral);
		LanguageTagLiteral rdflit = (LanguageTagLiteral) s.getObject();
		assertEquals("Test", rdflit.getValue());
		assertTrue("DE".equalsIgnoreCase(rdflit.getLanguageTag()));
		// should be only one iteration
		assertFalse(sit.hasNext());
	}

	/*
	 * Class under test for void removeStatement(URI, URI, String, String)
	 */
	public void testRemoveStatementLanguageTaggedLiteral() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, "Test", "DE");
		assertTrue(model.contains(subject, predicate, model
				.createLanguageTagLiteral("Test", "DE")));
		ClosableIterator iter = model.iterator();
		assertTrue(iter.hasNext());
		iter.close();
		model.removeStatement(subject, predicate, "Test", "DE");
		assertFalse(model.contains(subject, predicate, model
				.createLanguageTagLiteral("Test", "DE")));
	}
	
	/*
	 * Class under test for void addStatement(URI, URI, String, URI)
	 * 
	 * PENDING due to an open fix in yars implementation
	 */
	public void testAddStatementDatatypedLiteral() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, "Test", dt);
		assertEquals(1, model.size());
		Iterator<Statement> sit = model.iterator();
		assertNotNull(sit);
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals(subject, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			Object literalObject = s.getObject();
			assertNotNull(literalObject);
			assertTrue("literalObject is if type: " + literalObject.getClass(),
					literalObject instanceof DatatypeLiteral);
			DatatypeLiteral rdflit = (DatatypeLiteral) s.getObject();
			System.err.println(rdflit);
			assertEquals("Test", rdflit.getValue());
			assertEquals(dt, rdflit.getDatatype());
		}

	}

	/*
	 * Class under test for void removeStatement(URI, URI, String, URI)
	 */
	public void testRemoveStatementDatatypedLiteral() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, "Test", dt);
		assertTrue(model.contains(subject, predicate, model
				.createDatatypeLiteral("Test", dt)));

		Iterator iter = model.iterator();
		assertTrue(iter.hasNext());
		while (iter.hasNext())
			iter.next();
		model.removeStatement(subject, predicate, "Test", dt);
		assertFalse(model.contains(subject, predicate, model
				.createDatatypeLiteral("Test", dt)));
	}

	/*
	 * Class under test for void addStatement(URI, URI, Object) and
	 * removeStatement(URI, URI, Object)
	 */
	public void testAddRemoveStatementBlankNode() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();

		BlankNode blankNode = model.createBlankNode();

		model.addStatement(subject, predicate, blankNode);
		Iterator<? extends Statement> sit = model.findStatements(subject,
				predicate, blankNode);
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals(subject, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(blankNode, s.getObject());
		}

		model.removeStatement(subject, predicate, blankNode);
		assertFalse(model.contains(subject, predicate, blankNode));
	}

	/*
	 * Class under test for void addStatement(Object, URI, URI) and
	 * removeStatement(Object, URI, URI)
	 */
	public void testAddRemoveStatementBlankNode2() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();

		BlankNode blankNode = model.createBlankNode();

		model.addStatement(blankNode, predicate, object);
		Iterator<? extends Statement> sit = model.findStatements(blankNode,
				predicate, object);
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertNotNull(s);
			assertTrue(s.getSubject() instanceof BlankNode);
			BlankNode back = (BlankNode) s.getSubject();

			assertTrue(blankNode.equals(back));
			assertEquals(blankNode, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(object, s.getObject());
		}

		model.removeStatement(blankNode, predicate, object);
		assertFalse(model.contains(blankNode, predicate, object));
	}

	/*
	 * Class under test for void addStatement(Object, URI, String) and
	 * removeStatement(Object, URI, String)
	 */
	public void testAddRemoveStatementObjectURIString() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();

		BlankNode blankNode = model.createBlankNode();

		model.addStatement(blankNode, predicate, "Test");
		Iterator<? extends Statement> sit = model
				.findStatements(new TriplePatternImpl((BlankNode) blankNode,
						predicate, "Test"));
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals("blank node equality", blankNode, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(s.getObject(), "Test");
		}

		model.removeStatement((BlankNode) blankNode, predicate, "Test");
		ClosableIterator<? extends Statement> it = model
				.findStatements(new TriplePatternImpl((BlankNode) blankNode,
						predicate, "Test"));
		assertFalse(it.hasNext());
		it.close();
	}

	/*
	 * Class under test for void addStatement(Object, URI, Object) and
	 * removeStatement(Object, URI, Object)
	 */
	public void testAddRemoveStatementURIObjectURIObject() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();

		BlankNode blankNodeSubject = model.createBlankNode();
		BlankNode blankNodeObject = model.createBlankNode();

		model.addStatement(blankNodeSubject, predicate, blankNodeObject);
		Iterator<? extends Statement> sit = model.findStatements(
				blankNodeSubject, predicate, blankNodeObject);
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals(blankNodeSubject, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(blankNodeObject, s.getObject());
		}

		model.removeStatement(blankNodeSubject, predicate, blankNodeObject);
		assertFalse(model
				.contains(blankNodeSubject, predicate, blankNodeObject));
	}

	/*
	 * Test method for 'org.ontoware.rdf2go.Model.getNewBlankNode()'
	 */
	public void testGetNewBlankNode() {
		Model model = getModelFactory().createModel();
		model.open();
		int size = 10;
		List<BlankNode> bNodes = new ArrayList<BlankNode>(size);
		for (int i = 0; i < size; i++) {
			bNodes.add(model.createBlankNode());
		}

		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				assertFalse(bNodes.get(i).equals(bNodes.get(j)));
			}
		}
	}

	/*
	 * Test method for
	 * 'org.ontoware.rdf2go.Model.getStatementWithLiteralAndNoLanguageTag(URI,
	 * URI, String)'
	 */
	public void testGetStatementWithLiteralAndNoLanguageTagURIURIString()
			throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, "Test", "DE");
		model.addStatement(subject, predicate, "Test");

		Iterator<? extends Statement> sit = model.findStatements(Variable.ANY,
				predicate, Variable.ANY);
		assertTrue(sit.hasNext());
		Statement s = sit.next();
		assertEquals(s.getSubject(), subject);
		assertEquals(s.getPredicate(), predicate);
		assertEquals(s.getObject().asLiteral().getValue(), "Test");

		assertTrue(sit.hasNext());
		sit.next();
		assertFalse(sit.hasNext());
	}

	/*
	 * Test method for
	 * 'org.ontoware.rdf2go.Model.getStatementWithLiteralAndNoLanguageTag(Object,
	 * URI, String)'
	 */
	public void testGetStatementWithLiteralAndNoLanguageTagObjectURIString()
			throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		BlankNode blankNodeSubject1 = model.createBlankNode();
		BlankNode blankNodeSubject2 = model.createBlankNode();
		model.addStatement(blankNodeSubject1, predicate, "Test");
		model.addStatement(blankNodeSubject2, predicate, "Test", "DE");

		Iterator<? extends Statement> sit = model.findStatements(Variable.ANY,
				predicate, Variable.ANY);
		assertTrue(sit.hasNext());
		Statement s = sit.next();
		// assertEquals(s.getSubject(), blankNodeSubject1);
		assertEquals(s.getPredicate(), predicate);
		assertEquals(s.getObject().asLiteral().getValue(), "Test");
		sit.next();
		assertFalse(sit.hasNext());

	}

	/*
	 * Test method for 'org.ontoware.rdf2go.Model.query(String)'
	 */
	public void testQuery() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		String query = "PREFIX \t:\t<test://test/>\n"
				+ "CONSTRUCT { ?s ?p \"Test2\" } WHERE { ?s ?p \"Test2\" }";
		// System.out.println(query);
		BlankNode bNode = model.createBlankNode();
		model.addStatement(subject, predicate, "Test1");
		model.addStatement(subject, predicate, "Test2");
		model.addStatement(bNode, predicate, "Test2");
		ClosableIterator<? extends Statement> iter = model.sparqlConstruct(
				query).iterator();

		assertTrue(iter.hasNext());
		Statement s = iter.next();
		assertEquals(predicate, s.getPredicate());
		assertEquals(s.getObject(), "Test2");
		assertTrue(iter.hasNext());
		s = iter.next();
		assertEquals(predicate, s.getPredicate());
		assertEquals(s.getObject(), "Test2");
		assertFalse(iter.hasNext());
	}

	public void testSelectQuery() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();
		String query = "PREFIX \t:\t<test://test/>\n" + "SELECT  ?s ?p \n"
				+ "WHERE { ?s ?p \"Test2\" }";
		// System.out.println(query);
		BlankNode bNode = model.createBlankNode();
		model.addStatement(subject, predicate, "Test1");
		model.addStatement(subject, predicate, "Test2");
		model.addStatement(bNode, predicate, "Test2");
		QueryResultTable result = model.sparqlSelect(query);
		Iterator<QueryRow> it = result.iterator();
		assertTrue(it.hasNext());
		QueryRow row = it.next();
		assertTrue(result.getVariables().size() == 2);
		assertEquals(predicate, row.getValue("p"));
		assertTrue(subject.equals(row.getValue("s"))
				|| bNode.equals(row.getValue("s")));
		row = it.next();
		assertEquals(predicate, row.getValue("p"));
		assertTrue(subject.equals(row.getValue("s"))
				|| bNode.equals(row.getValue("s")));
	}

	public void testOpenClose() {
		Model m = RDF2Go.getModelFactory().createModel();
		assertFalse(m.isOpen());
		m.open();
		assertTrue(m.isOpen());
	}

	/**
	 * Check for valid and invalid URIs
	 * 
	 */
	public void testURIs() {
		Model model = getModelFactory().createModel();
		model.open();
		try {
			model.createURI("file:///c/my%20documents/blah.pdf");
		} catch (ModelRuntimeException e) {
			fail();
		}
	}

	public void testRdfsReasoning() throws ReasoningNotSupportedException,
			ModelRuntimeException {
		Model model = getModelFactory().createModel(Reasoning.rdfs);
		model.open();

		log.debug("Using internal impl: "
				+ model.getUnderlyingModelImplementation().getClass());

		// a rdf:type classA
		// classA rdfs:subClassOf classB
		// -->
		// a rdf:type classB

		URI a = model.createURI("urn:test:a");
		URI classA = model.createURI("urn:test:classA");
		URI classB = model.createURI("urn:test:classB");

		model.addStatement(a, RDF.type, classA);
		model.addStatement(classA, RDFS.subClassOf, classB);

		boolean inferencedStatement = model.contains(Variable.ANY, RDF.type,
				classB);
		if (!inferencedStatement) {
			model.dump();
		}
		assertTrue(inferencedStatement);
	}

	public void testSimpleQuery1() throws Exception {
		Model m = getModelFactory().createModel();
		m.open();
		URI a = new URIImpl("test://a");
		URI b = new URIImpl("test://b");
		URI c = new URIImpl("test://c");
		m.addStatement(a,b,c);
		Iterator<? extends Statement> it = m.findStatements(a, b, c);
		assertTrue( it.hasNext() );
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}

	public void testSimpleQuery() throws Exception {
		Model m = getModelFactory().createModel();
		m.open();
		URI a = new URIImpl("test://a");
		URI b = new URIImpl("test://b");
		URI c = new URIImpl("test://c");
		m.addStatement(a,b,c);
		assertEquals( 1, m.size() );
		
		Iterator<? extends Statement> it = m.findStatements(Variable.ANY, b, c);
		assertTrue( it.hasNext() );
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}
	
	public void testDiff_empty() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();
		Diff diff = a.getDiff(b.iterator());
		assertFalse( diff.getAdded().iterator().hasNext() );
		assertFalse( diff.getRemoved().iterator().hasNext() );
	}

	public void testDiff_different() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();

		a.addStatement(new URIImpl("urn:test:a",false),new URIImpl("urn:test:b",false),new URIImpl("urn:test:c",false));
		b.addStatement(new URIImpl("urn:test:x",false),new URIImpl("urn:test:y",false),new URIImpl("urn:test:z",false));
		Diff diff = a.getDiff(b.iterator());
		assertTrue( diff.getAdded().iterator().hasNext() );
		assertTrue( diff.getRemoved().iterator().hasNext() );
	}

	public void testDiff_same() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();

		a.addStatement(new URIImpl("urn:test:a",false),new URIImpl("urn:test:b",false),new URIImpl("urn:test:c",false));

		b.addStatement(new URIImpl("urn:test:a",false),new URIImpl("urn:test:b",false),new URIImpl("urn:test:c",false));

		Statement aStmt = a.iterator().next();
		Statement bStmt = b.iterator().next();
		
		assertTrue("a statement should be equal to itself",	aStmt.equals( aStmt ) );
		assertEquals( aStmt, aStmt );
		assertTrue(	aStmt.equals( bStmt ) );
		assertEquals( aStmt, bStmt );
		
		assertEquals( aStmt.hashCode(), aStmt.hashCode() );
		assertEquals( aStmt.getSubject().hashCode(), bStmt.getSubject().hashCode() );
		assertEquals( aStmt.getPredicate().hashCode(), bStmt.getPredicate().hashCode() );
		assertEquals( aStmt.getObject().hashCode(), bStmt.getObject().hashCode() );
		assertEquals( aStmt.hashCode(), bStmt.hashCode() );
		
		Diff diff = a.getDiff(b.iterator());
		assertFalse( diff.getAdded().iterator().hasNext() );
		assertFalse( diff.getRemoved().iterator().hasNext() );
	}
	
	public void testComparable() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();

		a.addStatement(new URIImpl("urn:test:a",false),new URIImpl("urn:test:b",false),new URIImpl("urn:test:c",false));
		Statement aStmt = a.iterator().next();
		
		assertEquals( 0, aStmt.compareTo(aStmt));

		b.addStatement(new URIImpl("urn:test:a",false),new URIImpl("urn:test:b",false),new URIImpl("urn:test:c",false));
		Statement bStmt = b.iterator().next();
		
		assertEquals( 0, aStmt.compareTo(bStmt));
		assertEquals( 0, bStmt.compareTo(aStmt));
	}
	public void testComparableWithDifferent() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();

		a.addStatement(new URIImpl("urn:test:a",false),new URIImpl("urn:test:b",false),new URIImpl("urn:test:c",false));
		Statement aStmt = a.iterator().next();
		
		assertEquals( 0, aStmt.compareTo(aStmt));

		b.addStatement(new URIImpl("urn:test:x",false),new URIImpl("urn:test:y",false),new URIImpl("urn:test:z",false));
		Statement bStmt = b.iterator().next();
		
		// one should be positive, one negative, both not 0
		assertTrue( aStmt.compareTo(bStmt) * bStmt.compareTo(aStmt) < 0);
	}
	
	
	public void testSparqlSelectWithURIS() throws Exception {
		Model m = getModelFactory().createModel(Reasoning.rdfs);
		m.open();

		m.removeAll();

		URI hasContent = new URIImpl("prop://hasContent");
		URI hasTag = new URIImpl("prop://hasTag");
		URI tagSemweb = new URIImpl("tag://semweb");
		URI tagPaper = new URIImpl("tag://paper");
		URI fileA = new URIImpl("file://a");
		URI fileB = new URIImpl("file://b");
		URI assignment1 = new URIImpl("ass://1");
		URI assignment2 = new URIImpl("ass://2");
		URI assignment3 = new URIImpl("ass://1");

		// a = 'paper'
		m.addStatement(assignment1, hasTag, tagPaper);
		m.addStatement(assignment1, hasContent, fileA);
		// a = 'semweb'
		m.addStatement(assignment2, hasTag, tagSemweb);
		m.addStatement(assignment2, hasContent, fileA);
		// b = 'semweb'
		m.addStatement(assignment3, hasTag, tagSemweb);
		m.addStatement(assignment3, hasContent, fileB);

		QueryResultTable result = m.sparqlSelect("SELECT ?f WHERE " + "{ ?a <" + hasContent
				+ "> ?f. " + "?a <" + hasTag + "> <" + tagSemweb + "> . }");

		// expect A and B
		QueryRow firstSolution = result.iterator().next();
		assertNotNull(firstSolution);
		assertEquals(1, result.getVariables().size());
		assertTrue(firstSolution.getValue("f").equals(fileA)
				|| firstSolution.getValue("f").equals(fileB));

	}

	public void testSparqlSelectWithStrings() throws Exception {
		Model m = getModelFactory().createModel(Reasoning.rdfs);
		m.open();

		URI hasContent = new URIImpl("prop://hasContent");
		URI hasTag = new URIImpl("prop://hasTag");
		String tagSemweb = "semweb";
		String tagPaper = "paper";
		URI fileA = new URIImpl("file://a");
		URI fileB = new URIImpl("file://b");
		URI assignment1 = new URIImpl("ass://1");
		URI assignment2 = new URIImpl("ass://2");
		URI assignment3 = new URIImpl("ass://1");

		// a = 'paper'
		m.addStatement(assignment1, hasTag, tagPaper);
		m.addStatement(assignment1, hasContent, fileA);
		// a = 'semweb'
		m.addStatement(assignment2, hasTag, tagSemweb);
		m.addStatement(assignment2, hasContent, fileA);
		// b = 'semweb'
		m.addStatement(assignment3, hasTag, tagSemweb);
		m.addStatement(assignment3, hasContent, fileB);

		QueryResultTable result = m.sparqlSelect("SELECT ?f WHERE " + "{ ?a <" + hasContent
				+ "> ?f. " + "?a <" + hasTag + "> '" + tagSemweb + "' . }");

		// expect A and B
		QueryRow firstSolution = result.iterator().next();
		assertNotNull(firstSolution);
		assertEquals(1, result.getVariables().size());
		assertTrue(firstSolution.getValue("f").equals(fileA)
				|| firstSolution.getValue("f").equals(fileB));

	}

	public void testAsk() throws ModelRuntimeException {
		Model m = getModelFactory().createModel(Reasoning.none);
		m.open();
		URI a = new URIImpl("urn:test:a");
		URI b = new URIImpl("urn:test:b");
		URI c = new URIImpl("urn:test:c");
		m.addStatement(a, b, c);
		m.dump();
		
		assertTrue( m.sparqlAsk("ASK { ?s ?p ?x . }") );
		assertTrue( m.sparqlAsk("ASK { <urn:test:a> <urn:test:b> ?x . }") );
		assertTrue( m.sparqlAsk("ASK { <urn:test:a> <urn:test:b> <urn:test:c> . }") );
	}

	/**
	 * This code works ok if I use a one-argument version of readFrom method
	 * (without Syntax.RdfXml argument). With it the second argument it
	 * generates an exception:
	 * 
	 * @throws Exception
	 */
	// this will not work because the file on the web has a bad syntax
	public void testReadFromURL() throws Exception {
		Model model = getModelFactory().createModel(Reasoning.none);
		model.open();

		URL url = new URL("http://www.w3.org/1999/02/22-rdf-syntax-ns");
		BufferedReader in = new BufferedReader(new InputStreamReader(url
				.openStream()));

		model.readFrom(in, Syntax.RdfXml);

		// Exception in thread "main" java.lang.RuntimeException:
		// java.io.IOException: Stream closed
		// at
		// org.ontoware.rdf2go.impl.sesame2.ModelImplSesame.readFrom(ModelImplSesame.java:461)
		// at ReaderTest.main(ReaderTest.java:12)
		// Caused by: java.io.IOException: Stream closed
		// at sun.nio.cs.StreamDecoder.ensureOpen(StreamDecoder.java:38)
		// at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:153)
		// at sun.nio.cs.StreamDecoder.read0(StreamDecoder.java:132)
		// at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:118)
		// at java.io.InputStreamReader.read(InputStreamReader.java:151)
		// at
		// org.openrdf.rio.ntriples.NTriplesParser.parse(NTriplesParser.java:158)
		// at
		// org.openrdf.repository.ConnectionImpl._addInputStreamOrReader(ConnectionImpl.java:304)
		// at
		// org.openrdf.repository.ConnectionImpl.add(ConnectionImpl.java:274)
		// at
		// org.ontoware.rdf2go.impl.sesame2.ModelImplSesame.readFrom(ModelImplSesame.java:454)
		// ... 1 more

		// It seems that NTriplesParser is somehow invoked somewhere.
		// This example
		// file is an icaltzd ontology available at
		// http:// www.w3.org/2002/12/cal/icaltzd
		// I use a local file, because the online version contains
		// multiply-defined
		// id's, that cause RIO parser to fail... I manually removed
		// those multiple
		// definitions. The result is attached to this email.
		//	
		// This is not the only file I get this result with...

	}

	public void testReadFromFile() throws ModelRuntimeException, IOException {
		Model model = getModelFactory().createModel(Reasoning.none);
		model.open();
		assertNotNull(model);
		InputStream reader = TestData.getFoafAsStream();
		assertNotNull(reader);
		Syntax rdfxml = Syntax.RdfXml;
		assertNotNull(rdfxml);
		model.readFrom(reader, rdfxml);
		model.close();
	}

	
	
	
}
