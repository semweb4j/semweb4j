
/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.ontoware.rdf2go.impl.sesame2;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;

public class SesameGraphIterator implements ClosableIterator<Statement> {

    private Iterator<org.openrdf.model.Statement> iterator;

    public SesameGraphIterator(Iterator<org.openrdf.model.Statement> iterator) {
        this.iterator = iterator;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Statement next() {
        org.openrdf.model.Statement statement = iterator.next();
        return new SesameStatementWrapper(statement);
    }

    public void remove() {
        iterator.remove();
    }

    public void close() {
        // no-op
    }
}
