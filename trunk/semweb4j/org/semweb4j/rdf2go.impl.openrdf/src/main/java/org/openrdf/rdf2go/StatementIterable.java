/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.openrdf.util.iterator.CloseableIterator;

/**
 * Iterable over OpenRDF Statements that converts them to R2Go Statements on
 * demand.
 */
public class StatementIterable implements ClosableIterable<Statement> {

	private final CloseableIterator<? extends org.openrdf.model.Statement> cit;

	private RepositoryModel model;

	public StatementIterable(CloseableIterator<? extends org.openrdf.model.Statement> cit,
			RepositoryModel model)
	{
		this.cit = cit;
		this.model = model;
	}

	public ClosableIterator<Statement> iterator() {
		return new StatementIterator(cit, model);
	}
}