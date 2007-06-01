/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.model.impl;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.TriplePattern;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;

/**
 * A statement with variables
 * 
 * @author voelkel
 * 
 */
public class TriplePatternImpl implements TriplePattern {

	public enum SPO {
		SUBJECT, PREDICATE, OBJECT
	}

	private ResourceOrVariable subject;

	private UriOrVariable predicate;

	private NodeOrVariable object;

	public SPO extract;

	public TriplePatternImpl(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public TriplePatternImpl(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object, SPO extract) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.extract = extract;
	}

	/**
	 * Convenience constructor
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	public TriplePatternImpl(ResourceOrVariable subject, URI predicate,
			String object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = new PlainLiteralImpl(object);
	}

	public NodeOrVariable getObject() {
		return object;
	}

	public UriOrVariable getPredicate() {
		return predicate;
	}

	public ResourceOrVariable getSubject() {
		return subject;
	}

	public boolean equals(Object o) {
		return ((o instanceof Statement)
				&& (this.getSubject().equals(((Statement) o).getSubject()))
				&& (this.getPredicate().equals(((Statement) o).getPredicate())) && (this
				.getObject().equals(((Statement) o).getObject())));
	}
	
	public int hashCode() {
		return object.hashCode() + predicate.hashCode() + object.hashCode();
	}

	public static TriplePatternImpl createObjectPattern(Resource resource,
			URI propertyURI) {
		return new TriplePatternImpl(resource, propertyURI, Variable.ANY,
				SPO.OBJECT);
	}

	public static TriplePatternImpl createSubjectPattern(URI propertyURI,
			Node objectNode) {
		return new TriplePatternImpl(Variable.ANY, propertyURI, objectNode,
				SPO.SUBJECT);
	}

	public boolean matches(Statement statement) {
		boolean matchesSubject = statement.getSubject().equals(
				this.getSubject())
				|| this.getSubject() instanceof Variable;

		boolean matchesPredicate = statement.getPredicate().equals(
				this.getPredicate())
				|| this.getPredicate() instanceof Variable;

		boolean matchesObject = statement.getObject().equals(this.getObject())
				|| this.getObject() instanceof Variable;

		return matchesSubject && matchesPredicate && matchesObject;
	}

}
