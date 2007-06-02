package org.ontoware.rdf2go.examples;

import junit.framework.TestCase;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsageExample extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(UsageExample.class);
	
	public void testWriteRead() throws ModelRuntimeException {
		
		log.debug("Launching test");
		
		Model m = RDF2Go.getModelFactory().createModel();

		URI konrad = m.createURI("urn:x-example:konrad");
		URI kennt = m.createURI("urn:x-example:kennt");
		URI max = m.createURI("urn:x-example:max");

		m.addStatement(konrad, kennt, max);

		String queryString = "SELECT ?x WHERE { <" + konrad + "> <" + kennt + "> ?x}";
		QueryResultTable table = m.sparqlSelect(queryString);
		ClosableIterator<QueryRow> it = table.iterator();
		QueryRow row = it.next();
		assertFalse("iterator should have only one result", it.hasNext() );
		Node n = row.getValue("x");
		assertEquals( n, max);
		
		m.dump();

	}

}
