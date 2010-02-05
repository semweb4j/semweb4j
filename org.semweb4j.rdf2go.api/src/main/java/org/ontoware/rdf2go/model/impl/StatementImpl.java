/**
 * LICENSE INFORMATION
 *
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2010
 *
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model.impl;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * StatementImpl is an implementation of Statement, so there are all necessary
 * constructors, and methods for getting the type and the parts of the statement
 * 
 * @author mvo
 */

public class StatementImpl extends AbstractStatement implements Statement {

	protected Resource subject;

	protected URI predicate;

	private Node object;

	private URI context;

	/**
	 * builds a new statement
	 * 
	 * @param subject
	 *            The subject of this statement
	 * @param predicate
	 *            The proerty of this statement
	 * @param object
	 *            The object of this statement
	 */
	public StatementImpl(URI context, Resource subject, URI predicate, Node object) {
		this.context = context;
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Statement#getSubject()
	 */
	public Resource getSubject() {
		return this.subject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Statement#getPredicate()
	 */
	public URI getPredicate() {
		return this.predicate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Statement#getObject()
	 */
	public Node getObject() {
		return this.object;
	}

	@Override
	public String toString() {
		return getSubject() + " - " + getPredicate() + " - " + getObject();
	}

	public URI getContext() {
		return this.context;
	}

	@Override
	public int hashCode() {
		return this.subject.hashCode() + this.predicate.hashCode() + this.object.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Statement) {
			Statement o = (Statement) other;
			return this.getSubject().equals(o.getSubject())
					&& this.getPredicate().equals(o.getPredicate())
					&& this.getObject().equals(o.getObject());
		}
		return false;
	}


}
