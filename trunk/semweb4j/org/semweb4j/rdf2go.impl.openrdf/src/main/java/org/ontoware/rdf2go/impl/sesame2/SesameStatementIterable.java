

/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.ontoware.rdf2go.impl.sesame2;

import java.util.Iterator;

import org.ontoware.rdf2go.model.Statement;


/**
 * Iterable over Sesame 2 Statements, which converts them to R2Go Statements on
 * demand.
 */
public class SesameStatementIterable implements Iterable<Statement> {

    private final Iterator<org.openrdf.model.Statement> cit;

    public SesameStatementIterable(Iterator<org.openrdf.model.Statement> cit)
    {
        this.cit = (Iterator<org.openrdf.model.Statement>)cit;
    }
    
    public Iterator<Statement> iterator() {
        return new SesameStatementIterator(cit);
    }
}