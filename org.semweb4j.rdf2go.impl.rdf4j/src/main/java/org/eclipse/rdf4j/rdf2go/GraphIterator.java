/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.eclipse.rdf4j.rdf2go;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Statement;

import org.eclipse.rdf4j.common.iteration.CloseableIteration;

import org.eclipse.rdf4j.query.QueryEvaluationException;

public class GraphIterator implements ClosableIterator<Statement> {

	private CloseableIteration<org.eclipse.rdf4j.model.Statement, QueryEvaluationException> iterator;

	private RepositoryModel model;

	public GraphIterator(CloseableIteration<org.eclipse.rdf4j.model.Statement, QueryEvaluationException> iterator,
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
		try {
			return new StatementWrapper(this.model, this.iterator.next());
		}
		catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		}
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
