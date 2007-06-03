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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.ModelAddRemove;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.LanguageTagLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * adapter that maps the rdf2go model functions to a smaller subset of methods
 * 
 * @author mvo
 * 
 */
public abstract class AbstractModelAddRemove extends AbstractModelWriter implements ModelAddRemove {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AbstractModelAddRemove.class);

	public void removeStatement(String subjectURIString, URI predicate, String literal)
			throws ModelRuntimeException {
		removeStatement(new URIImpl(subjectURIString), predicate, new PlainLiteralImpl(literal));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#removeStatement(java.lang.Object,
	 *      java.net.URI, java.lang.String, java.lang.String)
	 */
	public void removeStatement(String subjectURIString, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		removeStatement(new URIImpl(subjectURIString), predicate, new LanguageTagLiteralImpl(
				literal, languageTag));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#removeStatement(java.lang.Object,
	 *      java.net.URI, java.lang.String, java.net.URI)
	 */
	public void removeStatement(String subjectURIString, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		removeStatement(new URIImpl(subjectURIString), predicate, new DatatypeLiteralImpl(
				literal, datatypeURI));
	}

	public void removeStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		removeStatement(subject, predicate, new PlainLiteralImpl(literal));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#removeStatement(java.lang.Object,
	 *      java.net.URI, java.lang.String, java.lang.String)
	 */
	public void removeStatement(Resource subject, URI predicate, String literal, String languageTag)
			throws ModelRuntimeException {
		removeStatement(subject, predicate, new LanguageTagLiteralImpl(literal, languageTag));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#removeStatement(java.lang.Object,
	 *      java.net.URI, java.lang.String, java.net.URI)
	 */
	public void removeStatement(Resource subject, URI predicate, String literal, URI datatypeURI)
			throws ModelRuntimeException {
		removeStatement(subject, predicate, new DatatypeLiteralImpl(literal, datatypeURI));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#removeStatement(org.ontoware.rdf2go.Statement)
	 */
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		removeStatement(statement.getSubject(), statement.getPredicate(), statement.getObject());
	}

	// essential methods

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.impl.ModelAddRemove#removeStatement(java.lang.Object,
	 *      java.net.URI, java.lang.Object)
	 */
	public abstract void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException;

	public void removeAll(Iterator<? extends Statement> other) throws ModelRuntimeException {
		while (other.hasNext()) {
			Statement stmt = other.next();
			removeStatement(stmt);
		}
	}

	public void removeAll() throws ModelRuntimeException {
		// fill temp
		Collection<Statement> temp = new LinkedList<Statement>();
		for (Statement statement : this) {
			temp.add(statement);
		}

		// delete
		for (Statement s : temp) {
			removeStatement(s);
		}
	}

	public void update(Diff diff) throws ModelRuntimeException {
		removeAll(diff.getRemoved().iterator());
		addAll(diff.getAdded().iterator());
	}

}
