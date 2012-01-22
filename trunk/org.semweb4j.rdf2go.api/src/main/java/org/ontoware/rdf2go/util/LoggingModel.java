/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2010
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
	
	/**
     * 
     */
	private static final long serialVersionUID = 5498871553897892116L;
	
	private static Logger log = LoggerFactory.getLogger(LoggingModel.class);
	
	private Model model;
	
	public LoggingModel(Model m) {
		this.model = m;
	}
	
	@Override
	public void addAll(Iterator<? extends Statement> other) throws ModelRuntimeException {
		log.debug("addAll");
		this.model.addAll(other);
	}
	
	@Override
	public void addModel(Model model) throws ModelRuntimeException {
		log.debug("addModel");
		model.addModel(model);
	}
	
	@Override
	public Resource addReificationOf(Statement statement, Resource resource) {
		log.debug("addReificationOf");
		return this.model.addReificationOf(statement, resource);
	}
	
	@Override
	public BlankNode addReificationOf(Statement statement) {
		log.debug("addReificationOf");
		return this.model.addReificationOf(statement);
	}
	
	@Override
	public void addStatement(Resource subject, URI predicate, Node object)
	        throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subject, predicate, object);
	}
	
	@Override
	public void addStatement(Resource subject, URI predicate, String literal, String languageTag)
	        throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subject, predicate, literal, languageTag);
	}
	
	@Override
	public void addStatement(Resource subject, URI predicate, String literal, URI datatypeURI)
	        throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subject, predicate, literal, datatypeURI);
	}
	
	@Override
	public void addStatement(Resource subject, URI predicate, String literal)
	        throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subject, predicate, literal);
	}
	
	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(statement);
	}
	
	@Override
	public void addStatement(String subjectURIString, URI predicate, String literal,
	        String languageTag) throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subjectURIString, predicate, literal, languageTag);
	}
	
	@Override
	public void addStatement(String subjectURIString, URI predicate, String literal, URI datatypeURI)
	        throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subjectURIString, predicate, literal, datatypeURI);
	}
	
	@Override
	public void addStatement(String subjectURIString, URI predicate, String literal)
	        throws ModelRuntimeException {
		log.debug("addStatement");
		this.model.addStatement(subjectURIString, predicate, literal);
	}
	
	@Override
	public void close() {
		log.debug("close");
		this.model.close();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	@Deprecated
	public void commit() throws ModelRuntimeException {
		log.debug("commit");
		this.model.commit();
	}
	
	@Override
	public boolean contains(ResourceOrVariable subject, UriOrVariable predicate,
	        NodeOrVariable object) throws ModelRuntimeException {
		log.debug("contains " + subject + " " + predicate + " " + object);
		return this.model.contains(subject, predicate, object);
	}
	
	@Override
	public boolean contains(ResourceOrVariable subject, UriOrVariable predicate, String plainLiteral)
	        throws ModelRuntimeException {
		log.debug("contains " + subject + " " + predicate + " " + plainLiteral);
		return this.model.contains(subject, predicate, plainLiteral);
	}
	
	@Override
	public boolean contains(Statement s) throws ModelRuntimeException {
		log.debug("contains " + s);
		return this.model.contains(s);
	}
	
	@Override
	public long countStatements(TriplePattern pattern) throws ModelRuntimeException {
		log.debug("countStatements");
		return this.model.countStatements(pattern);
	}
	
	@Override
	public BlankNode createBlankNode() {
		log.debug("create");
		return this.model.createBlankNode();
	}
	
	@Override
	public BlankNode createBlankNode(String internalID) {
		log.debug("create");
		return this.model.createBlankNode(internalID);
	}
	
	@Override
	public DatatypeLiteral createDatatypeLiteral(String literal, URI datatypeURI)
	        throws ModelRuntimeException {
		log.debug("create");
		return this.model.createDatatypeLiteral(literal, datatypeURI);
	}
	
	@Override
	public LanguageTagLiteral createLanguageTagLiteral(String literal, String langugeTag)
	        throws ModelRuntimeException {
		log.debug("create");
		return this.model.createLanguageTagLiteral(literal, langugeTag);
	}
	
	@Override
	public PlainLiteral createPlainLiteral(String literal) {
		log.debug("create");
		return this.model.createPlainLiteral(literal);
	}
	
	@Override
	public Statement createStatement(Resource subject, URI predicate, Node object) {
		log.debug("create");
		return this.model.createStatement(subject, predicate, object);
	}
	
	@Override
	public TriplePattern createTriplePattern(ResourceOrVariable subject, UriOrVariable predicate,
	        NodeOrVariable object) {
		log.debug("create");
		return this.model.createTriplePattern(subject, predicate, object);
	}
	
	@Override
	public URI createURI(String uriString) throws IllegalArgumentException {
		log.debug("create");
		return this.model.createURI(uriString);
	}
	
	@Override
	public void deleteReification(Resource reificationResource) {
		log.debug("create");
		this.model.deleteReification(reificationResource);
	}
	
	@Override
	public void dump() {
		log.debug("dump");
		this.model.dump();
	}
	
	@Override
	public ClosableIterator<Statement> findStatements(ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException {
		log.debug("findStatements " + subject + " " + predicate + " " + object);
		return this.model.findStatements(subject, predicate, object);
	}
	
	@Override
	public ClosableIterator<Statement> findStatements(TriplePattern pattern)
	        throws ModelRuntimeException {
		log.debug("findStatements " + pattern);
		return this.model.findStatements(pattern);
	}
	
	@Override
	public Collection<Resource> getAllReificationsOf(Statement statement) {
		log.debug("getAllReificationsOf");
		return this.model.getAllReificationsOf(statement);
	}
	
	@Override
	public URI getContextURI() {
		log.debug("getContextURI");
		return this.model.getContextURI();
	}
	
	@Override
	public Diff getDiff(Iterator<? extends Statement> statements) throws ModelRuntimeException {
		log.debug("getDiff");
		return this.model.getDiff(statements);
	}
	
	@Override
	public String getNamespace(String prefix) {
		log.debug("getNamespace");
		return this.model.getNamespace(prefix);
	}
	
	@Override
	public Map<String,String> getNamespaces() {
		log.debug("getNamespaces");
		return this.model.getNamespaces();
	}
	
	@Override
	public Object getProperty(URI propertyURI) {
		log.debug("getProperty");
		return this.model.getProperty(propertyURI);
	}
	
	@Override
	public Object getUnderlyingModelImplementation() {
		log.debug("getUnderlyingModelImplementation");
		return this.model.getUnderlyingModelImplementation();
	}
	
	@Override
	public boolean hasReifications(Statement stmt) {
		log.debug("hasReifications");
		return this.model.hasReifications(stmt);
	}
	
	@Override
	public boolean isEmpty() {
		log.debug("isEmpty");
		return this.model.isEmpty();
	}
	
	@Override
	public boolean isIsomorphicWith(Model other) {
		log.debug("isIsomorphicWith");
		return this.model.isIsomorphicWith(other);
	}
	
	@Override
	public boolean isLocked() {
		log.debug("isLocked");
		return this.model.isLocked();
	}
	
	@Override
	public boolean isOpen() {
		log.debug("isOpen");
		return this.model.isOpen();
	}
	
	@Override
	public boolean isValidURI(String uriString) {
		log.debug("isValidURI");
		return this.model.isValidURI(uriString);
	}
	
	@Override
	public ClosableIterator<Statement> iterator() {
		log.debug("iterator");
		return this.model.iterator();
	}
	
	@Override
	public void lock() throws LockException {
		log.debug("lock");
		this.model.lock();
	}
	
	@Override
	public URI newRandomUniqueURI() {
		log.debug("newRandomUniqueURI");
		return this.model.newRandomUniqueURI();
	}
	
	@Override
	public Model open() {
		log.debug("open");
		return this.model.open();
	}
	
	@Override
	public ClosableIterable<Statement> queryConstruct(String query, String querylanguage)
	        throws QueryLanguageNotSupportedException, MalformedQueryException,
	        ModelRuntimeException {
		log.debug("query");
		return this.model.queryConstruct(query, querylanguage);
	}
	
	@Override
	public QueryResultTable querySelect(String query, String querylanguage)
	        throws QueryLanguageNotSupportedException, MalformedQueryException,
	        ModelRuntimeException {
		log.debug("query");
		return this.model.querySelect(query, querylanguage);
	}
	
	@Override
	public void readFrom(InputStream in, Syntax syntax, String baseURI) throws IOException,
	        ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in, syntax, baseURI);
	}
	
	@Override
	public void readFrom(InputStream reader, Syntax syntax) throws IOException,
	        ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(reader, syntax);
	}
	
	@Override
	public void readFrom(InputStream in) throws IOException, ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in);
	}
	
	@Override
	public void readFrom(Reader in, Syntax syntax, String baseURI) throws IOException,
	        ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in, syntax, baseURI);
	}
	
	@Override
	public void readFrom(Reader in, Syntax syntax) throws IOException, ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in, syntax);
	}
	
	@Override
	public void readFrom(Reader in) throws IOException, ModelRuntimeException {
		log.debug("read");
		this.model.readFrom(in);
	}
	
	@Override
	public void removeAll() throws ModelRuntimeException {
		log.debug("removeAll");
		this.model.removeAll();
	}
	
	@Override
	public void removeAll(Iterator<? extends Statement> statements) throws ModelRuntimeException {
		log.debug("removeAll");
		this.model.removeAll(statements);
	}
	
	@Override
	public void removeNamespace(String prefix) {
		log.debug("removeNamespace");
		this.model.removeNamespace(prefix);
	}
	
	@Override
	public void removeStatement(Resource subject, URI predicate, Node object)
	        throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subject, predicate, object);
	}
	
	@Override
	public void removeStatement(Resource subject, URI predicate, String literal, String languageTag)
	        throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subject, predicate, literal, languageTag);
	}
	
	@Override
	public void removeStatement(Resource subject, URI predicate, String literal, URI datatypeURI)
	        throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subject, predicate, literal, datatypeURI);
	}
	
	@Override
	public void removeStatement(Resource subject, URI predicate, String literal)
	        throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subject, predicate, literal);
	}
	
	@Override
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(statement);
	}
	
	@Override
	public void removeStatement(String subjectURIString, URI predicate, String literal,
	        String languageTag) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subjectURIString, predicate, literal, languageTag);
	}
	
	@Override
	public void removeStatement(String subjectURIString, URI predicate, String literal,
	        URI datatypeURI) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subjectURIString, predicate, literal, datatypeURI);
	}
	
	@Override
	public void removeStatement(String subjectURIString, URI predicate, String literal)
	        throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatement(subjectURIString, predicate, literal);
	}
	
	@Override
	public void removeStatements(ResourceOrVariable subject, UriOrVariable predicate,
	        NodeOrVariable object) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatements(subject, predicate, object);
	}
	
	@Override
	public void removeStatements(TriplePattern triplePattern) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.model.removeStatements(triplePattern);
	}
	
	@Override
	public String serialize(Syntax syntax) throws SyntaxNotSupportedException {
		log.debug("serialize");
		return this.model.serialize(syntax);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	@Deprecated
	public void setAutocommit(boolean autocommit) {
		log.debug("setAutocommit");
		this.model.setAutocommit(autocommit);
	}
	
	@Override
	public void setNamespace(String prefix, String namespaceURI) throws IllegalArgumentException {
		log.debug("setNamespace");
		this.model.setNamespace(prefix, namespaceURI);
	}
	
	@Override
	public void setProperty(URI propertyURI, Object value) {
		log.debug("setProperty");
		this.model.setProperty(propertyURI, value);
	}
	
	@Override
	public long size() throws ModelRuntimeException {
		log.debug("size");
		return this.model.size();
	}
	
	@Override
	public boolean sparqlAsk(String query) throws ModelRuntimeException, MalformedQueryException {
		log.debug("sparqlAsk");
		return this.model.sparqlAsk(query);
	}
	
	@Override
	public ClosableIterable<Statement> sparqlConstruct(String query) throws ModelRuntimeException,
	        MalformedQueryException {
		log.debug("sparqlConstruct");
		return this.model.sparqlConstruct(query);
	}
	
	@Override
	public ClosableIterable<Statement> sparqlDescribe(String query) throws ModelRuntimeException {
		log.debug("sparqlDescribe");
		return this.model.sparqlDescribe(query);
	}
	
	@Override
	public QueryResultTable sparqlSelect(String queryString) throws MalformedQueryException,
	        ModelRuntimeException {
		log.debug("sparqlSelect");
		return this.model.sparqlSelect(queryString);
	}
	
	@Override
	public void unlock() {
		log.debug("unlock");
		this.model.unlock();
	}
	
	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
		log.debug("update");
		this.model.update(diff);
	}
	
	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws IOException, ModelRuntimeException {
		log.debug("write");
		this.model.writeTo(out, syntax);
	}
	
	@Override
	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		log.debug("write");
		this.model.writeTo(out);
	}
	
	@Override
	public void writeTo(Writer out, Syntax syntax) throws IOException, ModelRuntimeException {
		log.debug("write");
		this.model.writeTo(out, syntax);
	}
	
	@Override
	public void writeTo(Writer out) throws IOException, ModelRuntimeException {
		log.debug("write");
		this.model.writeTo(out);
	}
	
}
