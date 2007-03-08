/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.ontoware.rdf2go.impl.sesame2;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.openrdf.util.iterator.CloseableIterator;

/**
 * Iterable over Sesame 2 Statements, which converts them to R2Go Statements on
 * demand.
 */
public class SesameCloseableStatementIterable implements
		ClosableIterable<Statement> {

	private final CloseableIterator<org.openrdf.model.Statement> cit;

	/**
	 * a new closeable iterable over a closeable iterator
	 * 
	 * @param cit
	 *            the iterator to wrap
	 */
	@SuppressWarnings("unchecked")
	public SesameCloseableStatementIterable(
			CloseableIterator<? extends org.openrdf.model.Statement> cit) {
		this.cit = (CloseableIterator<org.openrdf.model.Statement>) cit;
	}

	public ClosableIterator<Statement> iterator() {
		return new SesameCloseableStatementIterator(cit);
	}
}