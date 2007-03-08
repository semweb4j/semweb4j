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
package org.ontoware.rdf2go.impl.sesame2;

import junit.framework.TestCase;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;

/**
 * Hello, this is the main TestCase for ModelImplSesame 
 * (the sesame2 specific implementation of the rdf2go adapter interface) 
 * 
 * This contains all testcase of the generic ModelTest (located in rdf2go/test-src)
 * which did not pass, because the underlying implementation has a bug.
 * 
 * So, these tests did not pass because of bugs in Sesame2.
 * 
 *  !!!!!!!!!!!!! see ModelImplSesame.java for a Warning regarding Sesame2 transactions !!!!!!!!!!!!!
 *  
 * @author Benjamin Heitmann <benjamin.heitmann@deri.org>
 *
 */
public class ModelImplSesameUniqueTest extends TestCase {

	public static org.ontoware.rdf2go.model.node.URI a = new URIImpl("test://test/a");

	public static org.ontoware.rdf2go.model.node.URI b = new URIImpl("test://test/b");

	public static org.ontoware.rdf2go.model.node.URI c = new URIImpl("test://test/c");

	public static org.ontoware.rdf2go.model.node.URI subject = new URIImpl("test://test/a");

	public static org.ontoware.rdf2go.model.node.URI predicate = new URIImpl("test://test/b");

	public static org.ontoware.rdf2go.model.node.URI object = new URIImpl("test://test/c");

	public static org.ontoware.rdf2go.model.node.URI dt = new URIImpl("test://somedata/dt");

	protected RepositoryModel model;
	
//	private Resource sesameSubject;
//	private org.openrdf.model.URI sesamePredicate;
//	private Value sesameObject;
//	private Resource sesameContext;
//	private TypeConversion conversion;
//	private Repository sesameRepository;
//	private Connection sesameConnection;
	
	public static org.ontoware.rdf2go.model.node.URI context = new URIImpl("test://test/context");
	
	protected void setUp() throws Exception {
		super.setUp();
		this.model = new RepositoryModel(context, false);
	}

	protected void tearDown() throws Exception {
		this.model.close();
		super.tearDown();	
	}
	
	
	public void testSesameAutoCommitMode() throws ModelRuntimeException {
		
//			this.model.getSesameConnection().setAutoCommit(true);
//			model.addStatement(subject, predicate, "Test");
//			this.model.getSesameConnection().commit();
//			this.model.getSesameConnection().setAutoCommit(true);
			
			assertTrue(!(model.isLocked()));
			model.lock();
			assertTrue(model.isLocked());
			model.addStatement(subject, predicate, "Test", "DE");
			model.unlock();
			assertTrue(!(model.isLocked()));
	
			assertTrue(model.contains(subject, predicate, model.createLanguageTagLiteral("Test", "DE")));
			
	}
	
	
	/**
	 * TODO: rdf2go inconsistency: sesame returns language tags not as they where given to sesame, 
	 * but as toLowerCase(). So testing of equalness to the original language tag fails. 
	 * Maybe we should adopt this behaviour in rdf2go.  
	 */
	public void testGetStatementWithLiteralAndNoLanguageTagURIURIString() throws Exception {
		model.addStatement(subject, predicate, "Test", "DE");
		model.addStatement(subject, predicate, "Test");

		ClosableIterator<? extends Statement> sit = model.findStatements( Variable.ANY, predicate, Variable.ANY);
		assertTrue(sit.hasNext());
		
		while (sit.hasNext()) {
			Statement s = sit.next();
			assertEquals(s.getSubject(), subject);
			assertEquals(s.getPredicate(), predicate);
			
			if (s.getObject() instanceof LanguageTagLiteral) {
				assertEquals( ((LanguageTagLiteral)  (s.getObject())).getValue(), "Test");
				assertEquals( ((LanguageTagLiteral)  (s.getObject())).getLanguageTag().toLowerCase(), new String("DE").toLowerCase());
			} else {
				assertTrue(s.getObject() instanceof PlainLiteral);
				assertTrue( ((PlainLiteral)  (s.getObject())).getValue().equals("Test"));
			}		
		}
		
		assertFalse(sit.hasNext());
		sit.close();
	}
	
	/**
	 * TODO: sesame bug: Seems like SPARQL is right now really not supported as this throws: 
	 * 
	 * Org.openrdf.querylanguage.UnsupportedQueryLanguageException: 
	 * Org.openrdf.util.reflect.NoSuchTypeException: No type found for key: SPARQL
	 * 
	 */
	public void testQuery() throws Exception {
//		String query = "PREFIX \t:\t<test://test/>\n"
//				+ "CONSTRUCT { ?s ?p \"Test2\" } WHERE { ?s ?p \"Test2\" }";
		// System.out.println(query);
		
		String query = "PREFIX : <test://test/> CONSTRUCT { ?s ?p \"Test2\" } WHERE { ?s ?p \"Test2\" }";
		
		BlankNode bNode = model.createBlankNode();
		model.addStatement(subject, predicate, "Test1");
		model.addStatement(subject, predicate, "Test2");
		model.addStatement(bNode, predicate, "Test2");
		ClosableIterator<Statement> iter = model.sparqlConstruct(query).iterator();

		assertTrue(iter.hasNext());
		Statement s = iter.next();
		assertEquals(predicate, s.getPredicate());
		assertEquals( s.getObject(), "Test2");
		assertTrue(iter.hasNext());
		s = iter.next();
		assertEquals(predicate, s.getPredicate());
		assertEquals( s.getObject(), "Test2");
		assertFalse(iter.hasNext());
		iter.close();
	}
	
	/**
	 * TODO: sesame bug: Seems like SPARQL is right now really not supported as this throws: 
	 * 
	 * Org.openrdf.querylanguage.UnsupportedQueryLanguageException: 
	 * Org.openrdf.util.reflect.NoSuchTypeException: No type found for key: SPARQL
	 * 
	 */
	public void testSelectQuery() throws Exception {
		String query = "PREFIX \t:\t<test://test/>\n" + "SELECT  ?s ?p \n"
				+ "WHERE { ?s ?p \"Test2\" }";
		// System.out.println(query);
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
		assertTrue(subject.equals(row.getValue("s")) || bNode.equals(row.getValue("s")));
		row = it.next();
		assertEquals(predicate, row.getValue("p"));
		assertTrue(subject.equals(row.getValue("s")) || bNode.equals(row.getValue("s")));
		it.close();
	}

	
	/**
	 * TODO: sesame bug? when trying to remove a statement which contains a LanguageTagLiteral Sesame gets caught in a 
	 * transaction lock. Removing of other statements succeds, see the tests below.
	 */
	public void testRemoveStatementLanguageTaggedLiteral() throws Exception {
		model.addStatement(subject, predicate, "Test", "DE");
		assertTrue(model.contains(subject, predicate, model.createLanguageTagLiteral("Test","DE")));
		ClosableIterator iter = model.iterator();
		assertTrue(iter.hasNext());
		while (iter.hasNext())
			iter.next();
		iter.close();
		model.removeStatement(subject, predicate, "Test", "DE");
		assertFalse(model.contains(subject, predicate, model.createLanguageTagLiteral("Test","DE")));
		
	}

	/**
	 * TODO: sesame bug? when trying to remove a statement which contains a DatatypedLiteral Sesame gets caught in a 
	 * transaction lock. Removing of other statements succeds, see the tests below.
	 */
	public void testRemoveStatementDatatypedLiteral() throws Exception {
		model.addStatement(subject, predicate, "Test", dt);
		assertTrue(model.contains(subject, predicate, model.createDatatypeLiteral("Test",dt)));
		
		ClosableIterator iter = model.iterator();
		assertTrue(iter.hasNext());
		while (iter.hasNext())
			iter.next();
		model.removeStatement(subject, predicate, "Test", dt);
		assertFalse(model.contains(subject, predicate, model.createDatatypeLiteral("Test",dt)));
		iter.close();
	}


	/**
	 * this test passes, Demonstration of successfull removing of statements
	 */
	public void testRemoveStatementSimple() throws Exception {
		model.addStatement(subject, predicate, object);
		ClosableIterator<? extends Statement> iter = model.findStatements(subject, predicate, object);
		assertTrue(iter.hasNext());
		while (iter.hasNext())
			iter.next();

		model.removeStatement(subject, predicate, object);
		assertFalse(model.findStatements(subject, predicate, object).hasNext());
		iter.close();
	}

	/**
	 * this test passes, Demonstration of successfull removing of statements
	 */
	public void testRemoveStatementURIURIString() throws Exception {
		model.addStatement(subject, predicate, "Test");

		ClosableIterator iter = model.findStatements( new TriplePatternImpl(subject,predicate, "Test"));
		assertTrue(iter.hasNext());
		while (iter.hasNext())
			iter.next();

		model.removeStatement(subject, predicate, "Test");
		assertFalse(model.contains(subject,predicate, "Test"));
		iter.close();
	}

	/**
	 * this test passes, Demonstration of successfull removing of statements
	 */
	public void testAddRemoveStatementBlankNode() throws Exception {

		BlankNode blankNode = model.createBlankNode();

		model.addStatement(subject, predicate, blankNode);
		ClosableIterator<? extends Statement> sit = model.findStatements(subject, predicate, blankNode);
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
		sit.close();
	}

	/**
	 * this test passes, Demonstration of successfull removing of statements
	 */
	public void testAddRemoveStatementBlankNode2() throws Exception {

		BlankNode blankNode = model.createBlankNode();

		model.addStatement(blankNode, predicate, object);
		ClosableIterator<? extends Statement> sit = model.findStatements(blankNode, predicate, object);
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
		assertFalse(model.contains( blankNode, predicate, object));
		sit.close();
	}

	/**
	 * this test passes, Demonstration of successfull removing of statements
	 */
	public void testAddRemoveStatementObjectURIString() throws Exception {

		BlankNode blankNode = model.createBlankNode();

		model.addStatement(blankNode, predicate, "Test");
		ClosableIterator<? extends Statement> sit = model.findStatements(new TriplePatternImpl(blankNode,predicate, "Test"));
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals("blank node equality", blankNode, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(s.getObject(), "Test");
		}

		model.removeStatement((BlankNode) blankNode, predicate, "Test");
		assertFalse(model.findStatements(new TriplePatternImpl((BlankNode) blankNode,predicate, "Test")).hasNext());
		sit.close();
	}

	/**
	 * this test passes, Demonstration of successfull removing of statements
	 */
	public void testAddRemoveStatementURIObjectURIObject() throws Exception {

		BlankNode blankNodeSubject = model.createBlankNode();
		BlankNode blankNodeObject = model.createBlankNode();

		model.addStatement(blankNodeSubject, predicate, blankNodeObject);
		ClosableIterator<? extends Statement> sit = model.findStatements(blankNodeSubject, predicate, blankNodeObject);
		assertTrue(sit.hasNext());
		while (sit.hasNext()) {
			// should be just one
			Statement s = sit.next();
			assertEquals(blankNodeSubject, s.getSubject());
			assertEquals(predicate, s.getPredicate());
			assertEquals(blankNodeObject, s.getObject());
		}

		model.removeStatement(blankNodeSubject, predicate, blankNodeObject);
		assertFalse(model.findStatements(blankNodeSubject, predicate, blankNodeObject).hasNext());
		sit.close();
	}
	
	
	// example of a test which directly probes inside the sesame2 Repository
	// you need to use ModelImplSesame and not the general interface to be able to .getSesameConnection().

	
//	public void testAddStatementVerySimple() throws Exception {
//		
//		sesameRepository = (Repository) model.getUnderlyingModelImplementation();
//		sesameConnection = (Connection) model.getSesameConnection();
//		conversion = new TypeConversion(this.sesameRepository);
//		
//		model.addStatement(subject, predicate, object);
//		
//		sesameSubject = (Resource) conversion.java2sesame(subject);
//		sesamePredicate = (org.openrdf.model.URI) conversion.java2sesame(predicate);
//		sesameObject = (Value) conversion.java2sesame(object);
//		sesameContext = (org.openrdf.model.URI) conversion.java2sesame(context);
//		
//		
//		
//		assertTrue(this.sesameConnection.hasStatement(sesameSubject, sesamePredicate, sesameObject, sesameContext));
//		
//		Iterator<Statement> sit = model.findStatements(subject, predicate, object).iterator();
//		assertNotNull(sit);
//		assertTrue(sit.hasNext());
//		
//		
//		while (sit.hasNext()) {
//			// should be just one
//			Statement s2 = sit.next();
//			
//			org.ontoware.rdf2go.model.node.URI s2s = (org.ontoware.rdf2go.model.node.URI) s2.getSubject();
//			org.ontoware.rdf2go.model.node.URI s2p = (org.ontoware.rdf2go.model.node.URI) s2.getPredicate();
//			org.ontoware.rdf2go.model.node.URI s2o = (org.ontoware.rdf2go.model.node.URI) s2.getObject();
//			
//			assertEquals(subject, s2s);
//			assertEquals(predicate, s2p);
//			assertEquals(object, s2o);
//		}
//		
//	}
	
}
