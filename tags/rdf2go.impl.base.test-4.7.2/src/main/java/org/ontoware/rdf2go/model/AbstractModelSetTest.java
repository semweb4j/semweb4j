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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.impl.QuadPatternImpl;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.testdata.TestData;
import org.ontoware.rdf2go.util.Iterators;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this to test implementations of ModelSet. Overwrite the class and
 * implement the "setup" method, initializing modelset and modelfactory.
 * 
 * @author voelkel
 * @author sauermann
 */
public abstract class AbstractModelSetTest {

	public static URI a = new URIImpl("test://test/a");

	public static URI b = new URIImpl("test://test/b");

	public static URI c = new URIImpl("test://test/c");

	public static URI dt = new URIImpl("test://somedata/dt");

	public static URI graphuri1 = new URIImpl("urn:first");

	public static URI graphuri2 = new URIImpl("urn:second");

	public static URI object = new URIImpl("test://test/c");

	public static URI predicate = new URIImpl("test://test/b");

	public static URI subject = new URIImpl("test://test/a");

	/**
	 * there are two graphs in the default test data
	 */
	public static int TESTGRAPHCOUNT = 2;

	private static Logger log = LoggerFactory
			.getLogger(AbstractModelSetTest.class);

	public static <T> ArrayList<T> asArrayListAndClose(ClosableIterator<T> it) {
		ArrayList<T> result = new ArrayList<T>();
		while (it.hasNext()) {
			T t = it.next();
			result.add(t);
			if (t instanceof Model) {
				((Model) t).close();
			}
		}
		it.close();
		return result;
	}

	private ModelSet modelset;

	/**
	 * @return a fresh ModelFactory, to be tested
	 */
	public abstract ModelFactory getModelFactory();

	/**
	 * Subclasses can create a configured ModelSet instance.
	 */
	@Before
	public void setUp() {
		this.modelset = this.getModelFactory().createModelSet();
		this.modelset.open();
	}

	@After
	public void tearDown() throws Exception {
		if (this.modelset != null)
		{
			this.modelset.close();
			this.modelset = null;
		}
		// IMPROVE Do we need to call System.gc() on tear-down?
		System.gc();
	}

	@Test
	public void testAddDataFromFileByCopying() throws Exception {
		this.modelset.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);
		assertTrue(this.modelset.size() > 0);

		ModelSet target = this.getModelFactory().createModelSet();
		target.open();

		TestUtils.copy(this.modelset, target);
		assertEquals(this.modelset.size(), target.size());
		target.close();
	}

	/**
	 * above test sometimes failed, here is a trace into the details that can
	 * happen
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddDataFromFileByCopyingMoreDetail() throws Exception {
		this.modelset.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);

		assertTrue(this.modelset.size() > 0);
		// the modelset loads into the default model
		assertFalse(
				"as we loaded data only in default model, there are no other models",
				this.modelset.getModels().hasNext());

		// the default model has something
		Model m = this.modelset.getDefaultModel();
		m.open();
		assertEquals("the default model has foaf", 536, m.size());
		int sizeByIterator = TestUtils.countAndClose(m);
		assertEquals("the default model can use an iterator", 536,
				sizeByIterator);
		m.close();

		ModelSet target = this.getModelFactory().createModelSet();
		target.open();

		// Es liegt also daran, das modelset.read die daten in irgendein nicht
		// mehr
		// zugreifbared modell mit dem context "null" steckt. Aber wenn man ein
		// model
		// mti "null" macht, ist es leer. Da stimmt was nicht mit sesame.
		TestUtils.copy(this.modelset, target);

		assertEquals(this.modelset.size(), target.size());
		target.close();
		m.close();
		this.modelset.close();
	}

	@Test
	public void testAddModel() {
		Model model = this.modelset.getModel(graphuri1);
		model.open();
		model.addStatement(a, b, c);

		// some sanity checking on the ModelSet
		assertEquals(1, this.modelset.size());

		// see if we can add the Model to the ModelSet: no deadlocks and nothing
		// should change
		this.modelset.addModel(model);
		assertEquals(1, this.modelset.size());

		// check that something does change when we add a different, stand-alone
		// Model
		Model model2 = getModelFactory().createModel(graphuri2);
		model2.open();
		model2.addStatement(subject, predicate, object);
		this.modelset.addModel(model2);
		assertEquals(2, this.modelset.size());

		// clean-up
		model.close();
		model2.close();
		this.modelset.close();
	}

	@Test
	public void testAddModelContextURI() {
		Model model = getModelFactory().createModel(graphuri1);
		model.open();
		model.addStatement(a, b, c);
		assertFalse(this.modelset.containsStatements(graphuri1, a, b, c));
		assertFalse(this.modelset.containsStatements(graphuri2, a, b, c));
		this.modelset.addModel(model, graphuri2);
		model.close();
		assertFalse(this.modelset.containsStatements(graphuri1, a, b, c));
		assertTrue(this.modelset.containsStatements(graphuri2, a, b, c));
		this.modelset.removeModel(graphuri2);
		assertFalse(this.modelset.containsStatements(graphuri1, a, b, c));
		assertFalse(this.modelset.containsStatements(graphuri2, a, b, c));
	}

	@Test
	public void testAddModelSet() {
		Model model = getModelFactory().createModel(graphuri1);
		model.open();
		model.addStatement(a, b, c);
		assertFalse(this.modelset.containsStatements(graphuri1, a, b, c));
		this.modelset.addModel(model);
		model.close();
		assertTrue(this.modelset.containsStatements(graphuri1, a, b, c));
		this.modelset.removeModel(graphuri2);
		assertTrue(this.modelset.containsStatements(graphuri1, a, b, c));
		this.modelset.removeModel(graphuri1);
		assertFalse(this.modelset.containsStatements(graphuri1, a, b, c));
	}

	@Test
	public void testAddRemovePatternsWithNull() {
		Assert.assertTrue(this.modelset.isOpen());
		this.modelset.addStatement(null, a, b, c);
		Assert.assertTrue(this.modelset.isOpen());
		Assert.assertEquals(1, this.modelset.size());
		Assert.assertTrue(this.modelset.isOpen());
		this.modelset.removeStatements(null, Variable.ANY, Variable.ANY,
				Variable.ANY);
		Assert.assertTrue(this.modelset.isOpen());
		Assert.assertEquals(0, this.modelset.size());
	}

	@Test
	public void testContainsModel() throws ModelRuntimeException {
		assertFalse(this.modelset.containsModel(graphuri1));
		Model model = this.modelset.getModel(graphuri1);
		model.open();
		model.addStatement(a, b, c);
		assertTrue(this.modelset.containsModel(graphuri1));

		model.removeStatement(a, b, c);
		// TODO Does a ModelSet.containsModel(x) if all triples of x have been
		// removed from ModelSet?
		// assertFalse(this.modelset.containsModel(graphuri1));
		model.close();
	}

	@Test
	public void testContainsStatements() throws ModelRuntimeException {
		assertFalse(this.modelset.containsStatements(graphuri1, a, b, c));
		Model model = this.modelset.getModel(graphuri1);
		model.open();
		model.addStatement(a, b, c);
		assertTrue(this.modelset.containsStatements(graphuri1, a, b, c));

		// also with wildcard
		assertTrue(this.modelset.containsStatements(Variable.ANY, a, b, c));

		model.removeStatement(a, b, c);
		//		
		assertFalse(this.modelset.containsStatements(graphuri1, a, b, c));
		model.close();
	}

	@Test
	public void testCopyModelSets() throws Exception {
		this.addTestDataToModelSet();
		// add a statement in the default context
		Model model = this.modelset.getDefaultModel();
		model.open();
		model.addStatement(subject, predicate, object);
		model.close();

		ModelSet modelset2 = this.getModelFactory().createModelSet();
		modelset2.open();
		TestUtils.copy(this.modelset, modelset2);
		assertEquals("copied all from source to target", this.modelset.size(),
				modelset2.size());
		modelset2.close();
	}

	@Test
	public void testCopyModelSets2() throws Exception {
		this.addTestDataToModelSet();
		// add a statement in the default context
		Model model = this.modelset.getDefaultModel();
		model.open();
		model.addStatement(subject, predicate, object);

		ModelSet modelset2 = this.getModelFactory().createModelSet();
		modelset2.open();
		TestUtils.copy(this.modelset, modelset2);
		assertEquals("copied all from source to target", this.modelset.size(),
				modelset2.size());
		modelset2.close();
		model.close();
	}

	@Test
	public void testCreateStatement() {
		Statement s = this.modelset.createStatement(a, b, c);
		assertEquals(s, new StatementImpl(null, a, b, c));
	}

	@Test
	public void testCreateURI() throws ModelRuntimeException {
		URI u = this.modelset.createURI("urn:test:x");
		assertNotNull(u);
	}

	@Test
	public void testDeleteStatement() {
		this.modelset.open();
		Assert.assertEquals(0, this.modelset.size());
		this.modelset.addStatement(graphuri1, a, b, c);
		Assert.assertEquals(1, this.modelset.size());
		this.modelset.addStatement(graphuri2, a, b, c);
		Assert.assertEquals(2, this.modelset.size());
		this.modelset.removeStatement(this.modelset.createStatement(graphuri1,
				a, b, c));
		Assert.assertEquals(1, this.modelset.size());
		this.modelset.addStatement(null, a, b, c);
		Assert.assertEquals(2, this.modelset.size());
		this.modelset.removeStatement(this.modelset.createStatement(null, a, b,
				c));
		Assert.assertEquals(1, this.modelset.size());
	}

	/**
	 * Gunnar: need a function that queries and removes triples from all
	 * contexts... i.e. remove all (null, rdf:type, null) from ALL contexts...
	 * 
	 * Max: So basically this would mean handling of Triple-patterns not just
	 * triples, in the add/remove methods. Ok.
	 * 
	 * 
	 * @throws Exception
	 * @throws ModelRuntimeException
	 */
	@Test
	public void testDeleteTriplePatternInAllGraphs()
			throws ModelRuntimeException, Exception {
		Iterator<? extends Model> it = this.modelset.getModels();
		while (it.hasNext()) {
			Model model = it.next();
			// This line breaks really badly on sesame2...
			// and on many other locking stores as well ..
			// model.removeAll(model.findStatements(Variable.ANY, RDF.type,
			// Variable.ANY).iterator());

			// just added this method
			model.removeStatements(Variable.ANY, RDF.type, Variable.ANY);
		}
	}

	/** test find with (c,x,y,z) on contained model */
	@Test
	public void testFindStatements() throws ModelRuntimeException {
		Model m = this.modelset.getModel(graphuri1);
		m.open();
		m.addStatement(a, b, c);
		ClosableIterator<? extends Statement> it = this.modelset
				.findStatements(graphuri1, a, b, c);
		assertTrue(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
		it.close();
		m.close();
	}

	/**
	 * Test find with (*,x,y,z) on modelset
	 * 
	 * @throws ModelRuntimeException
	 */
	@Test
	public void testFindStatements2() throws ModelRuntimeException {
		URI a = new URIImpl("urn:test:a");
		URI b = new URIImpl("urn:test:b");
		URI c = new URIImpl("urn:test:c");
		URI d = new URIImpl("urn:test:d");
		this.modelset.addStatement(a, b, c, d);

		ClosableIterator<? extends Statement> it = this.modelset
				.findStatements(Variable.ANY, b, c, d);
		Statement stmt = it.next();
		Assert.assertNotNull(stmt.getContext());
		Assert.assertEquals(a, stmt.getContext());
		it.close();
	}

	/**
	 * The default model as defined in the SPARQL semantics. Each data set
	 * consisting of Models/Graphs has one default graph.
	 * 
	 */
	@Test
	public void testGetDefaultModel() {
		Model defaultModel = this.modelset.getDefaultModel();
		assertNotNull(defaultModel);
		assertNull("the default model must have the NULL context", defaultModel
				.getContextURI());
		assertTrue("new open/close policy: open modelsets return open models",
				defaultModel.isOpen());
		assertTrue(defaultModel.isEmpty());
		// add stuff to it
		defaultModel.addStatement(a, b, c);
		assertTrue(defaultModel.contains(a, b, c));
		defaultModel.removeStatement(a, b, c);
		assertTrue(defaultModel.isEmpty());
		defaultModel.close();
	}

	@Test
	public void testGetModel() throws Exception {
		this.addTestDataToModelSet();
		Model m = this.modelset.getModel(graphuri1);
		m.open();
		assertNotNull(m);
		assertTrue("graph1 contains less than 10 statements, it contains: "
				+ m.size(), m.size() > 10);
		m = this.modelset.getModel(graphuri2);
		m.open();
		assertNotNull(m);
		assertTrue("graph2 contains more than 10 statements, it contains: "
				+ m.size(), m.size() > 10);
		m.close();
	}

	@Test
	public void testGetModels() throws Exception {
		this.addTestDataToModelSet();
		ClosableIterator<Model> i = this.modelset.getModels();
		ArrayList<Model> m = asArrayListAndClose(i);
		assertEquals(TESTGRAPHCOUNT, m.size());
	}

	@Test
	public void testGetModelURIs() throws Exception {
		this.addTestDataToModelSet();
		ClosableIterator<URI> l = this.modelset.getModelURIs();
		ArrayList<URI> test = new ArrayList<URI>();
		test.add(graphuri1);
		test.add(graphuri2);
		ArrayList<URI> uris = asArrayListAndClose(l);
		assertEquals(2, uris.size());
		test.removeAll(uris);
		assertEquals(0, test.size());
	}

	@Test
	public void testGetUnderlyingModelSetImplementation() {
		assertNotNull(this.modelset.getUnderlyingModelSetImplementation());
	}

	@Test
	public void testLoadDataIntoDefaultModel() throws Exception {
		this.modelset.readFrom(TestData.getFoafAsStream());
		assertTrue(this.modelset.size() > 10);
		// check default model
		Model def = this.modelset.getDefaultModel();
		def.open();
		assertTrue(def.isOpen());
		assertNotNull(def);
		assertTrue("default model has something", def.size() > 10);
		ArrayList<URI> uris = asArrayListAndClose(this.modelset.getModelURIs());
		assertEquals("default model has no context uri", 0, uris.size());
		def.close();
	}

	@Test
	public void testNamespaceSupport() {
		assertEquals(0, this.modelset.getNamespaces().size());
		this.modelset.setNamespace("foo", "http://foo.com");
		assertEquals(1, this.modelset.getNamespaces().size());
		assertNull(this.modelset.getNamespaces().get("bar"));
		assertNotNull(this.modelset.getNamespaces().get("foo"));
		assertEquals("http://foo.com", this.modelset.getNamespaces().get("foo"));
		this.modelset.removeNamespace("foo");
		assertEquals(0, this.modelset.getNamespaces().size());
	}

	@Test
	public void testOpenClose() {
		assertTrue(this.modelset.isOpen());
		Model defaultModel = this.modelset.getDefaultModel();
		assertTrue(defaultModel.isOpen());
		defaultModel.close();
		Model model = this.modelset.getModel(new URIImpl("urn:test:model1"));
		assertTrue("ModelSet returns open models", model.isOpen());
		model.close();
	}

	@Test
	public void testRDF2GoBug() {
		for (ClosableIterator<? extends Statement> i = this.modelset.iterator(); i
				.hasNext();) {
			i.next();
		}
	}

	@Test
	public void testRDF2GoBug2() {
		Statement s = new StatementImpl(new URIImpl("urn:testcontext"),
				new URIImpl("urn:test"), new URIImpl("urn:testpred"),
				new URIImpl("urn:testobj"));
		this.modelset.addStatement(s);
		for (ClosableIterator<? extends Statement> i = this.modelset
				.findStatements(new QuadPatternImpl(Variable.ANY, Variable.ANY,
						Variable.ANY, Variable.ANY)); i.hasNext();) {
			i.next();
		}
	}

	@Test
	public void testRDF2GoBugAddRemove() {
		URI uri = this.modelset.createURI("http://example");
		this.modelset.addStatement(null, uri, uri, uri);
		this.modelset.removeStatements(null, Variable.ANY, Variable.ANY,
				Variable.ANY);
	}

	@Test
	public void testReadFromInputStream() {
		InputStream in = TestData.getFoafAsStream();
		try {
			this.modelset.readFrom(in);
		} catch (ModelRuntimeException e) {
			fail();
		} catch (IOException e) {
			fail();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				fail();
			}
		}
	}

	@Test
	public void testReadFromInputStreamIntoDefaultModel() throws Exception {
		this.modelset.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);

		assertTrue(this.modelset.size() > 0);
		// the modelset loads into the default model
		assertFalse(
				"as we loaded data only in default model, there are no other models",
				this.modelset.getModels().hasNext());

		// the default model has something
		Model m = this.modelset.getDefaultModel();
		m.open();
		assertEquals("the default model has foaf", 536, m.size());

		int sizeByIterator = TestUtils.countAndClose(m);
		assertEquals("the default model can use an iterator", 536,
				sizeByIterator);
		m.close();
	}

	@Test
	public void testReadFromInputStreamIntoNamedModel() throws Exception {
		Model m = this.modelset.getModel(graphuri1);
		m.open();
		m.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);
		m.close();

		// add data to second graph
		m = this.modelset.getModel(graphuri2);
		m.open();
		m.readFrom(TestData.getICALAsStream(), Syntax.RdfXml);
		m.close();

		// assertEquals("The modelset contains exactly loaded triples.",
		// TestData.FOAF_SIZE + TestData.ICALSIZE, this.modelset.size());
		// the modelset loads into the named graph
		int modelcount = TestUtils.countAndClose(this.modelset.getModels());
		assertEquals("there are two models", 2, modelcount);
		// no default model
		m = this.modelset.getDefaultModel();
		m.open();
		assertEquals("nothing in default model", 0, m.size());
		m.close();

		// get the named model1
		m = this.modelset.getModel(graphuri1);
		m.open();
		assertEquals("the named graph model has foaf", TestData.FOAFSIZE, m
				.size());
		// at some point, iterators were broken here, so test if it returns one
		int sizeByIterator = TestUtils.countAndClose(m);
		assertEquals("the model supports iterators", TestData.FOAFSIZE,
				sizeByIterator);
		m.close();

		// get the named model2
		m = this.modelset.getModel(graphuri2);
		m.open();
		assertEquals("the named graph model has ical", TestData.ICALSIZE, m
				.size());
		m.close();

	}

	@Test
	public void testReadFromInputStreamSyntax() {
		// TODO write test for testReadFromInputStreamSyntax
	}

	@Test
	public void testReadFromReader() {
		InputStreamReader isr = new InputStreamReader(TestData
				.getFoafAsStream());
		try {
			this.modelset.readFrom(isr);
		} catch (ModelRuntimeException e) {
			fail();
		} catch (IOException e) {
			fail();
		} finally {
			try {
				isr.close();
			} catch (IOException e) {
				fail();
			}
		}
	}

	@Test
	public void testReadFromReaderSyntax() {
		// TODO write test for testReadFromReaderSyntax
	}

	@Test
	public void testReadFromReaderSyntaxBaseURI() {
		// TODO write test for testReadFromReaderSyntaxBaseURI
	}

	/** test {@link ReificationSupport} */
	@Test
	public void testReification() {
		Statement stmt = this.modelset.createStatement(a, b, c);
		BlankNode blankNode = this.modelset.addReificationOf(stmt);
		assertTrue(this.modelset.contains(this.modelset.createStatement(
				blankNode, RDF.subject, a)));
		assertTrue(this.modelset.contains(this.modelset.createStatement(
				blankNode, RDF.predicate, b)));
		assertTrue(this.modelset.contains(this.modelset.createStatement(
				blankNode, RDF.object, c)));
		assertTrue(this.modelset.contains(this.modelset.createStatement(
				blankNode, RDF.type, RDF.Statement)));
	}

	@Test
	public void testRemoveAll() throws ModelRuntimeException {
		Model m = this.modelset.getModel(graphuri1);
		m.open();
		assertEquals(0, m.size());
		m.addStatement(a, b, c);
		assertTrue(m.size() > 0);
		this.modelset.removeAll();
		m.close();
		m = this.modelset.getModel(graphuri1);
		m.open();
		assertEquals(0, m.size());
		m.close();
	}

	@Test
	public void testRemoveModel() throws ModelRuntimeException {
		Model m = this.modelset.getModel(graphuri1);
		m.open();
		assertEquals(0, m.size());
		m.addStatement(a, b, c);
		assertTrue(m.size() > 0);
		this.modelset.removeModel(graphuri1);
		m.close();
		m = this.modelset.getModel(graphuri1);
		m.open();
		assertEquals(0, m.size());
		m.close();
	}

	@Test
	public void testRemoveModel2() throws ModelRuntimeException, IOException {
		Model rdfModel = RDF2Go.getModelFactory().createModel();
		rdfModel.open();

		rdfModel.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);
		rdfModel.close();

		Model naoModel = RDF2Go.getModelFactory().createModel();
		naoModel.open();
		naoModel.readFrom(TestData.getICALAsStream(), Syntax.RdfXml);
		naoModel.close();

		ModelSet mainRepository = RDF2Go.getModelFactory().createModelSet();
		mainRepository.open();

		URI rdfModelURI = new URIImpl("ontology:rdf");
		URI naoModelURI = new URIImpl("ontology:nao");

		Model model = mainRepository.getModel(rdfModelURI);
		model.open();
		rdfModel.open();
		model.addAll(rdfModel.iterator());
		rdfModel.close();
		model.close();

		model = mainRepository.getModel(naoModelURI);
		model.open();
		naoModel.open();
		model.addAll(naoModel.iterator());
		naoModel.close();
		model.close();

		log.debug("Removing model: " + rdfModelURI);
		mainRepository.removeModel(rdfModelURI);

		mainRepository.close();
	}

	@Test
	public void testRTGO_39() {
		for (ClosableIterator<Statement> i = this.modelset.iterator(); i
				.hasNext();) {
			i.next();
		}
	}

	@Test
	public void testSize() throws Exception {
		this.modelset.readFrom(TestData.getFoafAsStream());
		assertEquals("Size of foaf", 536, this.modelset.size());
	}

	@Test
	public void testSparqlAsk() throws Exception {
		this.addTestDataToModelSet();
		assertTrue(this.modelset
				.sparqlAsk("PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n "
						+ "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
						+ "ASK {?s rdf:type ?o}"));
	}

	@Test
	public void testSparqlConstruct() throws Exception {
		this.addTestDataToModelSet();
		ClosableIterable<? extends Statement> i = this.modelset
				.sparqlConstruct("PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n "
						+ "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
						+ "construct {?s rdf:type ?o} where {?s rdf:type ?o}");
		int size = TestUtils.countAndClose(i);
		assertEquals("sparql construct works getting types", 395, size);
	}

	@Test
	public void testSparqlDescribe() throws Exception {
		this.addTestDataToModelSet();
		ClosableIterable<? extends Statement> iterable = this.modelset
				.sparqlConstruct("PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n "
						+ "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
						+ "DESCRIBE <http://xmlns.com/foaf/0.1/thumbnail>");

		int size = TestUtils.countAndClose(iterable);
		assertEquals("sparql DESCRIBE", 8, size);
	}

	@Test
	public void testSparqlSelect() throws ModelRuntimeException {
		Model m = this.modelset.getDefaultModel();
		m.open();
		m.addStatement(a, b, c);

		QueryResultTable table = this.modelset
				.sparqlSelect("SELECT ?s ?p ?o WHERE { ?s ?p ?o . }");
		ClosableIterator<QueryRow> it = table.iterator();
		assertTrue(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
		it.close();
	}

	@Test
	public void testSparqlSelectFOAF() throws ModelRuntimeException,
			IOException {
		// URL foafURL = new URL("http://xmlns.com/foaf/0.1/20050603.rdf");
		// this.modelset.readFrom( foafURL.openStream() );
		InputStream in = TestData.getFoafAsStream();
		this.modelset.readFrom(in);

		QueryResultTable table = this.modelset
				.sparqlSelect("SELECT ?s ?o WHERE { ?s <" + RDFS.comment
						+ "> ?o . }");
		ClosableIterator<QueryRow> it = table.iterator();
		assertTrue(it.hasNext());
		it.next();
		assertTrue(it.hasNext());
		it.close();
	}

	@Test
	public void testWriteToOutputStream() throws Exception {
		this.addTestDataToModelSet();

		try {
			this.modelset.writeTo(new OutputStream() {
				@Override
				public void write(@SuppressWarnings("unused")
				int b) {
				}
			});
		} catch (ModelRuntimeException e) {
			fail();
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void testWriteToOutputStreamSyntax() throws Exception {
		this.addTestDataToModelSet();

		try {
			this.modelset.writeTo(new OutputStream() {
				@Override
				public void write(@SuppressWarnings("unused")
				int b) {
				}
			}, Syntax.Turtle);
		} catch (ModelRuntimeException e) {
			fail();
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void testWriteToWriter() throws Exception {
		this.addTestDataToModelSet();

		StringWriter sw = new StringWriter();
		try {
			this.modelset.writeTo(sw);
		} catch (ModelRuntimeException e) {
			fail();
		} catch (IOException e) {
			fail();
		}
		assertTrue(sw.getBuffer().toString().length() > 1000);
		sw.close();
	}

	@Test
	public void testWriteToWriterSyntax() {
		// TODO write test for testWriteToWriterSyntax
	}

	/**
	 * Add some test data to the modelsets . Test data will use graphuri1,
	 * graphuri2
	 * 
	 */
	protected void addTestDataToModelSet() throws Exception {
		assertNotNull("should be initialised by test method already",
				this.modelset);
		// add two models
		Model foaf = TestData.loadFoafBuffered(this.getModelFactory());
		Model m = this.modelset.getModel(graphuri1);
		m.open();
		m.addAll(foaf.iterator());
		m.close();

		Model ical = TestData.loadICALBuffered(this.getModelFactory());
		m = this.modelset.getModel(graphuri2);
		m.open();
		m.addAll(ical.iterator());
		assertTrue("the test data works", foaf.size() > 10);
		assertTrue("the test data works", ical.size() > 10);
		assertTrue("the modelset contains some triples",
				this.modelset.size() > 20);
		assertEquals(foaf.size() + ical.size(), this.modelset.size());
		// never close them when you loaded them buffered
		// foaf.close();
		// ical.close();
		m.close();
	}
	@Test
	public void testAddAll(){
		List<Statement> smt = new ArrayList<Statement>();
		smt.add(this.modelset.createStatement(subject, predicate, object));
		smt.add(this.modelset.createStatement(c, b, a));
		this.modelset.addAll(smt.iterator());
		assertTrue(this.modelset.contains(smt.get(0)));
		assertTrue(this.modelset.contains(smt.get(1)));
	}
	@Test
	public void testAddStatement(){
		Statement statement = this.modelset.createStatement(subject, predicate, object);
		this.modelset.addStatement(statement);
		assertTrue(this.modelset.contains(statement));
	}
	@Test
	public void testContains(){
		Statement statement = this.modelset.createStatement(subject, predicate, object);
		assertFalse(this.modelset.contains(statement));
		this.modelset.addStatement(statement);
		assertTrue(this.modelset.contains(statement));
	}
	@Test
	public void testCountStatements(){
		Statement statement = this.modelset.createStatement(a,subject, predicate, object);
		assertEquals(0, this.modelset.countStatements(this.modelset.createQuadPattern(a, subject, predicate, object)));
		this.modelset.addStatement(statement);
		assertEquals(1, this.modelset.countStatements(this.modelset.createQuadPattern(a, subject, predicate, object)));
	}
	@Test
	public void testCreateBlankNode(){
		this.modelset.createBlankNode();
		assertEquals("internalID",this.modelset.createBlankNode("internalID").getInternalID());
	}
	@Test
	public void testCreateDatatypeLiteral(){
		DatatypeLiteral datatypeLiteral = this.modelset.createDatatypeLiteral("literal", dt);
		assertEquals("literal^^" + dt,datatypeLiteral.asLiteral().toString());
	}
	@Test
	public void testCreateLanguageTagLiteral(){
		LanguageTagLiteral languageTagLiteral = this.modelset.createLanguageTagLiteral("literal", "en-us");
		assertEquals("literal@en-us",languageTagLiteral.toString());
	}
	@Test
	public void testCreatePlainLiteral(){
		PlainLiteral literal = this.modelset.createPlainLiteral("Something");
		assertEquals("Something", literal.getValue());
	}
	@Test
	public void testCreateQuadPattern(){
		QuadPattern quadPattern = this.modelset.createQuadPattern(a, subject, predicate, object);
		assertEquals(a, quadPattern.getContext());
		assertEquals(subject, quadPattern.getSubject());
		assertEquals(predicate, quadPattern.getPredicate());
		assertEquals(object, quadPattern.getObject());
	}
	@Test
	public void testDeleteReification(){
		Statement stmt = this.modelset.createStatement(a, subject, predicate, object);
		BlankNode blankNode = this.modelset.addReificationOf(stmt);
		assertTrue(this.modelset.containsStatements(a,blankNode, RDF.subject, subject));
		assertTrue(this.modelset.containsStatements(a,blankNode, RDF.predicate, predicate));
		assertTrue(this.modelset.containsStatements(a,blankNode, RDF.object, object));
		assertTrue(this.modelset.containsStatements(a,blankNode, RDF.type, RDF.Statement));
		this.modelset.deleteReification(blankNode);
		assertFalse(this.modelset.containsStatements(a,blankNode, RDF.subject, subject));
		assertFalse(this.modelset.containsStatements(a,blankNode, RDF.predicate, predicate));
		assertFalse(this.modelset.containsStatements(a,blankNode, RDF.object, object));
		assertFalse(this.modelset.containsStatements(a,blankNode, RDF.type, RDF.Statement));
		
		
		// test also the method where the resource is passed
		stmt = this.modelset.createStatement(a, b, a);
		URI u = this.modelset.newRandomUniqueURI();
		Resource reified = this.modelset.addReificationOf(stmt, u);
		assertEquals(u, reified);
		assertTrue(this.modelset.containsStatements(a,u, RDF.subject, subject));
		assertTrue(this.modelset.containsStatements(a,u, RDF.predicate, predicate));
		assertTrue(this.modelset.containsStatements(a,u, RDF.object, object));
		assertTrue(this.modelset.containsStatements(a,u, RDF.type, RDF.Statement));
		this.modelset.deleteReification(u);
		assertFalse(this.modelset.containsStatements(a,u, RDF.subject, subject));
		assertFalse(this.modelset.containsStatements(a,u, RDF.predicate, predicate));
		assertFalse(this.modelset.containsStatements(a,u, RDF.object, object));
		assertFalse(this.modelset.containsStatements(a,u, RDF.type, RDF.Statement));

	}
	@Test
	public void testDump(){
		this.modelset.dump();
	}
	@Test
	public void testGetAllReificationsOf(){
		Statement s = this.modelset.createStatement(a,b,c);
		BlankNode reificationBlankNode = this.modelset.addReificationOf(s);
		
		Collection<Resource> reifications = this.modelset.getAllReificationsOf(s);
		assertTrue( reifications.contains(reificationBlankNode) );
		assertEquals(1, reifications.size() );
	}
	@Test
	public void testHasReifications(){
		Statement stmt = this.modelset.createStatement(a, b, c);
		assertFalse(this.modelset.hasReifications(stmt));
		Resource r = this.modelset.addReificationOf(stmt);
		// we already verified in testAddReification() that the reification is in the store!
		assertTrue(this.modelset.hasReifications(stmt));
		// remove an essential part
		this.modelset.removeStatement(this.modelset.createStatement(r, RDF.subject, a));
		assertFalse(this.modelset.hasReifications(stmt));
	}
	@Test
	public void testIsEmpty(){
		assertTrue(this.modelset.isEmpty());
		this.modelset.addStatement(this.modelset.createStatement(a, b, c));
		assertFalse(this.modelset.isEmpty());
	}
	@Test
	public void testIsLocked(){
		assertFalse(this.modelset.isLocked());
		this.modelset.lock();
		assertTrue(this.modelset.isLocked());
	}
	@Test
	public void testIsOpen(){
		assertTrue(this.modelset.isOpen());
		this.modelset.close();
		assertFalse(this.modelset.isOpen());
	}
	@Test
	public void testIsValidURI(){
		assertTrue(this.modelset.isValidURI(a.toString()));
		String wrong = "error-.#'?.&4$%\\__%&$!";
		assertFalse("should not be a valid uri: "+wrong,this.modelset.isValidURI(wrong));
	}
	@Test
	public void testIterator(){
		ClosableIterator<Statement> iterator = this.modelset.iterator();
		while(iterator.hasNext()){
			iterator.next();
		}
	}
	@Test
	public void testLock(){
		this.modelset.lock();
		assertTrue(this.modelset.isLocked());
	}
	@Test
	public void testNewRandomUniqueURI(){
		URI newRandomUniqueURI = this.modelset.newRandomUniqueURI();
		assertTrue(this.modelset.isValidURI(newRandomUniqueURI.toString()));
	}
	@Test
	public void testQueryConstruct() throws Exception{
		InputStream in = TestData.getFoafAsStream();
		this.modelset.readFrom(in);
		// count the classes in foaf
		ClosableIterable<Statement> i = this.modelset.queryConstruct(
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"CONSTRUCT {?s rdf:type rdfs:Class.} " +
				"WHERE {?s rdf:type rdfs:Class}", 
				"SPARQL");
		assertEquals(12, Iterators.count(i.iterator()));

	}
	@Test
	public void testQuerySelect()throws Exception{
		InputStream in = TestData.getFoafAsStream();
		this.modelset.readFrom(in);
		// count the classes in foaf
		QueryResultTable i = this.modelset.querySelect(
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"SELECT ?s " +
				"WHERE {?s rdf:type rdfs:Class.}", 
				"SPARQL");
		assertEquals(12, Iterators.count(i.iterator()));
	}
	@Test
	public void testRemoveStatement(){
		Statement statement = this.modelset.createStatement(subject, predicate, object);
		this.modelset.addStatement(statement);
		assertTrue(this.modelset.contains(statement));
		this.modelset.removeStatement(statement);
		assertFalse(this.modelset.contains(statement));
		this.modelset.addStatement(a, subject, predicate, object);
		assertTrue(this.modelset.contains(this.modelset.createStatement(a, subject, predicate, object)));
		this.modelset.removeStatement(a, subject, predicate, object);
		assertFalse(this.modelset.contains(this.modelset.createStatement(a, subject, predicate, object)));
	}
	@Test
	public void testRemoveStatements(){
		this.modelset.addStatement(a, subject, predicate, object);
		assertTrue(this.modelset.contains(this.modelset.createStatement(a, subject, predicate, object)));
		this.modelset.removeStatements(this.modelset.createQuadPattern(a, subject, predicate, object));
		assertFalse(this.modelset.contains(this.modelset.createStatement(a, subject, predicate, object)));
		
		this.modelset.addStatement(a, subject, predicate, object);
		assertTrue(this.modelset.contains(this.modelset.createStatement(a, subject, predicate, object)));
		this.modelset.removeStatements(a, subject, predicate, object);
		assertFalse(this.modelset.contains(this.modelset.createStatement(a, subject, predicate, object)));
	}
	@Test
	public void testSerialize()throws Exception{
		this.modelset.readFrom(TestData.getFoafAsStream());
		String serialize = this.modelset.serialize(Syntax.RdfXml);
		ModelSet m1 = getModelFactory().createModelSet();
		m1.open();
		m1.readFrom(new StringReader(serialize), Syntax.RdfXml);
		assertEquals(this.modelset.size(), m1.size());
		m1.close();	
	}
	@Test
	public void testUnlock(){
		this.modelset.unlock();
		assertFalse(this.modelset.isLocked());
		this.modelset.lock();
		assertTrue(this.modelset.isLocked());
		this.modelset.unlock();
		assertFalse(this.modelset.isLocked());
	}
	@Test
	public void testUpdate(){
		Model remove = RDF2Go.getModelFactory().createModel();
		remove.open();
		Model add = RDF2Go.getModelFactory().createModel();
		add.open();
		add.addStatement(a, b, c);
		DiffReader diff = new DiffImpl(add.iterator(), remove.iterator());
		add.close();
		remove.close();

		this.modelset.update(diff);
	}	
}
