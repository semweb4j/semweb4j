/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.openrdf.model.Statement;
import org.openrdf.util.iterator.CloseableIterator;

/**
 * Closable iterator over OpenRDF Statements that converts them to RDF2Go
 * Statements on demand. This iterator closes itself automatically after having
 * returned the last statement.
 */
public class StatementIterator implements ClosableIterator<org.ontoware.rdf2go.model.Statement> {

	private final Log log = LogFactory.getLog(this.getClass());

	private final CloseableIterator<? extends Statement> cit;

	private boolean closed = false;

	private RepositoryModel model;

	public StatementIterator(CloseableIterator<? extends Statement> cit, RepositoryModel model) {
		this.cit = cit;
		this.model = model;
	}

	public boolean hasNext() {
		if (closed) {
			return false;
		}
		else {
			boolean hasNext = cit.hasNext();
			if (!hasNext) {
				close();
			}
			return hasNext;
		}
	}

	public org.ontoware.rdf2go.model.Statement next() {
		Statement nextStatement = cit.next();
		org.ontoware.rdf2go.model.Statement result = new StatementWrapper(model, nextStatement);

		if (!cit.hasNext()) {
			close();
		}

		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	public void close() {
		cit.close();
		closed = true;
	}

	protected void finalize() {
		if (!closed) {
			log.warn(this.getClass().getName() + " not closed, closing now");
			close();
		}
	}
}
