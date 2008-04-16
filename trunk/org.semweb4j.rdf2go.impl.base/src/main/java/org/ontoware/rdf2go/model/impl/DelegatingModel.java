package org.ontoware.rdf2go.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.DiffReader;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.TriplePattern;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegates all calls to underlying model. This class is meant to be subclassed
 * and have some methods overriden. This was inspired by Sesame's SAIL API.
 * 
 * @author voelkel
 */
public class DelegatingModel extends AbstractModel implements Model {

	private static final Logger log = LoggerFactory.getLogger(DelegatingModel.class);

	private Model delegatedModel;

	/**
	 * Please set model!
	 */
	protected DelegatingModel() {
		// use the setModel method
	}

	public DelegatingModel(Model model) {
		this.delegatedModel = model;
	}

	@Override
	public void addAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		this.delegatedModel.addAll(other);
	}
	
	@Override
	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		this.delegatedModel.addStatement(subject, predicate, object);
	}

	@Override
	public void addStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		this.delegatedModel.addStatement(subject, predicate, literal);
	}

	@Override
	public void addStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subject, predicate, literal, languageTag);
	}

	@Override
	public void addStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subject, predicate, literal, datatypeURI);
	}

	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		this.delegatedModel.addStatement(statement);
	}

	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subjectURIString, predicate, literal);
	}

	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subjectURIString, predicate, literal, languageTag);
	}

	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subjectURIString, predicate, literal, datatypeURI);
	}

	@Override
	public void close() {
		this.delegatedModel.close();
	}

	@Override
	public void commit() {
		this.delegatedModel.commit();
	}

	@Override
	public boolean contains(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		return this.delegatedModel.contains(subject, predicate, object);
	}

	public BlankNode createBlankNode() {
		return this.delegatedModel.createBlankNode();
	}

	public BlankNode createBlankNode(String internalID) {
		return delegatedModel.createBlankNode(internalID);
	}

	@Override
	public URI createURI(String uriString) throws ModelRuntimeException {
		return this.delegatedModel.createURI(uriString);
	}

	public void dump() {
		this.delegatedModel.dump();
	}

	public ClosableIterator<Statement> findStatements(
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		return this.delegatedModel.findStatements(subject, predicate, object);
	}

	@Override
	public ClosableIterator<Statement> findStatements(
			TriplePattern pattern) throws ModelRuntimeException {
		return this.delegatedModel.findStatements(pattern);
	}

	public URI getContextURI() {
		return this.delegatedModel.getContextURI();
	}

	public Model getDelegatedModel() {
		return this.delegatedModel;
	}

	@Override
	public Diff getDiff(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		return this.delegatedModel.getDiff(other);
	}

	@Override
	public Object getProperty(URI propertyURI) {
		return this.delegatedModel.getProperty(propertyURI);
	}

	@Override
	public Object getUnderlyingModelImplementation() {
		return this.delegatedModel.getUnderlyingModelImplementation();
	}

	public boolean isEmpty() {
		return delegatedModel.isEmpty();
	}

	public boolean isIsomorphicWith(Model other) {
		return this.delegatedModel.isIsomorphicWith(other);
	}

	public boolean isLocked() {
		return this.delegatedModel.isLocked();
	}

	@Override
	public boolean isOpen() {
		return this.delegatedModel.isOpen();
	}

	public boolean isValidURI(String uriString) {
		return this.delegatedModel.isValidURI(uriString);
	}

	public ClosableIterator<Statement> iterator() {
		return this.delegatedModel.iterator();
	}

	public void lock() throws LockException {
		this.delegatedModel.lock();
	}

	@Override
	public URI newRandomUniqueURI() {
		return this.delegatedModel.newRandomUniqueURI();
	}

	@Override
	public void open() {
		this.delegatedModel.open();
	}

	@Override
	public ClosableIterable<Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			ModelRuntimeException {
		return this.delegatedModel.queryConstruct(query, querylanguage);
	}

	@Override
	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, ModelRuntimeException {
		return this.delegatedModel.querySelect(query, querylanguage);
	}

	public void readFrom(InputStream in) throws IOException, ModelRuntimeException {
		this.delegatedModel.readFrom(in);
	}

	@Override
	public void readFrom(InputStream reader, Syntax syntax) throws IOException,
			ModelRuntimeException {
		this.delegatedModel.readFrom(reader, syntax);
	}

	public void readFrom(Reader r) throws IOException, ModelRuntimeException {
		this.delegatedModel.readFrom(r);
	}

	public void readFrom(Reader reader, Syntax syntax) throws ModelRuntimeException, IOException {
		this.delegatedModel.readFrom(reader, syntax);
	}

	@Override
	public void removeAll() throws ModelRuntimeException {
		this.delegatedModel.removeAll();
	}

	@Override
	public void removeAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		this.delegatedModel.removeAll(other);
	}

	@Override
	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subject, predicate, object);
	}

	@Override
	public void removeStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subject, predicate, literal);
	}

	@Override
	public void removeStatement(Resource subject, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subject, predicate, literal, languageTag);
	}

	@Override
	public void removeStatement(Resource subject, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subject, predicate, literal, datatypeURI);
	}

	@Override
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		this.delegatedModel.removeStatement(statement);
	}

	@Override
	public void removeStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subjectURIString, predicate, literal);
	}

	@Override
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		this.delegatedModel
				.removeStatement(subjectURIString, predicate, literal,
						languageTag);
	}

	@Override
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		this.delegatedModel
				.removeStatement(subjectURIString, predicate, literal,
						datatypeURI);
	}

	@Override
	public void removeStatements(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		this.delegatedModel.removeStatements(subject, predicate, object);
	}

	@Override
	public void setAutocommit( boolean autocommit) {
		this.delegatedModel.setAutocommit(autocommit);
	}

	protected void setDelegatedModel(Model model) {
		this.delegatedModel = model;
	}

	@Override
	public void setProperty(URI propertyURI, Object value) {
		this.delegatedModel.setProperty(propertyURI, value);
	}

	@Override
	public long size() throws ModelRuntimeException {
		return this.delegatedModel.size();
	}

	public boolean sparqlAsk(String query) throws ModelRuntimeException {
		boolean result = this.delegatedModel.sparqlAsk(query);
		return result;
	}

	public ClosableIterable<Statement> sparqlConstruct(String query)
			throws ModelRuntimeException {
		return this.delegatedModel.sparqlConstruct(query);
	}

	public ClosableIterable<Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		return this.delegatedModel.sparqlDescribe(query);
	}
	
	public QueryResultTable sparqlSelect(String queryString)
			throws ModelRuntimeException {
		log.debug("SPARQL query: " + queryString);
		return this.delegatedModel.sparqlSelect(queryString);
	}
	
	public void unlock() {
		this.delegatedModel.unlock();
	}
	
	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
		this.delegatedModel.update(diff);
	}

	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		this.delegatedModel.writeTo(out);
	}

	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException {
		this.delegatedModel.writeTo(out, syntax);
	}

	public void writeTo(Writer w) throws IOException, ModelRuntimeException {
		this.delegatedModel.writeTo(w);
	}

	public void writeTo(Writer writer, Syntax syntax) throws ModelRuntimeException, IOException {
		this.delegatedModel.writeTo(writer, syntax);
	}

	public void readFrom(Reader in, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException {
		delegatedModel.readFrom(in, syntax, baseURI);
	}

	public void readFrom(InputStream reader, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException {
		delegatedModel.readFrom(reader, syntax, baseURI);
	}

	public String getNamespace(String prefix) {
		return delegatedModel.getNamespace(prefix);
	}

	public Map<String, String> getNamespaces() {
		return delegatedModel.getNamespaces();
	}

	public void removeNamespace(String prefix) {
		delegatedModel.removeNamespace(prefix);
	}

	public void setNamespace(String prefix, String namespaceURI)
			throws IllegalArgumentException {
		delegatedModel.setNamespace(prefix, namespaceURI);
	}

	public void addModel(Model model) throws ModelRuntimeException {
		delegatedModel.addModel(model);
	}


}
