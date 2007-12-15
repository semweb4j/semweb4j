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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.DiffReader;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QuadPattern;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
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
import org.ontoware.rdf2go.vocabulary.RDF;

public abstract class AbstractModelSetImpl implements ModelSet {

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

	/* subclasses should overwrite this method for better performance */
	public void removeAll() throws ModelRuntimeException {
		List<Model> models = new LinkedList<Model>();
		Iterator<? extends Model> it = getModels();
		while (it.hasNext()) {
			models.add(it.next());
		}
		for (Model m : models) {
			assert m.isOpen();
			m.removeAll();
		}
	}

	public Statement createStatement(URI context, Resource subject,
			URI predicate, Node object) {
		return new StatementImpl(context, subject, predicate, object);
	}

	class ModelIterator implements Iterator<Model> {

		private Iterator<URI> iterator;

		public ModelIterator(Iterator<URI> modelURIs) {
			this.iterator = modelURIs;
		}

		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		public Model next() {
			return getModel(this.iterator.next());
		}

		public void remove() {
			this.iterator.next();
		}

	}

	/* subclasses should overwrite this method to read any syntax besides TriX */
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

	/* subclasses should overwrite this method to read any syntax besides TriX */
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

	/* subclasses should overwrite this method to write any syntax besides TriX */
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

	/* subclasses should overwrite this method to write any syntax besides TriX */
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

	public String serialize(Syntax syntax) throws SyntaxNotSupportedException {
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

	/* subclasses should overwrite this method for better performance */
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

	/* subclasses should overwrite this method for better performance */
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

	public void removeStatements(QuadPattern quadPattern)
			throws ModelRuntimeException {
		removeStatements(quadPattern.getContext(), quadPattern.getSubject(),
				quadPattern.getPredicate(), quadPattern.getObject());
	}

	/* subclasses should overwrite this method for better performance */
	public void removeStatements(UriOrVariable context,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {

		ClosableIterator<? extends Statement> it = findStatements(context,
				subject, predicate, object);
		List<Statement> stmts = new LinkedList<Statement>();
		while (it.hasNext()) {
			Statement stmt = it.next();
			stmts.add(stmt);
		}
		it.close();
		for (Statement stmt : stmts) {
			this.removeStatement(stmt);
		}
	}

	// implement value factory by delgating to default model

	/* subclasses should overwrite this method for better performance */
	public BlankNode createBlankNode() {
		return this.getDefaultModel().createBlankNode();
	}

	/* subclasses should overwrite this method for better performance */
	public BlankNode createBlankNode(String internalID) {
		return this.getDefaultModel().createBlankNode(internalID);
	}

	/* subclasses should overwrite this method for better performance */
	public URI createURI(String uriString) throws ModelRuntimeException {
		return this.getDefaultModel().createURI(uriString);
	}

	/* subclasses should overwrite this method for better performance */
	public boolean isValidURI(String uriString) {
		return this.getDefaultModel().isValidURI(uriString);
	}

	/* subclasses should overwrite this method for better performance */
	public PlainLiteral createPlainLiteral(String literal) {
		return this.getDefaultModel().createPlainLiteral(literal);
	}

	/* subclasses should overwrite this method for better performance */
	public LanguageTagLiteral createLanguageTagLiteral(String literal,
			String langugeTag) throws ModelRuntimeException {
		return this.getDefaultModel().createLanguageTagLiteral(literal,
				langugeTag);
	}

	/* subclasses should overwrite this method for better performance */
	public DatatypeLiteral createDatatypeLiteral(String literal, URI datatypeURI)
			throws ModelRuntimeException {
		return this.getDefaultModel().createDatatypeLiteral(literal,
				datatypeURI);
	}

	/* subclasses should overwrite this method for better performance */
	public Statement createStatement(Resource subject, URI predicate,
			Node object) {
		return this.getDefaultModel().createStatement(subject, predicate,
				object);
	}

	/* subclasses should overwrite this method for better performance */
	public URI newRandomUniqueURI() {
		return this.getDefaultModel().newRandomUniqueURI();
	}

	// naive locking

	private boolean locked = false;

	/* subclasses should overwrite this method for better performance */
	public boolean isLocked() {
		return this.locked;
	}

	/* subclasses should overwrite this method for better performance */
	public void lock() throws LockException {
		if (isLocked())
			throw new LockException("Already locked");
		this.locked = true;
	}

	/* subclasses should overwrite this method for better performance */
	public void unlock() {
		this.locked = false;
	}

	// findable modelset

	public boolean contains(Statement s) throws ModelRuntimeException {
		QuadPattern quadPattern = new QuadPatternImpl(s.getContext(), s
				.getSubject(), s.getPredicate(), s.getObject());
		ClosableIterator<? extends Statement> x = findStatements(quadPattern);
		boolean result = x.hasNext();
		x.close();
		return result;
	}

	/* subclasses should overwrite this method for better performance */
	public long countStatements(QuadPattern pattern)
			throws ModelRuntimeException {
		if (pattern.getContext() == Variable.ANY) {
			// match all
			long count = 0;
			Iterator<?> it = getModels();
			while (it.hasNext()) {
				Model m = (Model) it.next();
				count += m.countStatements(pattern);
			}
			return count;
		}
		// else
		assert pattern.getContext() instanceof URI;
		Model m = getModel((URI) pattern.getContext());
		return m.countStatements(pattern);
	}

	/**
	 * Inefficient: Looks into each model and asks to match the triplepattern
	 * part of the quad pattern.
	 */
	/* subclasses should overwrite this method for better performance */
	public ClosableIterator<Statement> findStatements(QuadPattern pattern)
			throws ModelRuntimeException {
		if (pattern.getContext() == Variable.ANY)
			// match all
			return new LazyUnionModelIterator(this, pattern);
		// else
		assert pattern.getContext() instanceof URI;
		Model m = getModel((URI) pattern.getContext());
		return m.findStatements(pattern);
	}

	public ClosableIterator<Statement> findStatements(UriOrVariable contextURI,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		QuadPattern quadPattern = this.createQuadPattern(contextURI, subject,
				predicate, object);
		return findStatements(quadPattern);
	}

	/* subclasses should overwrite this method for better performance */
	public ClosableIterator<Statement> iterator() {
		return new LazyUnionModelIterator(this, new QuadPatternImpl(
				Variable.ANY, Variable.ANY, Variable.ANY, Variable.ANY));
	}

	/* subclasses should overwrite this method for better performance */
	public boolean addModel(Model model) {
		for (Statement s : model) {
			addStatement(model.getContextURI(), s.getSubject(), s
					.getPredicate(), s.getObject());
		}
		return true;
	}

	/**
	 * @throws ModelRuntimeException
	 *             if the ModelSet is locked
	 */
	public void update(DiffReader diff) throws ModelRuntimeException {
		synchronized (this) {
			if (this.isLocked()) {
				throw new ModelRuntimeException(
						"ModelSet is locked, cannot perform an update.");
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

	/** sublcasses should override this method for performance */
	public boolean isEmpty() {
		return size() == 0;
	}

	// work around Sesame not having this yet
	/* subclasses should overwrite this method for better performance */
	public boolean sparqlAsk(String query) throws ModelRuntimeException,
			MalformedQueryException {
		QueryResultTable table = sparqlSelect(query);
		ClosableIterator<QueryRow> it = table.iterator();
		boolean result = it.hasNext();
		it.close();
		return result;
	}

	/* fast, no need to override */
	public BlankNode createReficationOf(Statement statement) {
		BlankNode bnode = createBlankNode();
		return (BlankNode) createReficationOf(statement, bnode);
	}

	/* reifications live in the context where the statement is */
	public Resource createReficationOf(Statement statement, Resource resource) {
		Diff diff = new DiffImpl();
		diff.addStatement(createStatement(statement.getContext(), resource,
				RDF.type, RDF.Statement));
		diff.addStatement(createStatement(statement.getContext(), resource,
				RDF.subject, statement.getSubject()));
		diff.addStatement(createStatement(statement.getContext(), resource,
				RDF.predicate, statement.getPredicate()));
		diff.addStatement(createStatement(statement.getContext(), resource,
				RDF.object, statement.getObject()));
		update(diff);
		return resource;
	}

	/* ignores context */
	public boolean hasReifications(Statement statement) {
		return this.sparqlAsk("ASK WHERE { " + " ?res " + RDF.type.toSPARQL()
				+ " " + RDF.Statement + " ." + " ?res "
				+ RDF.subject.toSPARQL() + " "
				+ statement.getSubject().toSPARQL() + " ." + " ?res "
				+ RDF.predicate.toSPARQL() + " "
				+ statement.getPredicate().toSPARQL() + " ." + " ?res "
				+ RDF.object.toSPARQL() + " "
				+ statement.getObject().toSPARQL() + " ." + " }");
	}

	/*
	 * inefficient, loads all in memory. should be OK for almost all practical
	 * cases (when each statement has a small number of refications)
	 * 
	 * ignores context
	 */
	public Collection<Resource> getAllReificationsOf(Statement statement) {
		QueryResultTable table = this.sparqlSelect("SELECT ?res WHERE { "
				+ " ?res " + RDF.type.toSPARQL() + " " + RDF.Statement + " ."
				+ " ?res " + RDF.subject.toSPARQL() + " "
				+ statement.getSubject().toSPARQL() + " ." + " ?res "
				+ RDF.predicate.toSPARQL() + " "
				+ statement.getPredicate().toSPARQL() + " ." + " ?res "
				+ RDF.object.toSPARQL() + " "
				+ statement.getObject().toSPARQL() + " ." + " }");
		LinkedList<Resource> result = new LinkedList<Resource>();
		ClosableIterator<QueryRow> it = table.iterator();
		while (it.hasNext()) {
			Resource res = it.next().getValue("res").asResource();
			result.add(res);
		}
		it.close();
		return result;
	}

	/* delete in ALL contexts */
	public void deleteReification(Resource reificationResource) {
		Diff diff = new DiffImpl();
		diff.removeStatement(reificationResource, RDF.type, RDF.Statement);
		ClosableIterator<Statement> it = findStatements(Variable.ANY, reificationResource,
				RDF.subject, Variable.ANY);
		while (it.hasNext()) {
			diff.removeStatement(it.next());
		}
		it.close();
		it = findStatements(Variable.ANY, reificationResource, RDF.predicate, Variable.ANY);
		while (it.hasNext()) {
			diff.removeStatement(it.next());
		}
		it.close();
		it = findStatements(Variable.ANY, reificationResource, RDF.object, Variable.ANY);
		while (it.hasNext()) {
			diff.removeStatement(it.next());
		}
		it.close();
		update(diff);
	}

}
