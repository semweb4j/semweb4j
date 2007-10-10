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

import java.util.Iterator;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.ModelWriter;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.LanguageTagLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public abstract class AbstractModelWriter implements ModelWriter {

	/**
	 * Add all statements contained in 'other' to this model = 'union'
	 * 
	 * Override this method for performance reasons, to avoid object creation.
	 * 
	 * @param other
	 *            another RDF2GO model
	 * @throws ModelRuntimeException
	 */
	public void addAll(Iterator<Statement> other) throws ModelRuntimeException {
		while (other.hasNext()) {
			addStatement(other.next());
		}
	}

	// core rdf2go model methods
	// /////////////////////////

	public void addStatement(Resource subject, URI predicate, String literal) throws ModelRuntimeException {
		addStatement(subject, predicate, new PlainLiteralImpl(literal));
	}

	/**
	 * Override this method for performance reasons, to avoid object creation.
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param languageTag
	 * @throws ModelRuntimeException
	 */
	public void addStatement(Resource subject, URI predicate, String literal, String languageTag)
			throws ModelRuntimeException {
		addStatement(subject, predicate, new LanguageTagLiteralImpl(literal, languageTag));
	}

	/*
	 * (wth) for information on typed literals see this very good how to
	 * http://jena.sourceforge.net/how-to/typedLiterals.html
	 * 
	 * (none javadoc) Override this method for performance reasons, to avoid
	 * object creation.
	 * 
	 * @see org.ontoware.rdf2go.Model#addStatement(Object, URI, String, URI)
	 */
	public void addStatement(Resource subject, URI predicate, String literal, URI datatypeURI)
			throws ModelRuntimeException {
		addStatement(subject, predicate, new DatatypeLiteralImpl(literal, datatypeURI));
	}

	public void addStatement(String subjectURIString, URI predicate, String literal)
			throws ModelRuntimeException {
		addStatement(new URIImpl(subjectURIString), predicate, new PlainLiteralImpl(literal));
	}

	/**
	 * Override this method for performance reasons, to avoid object creation.
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param languageTag
	 * @throws ModelRuntimeException
	 */
	public void addStatement(String subjectURIString, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		addStatement(new URIImpl(subjectURIString), predicate, new LanguageTagLiteralImpl(
				literal, languageTag));
	}

	/*
	 * (wth) for information on typed literals see this very good how to
	 * http://jena.sourceforge.net/how-to/typedLiterals.html
	 * 
	 * (none javadoc) Override this method for performance reasons, to avoid
	 * object creation.
	 * 
	 * @see org.ontoware.rdf2go.Model#addStatement(Object, URI, String, URI)
	 */
	public void addStatement(String subjectURIString, URI predicate, String literal, URI datatypeURI)
			throws ModelRuntimeException {
		addStatement(new URIImpl(subjectURIString), predicate, new DatatypeLiteralImpl(literal,
				datatypeURI));
	}

	/*
	 * (non-Javadoc) Override this method for performance reasons, to avoid
	 * object creation.
	 * 
	 * @see org.ontoware.rdf2go.Model#addStatement(org.ontoware.rdf2go.Statement)
	 */
	public void addStatement(Statement statement) throws ModelRuntimeException {
		addStatement(statement.getSubject(), statement.getPredicate(), statement.getObject());
	}

	// core rdf2go model methods
	// /////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#addStatement(java.lang.Object,
	 *      java.net.URI, java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.impl.ModelAddRemove#addStatement(java.lang.Object,
	 *      java.net.URI, java.lang.Object)
	 */
	public abstract void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException;
}
