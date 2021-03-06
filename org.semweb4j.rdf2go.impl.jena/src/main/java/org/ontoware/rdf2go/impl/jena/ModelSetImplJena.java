package org.ontoware.rdf2go.impl.jena;

import static org.ontoware.rdf2go.impl.jena.ModelImplJena.getJenaLang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.RiotWriter;
import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QuadPattern;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.impl.AbstractModelSetImpl;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;


/**
 * A ModelSet implementation for Jena. It relies on the Jena
 * {@linkplain com.hp.hpl.jena.query.Dataset}.
 * 
 * @since 4.8.1
 * 
 * @author Roland Stühmer
 * 
 * @version $Revision$
 * 
 */
public class ModelSetImplJena extends AbstractModelSetImpl {

	private static final long serialVersionUID = 9211877052180956697L;
	
	private final com.hp.hpl.jena.query.Dataset dataset;
	private final com.hp.hpl.jena.shared.Lock lock;
	private Query countStatementsQuery;
	private boolean open = true;
	
	private static class ContextIterator implements ClosableIterator<URI> {

		private final Iterator<String> underlying;

		public ContextIterator(Iterator<String> idIterator) {
			this.underlying = idIterator;
		}

		@Override
		public void close() {
		}

		@Override
		public boolean hasNext() {
			return this.underlying.hasNext();
		}

		@Override
		public URI next() {
			return new URIImpl(this.underlying.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private class ModelIterator implements ClosableIterator<Model> {

		private final ClosableIterator<URI> underlying;

		private URI lastURI;

		public ModelIterator(ClosableIterator<URI> contextIterator) {
			this.underlying = contextIterator;
		}

		@Override
		public void close() {
			this.underlying.close();
		}

		@Override
		public boolean hasNext() {
			return this.underlying.hasNext();
		}

		@Override
		public Model next() {
			URI uri = this.underlying.next();

			Model model = getModel(uri);

			this.lastURI = uri;
			return model;
		}

		@Override
		public void remove() {
			// only possible when next() has been invoked at least once
			if (this.lastURI == null) {
				throw new IllegalStateException();
			}

			removeModel(lastURI);

			this.lastURI = null;
		}

	}

	private class JenaQuadPattern implements QuadPattern {

		private static final long serialVersionUID = -2397218722246644188L;

		private final UriOrVariable context;

		private final NodeOrVariable object;

		private final UriOrVariable predicate;

		private final ResourceOrVariable subject;

		public JenaQuadPattern(UriOrVariable context,
				ResourceOrVariable subject, UriOrVariable predicate,
				NodeOrVariable object) {
			this.checkNonNull(context);
			this.checkNonNull(subject);
			this.checkNonNull(predicate);
			this.checkNonNull(object);

			this.context = context;
			this.subject = subject;
			this.predicate = predicate;
			this.object = object;
		}

		@Override
		public UriOrVariable getContext() {
			return this.context;
		}

		@Override
		public NodeOrVariable getObject() {
			return this.object;
		}

		@Override
		public UriOrVariable getPredicate() {
			return this.predicate;
		}

		@Override
		public ResourceOrVariable getSubject() {
			return this.subject;
		}

		@Override
		public boolean matches(Statement statement) {
			return this.matches(statement.getContext(), this.context)
					&& this.matches(statement.getSubject(), this.subject)
					&& this.matches(statement.getPredicate(), this.predicate)
					&& this.matches(statement.getObject(), this.object);
		}

		private void checkNonNull(NodeOrVariable value) {
			if (value == null) {
				throw new NullPointerException();
			}
		}

		private boolean matches(Node node, NodeOrVariable variable) {
			return variable.equals(Variable.ANY) || variable.equals(node);
		}
	}

	private static class StatementIterator implements ClosableIterator<Statement> {
		
		private final Iterator<com.hp.hpl.jena.sparql.core.Quad> underlying;
		
		public StatementIterator(
				Iterator<com.hp.hpl.jena.sparql.core.Quad> jenaQuadIterator) {
			this.underlying = jenaQuadIterator;
		}

		@Override
		public void close() {
		}

		@Override
		public boolean hasNext() {
			return this.underlying.hasNext();
		}

		@Override
		public Statement next() {
			com.hp.hpl.jena.sparql.core.Quad quad = this.underlying
					.next();
			return new StatementJena29Impl(quad.getGraph(), quad.getSubject(),
					quad.getPredicate(), quad.getObject());
		}

		@Override
		public void remove() {
			this.underlying.remove();
		}
	}

	public ModelSetImplJena(com.hp.hpl.jena.query.Dataset dataset) {
		this.dataset = dataset;
		this.lock = this.dataset.getLock();
		org.apache.jena.riot.RIOT.init(); //wires RIOT readers/writers into Jena
	}

	@Override
	public ModelSet open() {
		return this;
	}

	@Override
	public boolean isOpen() {
		return this.open;
	}

	@Override
	public void close() {
		this.open = false;
	}

	@Override
	public long size() throws ModelRuntimeException {

		// Start with the size of the default graph
		long size = this.dataset.getDefaultModel().size();

		// Loop and add the sizes of all contained graphs
		Iterator<String> it = this.dataset.listNames();
		while (it.hasNext()) {
			size += this.dataset.getNamedModel(it.next()).size();
		}

		return size;
	}

	@Override
	public Model getModel(URI contextURI) {
		com.hp.hpl.jena.rdf.model.Model jenaModel = dataset
				.getNamedModel(contextURI.toString());

		if (jenaModel == null) {
			jenaModel = com.hp.hpl.jena.rdf.model.ModelFactory
					.createDefaultModel();
		}
		
		// return opened model
		return new ModelImplJena(contextURI, jenaModel).open();
	}

	@Override
	public boolean removeModel(URI contextURI) {
		this.dataset.removeNamedModel(contextURI.toString());
		return true;
	}

	@Override
	public boolean containsModel(URI contextURI) {
		return this.dataset.containsNamedModel(contextURI.toString());
	}

	@Override
	public Model getDefaultModel() {
		com.hp.hpl.jena.rdf.model.Model jenaModel = this.dataset
				.getDefaultModel();
		// return opened model
		return new ModelImplJena(jenaModel).open();
	}

	/**
	 * Return all <i>named</i> models. This does not currently return the
	 * default model because there is no usable iterator in Jena for this. Also
	 * the default Model in Jena has no usable context URI which causes problems
	 * later when iterating over the models e.g., using
	 * {@linkplain ClosableIterator#remove()}
	 */
	@Override
	public ClosableIterator<Model> getModels() {
		return new ModelIterator(this.getModelURIs());
	}

	@Override
	public ClosableIterator<URI> getModelURIs() {
		return new ContextIterator(this.dataset.listNames());
	}

	@Override
	public Object getUnderlyingModelSetImplementation() {
		return this.dataset;
	}

	@Override
	public ClosableIterable<Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			MalformedQueryException, ModelRuntimeException {

		Query jenaQuery = QueryFactory.create(query);
		QueryExecution qexec = QueryExecutionFactory.create(jenaQuery,
				this.dataset);

		if (jenaQuery.isConstructType()) {
			com.hp.hpl.jena.rdf.model.Model m = qexec.execConstruct();
			Model resultModel = new ModelImplJena(null, m, Reasoning.none);
			resultModel.open();
			return resultModel;
		} else {
			throw new RuntimeException(
					"Cannot handle this type of query! Please use CONSTRUCT.");
		}
	}

	@Override
	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, MalformedQueryException,
			ModelRuntimeException {
		com.hp.hpl.jena.query.Syntax syntax = com.hp.hpl.jena.query.Syntax
				.lookup(querylanguage);
		if (syntax == null) {
			// delegate to super
			throw new QueryLanguageNotSupportedException(
					"Unsupported query language: " + querylanguage);
		}
		Query jenaQuery = QueryFactory.create(query, syntax);
		return new QueryResultTableImpl(jenaQuery,
				this.dataset);
	}

	@Override
	public ClosableIterable<Statement> sparqlConstruct(String query)
			throws ModelRuntimeException, MalformedQueryException {
		Query jenaQuery = QueryFactory.create(query);
		QueryExecution qexec = QueryExecutionFactory.create(jenaQuery,
				this.dataset);

		if (jenaQuery.isConstructType()) {
			com.hp.hpl.jena.rdf.model.Model m = qexec.execConstruct();
			Model resultModel = new ModelImplJena(null, m, Reasoning.none);
			resultModel.open();
			return resultModel;
		} else {
			throw new RuntimeException(
					"Cannot handle this type of query! Please use CONSTRUCT.");
		}

	}

	@Override
	public ClosableIterable<Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		Query jenaQuery = QueryFactory.create(query);
		QueryExecution qexec = QueryExecutionFactory.create(jenaQuery,
				this.dataset);

		if (jenaQuery.isDescribeType()) {
			com.hp.hpl.jena.rdf.model.Model m = qexec.execDescribe();
			Model resultModel = new ModelImplJena(null, m, Reasoning.none);
			resultModel.open();
			return resultModel;
		} else {
			throw new RuntimeException(
					"Cannot handle this type of query! Please use DESCRIBE.");
		}
	}

	@Override
	public QueryResultTable sparqlSelect(String queryString)
			throws MalformedQueryException, ModelRuntimeException {
		Query jenaQuery = QueryFactory.create(queryString);
		return new QueryResultTableImpl(jenaQuery,
				this.dataset);
	}

	@Override
	public boolean sparqlAsk(String query) throws ModelRuntimeException,
			MalformedQueryException {
		Query jenaQuery = QueryFactory.create(query);
		QueryExecution qexec = QueryExecutionFactory.create(jenaQuery,
				this.dataset);

		if (jenaQuery.isAskType()) {
			return qexec.execAsk();
		} else {
			throw new RuntimeException(
					"Cannot handle this type of query! Please use ASK.");
		}
	}

	/**
	 * Read data from an {@linkplain Reader} with RDF syntax {@link Syntax#Trix}.
	 */
	@Override
	public void readFrom(Reader in) throws IOException, ModelRuntimeException {
		readFrom(in, Syntax.Trix, null);
	}

	@Override
	public void readFrom(Reader in, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		readFrom(in, syntax, null);
	}

	@Override
	public void readFrom(Reader in, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException,
			SyntaxNotSupportedException {
		ReaderInputStream is = new ReaderInputStream(in, StandardCharsets.UTF_8);
		readFrom(is, syntax, baseURI);
	}

	/**
	 * Read data from an {@linkplain InputStream} with RDF syntax
	 * {@link Syntax#Trix}.
	 * 
	 * <br />
	 * <b>Please note:</b><br />
	 * In this Jena implementation this will fail until a matching
	 * {@linkplain RiotReader} is available. Please use
	 * {@linkplain ModelSetImplJena#readFrom(Reader, Syntax)} with an available
	 * syntax such as {@link Syntax#Nquads} or {@link Syntax#Trig}.
	 */
	@Override
	public void readFrom(InputStream in) throws IOException,
			ModelRuntimeException {
		readFrom(in, Syntax.Trix, null);
	}

	@Override
    public void readFrom(InputStream in, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		readFrom(in, syntax, null);
    }
    
	@Override
	public void readFrom(InputStream in, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException,
			SyntaxNotSupportedException {

		RDFDataMgr.read(this.dataset.asDatasetGraph(), in, baseURI, getJenaLang(syntax));
	}

	/**
	 * Write data to a {@linkplain Writer} with RDF syntax
	 * {@link Syntax#Trix}.
	 * 
	 * <br />
	 * <b>Please note:</b><br />
	 * In this Jena implementation this will fail until a matching
	 * {@linkplain RiotWriter} is available. Please use
	 * {@linkplain ModelSetImplJena#writeTo(Writer, Syntax)} with an available
	 * syntax such as {@link Syntax#Nquads}.
	 */
	@Override
	public void writeTo(Writer out) throws IOException, ModelRuntimeException {
		writeTo(out, Syntax.Trix);
	}

	/**
	 * Write data to an {@linkplain OutputStream} with RDF syntax
	 * {@link Syntax#Trix}.
	 * 
	 * <br />
	 * <b>Please note:</b><br />
	 * In this Jena implementation this will fail until a matching
	 * {@linkplain RiotWriter} is available. Please use
	 * {@linkplain ModelSetImplJena#writeTo(OutputStream, Syntax)} with an available
	 * syntax such as {@link Syntax#Nquads}.
	 */
	@Override
	public void writeTo(OutputStream out) throws IOException,
			ModelRuntimeException {
		writeTo(out, Syntax.Trix);
	}

	@Override
    public void writeTo(Writer writer, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		WriterOutputStream stream = new WriterOutputStream(writer, StandardCharsets.UTF_8);
		writeTo(stream, syntax);
	}

	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException, SyntaxNotSupportedException {
		
		if (syntax == null) {
			throw new NullPointerException("syntax may not be null");
		}
		
		Lang jenaLang = getJenaLang(syntax);

//		if (RDFLanguages.isTriples(jenaLang)) {
//			/*
//			 * NB: Writing a ModelSet to a triple serialization loses the
//			 * context of any quads if present.
//			 */
//			Iterator<Model> it = this.getModels();
//			while (it.hasNext()) {
//				Model model = it.next();
//				model.writeTo(out, syntax);
//			}
//			this.getDefaultModel().writeTo(out, syntax);
//		}
// FIXME stuehmer: write unit test to see if this can be removed
//		else {
		try {
			RDFDataMgr.write(out, this.dataset, jenaLang);
		}
		catch (RiotException e) {
			throw new SyntaxNotSupportedException(
					"error writing syntax " + syntax + ": " + e.getMessage());
		}
	}

	@Override
	public QuadPattern createQuadPattern(UriOrVariable context,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) {
		return new JenaQuadPattern(context, subject, predicate, object);
	}

	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		addStatement(statement.getContext(), statement.getSubject(),
				statement.getPredicate(), statement.getObject());
	}

	@Override
	public void addStatement(URI context, Resource subject, URI predicate,
			Node object) throws ModelRuntimeException {
		com.hp.hpl.jena.rdf.model.Model jenaModel;
		
		if (context != null) {
			jenaModel = this.dataset.getNamedModel(context.toString());
		} else {
			jenaModel = this.dataset.getDefaultModel();
		}
		jenaModel.getGraph().add(
				new com.hp.hpl.jena.graph.Triple(TypeConversion.toJenaNode(
						subject, jenaModel), TypeConversion.toJenaNode(
						predicate, jenaModel), TypeConversion.toJenaNode(
						object, jenaModel)));
	}

	@Override
	public void removeAll() throws ModelRuntimeException {
		// Empty the default graph
		this.dataset.getDefaultModel().removeAll();
		// Remove all named graphs
		Iterator<String> it = this.dataset.listNames();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}

	@Override
    public void removeStatements(QuadPattern quadPattern)
			throws ModelRuntimeException {
		removeStatements(quadPattern.getContext(), quadPattern.getSubject(),
				quadPattern.getPredicate(), quadPattern.getObject());
	}

	@Override
    public void removeStatements(UriOrVariable context,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		this.dataset.asDatasetGraph().deleteAny(
				(context != null) ? TypeConversion.toJenaNode(context)
						: com.hp.hpl.jena.graph.Node.ANY,
				TypeConversion.toJenaNode(subject),
				TypeConversion.toJenaNode(predicate),
				TypeConversion.toJenaNode(object));
	}
	
	@Override
	public void removeStatement(Statement statement)
			throws ModelRuntimeException {
		removeStatement(statement.getContext(), statement.getSubject(),
				statement.getPredicate(), statement.getObject());
	}

	@Override
	public void removeStatement(URI context, Resource subject, URI predicate,
			Node object) throws ModelRuntimeException {
		
		if (context == null) {
			this.dataset
					.getDefaultModel()
					.getGraph()
					.delete(new com.hp.hpl.jena.graph.Triple(TypeConversion
							.toJenaNode(subject), TypeConversion
							.toJenaNode(predicate), TypeConversion
							.toJenaNode(object)));
		} else {
			this.dataset.asDatasetGraph().delete(
					TypeConversion.toJenaNode(context),
					TypeConversion.toJenaNode(subject),
					TypeConversion.toJenaNode(predicate),
					TypeConversion.toJenaNode(object));
		}
	}

	@Override
    public long countStatements(QuadPattern pattern) {
		String context = (pattern.getContext() != null) ? pattern.getContext()
				.toString() : com.hp.hpl.jena.graph.Node.ANY.getName();
		String subject = pattern.getSubject().toString();
		String predicate = pattern.getPredicate().toString();
		String object = pattern.getObject().toString();

		com.hp.hpl.jena.query.QuerySolutionMap initialBindings =
				new com.hp.hpl.jena.query.QuerySolutionMap();
		if (pattern.getContext() != null) {
			initialBindings.add("c", this.dataset.getDefaultModel()
					.createResource(context));
		}
		initialBindings.add("s",
				this.dataset.getDefaultModel().createResource(subject));
		initialBindings.add("p",
				this.dataset.getDefaultModel().createResource(predicate));
		initialBindings.add("o",
				this.dataset.getDefaultModel().createResource(object));

		String query = "SELECT (count(*) AS ?count) WHERE { GRAPH ?c { ?s ?p ?o } }";

		// Initialize the reusable query
		if (this.countStatementsQuery == null) {
			this.countStatementsQuery = QueryFactory.create(query,
					com.hp.hpl.jena.query.Syntax.syntaxARQ);
		}

		QueryExecution exec = QueryExecutionFactory.create(
				this.countStatementsQuery, this.dataset, initialBindings);
		QueryResultTable result = new QueryResultTableImpl(exec);
		return Long
				.parseLong(result.iterator().next().getLiteralValue("count"));
	}

	@Override
    public ClosableIterator<Statement> findStatements(QuadPattern pattern)
			throws ModelRuntimeException {
		return this.findStatements(pattern.getContext(),
				pattern.getSubject(), pattern.getPredicate(),
				pattern.getObject());
	}
	
	@Override
    public ClosableIterator<Statement> findStatements(UriOrVariable contextURI,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		return new StatementIterator(this.dataset.asDatasetGraph().find(
				(contextURI != null) ? TypeConversion.toJenaNode(contextURI)
						: com.hp.hpl.jena.graph.Node.ANY,
				TypeConversion.toJenaNode(subject),
				TypeConversion.toJenaNode(predicate),
				TypeConversion.toJenaNode(object)));
	}

	@Override
	public ClosableIterator<Statement> iterator() {
		return new StatementIterator(this.dataset.asDatasetGraph().find());
	}
	
	@Override
	public void commit() throws ModelRuntimeException {
	}

	@Override
	public void setAutocommit(boolean autocommit) {
	}

	@Override
	public String getNamespace(String prefix) {
		// We use the default model because there is no prefix mapping on
		// Dataset itself:
		return this.dataset.getDefaultModel().getNsPrefixURI(prefix);
	}

	@Override
	public Map<String, String> getNamespaces() {
		// We use the default model because there is no prefix mapping on
		// Dataset itself:
		return this.dataset.getDefaultModel().getNsPrefixMap();
	}

	/**
	 * Remove the specified namespace from all {@linkplain Model}s
	 * in this {@linkplain ModelSet} including the default graph.
	 */
	@Override
	public void removeNamespace(String prefix) {
		this.dataset.getDefaultModel().removeNsPrefix(prefix);
		Iterator<Model> it = this.getModels();
		while (it.hasNext()) {
			it.next().removeNamespace(prefix);
		}
	}

	/**
	 * Set the specified namespace for all {@linkplain Model}s
	 * in this {@linkplain ModelSet} including the default graph.
	 */
	@Override
	public void setNamespace(String prefix, String namespaceURI)
			throws IllegalArgumentException {
		this.dataset.getDefaultModel().setNsPrefix(prefix, namespaceURI);
		Iterator<Model> it = this.getModels();
		while (it.hasNext()) {
			it.next().setNamespace(prefix, namespaceURI);
		}
	}

	@Override
	public boolean isLocked() {
		return super.isLocked();
	}

	@Override
	public void lock() throws LockException {
		if (isLocked()) {
			throw new LockException("Already locked");
		}
		else {
			super.lock();
			this.lock.enterCriticalSection(com.hp.hpl.jena.shared.Lock.WRITE);
		}
	}

	@Override
	public void unlock() {
		if (isLocked()) {
			this.lock.leaveCriticalSection();
			super.unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		return this.dataset.asDatasetGraph().isEmpty();
	}

	@Override
	public String toString() {
		return this.dataset.asDatasetGraph().toString();
	}

	@Override
	public boolean addModel(Model model) {
		for (String prefix : model.getNamespaces().keySet()) {
			this.dataset.getDefaultModel().setNsPrefix(prefix, model.getNamespace(prefix));
		}
			
		if (model instanceof ModelImplJena) {
			this.dataset.getNamedModel(model.getContextURI().toString()).add(
					((com.hp.hpl.jena.rdf.model.Model) model
							.getUnderlyingModelImplementation()));
			return true;
		}
		else {
			return super.addModel(model);
		}
	}

	@Override
	public void addModel(Model model, URI contextURI) {
		for (String prefix : model.getNamespaces().keySet()) {
			this.dataset.getDefaultModel().setNsPrefix(prefix, model.getNamespace(prefix));
		}
		
		if (model instanceof ModelImplJena) {
			this.dataset.getNamedModel(contextURI.toString()).add(
					((com.hp.hpl.jena.rdf.model.Model) model
							.getUnderlyingModelImplementation()));
		}
		else {
			super.addModel(model, contextURI);
		}
	}
}
