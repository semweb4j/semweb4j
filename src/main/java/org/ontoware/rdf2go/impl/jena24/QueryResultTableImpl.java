package org.ontoware.rdf2go.impl.jena24;

import java.util.HashSet;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class QueryResultTableImpl implements QueryResultTable {

	private Set<String> varnames;

	private QueryExecution qexec;

	public QueryResultTableImpl(Query query, Model jenaModel) throws ModelRuntimeException {

		if (!query.isSelectType()) {
			throw new ModelRuntimeException("The given query is not a SELECT query");
		}
		// else
		this.varnames = new HashSet<String>();
		for (Object o : query.getResultVars()) {
			varnames.add((String) o);
		}
		qexec = QueryExecutionFactory.create(query, jenaModel);
	}

	public Set<String> getVariables() {
		return this.varnames;
	}


	public ClosableIterator<QueryRow> iterator() {
		ResultSet results = qexec.execSelect();
		return new QueryIterator(this,results);
	}

}
