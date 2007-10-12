/**
 * 
 */
package org.ontoware.rdf2go.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QuadPattern;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;

/**
 * @author grimnes, voelkel
 * 
 */
public class DelegatingModelSet extends AbstractModelSetImpl implements
		ModelSet {

	protected ModelSet baseModelSet;

	public DelegatingModelSet(ModelSet baseModelSet) {
		this.baseModelSet = baseModelSet;
	}

	@Override
	public boolean addModel(Model model) {
		return this.baseModelSet.addModel(model);
	}

	public void addStatement(Statement statement) throws ModelRuntimeException {
		this.baseModelSet.addStatement(statement);
	}

	public void close() {
		this.baseModelSet.close();
	}

	@Override
	public boolean containsStatements(UriOrVariable contextURI,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		return this.baseModelSet.containsStatements(contextURI, subject, predicate,
				object);
	}

	public QuadPattern createQuadPattern(UriOrVariable context,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) {
		return this.baseModelSet.createQuadPattern(context, subject, predicate,
				object);
	}

	@Override
	public URI createURI(String uriString) throws ModelRuntimeException {
		return this.baseModelSet.createURI(uriString);
	}

	@Override
	public ClosableIterator<Statement> findStatements(
			UriOrVariable contextURI, ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		return this.baseModelSet.findStatements(contextURI, subject, predicate,
				object);
	}

	public Model getDefaultModel() {
		return this.baseModelSet.getDefaultModel();
	}

	/**
	 * @return the modelset to which this instances delegates everything.
	 */
	public ModelSet getDelegatedModelSet() {
		return this.baseModelSet;
	}

	public Model getModel(URI contextURI) {
		return this.baseModelSet.getModel(contextURI);
	}

	public ClosableIterator<Model> getModels() {
		return this.baseModelSet.getModels();
	}

	public ClosableIterator<URI> getModelURIs() {
		return this.baseModelSet.getModelURIs();
	}

	@Deprecated
	public Object getUnderlyingModelImplementation() {
		return this.baseModelSet.getUnderlyingModelSetImplementation();
	}

	public Object getUnderlyingModelSetImplementation() {
		return this.baseModelSet.getUnderlyingModelSetImplementation();
	}

	public boolean isOpen() {
		return this.baseModelSet.isOpen();
	}

	public void open() {
		this.baseModelSet.open();
	}

	public ClosableIterable<Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			MalformedQueryException, ModelRuntimeException {
		return this.baseModelSet.queryConstruct(query, querylanguage);
	}

	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, MalformedQueryException,
			ModelRuntimeException {
		return this.baseModelSet.querySelect(query, querylanguage);
	}

	public void readFrom(InputStream in) throws IOException,
			ModelRuntimeException {
		this.baseModelSet.readFrom(in);
	}

	@Override
	public void readFrom(InputStream in, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		this.baseModelSet.readFrom(in, syntax);
	}

	public void readFrom(Reader in) throws IOException, ModelRuntimeException {
		this.baseModelSet.readFrom(in);
	}

	@Override
	public void readFrom(Reader in, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		this.baseModelSet.readFrom(in, syntax);
	}

	public void removeStatement(Statement statement)
			throws ModelRuntimeException {
		this.baseModelSet.removeStatement(statement);
	}

	public long size() throws ModelRuntimeException {
		return this.baseModelSet.size();
	}

	public boolean sparqlAsk(String query) throws ModelRuntimeException,
			MalformedQueryException {
		return this.baseModelSet.sparqlAsk(query);
	}

	public ClosableIterable<Statement> sparqlConstruct(String query)
			throws ModelRuntimeException, MalformedQueryException {
		return this.baseModelSet.sparqlConstruct(query);
	}

	public ClosableIterable<Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		return this.baseModelSet.sparqlDescribe(query);
	}

	public QueryResultTable sparqlSelect(String queryString)
			throws MalformedQueryException, ModelRuntimeException {
		return this.baseModelSet.sparqlSelect(queryString);
	}

	@Override
	public void update(Diff diff) throws ModelRuntimeException {
		this.baseModelSet.update(diff);
	}

	public void writeTo(OutputStream out) throws IOException,
			ModelRuntimeException {
		this.baseModelSet.writeTo(out);
	}

	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		this.baseModelSet.writeTo(out, syntax);
	}

	public void writeTo(Writer out) throws IOException, ModelRuntimeException {
		this.baseModelSet.writeTo(out);
	}

	@Override
	public void writeTo(Writer out, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		this.baseModelSet.writeTo(out, syntax);
	}

	public boolean isEmpty() {
		return baseModelSet.isEmpty();
	}

	public boolean containsModel(URI contextURI) {
		return baseModelSet.containsModel(contextURI);
	}

	public boolean removeModel(URI contextURI) {
		return baseModelSet.removeModel(contextURI);
	}

	public BlankNode createBlankNode(String internalID) {
		return baseModelSet.createBlankNode(internalID);
	}

	public void commit() {
		baseModelSet.commit();
	}

	public void setAutocommit(boolean autocommit) {
		baseModelSet.setAutocommit(autocommit);
	}

}
