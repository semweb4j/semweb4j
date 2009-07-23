/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import info.aduna.iteration.CloseableIteration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.ontoware.rdf2go.model.impl.AbstractLockingModel;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Namespace;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the RDF2Go model interface for an OpenRDF Repository.
 * 
 * Note that RepositoryModel and RepositoryModelSet only work well together
 * because they both keep their RepositoryConnections in auto-commit mode. This
 * cannot be changed by the user. Do mass-updates using {@link #update(Diff)},
 * {@link #addAll(Iterator)} or {@link #removeAll(Iterator)}, then the current
 * connection will be used in non-autocommit mode and commited, including a
 * rollback when it fails.
 */
public class RepositoryModel extends AbstractLockingModel implements Model {

	private static Logger log = LoggerFactory.getLogger(RepositoryModel.class);

	public static final String DEFAULT_CONTEXT = "urn:nullcontext";

	public static final org.openrdf.model.URI DEFAULT_OPENRDF_CONTEXT = null;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Repository repository;

	protected RepositoryConnection connection;

	protected ValueFactory valueFactory;

	private boolean locked = false;

	protected URI context;

	private org.openrdf.model.URI openRdfContext;

	private boolean autocommitBeforeLock;

	public RepositoryModel(Repository repository) throws ModelRuntimeException {
		if (repository == null) {
			throw new IllegalArgumentException("Repository cannot be null");
		}

		this.repository = repository;
		init();
	}

	public RepositoryModel(URI context, Repository repository)
			throws ModelRuntimeException {
		if (repository == null) {
			throw new IllegalArgumentException("Repository cannot be null");
		}

		this.repository = repository;
		this.context = context;
		init();
	}

	private void init() {
		this.valueFactory = this.repository.getValueFactory();

		if (this.context == null) {
			this.context = new URIImpl(DEFAULT_CONTEXT, false);
			this.openRdfContext = DEFAULT_OPENRDF_CONTEXT;
		} else {
			this.openRdfContext = this.valueFactory.createURI(this.context
					.toString());
		}
	}

	/**
	 * Returns the context as a OpenRDF URI.
	 */
	public org.openrdf.model.URI getOpenRDFContextURI() {
		return this.openRdfContext;
	}

	private Throwable caller;

	@Override
	public Model open() {
		// establish a connection
		try {
			this.connection = this.repository.getConnection();
			this.connection.setAutoCommit(true);
			if (log.isDebugEnabled()) {
				try {
					throw new RuntimeException("Opening model");
				} catch (RuntimeException e) {
					this.caller = e;
					this.caller.fillInStackTrace();
				}
			}
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
		return this;
	}

	@Override
	public boolean isOpen() throws ModelRuntimeException {
		try {
			return this.connection != null && this.connection.isOpen();
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	/**
	 * Closes the Connection to the wrapper Repository.
	 */
	@Override
	public void close() {
		try {
			if (isOpen()) {
				this.connection.close();
				this.connection = null;
			}
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public BlankNode createBlankNode() {
		return new OpenrdfBlankNode(this.valueFactory.createBNode());
	}

	public BlankNode createBlankNode(String internalID) {
		return new OpenrdfBlankNode(this.valueFactory.createBNode(internalID));
	}

	public boolean isValidURI(String uriString) {
		boolean isValid = true;
		try {
			this.valueFactory.createURI(uriString);
		} catch (IllegalArgumentException e) {
			isValid = false;
		}
		return isValid;
	}

	@Override
	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		assertModel();
		try {
			// convert parameters to OpenRDF data types
			org.openrdf.model.Resource openRdfSubject = (org.openrdf.model.Resource) ConversionUtil
					.toOpenRDF(subject, this.valueFactory);
			org.openrdf.model.URI openRdfPredicate = ConversionUtil.toOpenRDF(
					predicate, this.valueFactory);
			Value openRdfObject = ConversionUtil.toOpenRDF(object,
					this.valueFactory);

			// add the statement
			this.connection.add(openRdfSubject, openRdfPredicate,
					openRdfObject, this.openRdfContext);
			if (log.isDebugEnabled()) {
				this.connection.commit();
				if (!contains(subject, predicate, object)) {
					log
							.warn("You just added a statement ("
									+ subject
									+ " "
									+ predicate
									+ " "
									+ object
									+ " ) which could not be stored. Most likely cause: http://openrdf.org/issues/browse/SES-521");
				}

			}
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	@Override
	public void removeAll(Iterator<? extends Statement> iterator)
			throws ModelRuntimeException {
		if (this.isLocked()) {
			throw new ModelRuntimeException(
					"Model is locked, cannot perform an update.");
		}
		// do not auto-commit
		assertModel();
		try {
			boolean autocommitBefore = this.connection.isAutoCommit();
			this.connection.setAutoCommit(false);
			try {
				try {
					// remove all
					while (iterator.hasNext()) {
						org.openrdf.model.Statement s = ConversionUtil
								.toOpenRDF(iterator.next(), this.valueFactory);
						this.connection.remove(s, this.openRdfContext);
					}
					this.connection.commit();
				} catch (RepositoryException x) {
					this.connection.rollback();
				}
			} finally {
				this.connection.setAutoCommit(autocommitBefore);
			}
		} catch (RepositoryException x) {
			throw new ModelRuntimeException(x);
		}
	}

	/* for performance reasons */
	@Override
	public void removeAll() throws ModelRuntimeException {
		if (this.isLocked()) {
			throw new ModelRuntimeException(
					"Model is locked, cannot perform an update.");
		}
		// do not auto-commit
		assertModel();
		try {
			boolean autocommitBefore = this.connection.isAutoCommit();
			this.connection.setAutoCommit(false);
			// remove all
			this.connection.clear(this.openRdfContext);
			this.connection.setAutoCommit(autocommitBefore);
		} catch (RepositoryException x) {
			throw new ModelRuntimeException(x);
		}

	}

	@Override
	public void addAll(Iterator<? extends Statement> iterator)
			throws ModelRuntimeException {
		if (this.isLocked()) {
			throw new ModelRuntimeException(
					"Model is locked, cannot perform an update.");
		}
		// do not auto-commit
		assertModel();
		try {
			boolean autocommitBefore = this.connection.isAutoCommit();
			this.connection.setAutoCommit(false);
			try {
				try {
					// add
					while (iterator.hasNext()) {
						org.openrdf.model.Statement s = ConversionUtil
								.toOpenRDF(iterator.next(), this.valueFactory);
						this.connection.add(s, this.openRdfContext);
					}
					this.connection.commit();
				} catch (RepositoryException x) {
					this.connection.rollback();
				}
			} finally {
				this.connection.setAutoCommit(autocommitBefore);
			}
		} catch (RepositoryException x) {
			throw new ModelRuntimeException(x);
		}
	}

	@Override
	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		assertModel();
		try {
			// convert parameters to OpenRDF data types
			org.openrdf.model.Resource openRdfSubject = (org.openrdf.model.Resource) ConversionUtil
					.toOpenRDF(subject, this.valueFactory);
			org.openrdf.model.URI openRdfPredicate = ConversionUtil.toOpenRDF(
					predicate, this.valueFactory);
			Value openRdfObject = ConversionUtil.toOpenRDF(object,
					this.valueFactory);

			// remove the statement
			this.connection.remove(openRdfSubject, openRdfPredicate,
					openRdfObject, this.openRdfContext);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);

		}
	}

	public ClosableIterator<org.ontoware.rdf2go.model.Statement> findStatements(
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		assertModel();
		// convert parameters to OpenRDF data types
		org.openrdf.model.Resource openRdfSubject = (org.openrdf.model.Resource) ConversionUtil
				.toOpenRDF(subject, this.valueFactory);
		org.openrdf.model.URI openRdfPredicate = (org.openrdf.model.URI) ConversionUtil
				.toOpenRDF(predicate, this.valueFactory);
		Value openRdfObject = ConversionUtil.toOpenRDF(object,
				this.valueFactory);

		try {
			// find the matching statements
			CloseableIteration<? extends org.openrdf.model.Statement, ? extends OpenRDFException> statements = this.connection
					.getStatements(openRdfSubject, openRdfPredicate,
							openRdfObject, true, this.openRdfContext);
			// wrap them in a StatementIterable
			return new StatementIterator(statements, this);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	@Override
	public boolean sparqlAsk(String query) throws ModelRuntimeException {
		assertModel();
		try {
			boolean result = this.connection.prepareBooleanQuery(
					QueryLanguage.SPARQL, query).evaluate();
			return result;
		} catch (MalformedQueryException e) {
			throw new ModelRuntimeException(e);
		} catch (UnsupportedQueryLanguageException e) {
			throw new ModelRuntimeException(e);
		} catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public ClosableIterable<Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		assertModel();
		try {
			GraphQueryResult graphQueryResult = this.connection
					.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate();
			return new GraphIterable(graphQueryResult, this);
		} catch (MalformedQueryException e) {
			throw new ModelRuntimeException(e);
		} catch (UnsupportedQueryLanguageException e) {
			throw new ModelRuntimeException(e);
		} catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public ClosableIterable<Statement> sparqlConstruct(String query)
			throws ModelRuntimeException {
		assertModel();
		try {
			GraphQueryResult graphQueryResult = this.connection
					.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate();
			return new GraphIterable(graphQueryResult, this);
		} catch (MalformedQueryException e) {
			throw new ModelRuntimeException(e);
		} catch (UnsupportedQueryLanguageException e) {
			throw new ModelRuntimeException(e);
		} catch (QueryEvaluationException e) {
			throw new ModelRuntimeException(e);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	/* enable SeRQL queries, too */
	@Override
	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, ModelRuntimeException {
		assertModel();
		if (querylanguage.equalsIgnoreCase("SPARQL"))
			return sparqlSelect(query);
		else {
			QueryLanguage ql = QueryLanguage.valueOf(querylanguage);
			if (ql == null) {
				throw new QueryLanguageNotSupportedException(
						"Unsupported query language: '" + querylanguage + "'");
			}
			return new RepositoryQueryResultTable(query, ql, this.connection);
		}
	}

	/* enable SeRQL queries, too */
	@Override
	public ClosableIterable<Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			ModelRuntimeException {
		assertModel();
		if (querylanguage.equalsIgnoreCase("SPARQL"))
			return sparqlConstruct(query);
		else {
			QueryLanguage ql = QueryLanguage.valueOf(querylanguage);
			if (ql == null) {
				throw new QueryLanguageNotSupportedException(
						"Unsupported query language: '" + querylanguage + "'");
			}
			try {
				GraphQueryResult graphQueryResult = this.connection
						.prepareGraphQuery(ql, query).evaluate();
				return new GraphIterable(graphQueryResult, this);
			} catch (MalformedQueryException e) {
				throw new ModelRuntimeException(e);
			} catch (UnsupportedQueryLanguageException e) {
				throw new ModelRuntimeException(e);
			} catch (QueryEvaluationException e) {
				throw new ModelRuntimeException(e);
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
	}

	public QueryResultTable sparqlSelect(String queryString)
			throws ModelRuntimeException {
		assertModel();
		return new RepositoryQueryResultTable(queryString, this.connection);
	}

	public ClosableIterator<Statement> iterator() {
		assertModel();
		try {
			CloseableIteration<? extends org.openrdf.model.Statement, RepositoryException> statements = this.connection
					.getStatements(null, null, null, true, this.openRdfContext);
			return new StatementIterator(statements, this);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	@Override
	public Object getUnderlyingModelImplementation() {
		return this.repository;
	}

	public void setUnderlyingModelImplementation(Object object) {
		this.repository = (Repository) object;
	}

	@Override
	public long size() throws ModelRuntimeException {
		assertModel();
		try {
			return this.connection.size(this.openRdfContext);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public URI getContextURI() {
		if (this.context.toString().equals(DEFAULT_CONTEXT)) {
			return null;
		} else {
			return this.context;
		}
	}

	public synchronized boolean isLocked() {
		return this.locked;
	}

	/**
	 * Locking a RepositoryModel disables auto-commit mode and starts a new
	 * transaction, which is left open until this RepositoryModel is unlocked.
	 */
	public synchronized void lock() throws LockException {
		if (isLocked()) {
			return;
		}

		try {
			// mark this model as locked
			this.locked = true;

			// disable auto-commit
			this.autocommitBeforeLock = this.connection.isAutoCommit();
			this.connection.setAutoCommit(false);

			// flush everything that has not been commited yet
			this.connection.commit();
		} catch (RepositoryException e) {
			throw new LockException(e);
		}
	}

	/**
	 * Ends the locking status, committing all changed that have been made since
	 * this RepositoryModel was locked and switching back to auto-commit mode.
	 */
	public synchronized void unlock() {
		if (!isLocked()) {
			return;
		}

		try {

			// commit all changes
			this.connection.commit();

			// re-enable auto commit mode
			this.connection.setAutoCommit(this.autocommitBeforeLock);

			// unlock this model
			this.locked = false;
		} catch (RepositoryException e) {
			// TODO: a LockException would be more appropriate IMHO but this
			// requires a change to the RDF2Go API
			throw new ModelRuntimeException(e);
		}
	}

	public synchronized void rollback() {
		if (!isLocked()) {
			return;
		}

		assertModel();
		try {
			this.connection.rollback();
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void dump() {
		assertModel();
		Iterator<Statement> iterator = iterator();
		System.out
				.println("Dumping Repository contents ----------------------------------------------");

		while (iterator.hasNext()) {
			iterator.next().dump(null);
		}
	}

	public void readFrom(InputStream stream) throws IOException,
			ModelRuntimeException {
		readFrom(stream, RDFFormat.RDFXML, "");
	}

	@Override
	public void readFrom(InputStream stream, Syntax syntax) throws IOException,
			ModelRuntimeException {
		RDFFormat format = RDFFormat.forMIMEType(syntax.getMimeType());
		if (format == null) {
			throw new ModelRuntimeException("unknown syntax: " + syntax);
		}

		readFrom(stream, format, "");
	}

	@Override
	public void readFrom(InputStream stream, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException {
		RDFFormat format = RDFFormat.forMIMEType(syntax.getMimeType());
		if (format == null) {
			throw new ModelRuntimeException("unknown syntax: " + syntax);
		}

		readFrom(stream, format, baseURI);
	}

	@Override
	public void readFrom(Reader reader, Syntax syntax, String baseURI)
			throws ModelRuntimeException, IOException {
		RDFFormat format = RDFFormat.forMIMEType(syntax.getMimeType());
		if (format == null) {
			throw new ModelRuntimeException("unknown syntax: " + syntax);
		}

		readFrom(reader, format, baseURI);

	}

	public void readFrom(InputStream stream, RDFFormat format, String baseURI)
			throws IOException, ModelRuntimeException {
		assertModel();
		try {
			this.connection.add(stream, baseURI, format, this.openRdfContext);
		} catch (RDFParseException e) {
			throw new ModelRuntimeException(e);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void readFrom(Reader reader) throws IOException,
			ModelRuntimeException {
		readFrom(reader, RDFFormat.RDFXML, "");
	}

	public void readFrom(Reader reader, Syntax syntax) throws IOException,
			ModelRuntimeException {
		RDFFormat format = RDFFormat.forMIMEType(syntax.getMimeType());
		if (format == null) {
			throw new ModelRuntimeException("unknown syntax: " + format);
		}

		readFrom(reader, format, "");
	}

	public void readFrom(Reader reader, RDFFormat format, String baseURL)
			throws IOException, ModelRuntimeException {
		assertModel();
		try {
			this.connection.add(reader, baseURL, format, this.openRdfContext);
		} catch (RDFParseException e) {
			IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void writeTo(OutputStream stream) throws IOException,
			ModelRuntimeException {
		writeTo(stream, Syntax.RdfXml);
	}

	@SuppressWarnings("unused")
	@Override
	public void writeTo(OutputStream stream, Syntax syntax) throws
	// interface allows it
			IOException, ModelRuntimeException {
		RDFWriter rdfWriter = Rio.createWriter(getRDFFormat(syntax), stream);
		writeTo(rdfWriter);
	}

	public void writeTo(Writer writer) throws ModelRuntimeException {
		writeTo(writer, Syntax.RdfXml);
	}

	public void writeTo(Writer writer, Syntax syntax)
			throws ModelRuntimeException {
		assertModel();
		RDFWriter rdfWriter = Rio.createWriter(getRDFFormat(syntax), writer);
		writeTo(rdfWriter);
	}

	/**
	 * Resolves a RDF2Go Syntax to an OpenRDF RDFFormat.
	 * 
	 * @param syntax
	 *            The RDF2Go Syntax to resolve.
	 * @return A RDFFormat for the specified syntax.
	 * @throws ModelRuntimeException
	 *             When the Syntax could not be resolved to a RDFFormat.
	 */
	public static RDFFormat getRDFFormat(Syntax syntax)
			throws ModelRuntimeException {
		String mimeType = syntax.getMimeType();
		return RDFFormat.forMIMEType(mimeType);
	}

	public void writeTo(RDFWriter writer) throws ModelRuntimeException {
		assertModel();
		try {
			this.connection.exportStatements(null, null, null, false, writer,
					this.openRdfContext);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		} catch (RDFHandlerException e) {
			throw new ModelRuntimeException(e);
		}
	}

	@Override
	public void commit() {
		try {
			this.connection.commit();
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	@Override
	public void setAutocommit(boolean autocommit) {
		assertModel();
		try {
			this.connection.setAutoCommit(autocommit);
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Makes sure that the Connection to the wrapped Repository has been closed.
	 */
	@Override
	public void finalize() throws Throwable {
		try {
			if (this.connection.isOpen()) {
				if (this.caller != null) {
					StringWriter sw = new StringWriter();
					this.caller.printStackTrace(new PrintWriter(sw));
					this.logger.warn(this.getClass().getName()
							+ " not closed, closing now. Cause "
							+ sw.getBuffer().toString());
				} else {
					this.logger.warn(this.getClass().getName()
							+ " not closed, closing now.");
				}
				close();
			}
		} finally {
			super.finalize();
		}
	}

	@Override
	protected void assertModel() {
		if (this.repository == null) {
			throw new ModelRuntimeException("Repository is null");
		}
		if (this.connection == null) {
			throw new ModelRuntimeException("Connection is null");
		}
	}

	/* NOT YET IMPLEMENTED */
	public boolean isIsomorphicWith(@SuppressWarnings("unused") Model other) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Deprecated
	public void update(Diff diff) throws ModelRuntimeException {
		update((DiffReader) diff);
	}

	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
		if (this.isLocked()) {
			throw new ModelRuntimeException(
					"Model is locked, cannot perform an update.");
		}
		// do not auto-commit
		assertModel();
		try {
			boolean autocommitBefore = this.connection.isAutoCommit();
			this.connection.setAutoCommit(false);
			try {
				try {
					// remove
					Iterator<? extends Statement> it = diff.getRemoved()
							.iterator();
					while (it.hasNext()) {
						org.openrdf.model.Statement s = ConversionUtil
								.toOpenRDF(it.next(), this.valueFactory);
						this.connection.remove(s, this.openRdfContext);
					}
					// add
					it = diff.getAdded().iterator();
					while (it.hasNext()) {
						org.openrdf.model.Statement s = ConversionUtil
								.toOpenRDF(it.next(), this.valueFactory);
						this.connection.add(s, this.openRdfContext);
					}
					this.connection.commit();
				} catch (RepositoryException x) {
					this.logger.warn("Could not commit, rolling back.", x);
					this.connection.rollback();
				}
			} finally {
				this.connection.setAutoCommit(autocommitBefore);
			}
		} catch (RepositoryException x) {
			throw new ModelRuntimeException(x);
		}

	}

	public String getNamespace(String prefix) {
		assertModel();
		try {
			return this.connection.getNamespace(prefix);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public Map<String, String> getNamespaces() {
		assertModel();
		Map<String, String> nsMap = new HashMap<String, String>();
		try {
			RepositoryResult<Namespace> openrdfMap = this.connection
					.getNamespaces();
			openrdfMap.enableDuplicateFilter();
			List<Namespace> openrdfList = openrdfMap.asList();
			for (Namespace openrdfNamespace : openrdfList) {
				nsMap.put(openrdfNamespace.getPrefix(), openrdfNamespace
						.getName());
			}
			return nsMap;
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void removeNamespace(String prefix) {
		assertModel();
		try {
			this.connection.removeNamespace(prefix);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void setNamespace(String prefix, String namespaceURI)
			throws IllegalArgumentException {
		assertModel();
		try {
			this.connection.setNamespace(prefix, namespaceURI);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

}
