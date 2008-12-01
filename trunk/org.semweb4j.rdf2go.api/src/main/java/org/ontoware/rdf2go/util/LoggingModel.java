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

package org.ontoware.rdf2go.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.DiffReader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Model that delegates to another Model and logs all calls to a SLF4j logger.
 * Ideal for performance analysis.
 * 
 * @author voelkel
 * 
 */
public class LoggingModel implements Model {
	
	private static Logger log = LoggerFactory.getLogger(LoggingModel.class);

	private Model model;

	public LoggingModel(Model m) {
		this.model = m;
	}

	public void addAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		log.debug("addAll");
		this.model.addAll(other);
	}

	public void addModel(Model model) throws ModelRuntimeException {
		log.debug("addModel");
		model.addModel(model);
	}

	public Resource addReificationOf(Statement statement, Resource resource) {
		log.debug("addReificationOf");
		return this.model.addReificationOf(statement, resource);
	}

	public BlankNode addReificationOf(Statement statement) {
		log.debug("addReificationOf");
		return this.model.addReificationOf(statement);
	}

	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subject, predicate, object);
	}

	public void addStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subject, predicate, literal, languageTag);
	}

	public void addStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subject, predicate, literal, datatypeURI);
	}

	public void addStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subject, predicate, literal);
	}

	public void addStatement(Statement statement) throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(statement);
	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subjectURIString, predicate, literal, languageTag);
	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subjectURIString, predicate, literal, datatypeURI);
	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subjectURIString, predicate, literal);
	}

	public void close() {
		log.debug("close");
		this.model.close();
	}

	public void commit() throws ModelRuntimeException {
		log.debug("commit");
		this.model.commit();
	}

	public boolean contains(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		log.debug("contains "+subject+" "+predicate+" "+object);
		return this.model.contains(subject, predicate, object);
	}

	public boolean contains(ResourceOrVariable subject,
			UriOrVariable predicate, String plainLiteral)
			throws ModelRuntimeException {
		log.debug("contains "+subject+" "+predicate+" "+plainLiteral);
		return this.model.contains(subject, predicate, plainLiteral);
	}

	public boolean contains(Statement s) throws ModelRuntimeException {
		log.debug("contains "+s);
		return this.model.contains(s);
	}

	public long countStatements(TriplePattern pattern)
			throws ModelRuntimeException {
		log.debug("countStatements");
		return this.model.countStatements(pattern);
	}

	public BlankNode createBlankNode() {
		log.debug("create");
		return this.model.createBlankNode();
	}

	public BlankNode createBlankNode(String internalID) {
		log.debug("create");
		return this.model.createBlankNode(internalID);
	}

	public DatatypeLiteral createDatatypeLiteral(String literal, URI datatypeURI)
			throws ModelRuntimeException {
		log.debug("create");
		return this.model.createDatatypeLiteral(literal, datatypeURI);
	}

	public LanguageTagLiteral createLanguageTagLiteral(String literal,
			String langugeTag) throws ModelRuntimeException {
		log.debug("create");
		return this.model.createLanguageTagLiteral(literal, langugeTag);
	}

	public PlainLiteral createPlainLiteral(String literal) {
		log.debug("create");
		return this.model.createPlainLiteral(literal);
	}

	public Statement createStatement(Resource subject, URI predicate,
			Node object) {
		log.debug("create");
		return this.model.createStatement(subject, predicate, object);
	}

	public TriplePattern createTriplePattern(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object) {
		log.debug("create");
		return this.model.createTriplePattern(subject, predicate, object);
	}

	public URI createURI(String uriString) throws IllegalArgumentException {
		log.debug("create");
		return this.model.createURI(uriString);
	}

	public void deleteReification(Resource reificationResource) {
		log.debug("create");
		this.model.deleteReification(reificationResource);
	}

	public void dump() {
		log.debug("dump");
		this.model.dump();
	}

	public ClosableIterator<Statement> findStatements(
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		log.debug("findStatements "+subject+ " "+predicate+" "+object);
		return this.model.findStatements(subject, predicate, object);
	}

	public ClosableIterator<Statement> findStatements(TriplePattern pattern)
			throws ModelRuntimeException {
		log.debug("findStatements "+pattern);
		return this.model.findStatements(pattern);
	}

	public Collection<Resource> getAllReificationsOf(Statement statement) {
		log.debug("getAllReificationsOf");
		return this.model.getAllReificationsOf(statement);
	}

	public URI getContextURI() {
		log.debug("getContextURI");
		return this.model.getContextURI();
	}

	public Diff getDiff(Iterator<? extends Statement> statements)
			throws ModelRuntimeException {
		log.debug("getDiff");
		return this.model.getDiff(statements);
	}

	public String getNamespace(String prefix) {
		log.debug("getNamespace");
		return this.model.getNamespace(prefix);
	}

	public Map<String, String> getNamespaces() {
		log.debug("getNamespaces");
		return this.model.getNamespaces();
	}

	public Object getProperty(URI propertyURI) {
		log.debug("getProperty");
		return this.model.getProperty(propertyURI);
	}

	public Object getUnderlyingModelImplementation() {
		log.debug("getUnderlyingModelImplementation");
		return this.model.getUnderlyingModelImplementation();
	}

	public boolean hasReifications(Statement stmt) {
		log.debug("hasReifications");
		return this.model.hasReifications(stmt);
	}

	public boolean isEmpty() {
		log.debug("isEmpty");
		return this.model.isEmpty();
	}

	public boolean isIsomorphicWith(Model other) {
		log.debug("isIsomorphicWith");
		return this.model.isIsomorphicWith(other);
	}

	public boolean isLocked() {
		log.debug("isLocked");
		return this.model.isLocked();
	}

	public boolean isOpen() {
		log.debug("isOpen");
		return this.model.isOpen();
	}

	public boolean isValidURI(String uriString) {
		log.debug("isValidURI");
		return this.model.isValidURI(uriString);
	}

	public ClosableIterator<Statement> iterator() {
		log.debug("iterator");
		return this.model.iterator();
	}

	public void lock() throws LockException {
		log.debug("lock");
		this.model.lock();
	}

	public URI newRandomUniqueURI() {
		log.debug("newRandomUniqueURI");
		return this.model.newRandomUniqueURI();
	}

	public Model open() {
		log.debug("open");
		return this.model.open();
	}

	public ClosableIterable<Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			MalformedQueryException, ModelRuntimeException {
		log.debug("query");
		return this.model.queryConstruct(query, querylanguage);
	}

	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, MalformedQueryException,
			ModelRuntimeException {
		log.debug("query");
		return this.model.querySelect(query, querylanguage);
	}

	public void readFrom(InputStream in, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in, syntax, baseURI);
	}

	public void readFrom(InputStream reader, Syntax syntax) throws IOException,
			ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(reader, syntax);
	}

	public void readFrom(InputStream in) throws IOException,
			ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in);
	}

	public void readFrom(Reader in, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in, syntax, baseURI);
	}

	public void readFrom(Reader in, Syntax syntax) throws IOException,
			ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in, syntax);
	}

	public void readFrom(Reader in) throws IOException, ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in);
	}

	public void removeAll() throws ModelRuntimeException {
		log.debug("removeAll");
		this.model.removeAll();
	}

	public void removeAll(Iterator<? extends Statement> statements)
			throws ModelRuntimeException {
		log.debug("removeAll");
		this.model.removeAll(statements);
	}

	public void removeNamespace(String prefix) {
		log.debug("removeNamespace");
		this.model.removeNamespace(prefix);
	}

	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subject, predicate, object);
	}

	public void removeStatement(Resource subject, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subject, predicate, literal, languageTag);
	}

	public void removeStatement(Resource subject, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subject, predicate, literal, datatypeURI);
	}

	public void removeStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subject, predicate, literal);
	}

	public void removeStatement(Statement statement)
			throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(statement);
	}

	public void removeStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model
				.removeStatement(subjectURIString, predicate, literal,
						languageTag);
	}

	public void removeStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model
				.removeStatement(subjectURIString, predicate, literal,
						datatypeURI);
	}

	public void removeStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subjectURIString, predicate, literal);
	}

	public void removeStatements(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatements(subject, predicate, object);
	}

	public void removeStatements(TriplePattern triplePattern)
			throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatements(triplePattern);
	}

	public String serialize(Syntax syntax) throws SyntaxNotSupportedException {
		log.debug("serialize");
		return this.model.serialize(syntax);
	}

	public void setAutocommit(boolean autocommit) {
		log.debug("setAutocommit");
		this.model.setAutocommit(autocommit);
	}

	public void setNamespace(String prefix, String namespaceURI)
			throws IllegalArgumentException {
		log.debug("setNamespace");
		this.model.setNamespace(prefix, namespaceURI);
	}

	public void setProperty(URI propertyURI, Object value) {
		log.debug("setProperty");
		this.model.setProperty(propertyURI, value);
	}

	public long size() throws ModelRuntimeException {
		log.debug("size");
		return this.model.size();
	}

	public boolean sparqlAsk(String query) throws ModelRuntimeException,
			MalformedQueryException {
		log.debug("sparqlAsk");
		return this.model.sparqlAsk(query);
	}

	public ClosableIterable<Statement> sparqlConstruct(String query)
			throws ModelRuntimeException, MalformedQueryException {
		log.debug("sparqlConstruct");
		return this.model.sparqlConstruct(query);
	}

	public ClosableIterable<Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		log.debug("sparqlDescribe");
		return this.model.sparqlDescribe(query);
	}

	public QueryResultTable sparqlSelect(String queryString)
			throws MalformedQueryException, ModelRuntimeException {
		log.debug("sparqlSelect");
		return this.model.sparqlSelect(queryString);
	}

	public void unlock() {
		log.debug("unlock");
		this.model.unlock();
	}

	@Deprecated
	public void update(Diff diff) throws ModelRuntimeException {
		log.debug("update");
		this.model.update(diff);
	}

	public void update(DiffReader diff) throws ModelRuntimeException {
		log.debug("update");
		this.model.update(diff);
	}

	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException {
		log.debug("write");
		this.model.writeTo(out, syntax);
	}

	public void writeTo(OutputStream out) throws IOException,
			ModelRuntimeException {
		log.debug("write");
		this.model.writeTo(out);
	}

	public void writeTo(Writer out, Syntax syntax) throws IOException,
			ModelRuntimeException {
		log.debug("write");
		this.model.writeTo(out, syntax);
	}

	public void writeTo(Writer out) throws IOException, ModelRuntimeException {
		log.debug("write");
		this.model.writeTo(out);
	}
	
	
	
}
