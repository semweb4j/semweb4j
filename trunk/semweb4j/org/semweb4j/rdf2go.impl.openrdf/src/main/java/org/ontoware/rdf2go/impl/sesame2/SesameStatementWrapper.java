/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.ontoware.rdf2go.impl.sesame2;

import org.ontoware.rdf2go.model.impl.AbstractStatement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.rdf2go.ConversionUtil;

/**
 * Wrapper for Sesame2 Statements, which implements the rdf2go statement api.
 * Note that this not includes any getContext() function, because each triple
 * belongs to only one context. If you know the context you know the triples,
 * but if you have just a triple there is no link to a context.
 */
class SesameStatementWrapper extends AbstractStatement implements org.ontoware.rdf2go.model.Statement {

    private org.openrdf.model.Statement statement;

    public SesameStatementWrapper(org.openrdf.model.Statement statement) {
        this.statement = statement;
    }

    public Node getObject() {
        return ConversionUtil.toRdf2go(statement.getObject());
    }

    public URI getPredicate() {
        return ConversionUtil.toRdf2go(statement.getPredicate());
    }

    public Resource getSubject() {
        return (Resource)ConversionUtil.toRdf2go(statement.getSubject());
    }

    public String toString() {
        return getSubject() + " - " + getPredicate() + " - " + getObject();
    }

    public URI getContext() {
        return (URI)ConversionUtil.toRdf2go(statement.getContext());
    }
    
}
