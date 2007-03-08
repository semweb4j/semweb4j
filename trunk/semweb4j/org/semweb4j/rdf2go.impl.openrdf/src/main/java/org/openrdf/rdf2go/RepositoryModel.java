/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

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
import org.ontoware.rdf2go.impl.sesame2.SesameGraphIterable;
import org.ontoware.rdf2go.impl.sesame2.SesameQueryResultTable;
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
import org.ontoware.rdf2go.model.node.impl.BlankNodeImpl;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.querylanguage.MalformedQueryException;
import org.openrdf.querylanguage.UnsupportedQueryLanguageException;
import org.openrdf.querymodel.QueryLanguage;
import org.openrdf.queryresult.GraphQueryResult;
import org.openrdf.repository.Connection;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.rio.rdfxml.RDFXMLPrettyWriter;
import org.openrdf.rio.trix.TriXWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.SailInitializationException;
import org.openrdf.sail.inferencer.MemoryStoreRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.util.iterator.CloseableIterator;

/**
 * NOTE: This is code from Aduna, for Sesame 2.0 SNAPSHOT, retrofitted by Max
 * Völkel to mathc the older Sesame 2.0 alpha 4 API.
 * 
 * Implementation of the RDF2Go model interface for an OpenRDF Repository.
 */
public class RepositoryModel extends AbstractLockingModel implements Model {

	private static final Log log = LogFactory.getLog(RepositoryModel.class);

	/**
	 * In Sesame/OpenRDF the default context (= context of the SPARQL default
	 * model) is "null"
	 */
	public static final String DEFAULT_CONTEXT = null;

	private Repository repository;

	private ValueFactory valueFactory;

	private boolean inferencing = false;

	private boolean locked = false;

	//private URI context;

	private org.openrdf.model.URI openRdfContext;

	private Connection connection;

	/**
	 * Creates a new RepositoryModel encapsulating a dedicated in-memory
	 * Repository.
	 * 
	 * @param rdfsInferencing
	 *            Indicates whether or not the Repository should apply RDFS
	 *            inferencing.
	 * @throws ModelRuntimeException
	 *             Whenever construction of the in-memory Repository throws an
	 *             Exception.
	 */
	public RepositoryModel(boolean rdfsInferencing) throws ModelRuntimeException {
		this.inferencing = rdfsInferencing;
		init(true);
	}

	public RepositoryModel(Repository repository) throws ModelRuntimeException {
		this.repository = repository;
		init(false);
	}

	public RepositoryModel(URI context, Repository repository)
			throws ModelRuntimeException {
		if (repository == null) {
			throw new IllegalArgumentException("Repository cannot be null");
		}

		if (context != null) {
			this.openRdfContext = ConversionUtil.toOpenRDF(context, repository.getValueFactory() );
		}
		this.repository = repository;

		init(false);
	}

	public RepositoryModel(URI context, boolean inferencing)
			throws ModelRuntimeException {
		this.inferencing = inferencing;
		init(true);
		this.openRdfContext = ConversionUtil.toOpenRDF(context, this.valueFactory );
	}

	private void init(boolean createRepository) throws ModelRuntimeException {
		// create the repository - if requested
		if (createRepository) {
			Sail sail = new MemoryStore();
			if (inferencing) {
				sail = new MemoryStoreRDFSInferencer(sail);
			}
			repository = new RepositoryImpl(sail);

			try {
				repository.initialize();
			} catch (SailInitializationException e) {
				throw new ModelRuntimeException(e);
			}
		}

		open();

		// setup ValueFactory and context
		valueFactory = repository.getValueFactory();
	}
	
	///////////////////////////
	// open & close

	/**
	 * Opens the underlying connection.
	 */
	public void open() {
		// establish a connection
		try {
			connection = repository.getConnection();
			connection.setAutoCommit(true);
		} catch (SailException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	public boolean isOpen() {
		return this.connection != null && this.connection.isOpen();
	}

	/**
	 * Closes the Connection to the wrapper Repository.
	 */
	public void close() {
		try {
			if (connection.isOpen()) {
				connection.close();
			}
		} catch (SailException e) {
			throw new ModelRuntimeException(e);
		}
	}

	/**
	 * Makes sure that the Connection to the wrapped Repository has been closed.
	 */
	public void finalize() throws Throwable {
		if (connection.isOpen()) {
			log.warn(this.getClass().getName() + " not closed, closing now");
			close();
		}
		super.finalize();
	}

	
	/////////////////////////////////////////
	
	public BlankNode createBlankNode() {
		return new BlankNodeImpl(valueFactory.createBNode());
	}

	public boolean isValidURI(String uriString) {
		boolean isValid = true;
		try {
			valueFactory.createURI(uriString);
		} catch (IllegalArgumentException e) {
			isValid = false;
		}
		return isValid;
	}

	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		try {
			// convert parameters to OpenRDF data types
			org.openrdf.model.Resource openRdfSubject = (org.openrdf.model.Resource) ConversionUtil
					.toOpenRDF(subject, valueFactory);
			org.openrdf.model.URI openRdfPredicate = ConversionUtil.toOpenRDF(
					predicate, valueFactory);
			Value openRdfObject = ConversionUtil
					.toOpenRDF(object, valueFactory);

			// add the statement
			connection.add(openRdfSubject, openRdfPredicate, openRdfObject,
					openRdfContext);
		} catch (SailException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		try {
			// convert parameters to OpenRDF data types
			org.openrdf.model.Resource openRdfSubject = (org.openrdf.model.Resource) ConversionUtil
					.toOpenRDF(subject, valueFactory);
			org.openrdf.model.URI openRdfPredicate = ConversionUtil.toOpenRDF(
					predicate, valueFactory);
			Value openRdfObject = ConversionUtil
					.toOpenRDF(object, valueFactory);

			// remove the statement
			connection.remove(openRdfSubject, openRdfPredicate, openRdfObject,
					openRdfContext);
		} catch (SailException e) {
			throw new ModelRuntimeException(e);

		}
	}

	public ClosableIterator<? extends Statement> findStatements(
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		// convert parameters to OpenRDF data types
		org.openrdf.model.Resource openRdfSubject = (org.openrdf.model.Resource) ConversionUtil
				.toOpenRDF(subject, valueFactory);
		org.openrdf.model.URI openRdfPredicate = (org.openrdf.model.URI) ConversionUtil
				.toOpenRDF(predicate, valueFactory);
		Value openRdfObject = ConversionUtil.toOpenRDF(object, valueFactory);

		// find the matching statements
		CloseableIterator<? extends org.openrdf.model.Statement> statements = connection
				.getStatements(openRdfSubject, openRdfPredicate, openRdfObject,
						openRdfContext, inferencing);

		// wrap them in a StatementIterable
		return new StatementIterator(statements, this);
	}

	public boolean sparqlAsk(String query) throws ModelRuntimeException {
		throw new UnsupportedOperationException(
				"SPARQL Ask not currently supported in Repository adapter");
	}

	public ClosableIterable<org.ontoware.rdf2go.model.Statement> sparqlDescribe(
			String query) throws ModelRuntimeException {
		throw new UnsupportedOperationException(
				"SPARQL Describe not currently supported in Repository Adapter");
	}

	public ClosableIterator<Statement> iterator() {
		CloseableIterator<? extends org.openrdf.model.Statement> statements = connection
				.getStatements(null, null, null, openRdfContext, inferencing);
		return new StatementIterable(statements, this).iterator();
	}

	public Object getUnderlyingModelImplementation() {
		return repository;
	}

	public void setUnderlyingModelImplementation(Object object) {
		repository = (Repository) object;
	}

	public long size() throws ModelRuntimeException {
		long size = connection.size(openRdfContext);
		if (size > Integer.MAX_VALUE) {
			throw new ModelRuntimeException("size too large to fit in int: " + size);
		} else {
			return (int) size;
		}
	}

	public URI getContextURI() {
		return ConversionUtil.toRdf2go(this.openRdfContext);
	}

	public synchronized boolean isLocked() {
		return locked;
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
			locked = true;

			// disable auto-commit
			connection.setAutoCommit(false);

			// flush everything that has not been commited yet
			connection.commit();
		} catch (SailException e) {
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
			connection.commit();

			// re-enable auto commit mode
			connection.setAutoCommit(true);

			// unlock this model
			locked = false;
		} catch (SailException e) {
			// TODO: a LockException would be more appropriate IMHO but this
			// requires a change to the RDF2Go API
			throw new ModelRuntimeException(e);
		}
	}

	public synchronized void rollback() {
		if (!isLocked()) {
			return;
		}

		try {
			connection.rollback();
		} catch (SailException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void dump() {
		Iterator<Statement> iterator = iterator();
		System.out
				.println("Dumping Repository contents ----------------------------------------------");

		while (iterator.hasNext()) {
			iterator.next().dump(null);
		}
	}

	public void readFrom(Reader r) {
		try {
			connection.add(r, "", RDFFormat.RDFXML, openRdfContext);
		} catch (Exception e) {
			throw e instanceof ModelRuntimeException ? (ModelRuntimeException) e
					: new ModelRuntimeException(e);
		}
	}

	public void writeTo(Writer w) {
		RDFWriter rdfWriter = new RDFXMLPrettyWriter(w);

		try {
			connection.exportStatements(null, null, null, openRdfContext,
					false, rdfWriter);
		} catch (RDFHandlerException e) {
			throw new ModelRuntimeException(e);
		}
	}


	// ////////////////////////////
	// copied from older impl

	public void readFrom(Reader r, Syntax syntax) {
		try {
			RDFFormat rdfformat = RDFFormat.forMIMEType(syntax.getMimeType());
			if (rdfformat == null) {
				throw new RuntimeException("unknown Syntax: "
						+ syntax.toString());
			} else {
				connection.add(r, "", rdfformat, this.openRdfContext);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (RDFParseException e) {
			throw new RuntimeException(e);
		} catch (SailException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedRDFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeTo(Writer writer, Syntax syntax) {

		RDFWriter sesameWriter = null;

		// TODO: maybe better compare the names
		if (syntax == Syntax.RdfXml) {
			sesameWriter = new RDFXMLPrettyWriter(writer);
		} else if (syntax == Syntax.Ntriples) {
			sesameWriter = new NTriplesWriter(writer);
		} else if (syntax == Syntax.Turtle) {
			sesameWriter = new TurtleWriter(writer);
		} else if (syntax == Syntax.Trix) {
			sesameWriter = new TriXWriter(writer);
		} else {
			throw new RuntimeException("unknown Syntax: " + syntax.toString());
		}

		try {
			connection.exportContext(this.openRdfContext, sesameWriter);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	public void readFrom(InputStream in) throws IOException, ModelRuntimeException {
		readFrom(in, Syntax.RdfXml);
	}

	public void readFrom(InputStream in, Syntax syntax) throws IOException,
			ModelRuntimeException {
		try {
			RDFFormat rdfformat = RDFFormat.forMIMEType(syntax.getMimeType());
			if (rdfformat == null) {
				throw new RuntimeException("unknown Syntax: "
						+ syntax.toString());
			} else {
				connection.add(in, "", rdfformat, openRdfContext);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (RDFParseException e) {
			throw new RuntimeException(e);
		} catch (SailException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedRDFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		writeTo(out, Syntax.RdfXml);
	}

	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException {

		RDFWriter sesameWriter = null;

		// TODO: maybe better compare the names
		if (syntax == Syntax.RdfXml) {
			sesameWriter = new RDFXMLPrettyWriter(out);
		} else if (syntax == Syntax.Ntriples) {
			sesameWriter = new NTriplesWriter(out);
		} else if (syntax == Syntax.Turtle) {
			sesameWriter = new TurtleWriter(out);
		} else if (syntax == Syntax.Trix) {
			sesameWriter = new TriXWriter(out);
		} else {
			throw new RuntimeException("unknown Syntax: " + syntax.toString());
		}

		try {
			connection.exportContext(this.openRdfContext, sesameWriter);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	public ClosableIterable<? extends org.ontoware.rdf2go.model.Statement> queryConstruct(
			String query, String querylanguage)
			throws QueryLanguageNotSupportedException, ModelRuntimeException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not yet impl");
	}

	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, ModelRuntimeException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not yet impl");
	}

	// sparql construct returns a graph
	public ClosableIterable<org.ontoware.rdf2go.model.Statement> sparqlConstruct(
			String query) throws ModelRuntimeException {
		try {
			// sesame returns a special result object
			GraphQueryResult graphQueryResult = connection.evaluateGraphQuery(
					QueryLanguage.SPARQL, query);

			return new SesameGraphIterable(graphQueryResult);

		} catch (MalformedQueryException e) {
			throw new ModelRuntimeException(e);
		} catch (UnsupportedQueryLanguageException e) {
			throw new ModelRuntimeException(e);
		}
	}

	// sparql select returns triples
	public QueryResultTable sparqlSelect(String queryString)
			throws ModelRuntimeException {
		// do all the dirty work inside of the iterator...
		return new SesameQueryResultTable(queryString, connection);
	}

	public boolean isIsomorphicWith(Model other) {
		throw new RuntimeException("NOT IMPLemented");
	}

}
