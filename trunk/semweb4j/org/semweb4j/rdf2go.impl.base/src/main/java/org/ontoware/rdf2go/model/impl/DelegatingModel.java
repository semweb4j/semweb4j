package org.ontoware.rdf2go.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

/**
 * Delegates all calls to underlying model. This class is meant to be subclassed
 * and have some methods overriden. This was inspired by Sesame's SAIL API.
 * 
 * @author voelkel
 */
public class DelegatingModel extends AbstractModel implements Model {

	private static final Log log = LogFactory.getLog(DelegatingModel.class);

	private Model model;

	/**
	 * Please set model!
	 */
	protected DelegatingModel() {
		// use the setModel method
	}

	public DelegatingModel(Model model) {
		this.model = model;
	}

	public Model getDelegatedModel() {
		return model;
	}

	protected void setDelegatedModel(Model model) {
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelWriter#addAll(org.ontoware.rdf2go.core.common.CommonModelReader)
	 */
	public void addAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		model.addAll(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI,
	 *      org.ontoware.rdf2go.core.node.Node)
	 */
	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		model.addStatement(subject, predicate, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	public void addStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		model.addStatement(subject, predicate, literal, languageTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	public void addStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		model.addStatement(subject, predicate, literal, datatypeURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	public void addStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		model.addStatement(subject, predicate, literal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelWriter#addStatement(S)
	 */
	public void addStatement(Statement statement) throws ModelRuntimeException {
		model.addStatement(statement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	public void addStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		model.addStatement(subjectURIString, predicate, literal, languageTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	public void addStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		model.addStatement(subjectURIString, predicate, literal, datatypeURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	public void addStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		model.addStatement(subjectURIString, predicate, literal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.Model#contains(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	public boolean contains(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		return model.contains(subject, predicate, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#createBlankNode()
	 */
	public BlankNode createBlankNode() {
		return model.createBlankNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#createURI(java.lang.String)
	 */
	public URI createURI(String uriString) throws ModelRuntimeException {
		return model.createURI(uriString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#getProperty(org.ontoware.rdf2go.core.node.URI)
	 */
	public Object getProperty(URI propertyURI) {
		return model.getProperty(propertyURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.Model#getStatement(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	public ClosableIterator<? extends Statement> findStatements(
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		return model.findStatements(subject, predicate, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#getUnderlyingModelImplementation()
	 */
	public Object getUnderlyingModelImplementation() {
		return model.getUnderlyingModelImplementation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelReader#iterator()
	 */
	public ClosableIterator<Statement> iterator() {
		return model.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#newRandomUniqueURI()
	 */
	public URI newRandomUniqueURI() {
		return model.newRandomUniqueURI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelAddRemove#removeAll()
	 */
	public void removeAll() throws ModelRuntimeException {
		model.removeAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelAddRemove#removeAll(org.ontoware.rdf2go.core.common.CommonModelReader)
	 */
	public void removeAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		model.removeAll(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelAddRemove#removeStatement(S)
	 */
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		model.removeStatement(statement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#setProperty(org.ontoware.rdf2go.core.node.URI,
	 *      java.lang.Object)
	 */
	public void setProperty(URI propertyURI, Object value) {
		model.setProperty(propertyURI, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#size()
	 */
	public long size() throws ModelRuntimeException {
		return model.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.Model#sparqlConstruct(java.lang.String)
	 */
	public ClosableIterable<? extends Statement> sparqlConstruct(String query)
			throws ModelRuntimeException {
		return model.sparqlConstruct(query);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.Model#sparqlSelect(java.lang.String)
	 */
	public QueryResultTable sparqlSelect(String queryString)
			throws ModelRuntimeException {
		log.debug("SPARQL query: " + queryString);
		return model.sparqlSelect(queryString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI,
	 *      org.ontoware.rdf2go.core.node.Node)
	 */
	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		model.removeStatement(subject, predicate, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	public void removeStatement(Resource subject, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		model.removeStatement(subject, predicate, literal, languageTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	public void removeStatement(Resource subject, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		model.removeStatement(subject, predicate, literal, datatypeURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	public void removeStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		model.removeStatement(subject, predicate, literal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		model
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
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		model
				.removeStatement(subjectURIString, predicate, literal,
						datatypeURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	public void removeStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		model.removeStatement(subjectURIString, predicate, literal);
	}

	public Diff getDiff(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		return model.getDiff(other);
	}

	public boolean sparqlAsk(String query) throws ModelRuntimeException {
		boolean result = model.sparqlAsk(query);
		return result;
	}

	public ClosableIterable<? extends Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		return model.sparqlDescribe(query);
	}

	public void update(Diff diff) throws ModelRuntimeException {
		model.update(diff);
	}

	public void lock() throws LockException {
		model.lock();
	}

	public boolean isLocked() {
		return model.isLocked();
	}

	public void unlock() {
		model.unlock();
	}

	public URI getContextURI() {
		return model.getContextURI();
	}

	public void readFrom(Reader r) throws IOException, ModelRuntimeException {
		model.readFrom(r);
	}

	public void writeTo(Writer w) throws IOException, ModelRuntimeException {
		model.writeTo(w);
	}

	public void dump() {
		model.dump();
	}

	public ClosableIterator<? extends Statement> findStatements(
			TriplePattern pattern) throws ModelRuntimeException {
		return model.findStatements(pattern);
	}

	public void removeStatements(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		model.removeStatements(subject, predicate, object);
	}

	public void readFrom(Reader reader, Syntax syntax) {
		readFrom(reader, syntax);
	}

	public void writeTo(Writer writer, Syntax syntax) {
		writeTo(writer, syntax);
	}

	public void readFrom(InputStream reader, Syntax syntax) throws IOException,
			ModelRuntimeException {
		model.readFrom(reader, syntax);
	}

	public void readFrom(InputStream in) throws IOException, ModelRuntimeException {
		model.readFrom(in);
	}

	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException {
		model.writeTo(out, syntax);
	}

	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		model.writeTo(out);
	}

	public ClosableIterable<? extends Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			ModelRuntimeException {
		return model.queryConstruct(query, querylanguage);
	}

	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, ModelRuntimeException {
		return model.querySelect(query, querylanguage);
	}
	
	@Override
	public boolean isOpen() {
		return model.isOpen();
	}
	
	@Override
	public void open() {
		model.open();
	}
	
	@Override
	public void close() {
		model.close();
	}

	public boolean isIsomorphicWith(Model other) {
		return model.isIsomorphicWith(other);
	}

	public boolean isValidURI(String uriString) {
		return model.isValidURI(uriString);
	}

}
