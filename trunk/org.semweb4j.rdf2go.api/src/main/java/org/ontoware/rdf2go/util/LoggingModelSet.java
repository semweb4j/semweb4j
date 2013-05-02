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
import org.ontoware.rdf2go.model.DiffReader;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QuadPattern;
import org.ontoware.rdf2go.model.QueryResultTable;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Model that delegates to another Model and logs all calls to a SLF4j logger.
 * Ideal for performance analysis.
 * 
 * @author voelkel
 * 
 */
public class LoggingModelSet implements ModelSet {
	
	/**
     * 
     */
	private static final long serialVersionUID = -5697227201151338229L;
	
	private static Logger log = LoggerFactory.getLogger(LoggingModelSet.class);
	
	ModelSet modelset;
	
	public LoggingModelSet(ModelSet modelset) {
		this.modelset = modelset;
	}
	
	@Override
	public void addAll(Iterator<? extends Statement> statement) throws ModelRuntimeException {
		log.debug("addAll");
		this.modelset.addAll(statement);
	}
	
	@Override
	public void addModel(Model model, URI contextURI) throws ModelRuntimeException {
		log.debug("addModel");
		this.modelset.addModel(model, contextURI);
	}
	
	@Override
	public boolean addModel(Model model) {
		log.debug("addModel");
		return this.modelset.addModel(model);
	}
	
	@Override
	public void addModelSet(ModelSet modelSet) throws ModelRuntimeException {
		log.debug("addModelSet");
		this.modelset.addModelSet(modelSet);
	}
	
	@Override
	public Resource addReificationOf(Statement statement, Resource resource) {
		log.debug("addReificationOf");
		return this.modelset.addReificationOf(statement, resource);
	}
	
	@Override
	public BlankNode addReificationOf(Statement statement) {
		log.debug("addReificationOf");
		return this.modelset.addReificationOf(statement);
	}
	
	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		log.debug("addStatement");
		this.modelset.addStatement(statement);
	}
	
	@Override
	public void addStatement(URI contextURI, Resource subject, URI predicate, Node object)
	        throws ModelRuntimeException {
		log.debug("addStatement");
		this.modelset.addStatement(contextURI, subject, predicate, object);
	}
	
	@Override
	public void close() {
		log.debug("close");
		this.modelset.close();
	}
	
	@Override
	@Deprecated
	public void commit() throws ModelRuntimeException {
		log.debug("commit");
		this.modelset.commit();
	}
	
	@Override
	public boolean contains(Statement s) throws ModelRuntimeException {
		log.debug("contains");
		return this.modelset.contains(s);
	}
	
	@Override
	public boolean containsModel(URI contextURI) {
		log.debug("containsModel");
		return this.modelset.containsModel(contextURI);
	}
	
	@Override
	public boolean containsStatements(UriOrVariable contextURI, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException {
		log.debug("containsStatements");
		return this.modelset.containsStatements(contextURI, subject, predicate, object);
	}
	
	@Override
	public long countStatements(QuadPattern pattern) throws ModelRuntimeException {
		log.debug("countStatements");
		return this.modelset.countStatements(pattern);
	}
	
	@Override
	public BlankNode createBlankNode() {
		log.debug("createBlankNode");
		return this.modelset.createBlankNode();
	}
	
	@Override
	public BlankNode createBlankNode(String internalID) {
		log.debug("createBlankNode");
		return this.modelset.createBlankNode(internalID);
	}
	
	@Override
	public DatatypeLiteral createDatatypeLiteral(String literal, URI datatypeURI)
	        throws ModelRuntimeException {
		log.debug("createDatatypeLiteral");
		return this.modelset.createDatatypeLiteral(literal, datatypeURI);
	}
	
	@Override
	public LanguageTagLiteral createLanguageTagLiteral(String literal, String langugeTag)
	        throws ModelRuntimeException {
		log.debug("createLanguageTagLiteral");
		return this.modelset.createLanguageTagLiteral(literal, langugeTag);
	}
	
	@Override
	public PlainLiteral createPlainLiteral(String literal) {
		log.debug("createPlainLiteral");
		return this.modelset.createPlainLiteral(literal);
	}
	
	@Override
	public QuadPattern createQuadPattern(UriOrVariable context, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) {
		log.debug("createQuadPattern");
		return this.modelset.createQuadPattern(context, subject, predicate, object);
	}
	
	@Override
	public Statement createStatement(Resource subject, URI predicate, Node object) {
		log.debug("createStatement");
		return this.modelset.createStatement(subject, predicate, object);
	}
	
	@Override
	public Statement createStatement(URI context, Resource subject, URI predicate, Node object) {
		log.debug("createStatement");
		return this.modelset.createStatement(context, subject, predicate, object);
	}
	
	@Override
	public URI createURI(String uriString) throws ModelRuntimeException {
		log.debug("createURI");
		return this.modelset.createURI(uriString);
	}
	
	@Override
	public void deleteReification(Resource reificationResource) {
		log.debug("deleteReification");
		this.modelset.deleteReification(reificationResource);
	}
	
	@Override
	public void dump() {
		log.debug("dump");
		this.modelset.dump();
	}
	
	@Override
	public ClosableIterator<Statement> findStatements(QuadPattern pattern)
	        throws ModelRuntimeException {
		log.debug("findStatements");
		return this.modelset.findStatements(pattern);
	}
	
	@Override
	public ClosableIterator<Statement> findStatements(UriOrVariable contextURI,
	        ResourceOrVariable subject, UriOrVariable predicate, NodeOrVariable object)
	        throws ModelRuntimeException {
		log.debug("findStatements");
		return this.modelset.findStatements(contextURI, subject, predicate, object);
	}
	
	@Override
	public Collection<Resource> getAllReificationsOf(Statement statement) {
		log.debug("getAllReificationsOf");
		return this.modelset.getAllReificationsOf(statement);
	}
	
	@Override
	public Model getDefaultModel() {
		log.debug("getDefaultModel");
		return new LoggingModel(this.modelset.getDefaultModel());
	}
	
	@Override
	public Model getModel(URI contextURI) {
		log.debug("getModel for URI " + contextURI);
		return new LoggingModel(this.modelset.getModel(contextURI));
	}
	
	@Override
	public ClosableIterator<Model> getModels() {
		log.debug("getModels");
		return this.modelset.getModels();
	}
	
	@Override
	public ClosableIterator<URI> getModelURIs() {
		log.debug("getModelURIs");
		return this.modelset.getModelURIs();
	}
	
	@Override
	public String getNamespace(String prefix) {
		log.debug("getNamespace");
		return this.modelset.getNamespace(prefix);
	}
	
	@Override
	public Map<String,String> getNamespaces() {
		log.debug("getNamespaces");
		return this.modelset.getNamespaces();
	}
	
	@Override
	public Object getUnderlyingModelSetImplementation() {
		log.debug("getUnderlyingModelSetImplementation");
		return this.modelset.getUnderlyingModelSetImplementation();
	}
	
	@Override
	public boolean hasReifications(Statement stmt) {
		log.debug("hasReifications");
		return this.modelset.hasReifications(stmt);
	}
	
	@Override
	public boolean isEmpty() {
		log.debug("isEmpty");
		return this.modelset.isEmpty();
	}
	
	@Override
	public boolean isLocked() {
		log.debug("isLocked");
		return this.modelset.isLocked();
	}
	
	@Override
	public boolean isOpen() {
		log.debug("isOpen");
		return this.modelset.isOpen();
	}
	
	@Override
	public boolean isValidURI(String uriString) {
		log.debug("isValidURI");
		return this.modelset.isValidURI(uriString);
	}
	
	@Override
	public ClosableIterator<Statement> iterator() {
		log.debug("iterator");
		return this.modelset.iterator();
	}
	
	@Override
	public void lock() throws LockException {
		log.debug("lock");
		this.modelset.lock();
	}
	
	@Override
	public URI newRandomUniqueURI() {
		log.debug("newRandomUniqueURI");
		return this.modelset.newRandomUniqueURI();
	}
	
	@Override
	public ModelSet open() {
		log.debug("open");
		this.modelset.open();
		return this;
	}
	
	@Override
	public ClosableIterable<Statement> queryConstruct(String query, String querylanguage)
	        throws QueryLanguageNotSupportedException, MalformedQueryException,
	        ModelRuntimeException {
		log.debug("queryConstruct");
		return this.modelset.queryConstruct(query, querylanguage);
	}
	
	@Override
	public QueryResultTable querySelect(String query, String querylanguage)
	        throws QueryLanguageNotSupportedException, MalformedQueryException,
	        ModelRuntimeException {
		log.debug("querySelect");
		return this.modelset.querySelect(query, querylanguage);
	}
	
	@Override
	public void readFrom(InputStream reader, Syntax syntax, String baseURI) throws IOException,
	        ModelRuntimeException, SyntaxNotSupportedException {
		log.debug("read");
		this.modelset.readFrom(reader, syntax, baseURI);
	}
	
	@Override
	public void readFrom(InputStream reader, Syntax syntax) throws IOException,
	        ModelRuntimeException, SyntaxNotSupportedException {
		log.debug("read");
		this.modelset.readFrom(reader, syntax);
	}
	
	@Override
	public void readFrom(InputStream in) throws IOException, ModelRuntimeException {
		log.debug("read");
		this.modelset.readFrom(in);
	}
	
	@Override
	public void readFrom(Reader in, Syntax syntax, String baseURI) throws IOException,
	        ModelRuntimeException, SyntaxNotSupportedException {
		log.debug("read");
		this.modelset.readFrom(in, syntax, baseURI);
	}
	
	@Override
	public void readFrom(Reader in, Syntax syntax) throws IOException, ModelRuntimeException,
	        SyntaxNotSupportedException {
		log.debug("read");
		this.modelset.readFrom(in, syntax);
	}
	
	@Override
	public void readFrom(Reader in) throws IOException, ModelRuntimeException {
		log.debug("read");
		this.modelset.readFrom(in);
	}
	
	@Override
	public void removeAll() throws ModelRuntimeException {
		log.debug("removeAll");
		this.modelset.removeAll();
	}
	
	@Override
	public void removeAll(Iterator<? extends Statement> statement) throws ModelRuntimeException {
		log.debug("removeAll");
		this.modelset.removeAll(statement);
	}
	
	@Override
	public boolean removeModel(URI contextURI) {
		log.debug("removeModel " + contextURI);
		return this.modelset.removeModel(contextURI);
	}
	
	@Override
	public void removeNamespace(String prefix) {
		log.debug("OTHER");
		this.modelset.removeNamespace(prefix);
	}
	
	@Override
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		log.debug("removeStatement");
		this.modelset.removeStatement(statement);
	}
	
	@Override
	public void removeStatement(URI contextURI, Resource subject, URI predicate, Node object)
	        throws ModelRuntimeException {
		log.debug("removeStatement");
		this.modelset.removeStatement(contextURI, subject, predicate, object);
	}
	
	@Override
	public void removeStatements(QuadPattern quadPattern) throws ModelRuntimeException {
		log.debug("removeStatements");
		this.modelset.removeStatements(quadPattern);
	}
	
	@Override
	public void removeStatements(UriOrVariable context, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException {
		log.debug("removeStatemens");
		this.modelset.removeStatements(context, subject, predicate, object);
	}
	
	@Override
	public String serialize(Syntax syntax) throws SyntaxNotSupportedException {
		log.debug("serialize");
		return this.modelset.serialize(syntax);
	}
	
	@Override
	@Deprecated
	public void setAutocommit(boolean autocommit) {
		log.debug("setAutocommit");
		this.modelset.setAutocommit(autocommit);
	}
	
	@Override
	public void setNamespace(String prefix, String namespaceURI) throws IllegalArgumentException {
		log.debug("setNamespace");
		this.modelset.setNamespace(prefix, namespaceURI);
	}
	
	@Override
	public long size() throws ModelRuntimeException {
		log.debug("size");
		return this.modelset.size();
	}
	
	@Override
	public boolean sparqlAsk(String query) throws ModelRuntimeException, MalformedQueryException {
		log.debug("sparqlAsk");
		return this.modelset.sparqlAsk(query);
	}
	
	@Override
	public ClosableIterable<Statement> sparqlConstruct(String query) throws ModelRuntimeException,
	        MalformedQueryException {
		log.debug("sparqlConstruct");
		return this.modelset.sparqlConstruct(query);
	}
	
	@Override
	public ClosableIterable<Statement> sparqlDescribe(String query) throws ModelRuntimeException {
		log.debug("sparqlDescribe");
		return this.modelset.sparqlDescribe(query);
	}
	
	@Override
	public QueryResultTable sparqlSelect(String queryString) throws MalformedQueryException,
	        ModelRuntimeException {
		log.debug("sparqlSelect");
		return this.modelset.sparqlSelect(queryString);
	}
	
	@Override
	public void unlock() {
		log.debug("unlock");
		this.modelset.unlock();
	}
	
	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
		log.debug("update");
		this.modelset.update(diff);
	}
	
	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws IOException, ModelRuntimeException,
	        SyntaxNotSupportedException {
		log.debug("write");
		this.modelset.writeTo(out, syntax);
	}
	
	@Override
	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		log.debug("write");
		this.modelset.writeTo(out);
	}
	
	@Override
	public void writeTo(Writer out, Syntax syntax) throws IOException, ModelRuntimeException,
	        SyntaxNotSupportedException {
		log.debug("write");
		this.modelset.writeTo(out, syntax);
	}
	
	@Override
	public void writeTo(Writer out) throws IOException, ModelRuntimeException {
		log.debug("write");
		this.modelset.writeTo(out);
	}
	
}
