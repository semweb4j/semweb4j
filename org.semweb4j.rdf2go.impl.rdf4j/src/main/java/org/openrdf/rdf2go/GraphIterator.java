/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Statement;

import info.aduna.iteration.CloseableIteration;

import org.openrdf.query.QueryEvaluationException;

public class GraphIterator implements ClosableIterator<Statement> {

	private CloseableIteration<org.openrdf.model.Statement, QueryEvaluationException> iterator;

	private RepositoryModel model;

	public GraphIterator(CloseableIteration<org.openrdf.model.Statement, QueryEvaluationException> iterator,
			RepositoryModel model)
	{
		this.iterator = iterator;
		this.model = model;
	}

	public boolean hasNext() {
		try {
			return this.iterator.hasNext();
		}
		catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public Statement next() {
		org.openrdf.model.Statement statement = null;
		try {
			statement = this.iterator.next();
		}
		catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		}
		return new StatementWrapper(this.model, statement);
	}

	public void remove() {
		try {
			this.iterator.remove();
		}
		catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void close() {
		try {
			this.iterator.close();
		}
		catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		}
	}
}
