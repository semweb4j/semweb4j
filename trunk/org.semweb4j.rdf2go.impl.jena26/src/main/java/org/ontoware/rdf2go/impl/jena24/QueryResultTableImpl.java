package org.ontoware.rdf2go.impl.jena24;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import com.hp.hpl.jena.rdf.model.Model;

public class QueryResultTableImpl implements QueryResultTable {

	private List<String> varnames;

	private QueryExecution qexec;

	public QueryResultTableImpl(Query query, Model jenaModel) throws ModelRuntimeException {

		if (!query.isSelectType()) {
			throw new ModelRuntimeException("The given query is not a SELECT query");
		}
		// else
		this.varnames = new ArrayList<String>();
		for (Object o : query.getResultVars()) {
			this.varnames.add((String) o);
		}
		this.qexec = QueryExecutionFactory.create(query, jenaModel);
	}

	public List<String> getVariables() {
		return this.varnames;
	}


	public ClosableIterator<QueryRow> iterator() {
		ResultSet results = this.qexec.execSelect();
		return new QueryIterator(this,results);
	}

}
