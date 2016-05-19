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
 */
public class StatementWrapper extends AbstractStatement {
	
	private static final long serialVersionUID = -1796668851117512578L;
	
	private org.openrdf.model.Statement statement;
	
	private RepositoryModel model;
	
	public StatementWrapper(RepositoryModel model, org.openrdf.model.Statement statement) {
		this.statement = statement;
		this.model = model;
	}
	
	public Node getObject() {
		return ConversionUtil.toRdf2go(this.statement.getObject());
	}
	
	public URI getPredicate() {
		return ConversionUtil.toRdf2go(this.statement.getPredicate());
	}
	
	public Resource getSubject() {
		return (Resource)ConversionUtil.toRdf2go(this.statement.getSubject());
	}
	
	public URI getContext() {
		return (URI)ConversionUtil.toRdf2go(this.statement.getContext());
	}
	
	@Override
	public String toString() {
		return getSubject() + " - " + getPredicate() + " - " + getObject();
	}
	
	public Model getModel() {
		return this.model;
	}
}
