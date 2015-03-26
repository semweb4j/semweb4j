package org.ontoware.rdfreactor.runtime;

import java.util.Calendar;

import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public class RDFReactorRuntimeTest {

	@Test
	public void testCalendarHandling() {
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		URI s = new URIImpl("urn:test:S");
		URI p = new URIImpl("urn:test:P");
		Calendar cal = Calendar.getInstance();
		BridgeBase.add(model, s, p, cal);
	}
	
}

