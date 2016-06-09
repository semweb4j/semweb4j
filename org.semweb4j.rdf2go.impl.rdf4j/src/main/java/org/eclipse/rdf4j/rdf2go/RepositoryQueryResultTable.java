/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 * 
 * Licensed under the Aduna BSD-style license.
 */
package org.eclipse.rdf4j.rdf2go;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;


/**
 * A QueryResultTable implementation that wraps the results of a table query on
 * a Repository.
 */
public class RepositoryQueryResultTable implements QueryResultTable {
	
	private static final long serialVersionUID = 877636708884805441L;
	private TupleQuery query;
	private TupleQueryResult queryResult;
	
	public RepositoryQueryResultTable(String queryString, RepositoryConnection connection)
	        throws ModelRuntimeException {
		this(queryString, QueryLanguage.SPARQL, connection);
	}
	
	public RepositoryQueryResultTable(String queryString, QueryLanguage language,
	        RepositoryConnection connection) throws ModelRuntimeException {
		try {
			this.query = connection.prepareTupleQuery(language, queryString);
			this.queryResult = this.query.evaluate();
		} catch(RDF4JException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public List<String> getVariables() {
		try {
			return new ArrayList<String>(this.queryResult.getBindingNames());
		} catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public ClosableIterator<QueryRow> iterator() {
		return new QueryRowIterator(this.queryResult);
	}
}
