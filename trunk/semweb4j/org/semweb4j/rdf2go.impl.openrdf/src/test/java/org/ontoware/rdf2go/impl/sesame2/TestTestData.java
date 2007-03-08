package org.ontoware.rdf2go.impl.sesame2;

import junit.framework.TestCase;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.testdata.TestData;

public class TestTestData extends TestCase {
    
    ModelFactory modelfactory;

    protected void setUp() throws Exception {
        super.setUp();
        modelfactory = new ModelFactoryImpl();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadFoaf() throws Exception {
        Model m = TestData.loadFoaf(modelfactory);
        assertTrue("foaf has triples", m.size()>10);
    }

    public void testLoadICAL()  throws Exception {
        Model m = TestData.loadICAL(modelfactory);
        assertTrue("ical has triples", m.size()>10);
    }

}
