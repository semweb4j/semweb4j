package org.ontoware.rdf2go.examples;

import java.util.Iterator;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

/**
 * how to make simple sparql queries and cope with the results
 */
public class QueryUsage {
	
	public void testGetSelectQueryResult() throws MalformedQueryException, ModelRuntimeException {
		
		Model model = RDF2Go.getModelFactory().createModel();
		QueryResultTable table = model.sparqlSelect("SELECT ?a ?b ?c WHERE { ... }");
		Iterator<QueryRow> iterator = table.iterator();
		
        QueryRow row = iterator.next();
        for( String varname : table.getVariables() ) {
        	@SuppressWarnings("unused")
			Node x = row.getValue(varname);
        }
      
	}

}
