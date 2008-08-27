package org.ontoware.rdfreactor.runtime;

import static org.junit.Assert.*;

import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.List;
import org.openrdf.rdf2go.RepositoryModelFactory;

public class TestReportedIssues {

	@Test
	public void testUsingList() {

		RepositoryModelFactory repositoryModelFactory = new RepositoryModelFactory();
		Model model = repositoryModelFactory.createModel();
		model.open();

		URI a = new URIImpl("urn:test:a");
		URI b = new URIImpl("urn:test:b");
		List list = new List(model, "urn:test:list", true);
		
		//assertTrue( list instanceof URI );  --> fails

		model.addStatement(a, b, list.asURI() );
		model.close();
	}

}
