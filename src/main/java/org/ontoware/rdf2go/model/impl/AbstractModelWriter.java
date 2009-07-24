/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
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

/**
 * All these methods create some RDF2Go objects before calling the base case.
 * For high-performance reasons, adapters should override all methods here.
 * 
 * @author voelkel
 */
public abstract class AbstractModelWriter implements ModelWriter {

	public void addAll(Iterator<? extends Statement> other) throws ModelRuntimeException {
		while (other.hasNext()) {
			addStatement(other.next());
		}
	}

	public void addStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		addStatement(subject, predicate, new PlainLiteralImpl(literal));
	}

	public void addStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		addStatement(subject, predicate, new LanguageTagLiteralImpl(literal,
				languageTag));
	}

	/*
	 * (wth) for information on typed literals see this very good how to
	 * http://jena.sourceforge.net/how-to/typedLiterals.html
	 */
	public void addStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		addStatement(subject, predicate, new DatatypeLiteralImpl(literal,
				datatypeURI));
	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		addStatement(new URIImpl(subjectURIString), predicate,
				new PlainLiteralImpl(literal));
	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		addStatement(new URIImpl(subjectURIString), predicate,
				new LanguageTagLiteralImpl(literal, languageTag));
	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		addStatement(new URIImpl(subjectURIString), predicate,
				new DatatypeLiteralImpl(literal, datatypeURI));
	}

	public void addStatement(Statement statement) throws ModelRuntimeException {
		addStatement(statement.getSubject(), statement.getPredicate(),
				statement.getObject());
	}

	// /////////////////////////

	public abstract void addStatement(Resource subject, URI predicate,
			Node object) throws ModelRuntimeException;
}
