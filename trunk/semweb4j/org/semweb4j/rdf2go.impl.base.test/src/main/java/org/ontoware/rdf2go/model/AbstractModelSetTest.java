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

	ModelSet modelset = null;

	public void setUp() {
		// done by each test, to allow for different Reasoning settings
	}
	
	public void tearDown() throws Exception {
		if (modelset != null) {
			modelset.close();
			modelset = null;
		}
	}

	/**
	 * Add some test data to the modelsets . Test data will use graphuri1,
	 * graphuri2
	 * 
	 */
	protected void addTestDataToModelSet() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		// add two models
		modelset.getModel(graphuri1).addAll(
				TestData.loadFoafBuffered(getModelFactory()).iterator());
		modelset.getModel(graphuri2).addAll(
				TestData.loadICALBuffered(getModelFactory()).iterator());
        assertTrue("the test data works", TestData.loadFoafBuffered(getModelFactory()).size() > 10);
        assertTrue("the test data works", TestData.loadICALBuffered(getModelFactory()).size() > 10);
        assertTrue("the modelset contains some triples", modelset.size()> 20);
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
		assertNull("the default model must have the NULL context", defaultModel.getContextURI() );
	}

	public void testGetModel() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		addTestDataToModelSet();
        modelset.close();
		Model m = modelset.getModel(graphuri1);
		assertNotNull(m);
		assertTrue("graph1 contains less than 10 statements, it contains: "+m.size(), m.size() > 10);
		m = modelset.getModel(graphuri2);
		assertNotNull(m);
		assertTrue("graph2 contains more than 10 statements, it contains: "+m.size(), m.size() > 10);
	}

	public void testRemoveModel() throws ModelRuntimeException {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		assertEquals(0, modelset.getModel(graphuri1).size());
		modelset.getModel(graphuri1).addStatement(a, b, c);
		assertTrue(modelset.getModel(graphuri1).size() > 0);
		modelset.removeModel(graphuri1);
		assertEquals(0, modelset.getModel(graphuri1).size());
	}

	public void testRemoveAll() throws ModelRuntimeException {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		assertEquals(0, modelset.getModel(graphuri1).size());
		modelset.getModel(graphuri1).addStatement(a, b, c);
		assertTrue(modelset.getModel(graphuri1).size() > 0);
		modelset.removeAll();
		assertEquals(0, modelset.getModel(graphuri1).size());
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
		modelset.getModel(graphuri1).addStatement(a, b, c);
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
        ClosableIterable<? extends Statement> i = modelset.sparqlConstruct(
            "PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n " +
            "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "construct {?s rdf:type ?o} where {?s rdf:type ?o}");
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
		modelset.getDefaultModel().addStatement(a, b, c);

		QueryResultTable table = modelset
				.sparqlSelect("SELECT ?s ?p ?o WHERE { ?s ?p ?o . }");
		ClosableIterator<QueryRow> it = table.iterator();
		assertTrue(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
	}

	public void testSparqlSelectFOAF() throws ModelRuntimeException, IOException {
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

	public void testGetUnderlyingModelImplementation() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		assertNotNull(modelset.getUnderlyingModelImplementation());
	}

	public void testSetUnderlyingModelImplementation() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		// TODO imagine a test ...fail("Not yet implemented");
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
		// TODO: invent a test fail("Not yet implemented");
	}

	public void testClose() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		// TODO: invent a test fail("Not yet implemented");
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
		// TODO invent a test case   fail("Not yet implemented");
	}

	public void testReadFromReader() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		fail("Not yet implemented");
	}

	public void testReadFromReaderSyntax() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		fail("Not yet implemented");
	}

	public void testReadFromInputStream() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		fail("Not yet implemented");
	}
	
    public void testReadFromInputStreamIntoDefaultModel() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		modelset.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);

        assertTrue(modelset.size() > 0);
        // the modelset loads into the default model 
        assertFalse("as we loaded data only in default model, there are no other models",
        		modelset.getModels().hasNext());

        // the default model has something
        Model m = modelset.getDefaultModel();
        assertEquals("the default model has foaf", 536, m.size());
        
        Iterator i = m.iterator();
        int sizeByIterator = ModelUtils.size(i);
        assertEquals("the default model can use an iterator", 536, sizeByIterator);    
	}

	public void testReadFromInputStreamSyntax() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		fail("Not yet implemented");
	}

	public void testSize() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
        modelset.readFrom(TestData.getFoafAsStream());
        assertEquals("Size of foaf", 536, modelset.size());
	}

	public void testWriteToWriter() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		fail("Not yet implemented");
	}

	public void testWriteToWriterSyntax() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		fail("Not yet implemented");
	}

	public void testWriteToOutputStream() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		fail("Not yet implemented");
	}

	public void testWriteToOutputStreamSyntax() {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		fail("Not yet implemented");
	}


	public void testLoadDataIntoDefaultModel() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		modelset.readFrom(TestData.getFoafAsStream());
		assertTrue(modelset.size() > 10);
		// check default model
		Model def = modelset.getDefaultModel();
		assertNotNull(def);
		assertTrue("default model has something", def.size() > 10);
		ArrayList<URI> uris = new ArrayList<URI>();
		Iterators.addAll(modelset.getModelURIs(), uris);
		assertEquals("default model has no context uri", 0, uris.size());
	}

	public void testCopyModelSets() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		addTestDataToModelSet();
		// add a statement in the default context
		modelset.getDefaultModel().addStatement(subject, predicate, object);
		ModelSet m = getModelFactory().createModelSet();
		ModelUtils.copy(modelset, m);
		assertEquals("copied all from source to target", modelset.size(), m
				.size());
	}

	public void testAddDataFromFileByCopying() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		modelset.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);
		assertTrue( modelset.size() > 0);
		
		ModelUtils.copy(modelset, modelset);
		assertEquals(modelset.size(), modelset.size());
	}
    
    /**
     * above test sometimes failed, here is a trace into the details that
     * can happen
     * @throws Exception
     */
    public void testAddDataFromFileByCopyingMoreDetail() throws Exception {
		modelset = getModelFactory().createModelSet();
		modelset.open();
		modelset.readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);

        assertTrue(modelset.size() > 0);
        // the modelset loads into the default model 
        assertFalse("as we loaded data only in default model, there are no other models",
        		modelset.getModels().hasNext());

        // the default model has something
        Model m = modelset.getDefaultModel();
        assertEquals("the default model has foaf", 536, m.size());
        int sizeByIterator = ModelUtils.size(m);
        assertEquals("the default model can use an iterator", 536, sizeByIterator);    
        //Es  liegt  also  daran,  das  modelset.read die daten in irgendein nicht mehr
        //zugreifbared  modell  mit  dem context "null" steckt. Aber wenn man ein model
        //mti "null" macht, ist es leer. Da stimmt was nicht mit sesame.
        ModelUtils.copy(modelset, modelset);
        assertEquals(modelset.size(), modelset.size());
    }

}
