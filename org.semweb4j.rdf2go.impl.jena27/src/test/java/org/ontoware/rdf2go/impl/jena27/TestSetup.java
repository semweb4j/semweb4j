package org.ontoware.rdf2go.impl.jena27;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;

public class TestSetup {
	
	@Test
	public void testSetup() {
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		assertNotNull(model);
		assertTrue(model.isOpen());
		model.close();
	}

}
