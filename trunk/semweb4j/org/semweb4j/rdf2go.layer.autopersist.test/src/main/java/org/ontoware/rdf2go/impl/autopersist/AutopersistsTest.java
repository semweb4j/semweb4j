package org.ontoware.rdf2go.impl.autopersist;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public class AutopersistsTest extends TestCase {

	/*
	 * Test method for
	 * 'org.ontoware.rdf2go.impl.autopersist.ModelImplAutoPersist.ModelImplAutoPersist(File,
	 * int, Reasoning)'
	 */
	public void testModelImplAutoPersist() throws IOException, ModelRuntimeException {
		File testdata = new File("./target/temp/test-data");
		testdata.mkdirs();
		File f = new File(testdata, "modelimplautopersist.test");
		f.delete();
		@SuppressWarnings("unused") ModelImplAutoPersist miap = new ModelImplAutoPersist( RDF2Go.getModelFactory().createModel(), f, 100);
	}

	/*
	 * Test method for
	 * 'org.ontoware.rdf2go.impl.autopersist.ModelImplAutoPersist.addStatement(Resource,
	 * URI, Node)'
	 */
	public void testAddStatementResourceURINode() throws IOException, ModelRuntimeException {
		File testdata = new File("./target/temp/test-data");
		testdata.mkdirs();
		File f = new File(testdata, "modelimplautopersist.test");
		File tmp = new File(testdata, "modelimplautopersist.test.tmp");
		tmp.delete();
		ModelImplAutoPersist miap = new ModelImplAutoPersist(RDF2Go.getModelFactory().createModel(), f, 0);
		miap.addStatement("urn:test:s", new URIImpl("urn:test:p"), "Test");
		assertEquals(0, miap.openchanges());
		assertTrue( tmp.exists() );
		long fModified = tmp.length();
		miap.addStatement("urn:test:s", new URIImpl("urn:test:p"), "Test2");
		assertEquals(0, miap.openchanges());
		long f2Modified = tmp.length();
		assertNotSame(fModified, f2Modified);
	}

	/*
	 * Test method for
	 * 'org.ontoware.rdf2go.impl.autopersist.ModelImplAutoPersist.save()'
	 */
	public void testSave() {

	}

	/*
	 * Test method for
	 * 'org.ontoware.rdf2go.impl.autopersist.ModelImplAutoPersist.load()'
	 */
	public void testLoad() {

	}

}
