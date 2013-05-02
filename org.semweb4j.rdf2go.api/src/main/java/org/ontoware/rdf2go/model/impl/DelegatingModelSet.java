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

/**
 * 
 */
package org.ontoware.rdf2go.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
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
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;


/**
 * @author grimnes, voelkel
 * 
 */
public class DelegatingModelSet extends AbstractModelSetImpl implements ModelSet {
	
	/**
     * 
     */
	private static final long serialVersionUID = -253433238968437407L;
	protected ModelSet baseModelSet;
	
	public DelegatingModelSet(ModelSet baseModelSet) {
		this.baseModelSet = baseModelSet;
	}
	
	@Override
	public boolean addModel(Model model) {
		return this.baseModelSet.addModel(model);
	}
	
	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		this.baseModelSet.addStatement(statement);
	}
	
	@Override
	public void close() {
		this.baseModelSet.close();
	}
	
	@Override
	public boolean containsStatements(UriOrVariable contextURI, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException {
		return this.baseModelSet.containsStatements(contextURI, subject, predicate, object);
	}
	
	@Override
	public QuadPattern createQuadPattern(UriOrVariable context, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) {
		return this.baseModelSet.createQuadPattern(context, subject, predicate, object);
	}
	
	@Override
	public URI createURI(String uriString) throws ModelRuntimeException {
		return this.baseModelSet.createURI(uriString);
	}
	
	@Override
	public ClosableIterator<Statement> findStatements(UriOrVariable contextURI,
	        ResourceOrVariable subject, UriOrVariable predicate, NodeOrVariable object)
	        throws ModelRuntimeException {
		return this.baseModelSet.findStatements(contextURI, subject, predicate, object);
	}
	
	@Override
	public Model getDefaultModel() {
		return this.baseModelSet.getDefaultModel();
	}
	
	/**
	 * @return the modelset to which this instances delegates everything.
	 */
	public ModelSet getDelegatedModelSet() {
		return this.baseModelSet;
	}
	
	@Override
	public Model getModel(URI contextURI) {
		return this.baseModelSet.getModel(contextURI);
	}
	
	@Override
	public ClosableIterator<Model> getModels() {
		return this.baseModelSet.getModels();
	}
	
	@Override
	public ClosableIterator<URI> getModelURIs() {
		return this.baseModelSet.getModelURIs();
	}
	
	@Deprecated
	public Object getUnderlyingModelImplementation() {
		return this.baseModelSet.getUnderlyingModelSetImplementation();
	}
	
	@Override
	public Object getUnderlyingModelSetImplementation() {
		return this.baseModelSet.getUnderlyingModelSetImplementation();
	}
	
	@Override
	public boolean isOpen() {
		return this.baseModelSet.isOpen();
	}
	
	@Override
	public ModelSet open() {
		this.baseModelSet.open();
		return this;
	}
	
	@Override
	public ClosableIterable<Statement> queryConstruct(String query, String querylanguage)
	        throws QueryLanguageNotSupportedException, MalformedQueryException,
	        ModelRuntimeException {
		return this.baseModelSet.queryConstruct(query, querylanguage);
	}
	
	@Override
	public QueryResultTable querySelect(String query, String querylanguage)
	        throws QueryLanguageNotSupportedException, MalformedQueryException,
	        ModelRuntimeException {
		return this.baseModelSet.querySelect(query, querylanguage);
	}
	
	@Override
	public void readFrom(InputStream in) throws IOException, ModelRuntimeException {
		this.baseModelSet.readFrom(in);
	}
	
	@Override
	public void readFrom(InputStream in, Syntax syntax) throws IOException, ModelRuntimeException,
	        SyntaxNotSupportedException {
		this.baseModelSet.readFrom(in, syntax);
	}
	
	@Override
	public void readFrom(Reader in) throws IOException, ModelRuntimeException {
		this.baseModelSet.readFrom(in);
	}
	
	@Override
	public void readFrom(Reader in, Syntax syntax) throws IOException, ModelRuntimeException,
	        SyntaxNotSupportedException {
		this.baseModelSet.readFrom(in, syntax);
	}
	
	@Override
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		this.baseModelSet.removeStatement(statement);
	}
	
	@Override
	public long size() throws ModelRuntimeException {
		return this.baseModelSet.size();
	}
	
	@Override
	public boolean sparqlAsk(String query) throws ModelRuntimeException, MalformedQueryException {
		return this.baseModelSet.sparqlAsk(query);
	}
	
	@Override
	public ClosableIterable<Statement> sparqlConstruct(String query) throws ModelRuntimeException,
	        MalformedQueryException {
		return this.baseModelSet.sparqlConstruct(query);
	}
	
	@Override
	public ClosableIterable<Statement> sparqlDescribe(String query) throws ModelRuntimeException {
		return this.baseModelSet.sparqlDescribe(query);
	}
	
	@Override
	public QueryResultTable sparqlSelect(String queryString) throws MalformedQueryException,
	        ModelRuntimeException {
		return this.baseModelSet.sparqlSelect(queryString);
	}
	
	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
		this.baseModelSet.update(diff);
	}
	
	@Override
	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		this.baseModelSet.writeTo(out);
	}
	
	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws IOException, ModelRuntimeException,
	        SyntaxNotSupportedException {
		this.baseModelSet.writeTo(out, syntax);
	}
	
	@Override
	public void writeTo(Writer out) throws IOException, ModelRuntimeException {
		this.baseModelSet.writeTo(out);
	}
	
	@Override
	public void writeTo(Writer out, Syntax syntax) throws IOException, ModelRuntimeException,
	        SyntaxNotSupportedException {
		this.baseModelSet.writeTo(out, syntax);
	}
	
	@Override
	public boolean isEmpty() {
		return this.baseModelSet.isEmpty();
	}
	
	@Override
	public boolean containsModel(URI contextURI) {
		return this.baseModelSet.containsModel(contextURI);
	}
	
	@Override
	public boolean removeModel(URI contextURI) {
		return this.baseModelSet.removeModel(contextURI);
	}
	
	@Override
	public BlankNode createBlankNode(String internalID) {
		return this.baseModelSet.createBlankNode(internalID);
	}
	
	@Override
	@Deprecated
	public void commit() {
		this.baseModelSet.commit();
	}
	
	@Override
	@Deprecated
	public void setAutocommit(boolean autocommit) {
		this.baseModelSet.setAutocommit(autocommit);
	}
	
	@Override
	public Resource addReificationOf(Statement statement, Resource resource) {
		return this.baseModelSet.addReificationOf(statement, resource);
	}
	
	@Override
	public BlankNode addReificationOf(Statement statement) {
		return this.baseModelSet.addReificationOf(statement);
	}
	
	@Override
	public void deleteReification(Resource reificationResource) {
		this.baseModelSet.deleteReification(reificationResource);
	}
	
	@Override
	public Collection<Resource> getAllReificationsOf(Statement statement) {
		return this.baseModelSet.getAllReificationsOf(statement);
	}
	
	@Override
	public boolean hasReifications(Statement stmt) {
		return this.baseModelSet.hasReifications(stmt);
	}
	
	@Override
	public void readFrom(InputStream reader, Syntax syntax, String baseURI) throws IOException,
	        ModelRuntimeException, SyntaxNotSupportedException {
		this.baseModelSet.readFrom(reader, syntax, baseURI);
	}
	
	@Override
	public void readFrom(Reader in, Syntax syntax, String baseURI) throws IOException,
	        ModelRuntimeException, SyntaxNotSupportedException {
		this.baseModelSet.readFrom(in, syntax, baseURI);
	}
	
	@Override
	public String getNamespace(String prefix) {
		return this.baseModelSet.getNamespace(prefix);
	}
	
	@Override
	public Map<String,String> getNamespaces() {
		return this.baseModelSet.getNamespaces();
	}
	
	@Override
	public void removeNamespace(String prefix) {
		this.baseModelSet.removeNamespace(prefix);
	}
	
	@Override
	public void setNamespace(String prefix, String namespaceURI) throws IllegalArgumentException {
		this.baseModelSet.setNamespace(prefix, namespaceURI);
	}
	
	@Override
	public void addModel(Model model, URI contextURI) throws ModelRuntimeException {
		this.baseModelSet.addModel(model, contextURI);
	}
	
	@Override
	public void addModelSet(ModelSet modelSet) throws ModelRuntimeException {
		this.baseModelSet.addModelSet(modelSet);
	}
	
}
