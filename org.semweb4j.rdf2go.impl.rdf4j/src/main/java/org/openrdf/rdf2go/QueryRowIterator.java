/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

/**
 * Iterator over an OpenRDF TupleQueryResult that converts its BindingSets to
 * RDF2Go QueryRows on demand. This iterator closes automatically after
 * returning the last QueryRow.
 */
public class QueryRowIterator implements ClosableIterator<QueryRow> {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	private TupleQueryResult queryResult;

	private boolean closed = false;

	public QueryRowIterator(TupleQueryResult queryResult) {
		this.queryResult = queryResult;
	}

	public boolean hasNext() {
		if (this.closed) {
			return false;
		}
		else {
			boolean hasNext = false;
			try {
				hasNext = this.queryResult.hasNext();
			}
			catch (QueryEvaluationException e) {
				throw new ModelRuntimeException(e);
			}
			if (!hasNext) {
				close();
			}
			return hasNext;
		}
	}

	public QueryRow next() {
		BindingSet nextBindingSet = null;

		try {
			nextBindingSet = this.queryResult.next();

			if (!this.queryResult.hasNext()) {
				close();
			}
		}
		catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		}

		return new QueryRowWrapper(nextBindingSet);
	}

	public void remove() {
		try {
			this.queryResult.remove();
		}
		catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void close() {
		try {
			this.queryResult.close();
		}
		catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		}
		this.closed = true;
	}

	@Override
	protected void finalize()
		throws Throwable
	{
		try {
			if (!this.closed) {
				this.logger.warn(this.getClass().getName() + " not closed, closing now");
				close();
			}
		}
		finally {
			super.finalize();
		}
	}
}
