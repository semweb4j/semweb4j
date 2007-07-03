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
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;

/**
 * @author grimnes
 * 
 */
public class DelegatingModelSet extends AbstractModelSetImpl implements
		ModelSet {

	protected ModelSet baseModelSet;

	public DelegatingModelSet(ModelSet baseModelSet) {
		this.baseModelSet = baseModelSet;
	}

	public boolean addModel(Model model) {
		return baseModelSet.addModel(model);
	}

	public void addStatement(Statement statement) throws ModelRuntimeException {
		baseModelSet.addStatement(statement);
	}

	public void close() {
		baseModelSet.close();
	}

	public boolean containsStatements(UriOrVariable contextURI,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		return baseModelSet.containsStatements(contextURI, subject, predicate,
				object);
	}

	public QuadPattern createQuadPattern(UriOrVariable context,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) {
		return baseModelSet.createQuadPattern(context, subject, predicate,
				object);
	}

	public URI createURI(String uriString) throws ModelRuntimeException {
		return baseModelSet.createURI(uriString);
	}

	public ClosableIterator<? extends Statement> findStatements(
			UriOrVariable contextURI, ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		return baseModelSet.findStatements(contextURI, subject, predicate,
				object);
	}

	public Model getDefaultModel() {
		return baseModelSet.getDefaultModel();
	}

	/**
	 * @return the modelset to which this instances delegates everything.
	 */
	public ModelSet getDelegatedModelSet() {
		return this.baseModelSet;
	}

	public Model getModel(URI contextURI) {
		return baseModelSet.getModel(contextURI);
	}

	public ClosableIterator<? extends Model> getModels() {
		return baseModelSet.getModels();
	}

	public ClosableIterator<URI> getModelURIs() {
		return baseModelSet.getModelURIs();
	}

	@Deprecated
	public Object getUnderlyingModelImplementation() {
		return baseModelSet.getUnderlyingModelImplementation();
	}

	public Object getUnderlyingModelSetImplementation() {
		return baseModelSet.getUnderlyingModelSetImplementation();
	}

	public boolean isOpen() {
		return baseModelSet.isOpen();
	}

	public void open() {
		baseModelSet.open();
	}

	public ClosableIterable<? extends Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			MalformedQueryException, ModelRuntimeException {
		return baseModelSet.queryConstruct(query, querylanguage);
	}

	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, MalformedQueryException,
			ModelRuntimeException {
		return baseModelSet.querySelect(query, querylanguage);
	}

	public void readFrom(InputStream in) throws IOException,
			ModelRuntimeException {
		baseModelSet.readFrom(in);
	}

	public void readFrom(InputStream in, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		baseModelSet.readFrom(in, syntax);
	}

	public void readFrom(Reader in) throws IOException, ModelRuntimeException {
		baseModelSet.readFrom(in);
	}

	public void readFrom(Reader in, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		baseModelSet.readFrom(in, syntax);
	}

	public void removeStatement(Statement statement)
			throws ModelRuntimeException {
		baseModelSet.removeStatement(statement);
	}

	public long size() throws ModelRuntimeException {
		return baseModelSet.size();
	}

	public boolean sparqlAsk(String query) throws ModelRuntimeException,
			MalformedQueryException {
		return baseModelSet.sparqlAsk(query);
	}

	public ClosableIterable<? extends Statement> sparqlConstruct(String query)
			throws ModelRuntimeException, MalformedQueryException {
		return baseModelSet.sparqlConstruct(query);
	}

	public ClosableIterable<? extends Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		return baseModelSet.sparqlDescribe(query);
	}

	public QueryResultTable sparqlSelect(String queryString)
			throws MalformedQueryException, ModelRuntimeException {
		return baseModelSet.sparqlSelect(queryString);
	}

	public void update(Diff diff) throws ModelRuntimeException {
		baseModelSet.update(diff);
	}

	public void writeTo(OutputStream out) throws IOException,
			ModelRuntimeException {
		baseModelSet.writeTo(out);
	}

	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		baseModelSet.writeTo(out, syntax);
	}

	public void writeTo(Writer out) throws IOException, ModelRuntimeException {
		baseModelSet.writeTo(out);
	}

	public void writeTo(Writer out, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		baseModelSet.writeTo(out, syntax);
	}

}
