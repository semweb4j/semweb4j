/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.aduna.iteration.CloseableIteration;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;

/**
 * Closable iterator over OpenRDF Statements that converts them to RDF2Go
 * Statements on demand. This iterator closes itself automatically after having
 * returned the last statement.
 */
public class StatementIterator implements ClosableIterator<org.ontoware.rdf2go.model.Statement> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final CloseableIteration<? extends Statement, ? extends OpenRDFException> cit;

	private boolean closed = false;

	private RepositoryModel model;

	public StatementIterator(CloseableIteration<? extends Statement, ? extends OpenRDFException> cit,
			RepositoryModel model)
	{
		this.cit = cit;
		this.model = model;
	}

	public boolean hasNext() {
		if (this.closed) {
			return false;
		}
		else {
			boolean hasNext;
			try {
				hasNext = this.cit.hasNext();
			}
			catch (OpenRDFException e) {
				throw new ModelRuntimeException(e);
			}
			if (!hasNext) {
				close();
			}
			return hasNext;
		}
	}

	public org.ontoware.rdf2go.model.Statement next() {
		org.ontoware.rdf2go.model.Statement result = null;
		try {
			Statement nextStatement = this.cit.next();
			result = new StatementWrapper(this.model, nextStatement);

			if (!this.cit.hasNext()) {
				close();
			}
		}
		catch (OpenRDFException e) {
			throw new ModelRuntimeException(e);

		}

		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	public void close() {
		try {
			this.cit.close();
		}
		catch (OpenRDFException e) {
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
