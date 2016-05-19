/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;

import org.openrdf.query.GraphQueryResult;

public class GraphIterable implements ClosableIterable<Statement> {

	/**
     * 
     */
    private static final long serialVersionUID = -4676097526635602838L;

	private RepositoryModel model;

	private GraphQueryResult queryResult;

	public GraphIterable(GraphQueryResult graphQueryResult, RepositoryModel model) {
		this.queryResult = graphQueryResult;
		this.model = model;
	}

	public ClosableIterator<Statement> iterator() {
		return new GraphIterator(this.queryResult, this.model);
	}
}
