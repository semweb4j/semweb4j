/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.ontoware.rdf2go.impl.sesame2;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.openrdf.queryresult.GraphQueryResult;

public class SesameGraphIterable implements ClosableIterable<Statement> {

    private GraphQueryResult queryResult;

    public SesameGraphIterable(GraphQueryResult graphQueryResult) {
        this.queryResult = graphQueryResult;
    }

    public ClosableIterator<Statement> iterator() {
        return new SesameGraphIterator(queryResult.iterator());
    }
}