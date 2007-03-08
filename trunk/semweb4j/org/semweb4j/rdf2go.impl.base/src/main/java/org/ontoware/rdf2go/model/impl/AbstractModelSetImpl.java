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
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QuadPattern;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
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
import org.ontoware.rdf2go.model.node.Variable;

public abstract class AbstractModelSetImpl implements ModelSet {

	// public static final URI DEFAULT_GRAPH = URIImpl
	// .createURIWithoutChecking("http://rdf2go.ontoware.org/2006#defaultGraph");
	//
	// public Model getDefaultModel() {
	// return getModel(DEFAULT_GRAPH);
	// }

	// public Iterator<Model> getModels() {
	// // IMPROVE: can be done with SPARQL, maybe
	// throw new UnsupportedOperationException();
	// }

	// public ClosableIterator<Statement> findStatements(UriOrVariable
	// contextURI,
	// ResourceOrVariable subject, UriOrVariable predicate, NodeOrVariable
	// object)
	// throws ModelException {
	// // IMPROVE: can be done with SPARQL, maybe
	// throw new UnsupportedOperationException();
	// }

	// public boolean containsStatements(UriOrVariable contextURI,
	// ResourceOrVariable subject,
	// UriOrVariable predicate, NodeOrVariable object) throws ModelException {
	// return findStatements(contextURI, subject, predicate, object).hasNext();
	// }

	public void dump() {
		Iterator<? extends Model> it = getModels();
		while (it.hasNext()) {
			Model m = it.next();
			System.out.println("Dumping model with context: "
					+ m.getContextURI() + " ----------");
			m.dump();
			m.close();
		}
	}

	public void removeAll() throws ModelRuntimeException {
		List<Model> models = new LinkedList<Model>();
		Iterator<? extends Model> it = getModels();
		while (it.hasNext()) {
			models.add(it.next());
		}
		for (Model m : models) {
			m.removeAll();
		}
	}

	public Statement createStatement(URI context, Resource subject,
			URI predicate, Node object) {
		return new StatementImpl(context, subject, predicate, object);
	}

	class ModelIterator implements Iterator<Model> {

		private Iterator<URI> it;

		public ModelIterator(Iterator<URI> modelURIs) {
			this.it = modelURIs;
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public Model next() {
			return getModel(it.next());
		}

		public void remove() {
			it.next();
		}

	}

	public void readFrom(Reader in, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		if (syntax == Syntax.Trix) {
			readFrom(in);
		} else {
			throw new SyntaxNotSupportedException(
					"Syntax '"
							+ syntax
							+ "' not supported. Or the adapter implementor was too lazy to override thid method");
		}
	}

	public void readFrom(InputStream in, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		if (syntax == Syntax.Trix) {
			readFrom(in);
		} else {
			throw new SyntaxNotSupportedException(
					"Syntax '"
							+ syntax
							+ "' not supported. Or the adapter implementor was too lazy to override thid method");
		}
	}

	public void writeTo(Writer writer, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		if (syntax == Syntax.Trix) {
			writeTo(writer);
		} else {
			throw new SyntaxNotSupportedException(
					"Syntax '"
							+ syntax
							+ "' not supported. Or the adapter implementor was too lazy to override thid method");
		}
	}

	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		if (syntax == Syntax.Trix) {
			writeTo(out);
		} else {
			throw new SyntaxNotSupportedException(
					"Syntax '"
							+ syntax
							+ "' not supported. Or the adapter implementor was too lazy to override thid method");
		}
	}

	public String serialize( Syntax syntax ) throws SyntaxNotSupportedException {
		StringWriter sw = new StringWriter();
		try {
			this.writeTo(sw, syntax);
		} catch (IOException e) {
			throw new ModelRuntimeException(e);
		}
		return sw.getBuffer().toString();
	}
	
	public boolean containsStatements(UriOrVariable contextURI,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {

		ClosableIterator<? extends Statement> it = findStatements(contextURI,
				subject, predicate, object);
		boolean result = it.hasNext();
		it.close();
		return result;
	}

	public void addStatement(URI context, Resource subject, URI predicate,
			Node object) throws ModelRuntimeException {
		addStatement(createStatement(context, subject, predicate, object));
	}

	public void addAll(Iterator<? extends Statement> statement)
			throws ModelRuntimeException {
		while (statement.hasNext()) {
			addStatement(statement.next());
		}
	}

	public void removeStatement(URI context, Resource subject, URI predicate,
			Node object) throws ModelRuntimeException {
		removeStatement(createStatement(context, subject, predicate, object));
	}

	public void removeAll(Iterator<? extends Statement> statement)
			throws ModelRuntimeException {
		while (statement.hasNext()) {
			removeStatement(statement.next());
		}
	}

	public void removeStatements(QuadPattern quadPattern) throws ModelRuntimeException {
		removeStatements(quadPattern.getContext(), quadPattern.getSubject(),
				quadPattern.getPredicate(), quadPattern.getObject());
	}

	/**
	 * Find all models matching the context, and remove all (subject, property
	 * ,object)-statements from these model
	 * 
	 * @throws ModelRuntimeException
	 */
	public void removeStatements(UriOrVariable context,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		if (context == null) {
			this.getDefaultModel().removeStatements(subject, predicate, object);
		} else if (context == Variable.ANY) {
			// remove from all models
			Iterator<? extends Model> it = this.getModels();
			while (it.hasNext()) {
				Model m = it.next();
				m.removeStatements(subject, predicate, object);
			}
		} else {
			Model m = getModel((URI) context);
			m.removeStatements(subject, predicate, object);
		}
	}

	// implement value factory by delgating to default model

	public BlankNode createBlankNode() {
		return this.getDefaultModel().createBlankNode();
	}

	public URI createURI(String uriString) throws ModelRuntimeException {
		return this.getDefaultModel().createURI(uriString);
	}

	public boolean isValidURI(String uriString) {
		return this.getDefaultModel().isValidURI(uriString);
	}

	public PlainLiteral createPlainLiteral(String literal) {
		return this.getDefaultModel().createPlainLiteral(literal);
	}

	public LanguageTagLiteral createLanguageTagLiteral(String literal,
			String langugeTag) throws ModelRuntimeException {
		return this.getDefaultModel().createLanguageTagLiteral(literal,
				langugeTag);
	}

	public DatatypeLiteral createDatatypeLiteral(String literal, URI datatypeURI)
			throws ModelRuntimeException {
		return this.getDefaultModel().createDatatypeLiteral(literal,
				datatypeURI);
	}

	public Statement createStatement(Resource subject, URI predicate,
			Node object) {
		return this.getDefaultModel().createStatement(subject, predicate,
				object);
	}

	public URI newRandomUniqueURI() {
		return this.getDefaultModel().newRandomUniqueURI();
	}

	// naive locking

	private boolean locked = false;

	/**
	 * OVERRIDE ME!
	 */
	public boolean isLocked() {
		return this.locked;
	}

	/**
	 * OVERRIDE ME!
	 */
	public void lock() throws LockException {
		if (isLocked())
			throw new LockException("Already locked");
		this.locked = true;
	}

	/**
	 * OVERRIDE ME!
	 */
	public void unlock() {
		this.locked = false;
	}

	// findable modelset

	public boolean contains(Statement s) throws ModelRuntimeException {
		if (containsModel(s.getContext())) {
			Model m = getModel(s.getContext());
			return m.contains(s);
		}
		return false;
	}

	public long countStatements(QuadPattern pattern) throws ModelRuntimeException {
		if (pattern.getContext() == Variable.ANY) {
			// match all
			long count = 0;
			Iterator it = getModels();
			while (it.hasNext()) {
				Model m = (Model) it.next();
				count += m.countStatements(pattern);
			}
			return count;
		} else {
			assert pattern.getContext() instanceof URI;
			Model m = getModel((URI) pattern.getContext());
			return m.countStatements(pattern);
		}
	}

	public ClosableIterator<? extends Statement> findStatements(
			QuadPattern pattern) throws ModelRuntimeException {
		if (pattern.getContext() == Variable.ANY) {
			// match all
			return new LazyUnionModelIterator(this, pattern);
		} else {
			assert pattern.getContext() instanceof URI;
			Model m = getModel((URI) pattern.getContext());
			return m.findStatements(pattern);
		}
	}

	public ClosableIterator<Statement> iterator() {
		return new LazyUnionModelIterator(this, new QuadPatternImpl(
				Variable.ANY, Variable.ANY, Variable.ANY, Variable.ANY));
	}

	/**
	 * FIXME: this method will return false for empty models
	 */
	public boolean containsModel(URI contextURI) {
		ClosableIterator<? extends Statement> it = findStatements(contextURI,
				Variable.ANY, Variable.ANY, Variable.ANY);
		boolean result = it.hasNext();
		it.close();
		return result;
	}

	/**
	 * FIXME: this impl does not really delete models. It merely removes all
	 * statements from the given model. Which is wrong.
	 */
	public boolean removeModel(URI contextURI) {
		boolean containsModel = containsModel(contextURI);
		if (containsModel) {
			this.getModel(contextURI).removeAll();
		}
		return containsModel;
	}
	
	/**
	 * @throws ModelRuntimeException if the ModelSet is locked 
	 */
	public void update(Diff diff) throws ModelRuntimeException {
		synchronized (this) {
			if( this.isLocked()) {
				throw new ModelRuntimeException("ModelSet is locked, cannot perform an update.");
			}
			// remove
			Iterator<? extends Statement> it = diff.getRemoved().iterator();
			while (it.hasNext()) {
				Statement stmt = it.next();
				this.removeStatement(stmt);
			}
			// add
			it = diff.getAdded().iterator();
			while (it.hasNext()) {
				Statement stmt = it.next();
				this.addStatement(stmt);
			}
		}
	}

}
