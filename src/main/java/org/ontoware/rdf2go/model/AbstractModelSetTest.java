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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.testdata.TestData;
import org.ontoware.rdf2go.util.Iterators;
import org.ontoware.rdf2go.util.ModelUtils;
import org.ontoware.rdf2go.vocabulary.RDFS;

/**
 * Use this to test implementations of ModelSet. Overwrite the class and
 * implement the "setup" method, initializing modelset and modelfactory.
 * 
 * @author voelkel
 * @author sauermann
 */
public abstract class AbstractModelSetTest extends TestCase {

	/**
	 * 
	 * @return a fresh ModelSet, to be tested
	 */
	public abstract ModelFactory getModelFactory();

	/**
	 * 
	 */
	public AbstractModelSetTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public AbstractModelSetTest(String arg0) {
		super(arg0);
	}

	public static URI graphuri1 = new URIImpl("urn:first");

	public static URI graphuri2 = new URIImpl("urn:second");

	/**
	 * there are two graphs in the default test data
	 */
	public static int TESTGRAPHCOUNT = 2;

	public static URI a = new URIImpl("test://test/a");

	public static URI b = new URIImpl("test://test/b");

	public static URI c = new URIImpl("test://test/c");

	public static URI subject = new URIImpl("test://test/a");

	public static URI predicate = new URIImpl("test://test/b");

	public static URI object = new URIImpl("test://test/c");

	public static URI dt = new URIImpl("test://somedata/dt");

	protected ModelSet modelset = null;

	public void setUp() {
		// done by each test, to allow for different Reasoning settings
	}

	public void tearDown() throws Exception {
		if (modelset != null) {
			modelset.close();
			modelset = null;
		}
		System.gc();
	}

	/**
	 * Add some test data to the modelsets . Test data will use graphuri1,
	 * graphuri2
	 * 
	 */
	protected void addTestDataToModelSet() throws Exception {
		assertNotNull("should be initialised by test method already",modelset);
		// add two models
		Model foaf = TestData.loadFoafBuffered(getModelFactory());
		Model m = modelset.getModel(graphuri1);
		m.open();
		m.addAll(
				foaf.iterator());
		Model ical = TestData.loadICALBuffered(getModelFactory()); 
		m = modelset.getModel(graphuri2);
		m.open();
		m.addAll(
				ical.iterator());
		assertTrue("the test data works", foaf.size() > 10);
		assertTrue("the test data works", ical.size() > 10);
		assertTrue("the modelset contains some triples", modelset.size() > 20);
		assertEquals(foaf.size() + ical.size(), modelset.size() );
		// never close them when you loaded them buffered
		//foaf.close();
		//ical.close();
	}

	/**
	 * The default model as defined in the SPARQL semantics. Each data set
	 * consisting of Models/Graphs has one default graph.
	 * 
	 */
	public void testGetDefaultModel() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		Model defaultModel = modelset.getDefaultModel();
		assertNotNull(defaultModel);
		assertNull("the default model must have the NULL context", defaultModel
				.getContextURI());
	}

	public void testGetModel() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		addTestDataToModelSet();
		modelset.close();
		Model m = modelset.getModel(graphuri1);
		m.open();
		assertNotNull(m);
		assertTrue("graph1 contains less than 10 statements, it contains: "
				+ m.size(), m.size() > 10);
		m = modelset.getModel(graphuri2);
		m.open();
		assertNotNull(m);
		assertTrue("graph2 contains more than 10 statements, it contains: "
				+ m.size(), m.size() > 10);
	}

	public void testRemoveModel() throws ModelRuntimeException {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		Model m = modelset.getModel(graphuri1);
		m.open();
		assertEquals(0, m.size());
		m.addStatement(a, b, c);
		assertTrue(m.size() > 0);
		modelset.removeModel(graphuri1);
		m = modelset.getModel(graphuri1);
		m.open();
		assertEquals(0, m.size());
	}

	public void testRemoveAll() throws ModelRuntimeException {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		Model m = modelset.getModel(graphuri1);
		m.open();
		assertEquals(0, m.size());
		m.addStatement(a, b, c);
		assertTrue(m.size() > 0);
		modelset.removeAll();
		m = modelset.getModel(graphuri1);
		m.open();
		assertEquals(0, m.size());
	}

	public void testGetModels() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		addTestDataToModelSet();
		Iterator<? extends Model> i = modelset.getModels();
		ArrayList<Model> m = new ArrayList<Model>();
		Iterators.addAll(i, m);
		assertEquals(TESTGRAPHCOUNT, m.size());
	}

	public void testFindStatements() throws ModelRuntimeException {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		Model m = modelset.getModel(graphuri1);
		m.open();
		m.addStatement(a, b, c);
		ClosableIterator<? extends Statement> it = modelset.findStatements(
				graphuri1, a, b, c);
		assertTrue(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
	}

	public void testContainsStatements() throws ModelRuntimeException {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		assertFalse(modelset.containsStatements(graphuri1, a, b, c));
		Model model = modelset.getModel(graphuri1);
		model.open();
		model.addStatement(a, b, c);
		assertTrue(modelset.containsStatements(graphuri1, a, b, c));

		// also with wildcard
		assertTrue(modelset.containsStatements(Variable.ANY, a, b, c));

		model.removeStatement(a, b, c);
		//		
		assertFalse(modelset.containsStatements(graphuri1, a, b, c));
	}

	public void testSparqlAsk() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		// TODO add test when sparql ask is availalbe fail("Not yet
		// implemented");
	}

	public void testSparqlConstruct() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		// TODO add test when sparql construct is availalbe fail("Not yet
		// implemented");
		addTestDataToModelSet();
		ClosableIterable<? extends Statement> i = modelset
				.sparqlConstruct("PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n "
						+ "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
						+ "construct {?s rdf:type ?o} where {?s rdf:type ?o}");
		int size = ModelUtils.size(i);
		assertEquals("sparql construct works getting types", 395, size);
	}

	public void testSparqlDescribe() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		// TODO add test when sparql describe fail("Not yet implemented");
	}

	public void testSparqlSelect() throws ModelRuntimeException {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		Model m = modelset.getDefaultModel();
		m.open();
		m.addStatement(a, b, c);

		QueryResultTable table = modelset
				.sparqlSelect("SELECT ?s ?p ?o WHERE { ?s ?p ?o . }");
		ClosableIterator<QueryRow> it = table.iterator();
		assertTrue(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
	}

	public void testSparqlSelectFOAF() throws ModelRuntimeException,
			IOException {
		modelset = getModelFactory().createModelSet();
		modelset.open();

		// URL foafURL = new URL("http://xmlns.com/foaf/0.1/20050603.rdf");
		// modelset.readFrom( foafURL.openStream() );
		InputStream in = TestData.getFoafAsStream();
		modelset.readFrom(in);

		QueryResultTable table = modelset
				.sparqlSelect("SELECT ?s ?o WHERE { ?s <" + RDFS.comment
						+ "> ?o . }");
		ClosableIterator<QueryRow> it = table.iterator();
		assertTrue(it.hasNext());
		it.next();
		assertTrue(it.hasNext());
	}

	public void testGetUnderlyingModelSetImplementation() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		assertNotNull(modelset.getUnderlyingModelSetImplementation());
	}

	public void testCreateURI() throws ModelRuntimeException {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		URI u = modelset.createURI("urn:test:x");
		assertNotNull(u);
	}

	public void testDump() {
		modelset = getModelFactory().createModelSet();
		modelset.open();

 		// TODO write test here
	}

	public void testClose() {
		modelset = getModelFactory().createModelSet();
		modelset.open();

		// TODO write test here
	}

	public void testGetModelURIs() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		addTestDataToModelSet();
		Iterator<URI> l = modelset.getModelURIs();
		ArrayList<URI> test = new ArrayList<URI>();
		test.add(graphuri1);
		test.add(graphuri2);
		ArrayList<URI> uris = new ArrayList<URI>();
		Iterators.addAll(l, uris);
		assertEquals(2, uris.size());
		test.removeAll(uris);
		assertEquals(0, test.size());
	}

	public void testOpen() {
		modelset = getModelFactory().createModelSet();
		modelset.open();

		// TODO write test here
	}

	public void testReadFromReader() {
		modelset = getModelFactory().createModelSet();
		modelset.open();

		InputStreamReader isr = new InputStreamReader(TestData
				.getFoafAsStream());
		try {
			modelset.readFrom(isr);
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

	public void testReadFromReaderSyntax() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		
		// TODO write test here
	}

	public void testReadFromInputStream() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		InputStream in = TestData.getFoafAsStream();
		try {
			modelset.readFrom(in);
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

	public void testReadFromInputStreamIntoDefaultModel() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		modelset.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);

		assertTrue(modelset.size() > 0);
		// the modelset loads into the default model
		assertFalse(
				"as we loaded data only in default model, there are no other models",
				modelset.getModels().hasNext());

		// the default model has something
		Model m = modelset.getDefaultModel();
		m.open();
		assertEquals("the default model has foaf", 536, m.size());

		Iterator i = m.iterator();
		int sizeByIterator = ModelUtils.size(i);
		assertEquals("the default model can use an iterator", 536,
				sizeByIterator);
	}
	
	public void testReadFromInputStreamIntoNamedModel() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		Model m = modelset.getModel(graphuri1);
		m.open();
		m.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);
		m.close();
		
		// add data to second graph
		m = modelset.getModel(graphuri2);
		m.open();
		m.readFrom(TestData.getICALAsStream(), Syntax.RdfXml);
		m.close();
		
		//assertEquals("The modelset contains exactly loaded triples.", TestData.FOAF_SIZE + TestData.ICALSIZE,				 modelset.size());
		// the modelset loads into the named graph
		int modelcount = ModelUtils.size(modelset.getModels());
		assertEquals(
				"there are two models",
				2, modelcount);
		// no default model
		m = modelset.getDefaultModel();
		m.open();
		assertEquals("nothing in default model", 0, m.size());

		// get the named model1
		m = modelset.getModel(graphuri1);
		m.open();
		assertEquals("the named graph model has foaf", TestData.FOAFSIZE, m.size());
		// at some point, iterators were broken here, so test if it returns one
		Iterator i = m.iterator();
		int sizeByIterator = ModelUtils.size(i);
		assertEquals("the model supports iterators", TestData.FOAFSIZE,
				sizeByIterator);
		
		// get the named model2
		m = modelset.getModel(graphuri2);
		m.open();
		assertEquals("the named graph model has ical", TestData.ICALSIZE, m.size());

	}

	public void testReadFromInputStreamSyntax() {
		modelset = getModelFactory().createModelSet();
		modelset.open();

		// TODO write test here
	}

	public void testSize() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		modelset.readFrom(TestData.getFoafAsStream());
		assertEquals("Size of foaf", 536, modelset.size());
	}

	public void testWriteToWriter() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		addTestDataToModelSet();

		StringWriter sw = new StringWriter();
		try {
			modelset.writeTo(sw);
		} catch (ModelRuntimeException e) {
			fail();
		} catch (IOException e) {
			fail();
		}
		assertTrue( sw.getBuffer().toString().length() > 1000);
	}

	public void testWriteToWriterSyntax() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		
		// TODO write test
	}

	public void testWriteToOutputStream() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		addTestDataToModelSet();

		try {
			modelset.writeTo(System.out);
		} catch (ModelRuntimeException e) {
			fail();
		} catch (IOException e) {
			fail();
		}
	}

	public void testWriteToOutputStreamSyntax() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		addTestDataToModelSet();

		try {
			modelset.writeTo(System.out, Syntax.Turtle);
		} catch (ModelRuntimeException e) {
			fail();
		} catch (IOException e) {
			fail();
		}
	}

	public void testLoadDataIntoDefaultModel() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		modelset.readFrom(TestData.getFoafAsStream());
		assertTrue(modelset.size() > 10);
		// check default model
		Model def = modelset.getDefaultModel();
		def.open();
		assertTrue( def.isOpen() );
		assertNotNull(def);
		assertTrue("default model has something", def.size() > 10);
		ArrayList<URI> uris = new ArrayList<URI>();
		Iterators.addAll(modelset.getModelURIs(), uris);
		assertEquals("default model has no context uri", 0, uris.size());
		def.close();
	}

	public void testCopyModelSets() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		addTestDataToModelSet();
		// add a statement in the default context
		Model model = modelset.getDefaultModel();
		model.open();
		model.addStatement(subject, predicate, object);
		ModelSet m = getModelFactory().createModelSet();
		m.open();
		ModelUtils.copy(modelset, m);
		assertEquals("copied all from source to target", modelset.size(), m
				.size());
		m.close();
	}

	public void testAddDataFromFileByCopying() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		modelset.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);
		assertTrue(modelset.size() > 0);
		
		ModelSet target = getModelFactory().createModelSet();
		target.open();

		ModelUtils.copy(modelset, target);
		assertEquals(modelset.size(), target.size());
	}

	/**
	 * above test sometimes failed, here is a trace into the details that can
	 * happen
	 * 
	 * @throws Exception
	 */
	public void testAddDataFromFileByCopyingMoreDetail() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		modelset.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);

		assertTrue(modelset.size() > 0);
		// the modelset loads into the default model
		assertFalse(
				"as we loaded data only in default model, there are no other models",
				modelset.getModels().hasNext());

		// the default model has something
		Model m = modelset.getDefaultModel();
		m.open();
		assertEquals("the default model has foaf", 536, m.size());
		int sizeByIterator = ModelUtils.size(m);
		assertEquals("the default model can use an iterator", 536,
				sizeByIterator);
		m.close();
		
		ModelSet target = getModelFactory().createModelSet();
		target.open();
		
		// Es liegt also daran, das modelset.read die daten in irgendein nicht
		// mehr
		// zugreifbared modell mit dem context "null" steckt. Aber wenn man ein
		// model
		// mti "null" macht, ist es leer. Da stimmt was nicht mit sesame.
		ModelUtils.copy(modelset, target);

		assertEquals(modelset.size(), target.size());
		m.close();
	}

}
