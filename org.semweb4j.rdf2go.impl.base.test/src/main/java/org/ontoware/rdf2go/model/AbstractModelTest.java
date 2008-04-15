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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.ReasoningNotSupportedException;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.testdata.TestData;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractModelTest extends TestCase {

	private static final Logger log = LoggerFactory
			.getLogger(AbstractModelTest.class);

	public static URI a = new URIImpl("test://test/a", false);

	public static URI b = new URIImpl("test://test/b", false);

	public static URI c = new URIImpl("test://test/c", false);

	public static URI subject = new URIImpl("test://test/a", false);

	public static URI predicate = new URIImpl("test://test/b", false);

	public static URI object = new URIImpl("test://test/c", false);

	public static URI dt = new URIImpl("test://somedata/dt", false);

	private Model model;

	/** @return a Model to be used in the test. It must be fresh, e.g. unused */
	public abstract ModelFactory getModelFactory();
	
	@Before
	public void setUp() {
		URI u = getModelFactory().createModel().newRandomUniqueURI();
		this.model = getModelFactory().createModel(u);
		this.model.open();
	}
	
	@After
	public void tearDown() {
		this.model.close();
	}

	@Test
	public void testIsOpen() {
		assert getModelFactory() != null;
		ModelFactory mf = getModelFactory();
		assert mf != null;
		Model model = mf.createModel();
		assert model != null;
		model.open();
		assertNotNull(model);
		assertTrue(model.isOpen());
		model.close();
	}

	/*
	 * Class under test for void addStatement(URI, URI, URI)
	 */
	@Test
	public void testAddStatementSimple() throws Exception {
		this.model.addStatement(subject, predicate, object);
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
	@Test
	public void testAddTwoStatements() throws Exception {
		model.addStatement(a, b, "Jim");
		model.addStatement(a, c, "Jon");
		ClosableIterator<Statement> sit = model.iterator();
		assertTrue(sit.hasNext());
		int count = 0;
		while (sit.hasNext()) {
			count++;
			@SuppressWarnings("unused")
			Statement s;
			s = sit.next();
		}
		assertEquals(2, count);
		sit.close(); // redudant
	}

	/*
	 * Class under test for void removeStatement(URI, URI, URI) and void
	 * addStatement(URI, URI, URI)
	 */
	@Test
	public void testRemoveStatementSimple() throws Exception {
		model = getModelFactory().createModel();
		model.open();
		model.addStatement(subject, predicate, object);
		ClosableIterator<? extends Statement> iter = model.findStatements(
				subject, predicate, object);
		assertTrue(iter.hasNext());
		while (iter.hasNext())
			iter.next();
		iter.close();

		model.removeStatement(subject, predicate, object);
		ClosableIterator<? extends Statement> it = model.findStatements(
				subject, predicate, object);
		assertFalse(it.hasNext());
		it.close();
	}

	/*
	 * Class under test for void addStatement(URI, URI, String) with a Literal
	 * without Language Tag or Data Type
	 */
	@Test
	public void testAddStatementURIURIString() throws Exception {
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

			assertTrue(s.getObject().equals("Test"));
		}
		sit.close();
	}

	/*
	 * Class under test for void removeStatement(URI, URI, String)
	 */
	@Test
	public void testRemoveStatementURIURIString() throws Exception {
		model.addStatement(subject, predicate, "Test");

		ClosableIterator<? extends Statement> iter = model.findStatements(
				subject, predicate, model.createPlainLiteral("Test"));
		assertTrue(iter.hasNext());
		while (iter.hasNext())
			iter.next();
		iter.close();
		model.removeStatement(subject, predicate, "Test");
		ClosableIterator<? extends Statement> it = model.findStatements(
				subject, predicate, model.createPlainLiteral("Test"));
		assertFalse(it.hasNext());
		it.close();
	}

	/*
	 * Class under test for void addStatement(URI, URI, String, String)
	 */
	@Test
	public void testAddStatementLanguageTaggedLiteral() throws Exception {
		model.addStatement(subject, predicate, "Test", "DE");
		ClosableIterator<? extends Statement> sit = model.findStatements(
				subject, predicate, Variable.ANY);
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
		sit.close();
	}

	/*
	 * Class under test for void removeStatement(URI, URI, String, String)
	 */
	@Test
	public void testRemoveStatementLanguageTaggedLiteral() throws Exception {
		model.addStatement(subject, predicate, "Test", "DE");
		assertTrue(model.contains(subject, predicate, model
				.createLanguageTagLiteral("Test", "DE")));
		ClosableIterator<Statement> iter = model.iterator();
		assertTrue(iter.hasNext());
		iter.close();
		model.removeStatement(subject, predicate, "Test", "DE");
		assertFalse(model.contains(subject, predicate, model
				.createLanguageTagLiteral("Test", "DE")));
	}

	/*
	 * Class under test for void addStatement(URI, URI, String, URI)
	 */
	@Test
	public void testAddStatementDatatypedLiteral() throws Exception {
		model.addStatement(subject, predicate, "Test", dt);
		assertEquals(1, model.size());
		ClosableIterator<Statement> sit = model.iterator();
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
			assertEquals("Test", rdflit.getValue());
			assertEquals(dt, rdflit.getDatatype());
		}
		sit.close();
	}

	/*
	 * Class under test for void removeStatement(URI, URI, String, URI)
	 */
	@Test
	public void testRemoveStatementDatatypedLiteral() throws Exception {
		model.addStatement(subject, predicate, "Test", dt);
		assertTrue(model.contains(subject, predicate, model
				.createDatatypeLiteral("Test", dt)));

		ClosableIterator<Statement> iter = model.iterator();
		assertTrue(iter.hasNext());

		while (iter.hasNext())
			iter.next();
		iter.close();

		model.removeStatement(subject, predicate, "Test", dt);
		assertFalse(model.contains(subject, predicate, model
				.createDatatypeLiteral("Test", dt)));
	}

	/*
	 * Class under test for void addStatement(URI, URI, Object) and
	 * removeStatement(URI, URI, Object)
	 */
	@Test
	public void testAddRemoveStatementBlankNode() throws Exception {
		BlankNode blankNode = model.createBlankNode();

		model.addStatement(subject, predicate, blankNode);
		ClosableIterator<? extends Statement> sit = model.findStatements(
				subject, predicate, blankNode);
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals(subject, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(blankNode, s.getObject());
		}
		sit.close();

		model.removeStatement(subject, predicate, blankNode);
		assertFalse(model.contains(subject, predicate, blankNode));
	}

	/*
	 * Class under test for void addStatement(Object, URI, URI) and
	 * removeStatement(Object, URI, URI)
	 */
	@Test
	public void testAddRemoveStatementBlankNode2() throws Exception {
		BlankNode blankNode = model.createBlankNode();

		model.addStatement(blankNode, predicate, object);
		ClosableIterator<? extends Statement> sit = model.findStatements(
				blankNode, predicate, object);
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
		sit.close();
		model.removeStatement(blankNode, predicate, object);
		assertFalse(model.contains(blankNode, predicate, object));
	}

	/*
	 * Class under test for void addStatement(Object, URI, String) and
	 * removeStatement(Object, URI, String)
	 */
	@Test
	public void testAddRemoveStatementObjectURIString() throws Exception {
		BlankNode blankNode = model.createBlankNode();

		model.addStatement(blankNode, predicate, "Test");
		ClosableIterator<? extends Statement> sit = model
				.findStatements(new TriplePatternImpl(blankNode, predicate,
						"Test"));
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals("blank node equality", blankNode, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(s.getObject(), "Test");
		}
		sit.close();
		model.removeStatement(blankNode, predicate, "Test");
		ClosableIterator<? extends Statement> it = model
				.findStatements(new TriplePatternImpl(blankNode, predicate,
						"Test"));
		assertFalse(it.hasNext());
		it.close();
	}

	/*
	 * Class under test for void addStatement(Object, URI, Object) and
	 * removeStatement(Object, URI, Object)
	 */
	@Test
	public void testAddRemoveStatementURIObjectURIObject() throws Exception {
		BlankNode blankNodeSubject = model.createBlankNode();
		BlankNode blankNodeObject = model.createBlankNode();

		model.addStatement(blankNodeSubject, predicate, blankNodeObject);
		ClosableIterator<? extends Statement> sit = model.findStatements(
				blankNodeSubject, predicate, blankNodeObject);
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals(blankNodeSubject, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(blankNodeObject, s.getObject());
		}
		sit.close();
		model.removeStatement(blankNodeSubject, predicate, blankNodeObject);
		assertFalse(model
				.contains(blankNodeSubject, predicate, blankNodeObject));
	}

	/*
	 * Test method for 'org.ontoware.rdf2go.Model.getNewBlankNode()'
	 */
	@Test
	public void testGetNewBlankNode() {
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
	@Test
	public void testGetStatementWithLiteralAndNoLanguageTagURIURIString()
			throws Exception {
		model.addStatement(subject, predicate, "Test", "DE");
		model.addStatement(subject, predicate, "Test");

		ClosableIterator<? extends Statement> sit = model.findStatements(
				Variable.ANY, predicate, Variable.ANY);
		assertTrue(sit.hasNext());
		Statement s = sit.next();
		assertEquals(s.getSubject(), subject);
		assertEquals(s.getPredicate(), predicate);
		assertEquals(s.getObject().asLiteral().getValue(), "Test");

		assertTrue(sit.hasNext());
		sit.next();
		assertFalse(sit.hasNext());
		sit.close();
	}

	/*
	 * Test method for
	 * 'org.ontoware.rdf2go.Model.getStatementWithLiteralAndNoLanguageTag(Object,
	 * URI, String)'
	 */
	@Test
	public void testGetStatementWithLiteralAndNoLanguageTagObjectURIString()
			throws Exception {
		BlankNode blankNodeSubject1 = model.createBlankNode();
		BlankNode blankNodeSubject2 = model.createBlankNode();
		model.addStatement(blankNodeSubject1, predicate, "Test");
		model.addStatement(blankNodeSubject2, predicate, "Test", "DE");

		ClosableIterator<? extends Statement> sit = model.findStatements(
				Variable.ANY, predicate, Variable.ANY);
		assertTrue(sit.hasNext());
		Statement s = sit.next();
		// assertEquals(s.getSubject(), blankNodeSubject1);
		assertEquals(s.getPredicate(), predicate);
		assertEquals(s.getObject().asLiteral().getValue(), "Test");
		sit.next();
		assertFalse(sit.hasNext());
		sit.close();
	}

	/*
	 * Test method for 'org.ontoware.rdf2go.Model.query(String)'
	 */
	@Test
	public void testQuery() throws Exception {
		String query = "PREFIX \t:\t<test://test/>\n"
				+ "CONSTRUCT { ?s ?p \"Test2\" } WHERE { ?s ?p \"Test2\" }";
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
		iter.close();
	}

	@Test
	public void testSelectQuery() throws Exception {
		String query = "PREFIX \t:\t<test://test/>\n" + "SELECT  ?s ?p \n"
				+ "WHERE { ?s ?p \"Test2\" }";
		BlankNode bNode = model.createBlankNode();
		model.addStatement(subject, predicate, "Test1");
		model.addStatement(subject, predicate, "Test2");
		model.addStatement(bNode, predicate, "Test2");
		QueryResultTable result = model.sparqlSelect(query);
		ClosableIterator<QueryRow> it = result.iterator();
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
		it.close();
	}

	@Test
	public void testOpenClose() {
		assertTrue(model.isOpen());
		model.close();
		assertFalse(model.isOpen());
		model = getModelFactory().createModel();
		assertFalse(model.isOpen());
		model.open();
		assertTrue(model.isOpen());
	}

	/**
	 * Check for valid and invalid URIs
	 * 
	 */
	public void testURIs() {
		try {
			model.createURI("file:///c/my%20documents/blah.pdf");
		} catch (ModelRuntimeException e) {
			fail();
		}
	}

	@Test
	public void testRdfsReasoning() throws ReasoningNotSupportedException,
			ModelRuntimeException {
		// replace model with RDFS based one
		this.model.close();
		this.model = getModelFactory().createModel(Reasoning.rdfs);
		this.model.open();
		
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
			log.warn("Could not find expected statement.");
			model.dump();
		}
		assertTrue(inferencedStatement);
	}

	@Test
	public void testRdfsReasoning2() throws ReasoningNotSupportedException,
			ModelRuntimeException {
		// replace model with RDFS based one
		this.model.close();
		this.model = getModelFactory().createModel(Reasoning.rdfs);
		this.model.open();

		URI resourceA = new URIImpl("urn:resource:A");
		URI resourceB = new URIImpl("urn:resource:B");
		URI propertyA = new URIImpl("urn:prop:A");
		URI propertyB = new URIImpl("urn:prop:B");
		URI propertyC = new URIImpl("urn:prop:C");
		
		model.addStatement(propertyA, propertyB, propertyC);
		model.addStatement(propertyB, RDFS.subPropertyOf, RDFS.subPropertyOf);
		Assert.assertTrue( model.contains(propertyA, RDFS.subPropertyOf, propertyC));
		
		model.addStatement(resourceA, propertyA, resourceB);
		Assert.assertTrue( model.contains(resourceA, propertyC, resourceB ));
	}

	@Test
	public void testSimpleQuery1() throws Exception {
		URI a = new URIImpl("test://a");
		URI b = new URIImpl("test://b");
		URI c = new URIImpl("test://c");
		model.addStatement(a, b, c);
		ClosableIterator<? extends Statement> it = model
				.findStatements(a, b, c);
		assertTrue(it.hasNext());
		it.close();
	}

	@Test
	public void testSimpleQuery() throws Exception {
		URI a = new URIImpl("test://a");
		URI b = new URIImpl("test://b");
		URI c = new URIImpl("test://c");
		model.addStatement(a, b, c);
		assertEquals(1, model.size());

		ClosableIterator<? extends Statement> it = model.findStatements(
				Variable.ANY, b, c);
		assertTrue(it.hasNext());
		it.close();
	}

	@Test
	public void testDiff_empty() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();
		Diff diff = a.getDiff(b.iterator());
		assertFalse(diff.getAdded().iterator().hasNext());
		assertFalse(diff.getRemoved().iterator().hasNext());
		a.close();
		b.close();
	}

	@Test
	public void testDiff_different() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();

		a.addStatement(new URIImpl("urn:test:a", false), new URIImpl(
				"urn:test:b", false), new URIImpl("urn:test:c", false));
		b.addStatement(new URIImpl("urn:test:x", false), new URIImpl(
				"urn:test:y", false), new URIImpl("urn:test:z", false));
		Diff diff = a.getDiff(b.iterator());
		assertTrue(diff.getAdded().iterator().hasNext());
		assertTrue(diff.getRemoved().iterator().hasNext());
		a.close();
		b.close();
	}

	@Test
	public void testDiff_same() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();

		a.addStatement(new URIImpl("urn:test:a", false), new URIImpl(
				"urn:test:b", false), new URIImpl("urn:test:c", false));

		b.addStatement(new URIImpl("urn:test:a", false), new URIImpl(
				"urn:test:b", false), new URIImpl("urn:test:c", false));

		ClosableIterator<Statement> i = a.iterator();
		Statement aStmt = i.next();
		i.close();
		i = b.iterator();
		Statement bStmt = i.next();
		i.close();

		assertTrue("a statement should be equal to itself", aStmt.equals(aStmt));
		assertEquals(aStmt, aStmt);
		assertTrue(aStmt.equals(bStmt));
		assertEquals(aStmt, bStmt);

		assertEquals(aStmt.hashCode(), aStmt.hashCode());
		assertEquals(aStmt.getSubject().hashCode(), bStmt.getSubject()
				.hashCode());
		assertEquals(aStmt.getPredicate().hashCode(), bStmt.getPredicate()
				.hashCode());
		assertEquals(aStmt.getObject().hashCode(), bStmt.getObject().hashCode());
		assertEquals(aStmt.hashCode(), bStmt.hashCode());

		Diff diff = a.getDiff(b.iterator());
		assertFalse(diff.getAdded().iterator().hasNext());
		assertFalse(diff.getRemoved().iterator().hasNext());

		a.close();
		b.close();
	}
	
	@Test
	public void testUpdate() {
		Model remove = RDF2Go.getModelFactory().createModel();
		remove.open();
		Model add = RDF2Go.getModelFactory().createModel();
		add.open();
		add.addStatement(a,b,c);
		DiffReader diff = new DiffImpl(add.iterator(), remove.iterator());
		add.close();
		remove.close();

		model.update(diff);
		
		Assert.assertTrue(model.contains(a,b,c));
	}

	@Test
	public void testComparable() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();

		a.addStatement(new URIImpl("urn:test:a", false), new URIImpl(
				"urn:test:b", false), new URIImpl("urn:test:c", false));

		ClosableIterator<Statement> i = a.iterator();
		Statement aStmt = i.next();
		i.close();

		assertEquals(0, aStmt.compareTo(aStmt));

		b.addStatement(new URIImpl("urn:test:a", false), new URIImpl(
				"urn:test:b", false), new URIImpl("urn:test:c", false));
		i = b.iterator();
		Statement bStmt = i.next();
		i.close();

		assertEquals(0, aStmt.compareTo(bStmt));
		assertEquals(0, bStmt.compareTo(aStmt));

		a.close();
		b.close();
	}

	@Test
	public void testComparableWithDifferent() throws ModelRuntimeException {
		Model a = getModelFactory().createModel();
		a.open();
		Model b = getModelFactory().createModel();
		b.open();

		a.addStatement(new URIImpl("urn:test:a", false), new URIImpl(
				"urn:test:b", false), new URIImpl("urn:test:c", false));
		Statement aStmt = a.iterator().next();

		assertEquals(0, aStmt.compareTo(aStmt));

		b.addStatement(new URIImpl("urn:test:x", false), new URIImpl(
				"urn:test:y", false), new URIImpl("urn:test:z", false));
		Statement bStmt = b.iterator().next();

		// one should be positive, one negative, both not 0
		assertTrue(aStmt.compareTo(bStmt) * bStmt.compareTo(aStmt) < 0);
		a.close();
		b.close();
	}

	@Test
	public void testSparqlSelectWithURIS() throws Exception {
		Model modelRDFS = getModelFactory().createModel(Reasoning.rdfs);
		modelRDFS.open();

		modelRDFS.removeAll();

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
		modelRDFS.addStatement(assignment1, hasTag, tagPaper);
		modelRDFS.addStatement(assignment1, hasContent, fileA);
		// a = 'semweb'
		modelRDFS.addStatement(assignment2, hasTag, tagSemweb);
		modelRDFS.addStatement(assignment2, hasContent, fileA);
		// b = 'semweb'
		modelRDFS.addStatement(assignment3, hasTag, tagSemweb);
		modelRDFS.addStatement(assignment3, hasContent, fileB);

		QueryResultTable result = modelRDFS.sparqlSelect("SELECT ?f WHERE " + "{ ?a <"
				+ hasContent + "> ?f. " + "?a <" + hasTag + "> <" + tagSemweb
				+ "> . }");

		// expect A and B
		ClosableIterator<QueryRow> i = result.iterator();
		QueryRow firstSolution = i.next();
		assertNotNull(firstSolution);
		assertEquals(1, result.getVariables().size());
		assertTrue(firstSolution.getValue("f").equals(fileA)
				|| firstSolution.getValue("f").equals(fileB));
		i.close();
		modelRDFS.close();
	}

	@Test
	public void testSparqlAsk() throws Exception {
		Model modelRDFS = getModelFactory().createModel(Reasoning.rdfs);
		modelRDFS.open();
		URI hasTag = new URIImpl("prop://hasTag");
		URI tagSemweb = new URIImpl("tag://semweb");
		URI fileA = new URIImpl("file://a");
		modelRDFS.addStatement(fileA, hasTag, tagSemweb);
		Assert.assertTrue(modelRDFS.sparqlAsk("ASK WHERE { " + fileA.toSPARQL() + " "
				+ hasTag.toSPARQL() + " ?tag . }"));
		Assert.assertTrue(modelRDFS.sparqlAsk("ASK WHERE { " + fileA.toSPARQL() + " "
				+ " ?prop " + tagSemweb.toSPARQL() + " . }"));
		Assert.assertFalse(modelRDFS.sparqlAsk("ASK WHERE { " + fileA.toSPARQL() + " "
				+ "<prop://bogus>" + " ?tag . }"));
		Assert.assertTrue(modelRDFS.sparqlAsk("ASK WHERE { ?s ?p ?o . }"));
		modelRDFS.close();
	}

	@Test
	public void testSparqlSelectWithStrings() throws Exception {
		Model modelRDFS = getModelFactory().createModel(Reasoning.rdfs);
		modelRDFS.open();

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
		modelRDFS.addStatement(assignment1, hasTag, tagPaper);
		modelRDFS.addStatement(assignment1, hasContent, fileA);
		// a = 'semweb'
		modelRDFS.addStatement(assignment2, hasTag, tagSemweb);
		modelRDFS.addStatement(assignment2, hasContent, fileA);
		// b = 'semweb'
		modelRDFS.addStatement(assignment3, hasTag, tagSemweb);
		modelRDFS.addStatement(assignment3, hasContent, fileB);

		QueryResultTable result = modelRDFS.sparqlSelect("SELECT ?f WHERE " + "{ ?a <"
				+ hasContent + "> ?f. " + "?a <" + hasTag + "> '" + tagSemweb
				+ "' . }");

		// expect A and B
		ClosableIterator<QueryRow> i = result.iterator();
		QueryRow firstSolution = i.next();
		assertNotNull(firstSolution);
		assertEquals(1, result.getVariables().size());
		assertTrue(firstSolution.getValue("f").equals(fileA)
				|| firstSolution.getValue("f").equals(fileB));
		i.close();
		modelRDFS.close();
	}

	@Test
	public void testAsk() throws ModelRuntimeException {
		URI a = new URIImpl("urn:test:a");
		URI b = new URIImpl("urn:test:b");
		URI c = new URIImpl("urn:test:c");
		model.addStatement(a, b, c);

		assertTrue(model.sparqlAsk("ASK { ?s ?p ?x . }"));
		assertTrue(model.sparqlAsk("ASK { <urn:test:a> <urn:test:b> ?x . }"));
		assertTrue(model
				.sparqlAsk("ASK { <urn:test:a> <urn:test:b> <urn:test:c> . }"));
	}

	@Test
	public void testReadFromFile() throws ModelRuntimeException, IOException {
		assertNotNull(model);
		InputStream reader = TestData.getFoafAsStream();
		assertNotNull("testdata stream should not be null", reader);
		Syntax rdfxml = Syntax.RdfXml;
		assertNotNull(rdfxml);
		model.readFrom(reader, rdfxml);
	}

	@Test
	public void testStringEncoding() {
		log
				.debug("create a String that contains each possible unicode value once. May take a while.");
		char[] allchars = new char[Character.MAX_VALUE];
		for (char i = 0; i < allchars.length; i++) {
			allchars[i] = i;
		}
		String inString = new String(allchars);

		model.addStatement(a, b, inString);

		ClosableIterator<Statement> it = model.iterator();
		Statement stmt = it.next();
		it.close();
		model.close();

		Assert.assertEquals(a, stmt.getSubject());
		Assert.assertEquals(b, stmt.getPredicate());
		Assert.assertEquals(inString, stmt.getObject().asLiteral().getValue());
	}

	@Test
	public void testWriteRead() throws ModelRuntimeException {
		URI konrad = model.createURI("urn:x-example:konrad");
		URI kennt = model.createURI("urn:x-example:kennt");
		URI max = model.createURI("urn:x-example:max");

		model.addStatement(konrad, kennt, max);

		String queryString = "SELECT ?x WHERE { <" + konrad + "> <" + kennt
				+ "> ?x}";
		QueryResultTable table = model.sparqlSelect(queryString);
		ClosableIterator<QueryRow> it = table.iterator();
		QueryRow row = it.next();
		assertFalse("iterator should have only one result", it.hasNext());
		Node n = row.getValue("x");
		assertEquals(n, max);
	}

	/**
	 * how to make simple sparql queries and cope with the results
	 */
	@Test
	public void testGetSelectQueryResult() throws MalformedQueryException,
			ModelRuntimeException {
		QueryResultTable table = model
				.sparqlSelect("SELECT ?a ?b ?c WHERE { ?a ?b ?c }");
		Iterator<QueryRow> iterator = table.iterator();

		while (iterator.hasNext()) {
			QueryRow row = iterator.next();
			for (String varname : table.getVariables()) {
				@SuppressWarnings("unused")
				Node x = row.getValue(varname);
			}
		}

	}

	/**
	 * how to write basic model usage operations
	 */
	@Test
	public void testBasicUsage() {
		// get a model
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();

		// add statements

		// the cleanest way
		Resource s = model.createURI("urn:test:a");
		URI p = model.createURI("http://www.w3.org/2000/01/rdf-schema#label");
		Node o = model.createPlainLiteral("Hello World A");
		model.addStatement(s, p, o);

		// a shortcut: built-in URIs for RDF and RDFS
		model.addStatement("urn:test:b", RDFS.label, "Hello World B");

		// a shortcut: resource URIs and plain literal objects as strings
		model.addStatement("urn:test:c", RDFS.label, "Hello World C");

		// list statements
		// for (Statement stmt : model) {
		// System.out.println(stmt);
		// }

		// query for triple pattern
		// ClosableIterator<? extends Statement> it = model.findStatements(s,
		// Variable.ANY, Variable.ANY);
		// while (it.hasNext()) {
		// Statement stmt = it.next();
		// System.out.println(stmt);
		// }
		// it.close();
		
		// always close after use to free resources
		model.close();
	}

	@Test
	public void testReadFromFileWithSyntaxArgument()
			throws ModelRuntimeException, IOException {
		InputStream stream = TestData.getICALAsStream();
		Assert.assertNotNull(stream);
		InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

		model.readFrom(reader, Syntax.RdfXml);

		reader.close();
		stream.close();
	}

	@Test
	public void testCheckForValidURI() {
		assertFalse(model.isValidURI("ping"));
		assertTrue(model.isValidURI("http://i.am.a.uri"));
	}

	@Test
	public void testAutoCommit() throws ModelRuntimeException {
		assertFalse(model.isLocked());
		model.lock();
		assertTrue(model.isLocked());

		model.addStatement(subject, predicate, "Test", "DE");
		model.unlock();
		assertFalse(model.isLocked());

		assertTrue(model.contains(subject, predicate, model
				.createLanguageTagLiteral("Test", "DE")));

	}

	/* assert that language tags are always in lower-case */
	@Test
	public void testLowerCaseLanguageTag() throws Exception {
		model.addStatement(subject, predicate, "Test", "DE");
		model.addStatement(subject, predicate, "Test");

		ClosableIterator<? extends Statement> iterator = model.findStatements(
				Variable.ANY, predicate, Variable.ANY);
		assertTrue(iterator.hasNext());

		while (iterator.hasNext()) {
			Statement statement = iterator.next();
			assertEquals(statement.getSubject(), subject);
			assertEquals(statement.getPredicate(), predicate);

			if (statement.getObject() instanceof LanguageTagLiteral) {
				assertEquals(((LanguageTagLiteral) (statement.getObject()))
						.getValue(), "Test");
				assertEquals(((LanguageTagLiteral) (statement.getObject()))
						.getLanguageTag(), "de");
			} else {
				assertTrue(statement.getObject() instanceof PlainLiteral);
				assertTrue(((PlainLiteral) (statement.getObject())).getValue()
						.equals("Test"));
			}
		}

		assertFalse(iterator.hasNext());

		iterator.close();
	}
	
	@Test
	public void testNamespaceSupport() {
		assertEquals(0, this.model.getNamespaces().size() );
		this.model.setNamespace("foo", "http://foo.com");
		assertEquals(1, this.model.getNamespaces().size() );
		assertNull( this.model.getNamespaces().get("bar") );
		assertNotNull( this.model.getNamespaces().get("foo") );
		assertEquals("http://foo.com", this.model.getNamespaces().get("foo") );
		this.model.removeNamespace("foo");
		assertEquals(0, this.model.getNamespaces().size() );
	}

	// TODO test public ClosableIterable<Statement> sparqlDescribe(String query)

}
