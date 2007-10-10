package org.ontoware.rdf2go.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.model.Diff;
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

	public Model getDelegatedModel() {
		return this.delegatedModel;
	}

	protected void setDelegatedModel(Model model) {
		this.delegatedModel = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelWriter#addAll(org.ontoware.rdf2go.core.common.CommonModelReader)
	 */
	@Override
	public void addAll(Iterator<Statement> other)
			throws ModelRuntimeException {
		this.delegatedModel.addAll(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI,
	 *      org.ontoware.rdf2go.core.node.Node)
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		this.delegatedModel.addStatement(subject, predicate, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subject, predicate, literal, languageTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subject, predicate, literal, datatypeURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		this.delegatedModel.addStatement(subject, predicate, literal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelWriter#addStatement(S)
	 */
	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		this.delegatedModel.addStatement(statement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subjectURIString, predicate, literal, languageTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subjectURIString, predicate, literal, datatypeURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		this.delegatedModel.addStatement(subjectURIString, predicate, literal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.Model#contains(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean contains(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		return this.delegatedModel.contains(subject, predicate, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#createBlankNode()
	 */
	@Override
	public BlankNode createBlankNode() {
		return this.delegatedModel.createBlankNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#createURI(java.lang.String)
	 */
	@Override
	public URI createURI(String uriString) throws ModelRuntimeException {
		return this.delegatedModel.createURI(uriString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#getProperty(org.ontoware.rdf2go.core.node.URI)
	 */
	@Override
	public Object getProperty(URI propertyURI) {
		return this.delegatedModel.getProperty(propertyURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.Model#getStatement(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	public ClosableIterator<Statement> findStatements(
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		return this.delegatedModel.findStatements(subject, predicate, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#getUnderlyingModelImplementation()
	 */
	@Override
	public Object getUnderlyingModelImplementation() {
		return this.delegatedModel.getUnderlyingModelImplementation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelReader#iterator()
	 */
	public ClosableIterator<Statement> iterator() {
		return this.delegatedModel.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#newRandomUniqueURI()
	 */
	@Override
	public URI newRandomUniqueURI() {
		return this.delegatedModel.newRandomUniqueURI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelAddRemove#removeAll()
	 */
	@Override
	public void removeAll() throws ModelRuntimeException {
		this.delegatedModel.removeAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelAddRemove#removeAll(org.ontoware.rdf2go.core.common.CommonModelReader)
	 */
	@Override
	public void removeAll(Iterator<Statement> other)
			throws ModelRuntimeException {
		this.delegatedModel.removeAll(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelAddRemove#removeStatement(S)
	 */
	@Override
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		this.delegatedModel.removeStatement(statement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#setProperty(org.ontoware.rdf2go.core.node.URI,
	 *      java.lang.Object)
	 */
	@Override
	public void setProperty(URI propertyURI, Object value) {
		this.delegatedModel.setProperty(propertyURI, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#size()
	 */
	@Override
	public long size() throws ModelRuntimeException {
		return this.delegatedModel.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.Model#sparqlConstruct(java.lang.String)
	 */
	public ClosableIterable<Statement> sparqlConstruct(String query)
			throws ModelRuntimeException {
		return this.delegatedModel.sparqlConstruct(query);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.Model#sparqlSelect(java.lang.String)
	 */
	public QueryResultTable sparqlSelect(String queryString)
			throws ModelRuntimeException {
		log.debug("SPARQL query: " + queryString);
		return this.delegatedModel.sparqlSelect(queryString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI,
	 *      org.ontoware.rdf2go.core.node.Node)
	 */
	@Override
	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subject, predicate, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void removeStatement(Resource subject, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subject, predicate, literal, languageTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	@Override
	public void removeStatement(Resource subject, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subject, predicate, literal, datatypeURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	@Override
	public void removeStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subject, predicate, literal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		this.delegatedModel
				.removeStatement(subjectURIString, predicate, literal,
						languageTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	@Override
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		this.delegatedModel
				.removeStatement(subjectURIString, predicate, literal,
						datatypeURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	@Override
	public void removeStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		this.delegatedModel.removeStatement(subjectURIString, predicate, literal);
	}

	@Override
	public Diff getDiff(Iterator<Statement> other)
			throws ModelRuntimeException {
		return this.delegatedModel.getDiff(other);
	}

	public boolean sparqlAsk(String query) throws ModelRuntimeException {
		boolean result = this.delegatedModel.sparqlAsk(query);
		return result;
	}

	public ClosableIterable<Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		return this.delegatedModel.sparqlDescribe(query);
	}

	@Override
	public void update(Diff diff) throws ModelRuntimeException {
		this.delegatedModel.update(diff);
	}

	public void lock() throws LockException {
		this.delegatedModel.lock();
	}

	public boolean isLocked() {
		return this.delegatedModel.isLocked();
	}

	public void unlock() {
		this.delegatedModel.unlock();
	}

	public URI getContextURI() {
		return this.delegatedModel.getContextURI();
	}

	public void readFrom(Reader r) throws IOException, ModelRuntimeException {
		this.delegatedModel.readFrom(r);
	}

	public void writeTo(Writer w) throws IOException, ModelRuntimeException {
		this.delegatedModel.writeTo(w);
	}

	public void dump() {
		this.delegatedModel.dump();
	}

	@Override
	public ClosableIterator<Statement> findStatements(
			TriplePattern pattern) throws ModelRuntimeException {
		return this.delegatedModel.findStatements(pattern);
	}

	@Override
	public void removeStatements(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		this.delegatedModel.removeStatements(subject, predicate, object);
	}

	public void readFrom(Reader reader, Syntax syntax) throws ModelRuntimeException, IOException {
		this.delegatedModel.readFrom(reader, syntax);
	}

	public void writeTo(Writer writer, Syntax syntax) throws ModelRuntimeException, IOException {
		this.delegatedModel.writeTo(writer, syntax);
	}

	@Override
	public void readFrom(InputStream reader, Syntax syntax) throws IOException,
			ModelRuntimeException {
		this.delegatedModel.readFrom(reader, syntax);
	}

	public void readFrom(InputStream in) throws IOException, ModelRuntimeException {
		this.delegatedModel.readFrom(in);
	}

	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException {
		this.delegatedModel.writeTo(out, syntax);
	}

	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		this.delegatedModel.writeTo(out);
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
	
	@Override
	public boolean isOpen() {
		return this.delegatedModel.isOpen();
	}
	
	@Override
	public void open() {
		this.delegatedModel.open();
	}
	
	@Override
	public void close() {
		this.delegatedModel.close();
	}

	public boolean isIsomorphicWith(Model other) {
		return this.delegatedModel.isIsomorphicWith(other);
	}

	public boolean isValidURI(String uriString) {
		return this.delegatedModel.isValidURI(uriString);
	}

	public boolean isEmpty() {
		return delegatedModel.isEmpty();
	}

	public BlankNode createBlankNode(String internalID) {
		return delegatedModel.createBlankNode(internalID);
	}

}
