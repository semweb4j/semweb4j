/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;

import info.aduna.iteration.CloseableIteration;

import org.openrdf.OpenRDFException;

/**
 * Iterable over OpenRDF Statements that converts them to R2Go Statements on
 * demand.
 */
public class StatementIterable implements ClosableIterable<Statement> {

	/**
     * 
     */
    private static final long serialVersionUID = -8172286299886107501L;

	private final CloseableIteration<? extends org.openrdf.model.Statement, ? extends OpenRDFException> cit;

	private RepositoryModel model;

	public StatementIterable(
			CloseableIteration<? extends org.openrdf.model.Statement, ? extends OpenRDFException> cit,
			RepositoryModel model)
	{
		this.cit = cit;
		this.model = model;
	}

	public ClosableIterator<Statement> iterator() {
		return new StatementIterator(this.cit, this.model);
	}
}