/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.impl.AbstractStatement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * Wrapper for OpenRDF Statements that implements the RDF2Go Statement API.
 * <p>
 * Note that this not include any getContext()-like functionality because each
 * triple belongs to only one context. If you know the context you know the
 * triples, but if you have just a triple there is no link to a context.
 */
public class StatementWrapper extends AbstractStatement {

	private org.openrdf.model.Statement statement;

	private RepositoryModel model;

	public StatementWrapper(RepositoryModel model, org.openrdf.model.Statement statement) {
		this.statement = statement;
		this.model = model;
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

	public Model getModel() {
		return model;
	}

	public URI getContext() {
		return this.model.getContextURI();
	}
}
