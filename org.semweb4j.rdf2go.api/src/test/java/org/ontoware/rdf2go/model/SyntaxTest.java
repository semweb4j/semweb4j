/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2010
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;


/**
 * @author sauermann
 */
public class SyntaxTest extends TestCase {
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Test method for
	 * {@link org.ontoware.rdf2go.model.Syntax#register(org.ontoware.rdf2go.model.Syntax)}
	 * .
	 */
	public void testRegister() {
    	int numSyntaxes = Syntax.list().size();
        Syntax s = new Syntax("spargel", "application/spargel", ".spargel");
        Syntax.register(s);
        Syntax s1 = Syntax.forMimeType("application/spargel");
        assertEquals("Check if the newly registered sytax can be properly retrieved.", s, s1);
        assertEquals("After registering a new syntax, check the overall number again.", numSyntaxes + 1,  Syntax.list().size());
	}
	
	/**
	 * Test method for
	 * {@link org.ontoware.rdf2go.model.Syntax#forName(java.lang.String)}.
	 */
	public void testForName() {
		Syntax.resetFactoryDefaults();
		Syntax s = Syntax.forName("rdfxml");
		assertNotNull(s);
		assertEquals("rdfxml", s.getName());
	}
	
	/**
	 * Test method for
	 * {@link org.ontoware.rdf2go.model.Syntax#forMimeType(java.lang.String)}.
	 */
	public void testForMimeType() {
		Syntax.resetFactoryDefaults();
		Syntax s = Syntax.forMimeType("application/x-turtle");
		assertNotNull(s);
		assertEquals("application/x-turtle", s.getMimeType());
	}
	
	/**
	 * Test method for
	 * {@link org.ontoware.rdf2go.model.Syntax#unregister(org.ontoware.rdf2go.model.Syntax)}
	 * .
	 */
	public void testUnregister() {
		Syntax.resetFactoryDefaults();
		Syntax.unregister(Syntax.Ntriples);
		Syntax s = Syntax.forName("ntriples");
		assertNull(s);
	}
	
	/**
	 * Test method for {@link org.ontoware.rdf2go.model.Syntax#list()}.
	 */
	public void testList() {
		Syntax.resetFactoryDefaults();
		List<Syntax> list = Syntax.list();
		// check overall number
		assertEquals("Check for the expected number of syntaxes", 8, list.size());
        int numSyntaxes = list.size();
        ArrayList<Syntax> l = new ArrayList<Syntax>(list);
        // remove just a few
        l.remove(Syntax.RdfXml);
        l.remove(Syntax.Nquads);
        assertEquals("After removing a number of syntaxes, check the overall number again.", numSyntaxes - 2,  l.size());

	}
	
	public void testCreateList() {
		List<Syntax> SYNTAXES = new ArrayList<Syntax>();
		SYNTAXES.size();
	}
	
}
