package org.ontoware.rdf2go.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

public class ReportedBugsTests {

	@Test
	public void testVocabulary() {
		URI three = RDF.li(3);
		assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#_3", three
				.toString());
	}

}
