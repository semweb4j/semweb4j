package org.ontoware.rdfreactor.generator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.openrdf.rdf2go.RepositoryModelFactory;


public class TestKnownBugs {
    
    @Test
    public void test() {
        RepositoryModelFactory rmf = new RepositoryModelFactory();
        RDF2Go.register(rmf);
        ModelFactory modelFactory = RDF2Go.getModelFactory();
        Model model = modelFactory.createModel();
        model.open();
        assertTrue(model.isOpen());
    }
    
}
