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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.TriplePattern;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
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
public abstract class AbstractModel extends AbstractModelRemovePatterns
		implements Model {

	private static Logger log = LoggerFactory.getLogger(AbstractModel.class);

	/**
	 * The underlying implementation.
	 */
	protected Object model;

	/**
	 * Uses to store runtime-properties - no related to RDF at all.
	 * You could for exmaple store a user session object here.
	 */
	private Map<URI, Object> runtimeProperties = new HashMap<URI, Object>();

	/**
	 * Convenience method. Might have faster implementations.
	 */
	public boolean contains(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		assertModel();
		ClosableIterator<? extends Statement> cit = findStatements(subject,
				predicate, object);
		boolean result = cit.hasNext();
		cit.close();
		return result;
	}

	/**
	 * Convenience method.
	 */
	public boolean contains(ResourceOrVariable subject,
			UriOrVariable predicate, String plainLiteral)
			throws ModelRuntimeException {
		assertModel();
		return contains(subject, predicate, new PlainLiteralImpl(plainLiteral));
	}

	/**
	 * Convenience method.
	 */
	public boolean contains(Statement s) throws ModelRuntimeException {
		assertModel();
		return contains(s.getSubject(), s.getPredicate(), s.getObject());
	}

	/**
	 * Convenience method.
	 */
	public ClosableIterator<? extends Statement> findStatements(
			TriplePattern triplepattern) throws ModelRuntimeException {
		assertModel();
		return findStatements(triplepattern.getSubject(), triplepattern
				.getPredicate(), triplepattern.getObject());
	}

	/**
	 * inefficient. Please override.
	 */
	public long countStatements(TriplePattern pattern)
			throws ModelRuntimeException {
		assertModel();
		ClosableIterator it = findStatements(pattern);
		int count = 0;
		while (it.hasNext()) {
			count++;
			it.next();
		}
		it.close();
		return count;
	}

	// essential methods

	// core rdf2go model methods
	// /////////////////////////

	public abstract BlankNode createBlankNode();

	public URI createURI(String uriString) throws ModelRuntimeException {
		return new URIImpl(uriString);
	}

	public PlainLiteral createPlainLiteral(String literal)
			throws ModelRuntimeException {
		return new PlainLiteralImpl(literal);
	}

	public LanguageTagLiteral createLanguageTagLiteral(String literal,
			String languageTag) throws ModelRuntimeException {
		return new LanguageTagLiteralImpl(literal, languageTag);
	}

	public DatatypeLiteral createDatatypeLiteral(String literal, URI datatypeURI)
			throws ModelRuntimeException {
		return new DatatypeLiteralImpl(literal, datatypeURI);
	}

	public TriplePattern createTriplePattern(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object) {
		return new TriplePatternImpl(subject, predicate, object);
	}

	public Statement createStatement(Resource subject, URI predicate,
			Node object) {
		return new StatementImpl(getContextURI(), subject, predicate, object);
	}

	/**
	 * This is a really slow implementation, please override.
	 */
	public long size() throws ModelRuntimeException {
		assertModel();
		ClosableIterator<Statement> it = iterator();
		int count = 0;
		while (it.hasNext()) {
			count++;
			it.next();
		}
		it.close();
		return count;
	}

	/**
	 * Add an arbitrary property, this will not be persisted and is only
	 * available at runtime. This allows Model to serve as a central data model
	 * in larger applications (like SemVersion.ontoware.org)
	 * 
	 * @param propertyURI
	 * @param value
	 */
	public void setProperty(URI propertyURI, Object value) {
		runtimeProperties.put(propertyURI, value);
	}

	/**
	 * Note: This is a property of the model, not an RDF property
	 * 
	 * @param propertyURI
	 * @return stored property value for this model or null
	 */
	public Object getProperty(URI propertyURI) {
		return runtimeProperties.get(propertyURI);
	}

	public URI newRandomUniqueURI() {
		return URIGenerator.createNewRandomUniqueURI();
	}

	public Object getUnderlyingModelImplementation() {
		// This introduces an infinite loop :)
		// assertModel();
		return model;
	}

	public void setUnderlyingModelImplementation(Object o) {
		this.model = o;
	}

	/**
	 * Computes a Diff by using HashSets.
	 */
	public Diff getDiff(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		assertModel();

		Set<Statement> otherSet = new HashSet<Statement>();
		while (other.hasNext())
			otherSet.add(other.next());
		log.debug("this has " + size() + " triples, other has "
				+ otherSet.size() + " triples");

		// added Statements = present in other, but not this
		Set<Statement> added = new HashSet<Statement>();
		for (Statement s : otherSet) {
			if (!this.contains(s))
				added.add(s);
		}

		// removed = present here, but no longer in other
		Set<Statement> removed = new HashSet<Statement>();
		for (Statement s : this) {
			if (!otherSet.contains(s)) {
				log.debug("otherSet does not contain " + s);
				removed.add(s);
			}
		}

		log.debug(added.size() + " triples added, " + removed.size()
				+ " removed.");

		// The iterators ar not closable, so we don't have to close them
		return new DiffImpl(added.iterator(), removed.iterator());
	}

	/**
	 * Implementations with support for transactions should use them instead of
	 * this implementation.
	 */
	public synchronized void update(Diff diff) throws ModelRuntimeException {
		assertModel();
		for (Statement r : diff.getRemoved()) {
			removeStatement(r);
		}

		for (Statement a : diff.getAdded()) {
			addStatement(a);
		}
	}

	// ///////////
	// stubs

	/**
	 * Throws an exception if the syntax is not SPARQL
	 */
	public void readFrom(InputStream in, Syntax syntax) throws IOException,
			ModelRuntimeException {
		assertModel();
		if (syntax == Syntax.RdfXml) {
			readFrom(in);
		} else {
			throw new ModelRuntimeException("Unsupported syntax: " + syntax);
		}
	}

	/**
	 * Throws an exception if the syntax is not known
	 */
	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException {
		assertModel();
		if (syntax == Syntax.RdfXml) {
			writeTo(out);
		} else {
			throw new ModelRuntimeException("Unsupported syntax: " + syntax);
		}
	}

	/**
	 * Convenience method.
	 */
	public String serialize(Syntax syntax) throws SyntaxNotSupportedException {
		StringWriter sw = new StringWriter();
		try {
			this.writeTo(sw, syntax);
		} catch (IOException e) {
			throw new ModelRuntimeException(e);
		}
		return sw.getBuffer().toString();
	}

	/**
	 * Throws an exception if the syntax is not SPARQL
	 */
	public ClosableIterable<? extends Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			ModelRuntimeException {
		assertModel();
		if (querylanguage.equalsIgnoreCase("SPARQL")) {
			return sparqlConstruct(query);
		} else {
			throw new QueryLanguageNotSupportedException(
					"Unsupported query language: " + querylanguage);
		}
	}

	/**
	 * Throws an exception if the syntax is not SPARQL
	 */
	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, ModelRuntimeException {
		assertModel();
		if (querylanguage.equalsIgnoreCase("SPARQL")) {
			return sparqlSelect(query);
		} else {
			throw new QueryLanguageNotSupportedException(
					"Unsupported query language: " + querylanguage);
		}
	}

	private boolean open = false;

	/**
	 * Open connection to defined, unterlying implementation.
	 */
	public void open() {
		if (isOpen()) {
			log.warn("Model is already open. Ignored.");
		} else {
			this.open = true;
		}
	}

	/**
	 * Close connection to defined, unterlying implementation
	 */
	public void close() {
		if(isOpen()) {
			this.open = false;
		} else {
			log.debug("Model was closed already, ignored.");
		}
	}

	public boolean isOpen() {
		return this.open;
	}

	/**
	 * This method checks if the model is properly initialized and i.e. not
	 * closed.
	 */
	protected void assertModel() {
		if (this.getUnderlyingModelImplementation() == null) {
			throw new ModelRuntimeException("Underlying model is null.");
		}
		if (!isOpen())
			throw new ModelRuntimeException("Model is not open");

	}

}
