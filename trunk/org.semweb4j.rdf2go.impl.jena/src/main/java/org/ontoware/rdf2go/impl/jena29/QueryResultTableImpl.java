package org.ontoware.rdf2go.impl.jena29;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;


public class QueryResultTableImpl implements QueryResultTable {
	
	private static final long serialVersionUID = 4376499706432505273L;
	
	private final List<String> varnames;
	
	private final QueryExecution qexec;
	
	public QueryResultTableImpl(Query query, Model jenaModel) throws ModelRuntimeException {
		
		if(!query.isSelectType()) {
			throw new ModelRuntimeException("The given query is not a SELECT query");
		}
		// else
		this.varnames = new ArrayList<String>();
		for(Object o : query.getResultVars()) {
			this.varnames.add((String)o);
		}
		this.qexec = QueryExecutionFactory.create(query, jenaModel);
	}

	public QueryResultTableImpl(Query query, Dataset jenaDataset) throws ModelRuntimeException {
		
		if(!query.isSelectType()) {
			throw new ModelRuntimeException("The given query is not a SELECT query");
		}
		// else
		this.varnames = new ArrayList<String>();
		for(Object o : query.getResultVars()) {
			this.varnames.add((String)o);
		}
		this.qexec = QueryExecutionFactory.create(query, jenaDataset);
	}
	
	public QueryResultTableImpl(QueryExecution qe) {
		if(!qe.getQuery().isSelectType()) {
			throw new ModelRuntimeException("The given query execution is not a SELECT query");
		}
		
		this.varnames = new ArrayList<String>();
		for(Object o : qe.getQuery().getResultVars()) {
			this.varnames.add((String)o);
		}
		this.qexec = qe;
	}
	
	@Override
	public List<String> getVariables() {
		return this.varnames;
	}
	
	@Override
	public ClosableIterator<QueryRow> iterator() {
		ResultSet results = this.qexec.execSelect();
		return new QueryIterator(this, results);
	}
	
}
