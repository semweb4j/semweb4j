/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import org.openrdf.OpenRDFException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

/**
 * A QueryResultTable implementation that wraps the results of a table query on
 * a Repository.
 */
public class RepositoryQueryResultTable implements QueryResultTable {

	private TupleQueryResult queryResult;

	public RepositoryQueryResultTable(String queryString, RepositoryConnection connection)
		throws ModelRuntimeException
	{
		this(queryString, QueryLanguage.SPARQL, connection);
	}

	public RepositoryQueryResultTable(String queryString, QueryLanguage language,
			RepositoryConnection connection)
		throws ModelRuntimeException
	{
		try {
			this.queryResult = connection.prepareTupleQuery(language, queryString).evaluate();
		}
		catch (OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public List<String> getVariables() {
		return this.queryResult.getBindingNames();
	}

	public ClosableIterator<QueryRow> iterator() {
		return new QueryRowIterator(this.queryResult);
	}
}
