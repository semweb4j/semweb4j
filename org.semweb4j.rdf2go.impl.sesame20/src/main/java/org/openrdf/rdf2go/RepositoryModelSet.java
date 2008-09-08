/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.DiffReader;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QuadPattern;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.impl.AbstractModelSetImpl;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

/**
 * A ModelSet implementation for the OpenRDF (Sesame) 2.0.1 API.
 * 
 * Note that RepositoryModel and RepositoryModelSet only work well together
 * because they both keep their RepositoryConnections in auto-commit mode. This
 * cannot be changed by the user. Do mass-updates using {@link #update(Diff)},
 * {@link #addAll(Iterator)} or {@link #removeAll(Iterator)}, then the current
 * connection will be used in non-autocommit mode and commited, including a
 * rollback when it fails.
 * 
 * <p>
 * Note: Every Model returned by this implementation should be explicitly closed
 * by the user.
 */
public class RepositoryModelSet extends AbstractModelSetImpl {

	private static class ContextIterator implements ClosableIterator<URI> {

		private RepositoryResult<org.openrdf.model.Resource> idIterator;

		public ContextIterator(
				RepositoryResult<org.openrdf.model.Resource> idIterator) {
			this.idIterator = idIterator;
		}

		public void close() {
			try {
				this.idIterator.close();
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}

		public boolean hasNext() {
			try {
				return this.idIterator.hasNext();
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}

		public URI next() {
			try {
				return (URI) ConversionUtil.toRdf2go(this.idIterator.next());
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/*
	 * Note that RepositoryModel and RepositoryModelSet only work well together
	 * when they both keep their RepositoryConnections in auto-commit mode.
	 * Currently this is the case and it cannot be changed by users of these
	 * instances (the connection is hidden), but it's important to realize when
	 * doing future modifications.
	 * 
	 * Note: update sets the connection's autocommit to false to perform mass
	 * updates, this can cause sideeffects.
	 */

	private class ModelIterator implements ClosableIterator<Model> {

		private ClosableIterator<URI> contextIterator;

		private URI lastURI;

		public ModelIterator(ClosableIterator<URI> contextIterator) {
			this.contextIterator = contextIterator;
		}

		public void close() {
			this.contextIterator.close();
		}

		public boolean hasNext() {
			return this.contextIterator.hasNext();
		}

		public Model next() {
			Model model = null;
			URI uri = this.contextIterator.next();
			model = new RepositoryModel(uri, RepositoryModelSet.this.repository);
			model.open();
			this.lastURI = uri;
			return model;
		}

		public void remove() {
			// only possible when next() has been invoked at least once
			if (this.lastURI == null) {
				throw new IllegalStateException();
			}

			// create a temporary Model for the last URI and let it remove all
			// statements
			Model tmpModel = null;

			try {
				tmpModel = new RepositoryModel(this.lastURI,
						RepositoryModelSet.this.repository);
				tmpModel.removeAll();
			} finally {
				if (tmpModel != null) {
					tmpModel.close();
				}
				this.lastURI = null;
			}
		}
	}

	private class OpenRDFQuadPattern implements QuadPattern {

		private UriOrVariable context;

		private NodeOrVariable object;

		private UriOrVariable predicate;

		private ResourceOrVariable subject;

		public OpenRDFQuadPattern(UriOrVariable context,
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

		public UriOrVariable getContext() {
			return this.context;
		}

		public NodeOrVariable getObject() {
			return this.object;
		}

		public UriOrVariable getPredicate() {
			return this.predicate;
		}

		public ResourceOrVariable getSubject() {
			return this.subject;
		}

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

	private static class StatementIterator implements
			ClosableIterator<Statement> {

		private RepositoryResult<org.openrdf.model.Statement> result;

		public StatementIterator(
				RepositoryResult<org.openrdf.model.Statement> result) {
			this.result = result;
		}

		public void close() {
			try {
				this.result.close();
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}

		public boolean hasNext() {
			try {
				return this.result.hasNext();
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}

		public Statement next() {
			try {
				org.openrdf.model.Statement statement = this.result.next();
				return new StatementWrapper(null, statement);
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}

		public void remove() {
			try {
				this.result.remove();
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
	}

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory
			.getLogger(RepositoryModelSet.class);

	private RepositoryConnection connection;

	private Repository repository;

	private ValueFactory valueFactory;

	public RepositoryModelSet(Repository repository)
			throws ModelRuntimeException {
		this.init(repository);
	}

	@Override
	public void addAll(Iterator<? extends Statement> iterator)
			throws ModelRuntimeException {
		if (this.isLocked()) {
			throw new ModelRuntimeException(
					"Model is locked, cannot perform an update.");
		}
		// do not auto-commit
		this.assertModel();
		try {

			boolean autocommitBefore = this.connection.isAutoCommit();
			this.connection.setAutoCommit(false);
			try {
				try {
					// add
					while (iterator.hasNext()) {
						Statement s = iterator.next();
						org.openrdf.model.URI context = ConversionUtil
								.toOpenRDF(s.getContext(), this.valueFactory);
						org.openrdf.model.Statement sd = ConversionUtil
								.toOpenRDF(s, this.valueFactory);
						this.connection.add(sd, context);
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
	public boolean addModel(Model model) throws ModelRuntimeException {
		this.assertModel();

		// When the Model and the ModelSet both use a Repository, we can
		// optimise adding statements.
		//
		// We use an explicit check for RepositoryModel instances rather than
		// checking the type of Model.getUnderlyingetc. as other
		// Repository-based Models may use the Repository in a different way
		// (think handling of null contexts, etc.)
		//
		// When both instances use the same Repository instance, copying can
		// even be left out, thus also preventing deadlocks when e.g. adding a
		// Model to the ModelSet that created it.

		if (model instanceof RepositoryModel) {
			RepositoryModel repositoryModel = (RepositoryModel) model;

			if (repositoryModel.repository == this.repository) {
				// done, no need to copy
				return true;
			} else {
				// copy statements directly from Repository to Repository,
				// without using RDF2Go-specific wrappers
				org.openrdf.model.URI context = repositoryModel
						.getOpenRDFContextURI();

				RepositoryResult<org.openrdf.model.Statement> statements = null;
				try {
					statements = repositoryModel.connection.getStatements(null,
							null, null, false, context);

					// doesn't hurt to explicitly add them to the same context
					this.connection.add(statements, context);
				} catch (RepositoryException e) {
					throw new ModelRuntimeException(e);
				} finally {
					if (statements != null) {
						try {
							statements.close();
						} catch (RepositoryException e) {
							throw new ModelRuntimeException(e);
						}
					}
				}
			}
		} else {
			return super.addModel(model);
		}

		return true;
	}

	@Override
	public void addModel(Model model, URI contextURI)
			throws ModelRuntimeException {
		this.assertModel();

		// When the Model and the ModelSet both use a Repository, we can
		// optimise adding statements.
		//
		// We use an explicit check for RepositoryModel instances rather than
		// checking the type of Model.getUnderlyingetc. as other
		// Repository-based Models may use the Repository in a different way
		// (think handling of null contexts, etc.)

		if (model instanceof RepositoryModel) {
			RepositoryModel repositoryModel = (RepositoryModel) model;

			org.openrdf.model.URI openrdfContextURI = ConversionUtil.toOpenRDF(
					contextURI, this.valueFactory);

			if (repositoryModel.repository == this.repository) {
				// When both instances use the same Repository instance, we need
				// to copy

				// find the matching OpenRDF Statements
				try {
					RepositoryResult<org.openrdf.model.Statement> statements = repositoryModel.connection
							.getStatements(null, null, null, true);
					// buffer in memory to avoid deadlocks due to mixed
					// read/write
					Set<org.openrdf.model.Statement> stmts = new HashSet<org.openrdf.model.Statement>();
					for (org.openrdf.model.Statement stmt : statements.asList()) {
						stmts.add(stmt);
					}
					// now insert with a different context URI
					for (org.openrdf.model.Statement stmt : stmts) {
						this.valueFactory.createStatement(stmt.getSubject(), stmt
								.getPredicate(), stmt.getObject(),
								openrdfContextURI);
					}
				} catch (RepositoryException e) {
					throw new ModelRuntimeException(e);
				}
			} else {
				// copy statements directly from Repository to Repository,
				// without using RDF2Go-specific wrappers
				org.openrdf.model.URI context = repositoryModel
						.getOpenRDFContextURI();

				RepositoryResult<org.openrdf.model.Statement> statements = null;
				try {
					statements = repositoryModel.connection.getStatements(null,
							null, null, false, context);

					// doesn't hurt to explicitly add them to the right context
					for (org.openrdf.model.Statement stmt : statements.asList()) {
						this.connection.add(this.valueFactory.createStatement(stmt
								.getSubject(), stmt.getPredicate(), stmt
								.getObject(), openrdfContextURI), openrdfContextURI);
					}
				} catch (RepositoryException e) {
					throw new ModelRuntimeException(e);
				} finally {
					if (statements != null) {
						try {
							statements.close();
						} catch (RepositoryException e) {
							throw new ModelRuntimeException(e);
						}
					}
				}
			}
		} else {
			super.addModel(model,contextURI);
		}
	}

	@Override
	public void addModelSet(ModelSet modelSet) throws ModelRuntimeException {
		this.assertModel();

		// When the Model and the ModelSet both use a Repository, we can
		// optimise adding statements.
		//
		// We use an explicit check for RepositoryModel instances rather than
		// checking the type of Model.getUnderlyingetc. as other
		// Repository-based Models may use the Repository in a different way
		// (think handling of null contexts, etc.)
		//
		// When both instances use the same Repository instance, copying can
		// even be left out, thus also preventing deadlocks when e.g. adding a
		// Model to the ModelSet that created it.

		if (modelSet instanceof RepositoryModel) {
			RepositoryModel repositoryModel = (RepositoryModel) modelSet;

			if (repositoryModel.repository == this.repository) {
				// done, no need to copy
			} else {
				// copy statements directly from Repository to Repository,
				// without using RDF2Go-specific wrappers
				RepositoryResult<org.openrdf.model.Statement> statements = null;
				try {
					statements = repositoryModel.connection.getStatements(null,
							null, null, false, new Resource[0]);

					this.connection.add(statements);
				} catch (RepositoryException e) {
					throw new ModelRuntimeException(e);
				} finally {
					if (statements != null) {
						try {
							statements.close();
						} catch (RepositoryException e) {
							throw new ModelRuntimeException(e);
						}
					}
				}
			}
		} else {
			super.addModelSet(modelSet);
		}

	}

	public void addStatement(Statement statement) throws ModelRuntimeException {
		this.assertModel();
		try {
			org.openrdf.model.Statement s = ConversionUtil.toOpenRDF(statement,
					this.valueFactory);
			this.connection.add(s, s.getContext());
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	/**
	 * Releases all resources held by the RepositoryModelSet. After this, the
	 * effect and results of all other operations are undefined.
	 */
	public void close() {
		if (this.isOpen()) {
			try {
				this.connection.close();
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			} finally {
				this.connection = null;
			}
		}
	}

	public void commit() {
		try {
			this.connection.commit();
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	@Override
	public boolean contains(Statement s) throws ModelRuntimeException {
		return this.containsStatements(s.getContext(), s.getSubject(), s
				.getPredicate(), s.getObject());
	}

	public boolean containsModel(URI contextURI) {
		try {
			return this.connection.hasStatement(null, null, null, false,
					ConversionUtil.toOpenRDF(contextURI, this.repository
							.getValueFactory()));
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean containsStatements(UriOrVariable contextURI,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		this.assertModel();
		ClosableIterator<Statement> i = this.findStatements(contextURI,
				subject, predicate, object);
		try {
			return i.hasNext();
		} finally {
			i.close();
		}
	}

	public QuadPattern createQuadPattern(UriOrVariable context,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) {
		return new OpenRDFQuadPattern(context, subject, predicate, object);
	}

	@Override
	public URI createURI(String uriString) throws ModelRuntimeException {
		// check if Sesame accepts this URI
		try {
			this.valueFactory.createURI(uriString);
		} catch (IllegalArgumentException e) {
			throw new ModelRuntimeException("Wrong URI format for Sesame", e);
		}

		// return an URI if no error occured
		return new URIImpl(uriString, false);
	}

	@Override
	public void dump() {
		this.assertModel();
		try {
			this.writeTo(System.out, Syntax.RdfXml);
		} catch (IOException e) {
			throw new ModelRuntimeException(e);
		}
		// RDFWriter sesameWriter = new RDFXMLPrettyWriter(System.out);
		// try {
		// connection.export(sesameWriter);
		// }
		// catch (Exception se) {
		// throw new ModelRuntimeException("Couldn't dump a model", se);
		// }
	}

	@Override
	public void finalize() throws Throwable {
		try {
			this.close();
		} finally {
			super.finalize();
		}
	}

	@Override
	public ClosableIterator<Statement> findStatements(QuadPattern pattern)
			throws ModelRuntimeException {
		return this.findStatements(pattern.getContext(), pattern.getSubject(),
				pattern.getPredicate(), pattern.getObject());
	}

	@Override
	public ClosableIterator<Statement> findStatements(UriOrVariable contextURI,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		this.assertModel();
		// convert parameters to OpenRDF data types
		org.openrdf.model.Resource sesameSubject = (org.openrdf.model.Resource) ConversionUtil
				.toOpenRDF(subject, this.valueFactory);
		org.openrdf.model.URI sesamePredicate = (org.openrdf.model.URI) ConversionUtil
				.toOpenRDF(predicate, this.valueFactory);
		Value sesameObject = ConversionUtil
				.toOpenRDF(object, this.valueFactory);
		org.openrdf.model.URI sesameContext = (org.openrdf.model.URI) ConversionUtil
				.toOpenRDF(contextURI, this.valueFactory);

		try {
			// find the matching OpenRDF Statements
			RepositoryResult<org.openrdf.model.Statement> statements;
			// had some null-pointer exceptions here, checking contextUri
			if ((contextURI != null) && !contextURI.equals(Variable.ANY)) {
				statements = this.connection.getStatements(sesameSubject,
						sesamePredicate, sesameObject, true, sesameContext);
			} else {
				statements = this.connection.getStatements(sesameSubject,
						sesamePredicate, sesameObject, true);
			}

			// wrap them in an iterator that converts them to RDF2Go Statements
			return new StatementIterator(statements);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public Model getDefaultModel() {
		Model model = new RepositoryModel(this.repository);
		model.open();
		return model;
	}

	public Model getModel(URI contextURI) {
		Model model = new RepositoryModel(contextURI, this.repository);
		model.open();
		return model;
	}

	public ClosableIterator<Model> getModels() {
		this.assertModel();
		return new ModelIterator(this.getModelURIs());
	}

	public ClosableIterator<URI> getModelURIs() {
		this.assertModel();
		try {
			return new ContextIterator(this.connection.getContextIDs());
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public String getNamespace(String prefix) {
		this.assertModel();
		try {
			return this.connection.getNamespace(prefix);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public Map<String, String> getNamespaces() {
		this.assertModel();
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

	public Repository getUnderlyingModelImplementation() {
		return this.getUnderlyingModelSetImplementation();
	}

	public Repository getUnderlyingModelSetImplementation() {
		return this.repository;
	}

	/**
	 * Returns whether the RepositoryModelSet is currently opened.
	 */
	public boolean isOpen() {
		return this.connection != null;
	}

	@Override
	public ClosableIterator<Statement> iterator() {
		this.assertModel();
		try {
			// find the matching OpenRDF Statements
			RepositoryResult<org.openrdf.model.Statement> statements = this.connection
					.getStatements(null, null, null, true);
			// wrap them in an iterator that converts them to RDF2Go Statements
			return new StatementIterator(statements);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	/**
	 * Prepares the RepositoryModelSet for operation. Before opening, the result
	 * and effects of all other operations are undefined.
	 */
	public void open() {
		if (!this.isOpen()) {
			try {
				this.connection = this.repository.getConnection();
				this.connection.setAutoCommit(true);
			} catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
	}

	public ClosableIterable<Statement> queryConstruct(String queryString,
			String queryLanguage) throws QueryLanguageNotSupportedException,
			ModelRuntimeException {
		this.assertModel();
		// resolve the query language String to a QueryLanguage
		QueryLanguage language = ConversionUtil
				.toOpenRDFQueryLanguage(queryLanguage);

		try {
			// determine query result
			GraphQuery query = this.connection.prepareGraphQuery(language,
					queryString);
			GraphQueryResult queryResult = query.evaluate();

			// wrap it in a GraphIterable
			return new GraphIterable(queryResult, null);
		} catch (OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public QueryResultTable querySelect(String queryString, String queryLanguage)
			throws QueryLanguageNotSupportedException, ModelRuntimeException {
		this.assertModel();
		QueryLanguage language = ConversionUtil
				.toOpenRDFQueryLanguage(queryLanguage);
		return new RepositoryQueryResultTable(queryString, language,
				this.connection);
	}

	public void readFrom(InputStream in) throws IOException,
			ModelRuntimeException {
		this.readFrom(in, Syntax.RdfXml);
	}

	@Override
	public void readFrom(InputStream in, Syntax syntax) throws IOException,
			ModelRuntimeException {
		this.assertModel();
		RDFFormat rdfformat = RDFFormat.forMIMEType(syntax.getMimeType());
		if (rdfformat == null) {
			throw new ModelRuntimeException("unknown Syntax: "
					+ syntax.toString());
		} else {
			try {
				this.connection.add(in, "", rdfformat);
			} catch (OpenRDFException e) {
				throw new ModelRuntimeException(e);
			}
		}
	}

	public void readFrom(InputStream in, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException {
		this.assertModel();
		RDFFormat rdfformat = RDFFormat.forMIMEType(syntax.getMimeType());
		if (rdfformat == null) {
			throw new ModelRuntimeException("unknown Syntax: "
					+ syntax.toString());
		} else {
			try {
				this.connection.add(in, baseURI, rdfformat);
			} catch (OpenRDFException e) {
				throw new ModelRuntimeException(e);
			}
		}
	}

	public void readFrom(Reader reader) throws IOException,
			ModelRuntimeException {
		this.readFrom(reader, Syntax.RdfXml);
	}

	@Override
	public void readFrom(Reader reader, Syntax syntax) throws IOException,
			ModelRuntimeException {
		this.assertModel();
		RDFFormat format = RDFFormat.forMIMEType(syntax.getMimeType());
		if (format == null) {
			throw new ModelRuntimeException("unknown RDF syntax: "
					+ syntax.toString());
		} else {
			try {
				this.connection.add(reader, "", format);
			} catch (OpenRDFException e) {
				throw new ModelRuntimeException(e);
			}
		}
	}

	public void readFrom(Reader reader, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException {
		this.assertModel();
		RDFFormat format = RDFFormat.forMIMEType(syntax.getMimeType());
		if (format == null) {
			throw new ModelRuntimeException("unknown RDF syntax: "
					+ syntax.toString());
		} else {
			try {
				this.connection.add(reader, baseURI, format);
			} catch (OpenRDFException e) {
				throw new ModelRuntimeException(e);
			}
		}
	}

	@Override
	public void removeAll() throws ModelRuntimeException {
		this.assertModel();
		try {
			this.connection.clear();
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
		this.assertModel();
		try {
			boolean autocommitBefore = this.connection.isAutoCommit();
			this.connection.setAutoCommit(false);
			try {
				try {
					// add
					while (iterator.hasNext()) {
						Statement s = iterator.next();
						org.openrdf.model.URI context = ConversionUtil
								.toOpenRDF(s.getContext(), this.valueFactory);
						org.openrdf.model.Statement sd = ConversionUtil
								.toOpenRDF(s, this.valueFactory);
						this.connection.remove(sd, context);
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

	// @Override
	public boolean removeModel(URI contextURI) {
		this.assertModel();
		org.openrdf.model.Resource context = ConversionUtil.toOpenRDF(
				contextURI, this.valueFactory);
		try {
			this.connection.clear(context);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
		return true;
	}

	public void removeNamespace(String prefix) {
		this.assertModel();
		try {
			this.connection.removeNamespace(prefix);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void removeStatement(Statement statement)
			throws ModelRuntimeException {
		this.assertModel();
		try {
			org.openrdf.model.Statement s = ConversionUtil.toOpenRDF(statement,
					this.valueFactory);
			this.connection.remove(s, s.getContext());
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	@Override
	public void removeStatements(QuadPattern pattern)
			throws ModelRuntimeException {
		this.removeStatements(pattern.getContext(), pattern.getSubject(),
				pattern.getPredicate(), pattern.getObject());
	}

	@Override
	public void removeStatements(UriOrVariable context,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		this.assertModel();
		try {
			Resource s = (Resource) ConversionUtil.toOpenRDF(subject,
					this.valueFactory);
			org.openrdf.model.URI p = (org.openrdf.model.URI) ConversionUtil
					.toOpenRDF(predicate, this.valueFactory);
			Value o = ConversionUtil.toOpenRDF(object, this.valueFactory);
			Resource c = (Resource) ConversionUtil.toOpenRDF(context,
					this.valueFactory);
			if (c != null)
				this.connection.remove(s, p, o, c);
			else
				this.connection.remove(s, p, o);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void setAutocommit(boolean autocommit) {
		try {
			this.connection.setAutoCommit(autocommit);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void setNamespace(String prefix, String namespaceURI)
			throws IllegalArgumentException {
		this.assertModel();
		try {
			this.connection.setNamespace(prefix, namespaceURI);
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void setUnderlyingModelSetImplementation(Repository repository) {
		// get rid of any connections
		boolean open = this.isOpen();
		if (open) {
			this.close();
		}

		// setup access to the new repository
		this.init(repository);

		// make sure the ModelSet is in the same "opened" state as before
		if (open) {
			this.open();
		}
	}

	public long size() throws ModelRuntimeException {
		this.assertModel();
		try {
			return this.connection.size();
		} catch (RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}

	@Override
	public boolean sparqlAsk(String queryString) throws ModelRuntimeException {
		this.assertModel();
		BooleanQuery booleanQuery;
		try {
			booleanQuery = this.connection.prepareBooleanQuery(
					QueryLanguage.SPARQL, queryString);
			boolean result = booleanQuery.evaluate();
			return result;
		} catch (OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public ClosableIterable<Statement> sparqlConstruct(String queryString)
			throws ModelRuntimeException {
		this.assertModel();
		GraphQuery query;
		try {
			query = this.connection.prepareGraphQuery(QueryLanguage.SPARQL,
					queryString);
			GraphQueryResult graphQueryResult = query.evaluate();
			return new StatementIterable(graphQueryResult, null);
		} catch (OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public ClosableIterable<Statement> sparqlDescribe(String queryString)
			throws ModelRuntimeException {
		this.assertModel();
		GraphQuery query;
		try {
			query = this.connection.prepareGraphQuery(QueryLanguage.SPARQL,
					queryString);
			GraphQueryResult graphQueryResult = query.evaluate();
			return new StatementIterable(graphQueryResult, null);
		} catch (OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public QueryResultTable sparqlSelect(String queryString)
			throws ModelRuntimeException {
		return new RepositoryQueryResultTable(queryString, this.connection);
	}

	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
		if (this.isLocked()) {
			throw new ModelRuntimeException(
					"ModelSet is locked, cannot perform an update.");
		}
		// do not auto-commit
		this.assertModel();
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
						this.connection.remove(s, s.getContext());
					}
					// add
					it = diff.getAdded().iterator();
					while (it.hasNext()) {
						org.openrdf.model.Statement s = ConversionUtil
								.toOpenRDF(it.next(), this.valueFactory);
						this.connection.add(s, s.getContext());
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

	/**
	 * Writes the whole ModelSet in TriX syntax to the OutputStream.
	 */
	public void writeTo(OutputStream out) throws IOException,
			ModelRuntimeException {
		this.writeTo(out, Syntax.Trix);
	}

	/**
	 * Writes the whole ModelSet to the OutputStream. Depending on the Syntax
	 * the context URIs might or might not be serialized. TriX should be able to
	 * serialize contexts.
	 */
	@SuppressWarnings("unused")
	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException {
		RDFWriter rdfWriter = Rio.createWriter(RepositoryModel
				.getRDFFormat(syntax), out);
		this.writeTo(rdfWriter);
	}

	/**
	 * Writes the whole ModelSet in TriX syntax to the writer.
	 */
	public void writeTo(Writer writer) throws IOException,
			ModelRuntimeException {
		this.writeTo(writer, Syntax.Trix);
	}

	/**
	 * Writes the whole ModelSet to the Writer. Depending on the Syntax the
	 * context URIs might or might not be serialized. TriX should be able to
	 * serialize contexts.
	 */
	@SuppressWarnings("unused")
	@Override
	public void writeTo(Writer writer, Syntax syntax) throws IOException,
			ModelRuntimeException {
		RDFWriter rdfWriter = Rio.createWriter(RepositoryModel
				.getRDFFormat(syntax), writer);
		this.writeTo(rdfWriter);
	}

	/**
	 * same as model.assetModel()
	 * 
	 */
	protected void assertModel() {
		if (this.repository == null) {
			throw new ModelRuntimeException("Repository is null");
		}
		if (this.connection == null) {
			throw new ModelRuntimeException("Connection is null");
		}
	}

	private void init(Repository repository) {
		this.repository = repository;
		this.valueFactory = repository.getValueFactory();
	}

	private void writeTo(RDFWriter writer) throws ModelRuntimeException {
		this.assertModel();
		try {
			this.connection.export(writer);
		} catch (OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}

}
