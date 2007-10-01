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
     * Test method for {@link org.ontoware.rdf2go.model.Syntax#register(org.ontoware.rdf2go.model.Syntax)}.
     */
    public void testRegister() {
        Syntax s = new Syntax("spargel", "application/spargel");
        Syntax.register(s);
        Syntax s1 = Syntax.forMimeType("application/spargel");
        assertEquals(s, s1);
    }

    /**
     * Test method for {@link org.ontoware.rdf2go.model.Syntax#forName(java.lang.String)}.
     */
    public void testForName() {
        Syntax s = Syntax.forName("rdfxml");
        assertNotNull(s);
        assertEquals( "rdfxml", s.getName());
    }

    /**
     * Test method for {@link org.ontoware.rdf2go.model.Syntax#forMimeType(java.lang.String)}.
     */
    public void testForMimeType() {
        Syntax s = Syntax.forMimeType("application/x-turtle");
        assertNotNull(s);
        assertEquals( "application/x-turtle", s.getMimeType());
    }

    /**
     * Test method for {@link org.ontoware.rdf2go.model.Syntax#unregister(org.ontoware.rdf2go.model.Syntax)}.
     */
    public void testUnregister() {
        Syntax.unregister(Syntax.Ntriples);
        Syntax s = Syntax.forName("ntriples");
        assertNull(s);
    }

    /**
     * Test method for {@link org.ontoware.rdf2go.model.Syntax#list()}.
     */
    public void testList() {
        List<Syntax> list = Syntax.list();
        assertEquals(5, list.size());
        ArrayList<Syntax> l = new ArrayList<Syntax>(list.size());
        l.remove(Syntax.Ntriples);
        l.remove(Syntax.RdfXml);
        l.remove(Syntax.Trix);
        l.remove(Syntax.Turtle);
        l.remove(Syntax.Trig);
        assertEquals(0, l.size());
    }
    
    public void testCreateList() {
        List<Syntax> SYNTAXES = new ArrayList<Syntax>();
        SYNTAXES.size();
    }

}
