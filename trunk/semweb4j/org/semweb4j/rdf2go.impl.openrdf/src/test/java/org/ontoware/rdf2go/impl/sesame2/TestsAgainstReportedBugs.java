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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.TestCase;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.testdata.TestData;
import org.openrdf.rdf2go.RepositoryModel;

/**
 * Tests agains reported bugs, to ensure that they really are fixed
 * 
 * TODO: Leo: this should move into the normal tests of the classes.
 *  
 * @author Benjamin Heitmann <benjamin.heitmann@deri.org>
 *
 */
public class TestsAgainstReportedBugs extends TestCase {

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

// TODO this junit annotation does not seem to work... because I need to use a boolean as a workaround 	
//	@Test(expected=RuntimeException.class)
	public void testConstructorCheckForEmptyRepository() throws ModelRuntimeException {
		boolean testy = false;
		
		// make sure that this throws a RuntimeException
		try {
			@SuppressWarnings("unused")
			RepositoryModel newModel = new RepositoryModel(context, null);	
		} catch(RuntimeException e) {
			testy = true;
		} 	
		assertTrue(testy);
	}
	
	// TODO: make this work with test-data/icaltzd.rdfs
	
	public void testModelReadFromWithOneArg() throws ModelRuntimeException, IOException {
	      Model model = new RepositoryModel(false);
	        model.readFrom(TestData.getICALAsStream()); 
	        
	        
	}
	
	public void testModelReadFromWithTwoArgs() throws ModelRuntimeException, IOException {
	      Model model = new RepositoryModel(false);
	        model.readFrom(TestData.getICALAsStream(),Syntax.RdfXml); 
	}

	
	public void testCheckForValidURI() {
		assertFalse(this.model.isValidURI("ping"));
		assertTrue(this.model.isValidURI("http://ich.bin.eine.uri.de"));
	}
	
	
	// this does not work because the actual file on the web has a bad syntax
	public void ReadFromURL() throws Exception {
		Model model = new RepositoryModel(false);

		URL url = new URL("http://www.w3.org/2002/12/cal/icaltzd");
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		model.readFrom(in, Syntax.RdfXml);

	}

	
	
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
