/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 * 
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import static org.openrdf.rdf2go.RepositoryModel.getRDFFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
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
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ModelSet implementation for the OpenRDF (Sesame) 2.0.1 API.
 * 
 * Note that RepositoryModel and RepositoryModelSet only work well together
 * because they both keep their RepositoryConnections in auto-commit mode. This
 * cannot be changed by the user. Do mass-updates using
 * {@link #update(DiffReader)}, {@link #addAll(Iterator)} or
 * {@link #removeAll(Iterator)}, then the current connection will be used in
 * non-autocommit mode and commited, including a rollback when it fails.
 * 
 * <p>
 * Note: Every Model returned by this implementation should be explicitly closed
 * by the user.
 */
public class RepositoryModelSet extends AbstractModelSetImpl {
	
	/**
     * 
     */
	private static final long serialVersionUID = 202502559337092796L;
	
	private static class ContextIterator implements ClosableIterator<URI> {
		
		private final RepositoryResult<org.openrdf.model.Resource> idIterator;
		
		public ContextIterator(RepositoryResult<org.openrdf.model.Resource> idIterator) {
			this.idIterator = idIterator;
		}
		
		@Override
		public void close() {
			try {
				this.idIterator.close();
			} catch(RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
		
		@Override
		public boolean hasNext() {
			try {
				return this.idIterator.hasNext();
			} catch(RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
		
		@Override
		public URI next() {
			try {
				return (URI)ConversionUtil.toRdf2go(this.idIterator.next());
			} catch(RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
		
		@Override
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
		
		private final ClosableIterator<URI> contextIterator;
		
		private URI lastURI;
		
		public ModelIterator(ClosableIterator<URI> contextIterator) {
			this.contextIterator = contextIterator;
		}
		
		@Override
		public void close() {
			this.contextIterator.close();
		}
		
		@Override
		public boolean hasNext() {
			return this.contextIterator.hasNext();
		}
		
		@Override
		public Model next() {
			RepositoryModel model = null;
			URI uri = this.contextIterator.next();
			model = new RepositoryModel(uri, RepositoryModelSet.this.repository);
			model.open();
			RepositoryModelSet.this.openModels.put(model, null);
			this.lastURI = uri;
			return model;
		}
		
		@Override
		public void remove() {
			// only possible when next() has been invoked at least once
			if(this.lastURI == null) {
				throw new IllegalStateException();
			}
			
			// create a temporary Model for the last URI and let it remove all
			// statements
			Model tmpModel = null;
			
			try {
				tmpModel = new RepositoryModel(this.lastURI, RepositoryModelSet.this.repository);
				tmpModel.removeAll();
			} finally {
				if(tmpModel != null) {
					tmpModel.close();
				}
				this.lastURI = null;
			}
		}
	}
	
	private class OpenRDFQuadPattern implements QuadPattern {
		
		/**
         * 
         */
		private static final long serialVersionUID = -2397218722246644188L;
		
		private final UriOrVariable context;
		
		private final NodeOrVariable object;
		
		private final UriOrVariable predicate;
		
		private final ResourceOrVariable subject;
		
		public OpenRDFQuadPattern(UriOrVariable context, ResourceOrVariable subject,
		        UriOrVariable predicate, NodeOrVariable object) {
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
			if(value == null) {
				throw new NullPointerException();
			}
		}
		
		private boolean matches(Node node, NodeOrVariable variable) {
			return variable.equals(Variable.ANY) || variable.equals(node);
		}
	}
	
	private static class StatementIterator implements ClosableIterator<Statement> {
		
		private final RepositoryResult<org.openrdf.model.Statement> result;
		
		public StatementIterator(RepositoryResult<org.openrdf.model.Statement> result) {
			this.result = result;
		}
		
		@Override
		public void close() {
			try {
				this.result.close();
			} catch(RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
		
		@Override
		public boolean hasNext() {
			try {
				return this.result.hasNext();
			} catch(RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
		
		@Override
		public Statement next() {
			try {
				org.openrdf.model.Statement statement = this.result.next();
				return new StatementWrapper(null, statement);
			} catch(RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
		
		@Override
		public void remove() {
			try {
				this.result.remove();
			} catch(RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(RepositoryModelSet.class);
	
	private RepositoryConnection connection;
	
	private Repository repository;
	
	private ValueFactory valueFactory;
	
	private final WeakHashMap<RepositoryModel,Object> openModels = new WeakHashMap<RepositoryModel,Object>();
	
	public RepositoryModelSet(Repository repository) throws ModelRuntimeException {
		this.init(repository);
	}
	
	@Override
	public void addAll(Iterator<? extends Statement> iterator) throws ModelRuntimeException {
		if(this.isLocked()) {
			throw new ModelRuntimeException("Model is locked, cannot perform an update.");
		}
		// do not auto-commit
		this.assertModel();
		try {
			
			this.connection.begin();
			try {
				try {
					// add
					while(iterator.hasNext()) {
						Statement s = iterator.next();
						org.openrdf.model.URI context = ConversionUtil.toOpenRDF(s.getContext(),
						        this.valueFactory);
						org.openrdf.model.Statement sd = ConversionUtil.toOpenRDF(s,
						        this.valueFactory);
						this.connection.add(sd, context);
					}
					this.connection.commit();
				} catch(RepositoryException x) {
					this.connection.rollback();
				}
			} finally {
				this.connection.commit();
			}
		} catch(RepositoryException x) {
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
		
		if(model instanceof RepositoryModel) {
			RepositoryModel repositoryModel = (RepositoryModel)model;
			
			if(repositoryModel.repository == this.repository) {
				// done, no need to copy
				return true;
			} else {
				// copy statements directly from Repository to Repository,
				// without using RDF2Go-specific wrappers
				org.openrdf.model.URI context = repositoryModel.getOpenRDFContextURI();
				
				RepositoryResult<org.openrdf.model.Statement> statements = null;
				try {
					statements = repositoryModel.connection.getStatements(null, null, null, false,
					        context);
					
					// doesn't hurt to explicitly add them to the same context
					this.connection.add(statements, context);
				} catch(RepositoryException e) {
					throw new ModelRuntimeException(e);
				} finally {
					if(statements != null) {
						try {
							statements.close();
						} catch(RepositoryException e) {
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
	public void addModel(Model model, URI contextURI) throws ModelRuntimeException {
		this.assertModel();
		
		// When the Model and the ModelSet both use a Repository, we can
		// optimise adding statements.
		//
		// We use an explicit check for RepositoryModel instances rather than
		// checking the type of Model.getUnderlyingetc. as other
		// Repository-based Models may use the Repository in a different way
		// (think handling of null contexts, etc.)
		
		if(model instanceof RepositoryModel) {
			RepositoryModel repositoryModel = (RepositoryModel)model;
			
			org.openrdf.model.URI openrdfContextURI = ConversionUtil.toOpenRDF(contextURI,
			        this.valueFactory);
			
			if(repositoryModel.repository == this.repository) {
				// When both instances use the same Repository instance, we need
				// to copy
				
				// find the matching OpenRDF Statements
				try {
					RepositoryResult<org.openrdf.model.Statement> statements = repositoryModel.connection
					        .getStatements(null, null, null, true);
					// buffer in memory to avoid deadlocks due to mixed
					// read/write
					Set<org.openrdf.model.Statement> stmts = new HashSet<org.openrdf.model.Statement>();
					while (statements.hasNext()) {
						stmts.add(statements.next());
					}
					// now insert with a different context URI
					for(org.openrdf.model.Statement stmt : stmts) {
						this.valueFactory.createStatement(stmt.getSubject(), stmt.getPredicate(),
						        stmt.getObject(), openrdfContextURI);
					}
				} catch(RepositoryException e) {
					throw new ModelRuntimeException(e);
				}
			} else {
				// copy statements directly from Repository to Repository,
				// without using RDF2Go-specific wrappers
				org.openrdf.model.URI context = repositoryModel.getOpenRDFContextURI();
				
				RepositoryResult<org.openrdf.model.Statement> statements = null;
				try {
					statements = repositoryModel.connection.getStatements(null, null, null, false,
					        context);
					
					// doesn't hurt to explicitly add them to the right context
					while (statements.hasNext()) {
						org.openrdf.model.Statement stmt = statements.next();
						this.connection.add(
						        this.valueFactory.createStatement(stmt.getSubject(),
						                stmt.getPredicate(), stmt.getObject(), openrdfContextURI),
						        openrdfContextURI);
					}
				} catch(RepositoryException e) {
					throw new ModelRuntimeException(e);
				} finally {
					if(statements != null) {
						try {
							statements.close();
						} catch(RepositoryException e) {
							throw new ModelRuntimeException(e);
						}
					}
				}
			}
		} else {
			super.addModel(model, contextURI);
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
		
		if(modelSet instanceof RepositoryModel) {
			RepositoryModel repositoryModel = (RepositoryModel)modelSet;
			
			if(repositoryModel.repository == this.repository) {
				// done, no need to copy
			} else {
				// copy statements directly from Repository to Repository,
				// without using RDF2Go-specific wrappers
				RepositoryResult<org.openrdf.model.Statement> statements = null;
				try {
					statements = repositoryModel.connection.getStatements(null, null, null, false,
					        new Resource[0]);
					
					this.connection.add(statements);
				} catch(RepositoryException e) {
					throw new ModelRuntimeException(e);
				} finally {
					if(statements != null) {
						try {
							statements.close();
						} catch(RepositoryException e) {
							throw new ModelRuntimeException(e);
						}
					}
				}
			}
		} else {
			super.addModelSet(modelSet);
		}
		
	}
	
	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		this.assertModel();
		try {
			org.openrdf.model.Statement s = ConversionUtil.toOpenRDF(statement, this.valueFactory);
			this.connection.add(s, s.getContext());
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	/**
	 * Releases all resources held by the RepositoryModelSet. After this, the
	 * effect and results of all other operations are undefined.
	 */
	@Override
	public void close() {
		if(this.isOpen()) {
			try {
				for(Iterator<RepositoryModel> i = this.openModels.keySet().iterator(); i.hasNext();) {
					Model m = i.next();
					if(m != null)
						m.close();
				}
				this.connection.close();
			} catch(RepositoryException e) {
				throw new ModelRuntimeException(e);
			} finally {
				this.connection = null;
			}
		}
	}
	
	@Override
	@Deprecated
	public void commit() {
		try {
			this.connection.commit();
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public boolean contains(Statement s) throws ModelRuntimeException {
		return this.containsStatements(s.getContext(), s.getSubject(), s.getPredicate(),
		        s.getObject());
	}
	
	@Override
	public boolean containsModel(URI contextURI) {
		try {
			return this.connection.hasStatement(null, null, null, false,
			        ConversionUtil.toOpenRDF(contextURI, this.repository.getValueFactory()));
		} catch(RepositoryException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean containsStatements(UriOrVariable contextURI, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException {
		this.assertModel();
		ClosableIterator<Statement> i = this.findStatements(contextURI, subject, predicate, object);
		try {
			return i.hasNext();
		} finally {
			i.close();
		}
	}
	
	@Override
	public QuadPattern createQuadPattern(UriOrVariable context, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) {
		return new OpenRDFQuadPattern(context, subject, predicate, object);
	}
	
	@Override
	public URI createURI(String uriString) throws ModelRuntimeException {
		// check if Sesame accepts this URI
		try {
			this.valueFactory.createURI(uriString);
		} catch(IllegalArgumentException e) {
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
		} catch(IOException e) {
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
	        ResourceOrVariable subject, UriOrVariable predicate, NodeOrVariable object)
	        throws ModelRuntimeException {
		this.assertModel();
		// convert parameters to OpenRDF data types
		org.openrdf.model.Resource sesameSubject = (org.openrdf.model.Resource)ConversionUtil
		        .toOpenRDF(subject, this.valueFactory);
		org.openrdf.model.URI sesamePredicate = (org.openrdf.model.URI)ConversionUtil.toOpenRDF(
		        predicate, this.valueFactory);
		Value sesameObject = ConversionUtil.toOpenRDF(object, this.valueFactory);
		org.openrdf.model.URI sesameContext = (org.openrdf.model.URI)ConversionUtil.toOpenRDF(
		        contextURI, this.valueFactory);
		
		try {
			// find the matching OpenRDF Statements
			RepositoryResult<org.openrdf.model.Statement> statements;
			// had some null-pointer exceptions here, checking contextUri
			if((contextURI != null) && !contextURI.equals(Variable.ANY)) {
				statements = this.connection.getStatements(sesameSubject, sesamePredicate,
				        sesameObject, true, sesameContext);
			} else {
				statements = this.connection.getStatements(sesameSubject, sesamePredicate,
				        sesameObject, true);
			}
			
			// wrap them in an iterator that converts them to RDF2Go Statements
			return new StatementIterator(statements);
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public Model getDefaultModel() {
		RepositoryModel model = new RepositoryModel(this.repository);
		model.open();
		this.openModels.put(model, null);
		return model;
	}
	
	@Override
	public Model getModel(URI contextURI) {
		RepositoryModel model = new RepositoryModel(contextURI, this.repository);
		model.open();
		this.openModels.put(model, null);
		return model;
	}
	
	@Override
	public ClosableIterator<Model> getModels() {
		this.assertModel();
		return new ModelIterator(this.getModelURIs());
	}
	
	@Override
	public ClosableIterator<URI> getModelURIs() {
		this.assertModel();
		try {
			return new ContextIterator(this.connection.getContextIDs());
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public String getNamespace(String prefix) {
		this.assertModel();
		try {
			return this.connection.getNamespace(prefix);
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public Map<String,String> getNamespaces() {
		this.assertModel();
		Map<String,String> nsMap = new HashMap<String,String>();
		try {
			RepositoryResult<Namespace> openrdfMap = this.connection.getNamespaces();
			openrdfMap.enableDuplicateFilter();
			while (openrdfMap.hasNext()) {
				Namespace openrdfNamespace = openrdfMap.next();
				nsMap.put(openrdfNamespace.getPrefix(), openrdfNamespace.getName());
			}
			return nsMap;
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	public Repository getUnderlyingModelImplementation() {
		return this.getUnderlyingModelSetImplementation();
	}
	
	@Override
	public Repository getUnderlyingModelSetImplementation() {
		return this.repository;
	}
	
	/**
	 * Returns whether the RepositoryModelSet is currently opened.
	 */
	@Override
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
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	/**
	 * Prepares the RepositoryModelSet for operation. Before opening, the result
	 * and effects of all other operations are undefined.
	 */
	@Override
	public ModelSet open() {
		if(!this.isOpen()) {
			try {
				this.connection = this.repository.getConnection();
			} catch(RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
		return this;
	}
	
	@Override
	public ClosableIterable<Statement> queryConstruct(String queryString, String queryLanguage)
	        throws QueryLanguageNotSupportedException, ModelRuntimeException {
		this.assertModel();
		// resolve the query language String to a QueryLanguage
		QueryLanguage language = ConversionUtil.toOpenRDFQueryLanguage(queryLanguage);
		
		try {
			// determine query result
			GraphQuery query = this.connection.prepareGraphQuery(language, queryString);
			GraphQueryResult queryResult = query.evaluate();
			
			// wrap it in a GraphIterable
			return new GraphIterable(queryResult, null);
		} catch(OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public QueryResultTable querySelect(String queryString, String queryLanguage)
	        throws QueryLanguageNotSupportedException, ModelRuntimeException {
		this.assertModel();
		QueryLanguage language = ConversionUtil.toOpenRDFQueryLanguage(queryLanguage);
		return new RepositoryQueryResultTable(queryString, language, this.connection);
	}
	
	@Override
	public void readFrom(InputStream in) throws IOException, ModelRuntimeException {
		this.readFrom(in, Syntax.RdfXml);
	}
	
	@Override
	public void readFrom(InputStream in, Syntax syntax) throws IOException, ModelRuntimeException {
		this.assertModel();

		try {
			this.connection.add(in, "", getRDFFormat(syntax));
		} catch(OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public void readFrom(InputStream in, Syntax syntax, String baseURI) throws IOException,
	        ModelRuntimeException {
		this.assertModel();

		try {
			this.connection.add(in, baseURI, getRDFFormat(syntax));
		} catch(OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public void readFrom(Reader reader) throws IOException, ModelRuntimeException {
		this.readFrom(reader, Syntax.RdfXml);
	}
	
	@Override
	public void readFrom(Reader reader, Syntax syntax) throws IOException, ModelRuntimeException {
		this.assertModel();

		try {
			this.connection.add(reader, "", getRDFFormat(syntax));
		} catch(OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public void readFrom(Reader reader, Syntax syntax, String baseURI) throws IOException,
	        ModelRuntimeException {
		this.assertModel();

		try {
			this.connection.add(reader, baseURI, getRDFFormat(syntax));
		} catch(OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public void removeAll() throws ModelRuntimeException {
		this.assertModel();
		try {
			this.connection.clear();
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public void removeAll(Iterator<? extends Statement> iterator) throws ModelRuntimeException {
		if(this.isLocked()) {
			throw new ModelRuntimeException("Model is locked, cannot perform an update.");
		}
		// do not auto-commit
		this.assertModel();
		try {
			this.connection.begin();
			try {
				try {
					// add
					while(iterator.hasNext()) {
						Statement s = iterator.next();
						org.openrdf.model.URI context = ConversionUtil.toOpenRDF(s.getContext(),
						        this.valueFactory);
						org.openrdf.model.Statement sd = ConversionUtil.toOpenRDF(s,
						        this.valueFactory);
						this.connection.remove(sd, context);
					}
					this.connection.commit();
				} catch(RepositoryException x) {
					this.connection.rollback();
				}
			} finally {
				this.connection.commit();
			}
		} catch(RepositoryException x) {
			throw new ModelRuntimeException(x);
		}
	}
	
	// @Override
	@Override
	public boolean removeModel(URI contextURI) {
		this.assertModel();
		org.openrdf.model.Resource context = ConversionUtil
		        .toOpenRDF(contextURI, this.valueFactory);
		try {
			this.connection.clear(context);
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
		return true;
	}
	
	@Override
	public void removeNamespace(String prefix) {
		this.assertModel();
		try {
			this.connection.removeNamespace(prefix);
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		this.assertModel();
		try {
			org.openrdf.model.Statement s = ConversionUtil.toOpenRDF(statement, this.valueFactory);
			this.connection.remove(s, s.getContext());
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public void removeStatements(QuadPattern pattern) throws ModelRuntimeException {
		this.removeStatements(pattern.getContext(), pattern.getSubject(), pattern.getPredicate(),
		        pattern.getObject());
	}
	
	@Override
	public void removeStatements(UriOrVariable context, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException {
		this.assertModel();
		try {
			Resource s = (Resource)ConversionUtil.toOpenRDF(subject, this.valueFactory);
			org.openrdf.model.URI p = (org.openrdf.model.URI)ConversionUtil.toOpenRDF(predicate,
			        this.valueFactory);
			Value o = ConversionUtil.toOpenRDF(object, this.valueFactory);
			Resource c = (Resource)ConversionUtil.toOpenRDF(context, this.valueFactory);
			if(c != null)
				this.connection.remove(s, p, o, c);
			else
				this.connection.remove(s, p, o);
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	@Deprecated
	public void setAutocommit(boolean autocommit) {
		try {
			this.connection.setAutoCommit(autocommit);
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public void setNamespace(String prefix, String namespaceURI) throws IllegalArgumentException {
		this.assertModel();
		try {
			this.connection.setNamespace(prefix, namespaceURI);
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	public void setUnderlyingModelSetImplementation(Repository repository) {
		// get rid of any connections
		boolean open = this.isOpen();
		if(open) {
			this.close();
		}
		
		// setup access to the new repository
		this.init(repository);
		
		// make sure the ModelSet is in the same "opened" state as before
		if(open) {
			this.open();
		}
	}
	
	@Override
	public long size() throws ModelRuntimeException {
		this.assertModel();
		try {
			return this.connection.size();
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public boolean sparqlAsk(String queryString) throws ModelRuntimeException {
		this.assertModel();
		BooleanQuery booleanQuery;
		try {
			booleanQuery = this.connection.prepareBooleanQuery(QueryLanguage.SPARQL, queryString);
			boolean result = booleanQuery.evaluate();
			return result;
		} catch(OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public ClosableIterable<Statement> sparqlConstruct(String queryString)
	        throws ModelRuntimeException {
		this.assertModel();
		GraphQuery query;
		try {
			query = this.connection.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
			GraphQueryResult graphQueryResult = query.evaluate();
			return new StatementIterable(graphQueryResult, null);
		} catch(OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public ClosableIterable<Statement> sparqlDescribe(String queryString)
	        throws ModelRuntimeException {
		this.assertModel();
		GraphQuery query;
		try {
			query = this.connection.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
			GraphQueryResult graphQueryResult = query.evaluate();
			return new StatementIterable(graphQueryResult, null);
		} catch(OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public QueryResultTable sparqlSelect(String queryString) throws ModelRuntimeException {
		return new RepositoryQueryResultTable(queryString, this.connection);
	}
	
	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
		if(this.isLocked()) {
			throw new ModelRuntimeException("ModelSet is locked, cannot perform an update.");
		}
		// do not auto-commit
		this.assertModel();
		try {
			this.connection.begin();
			try {
				try {
					// remove
					Iterator<? extends Statement> it = diff.getRemoved().iterator();
					while(it.hasNext()) {
						org.openrdf.model.Statement s = ConversionUtil.toOpenRDF(it.next(),
						        this.valueFactory);
						this.connection.remove(s, s.getContext());
					}
					// add
					it = diff.getAdded().iterator();
					while(it.hasNext()) {
						org.openrdf.model.Statement s = ConversionUtil.toOpenRDF(it.next(),
						        this.valueFactory);
						this.connection.add(s, s.getContext());
					}
					this.connection.commit();
				} catch(RepositoryException x) {
					this.connection.rollback();
				}
			} finally {
				this.connection.commit();
			}
		} catch(RepositoryException x) {
			throw new ModelRuntimeException(x);
		}
	}
	
	/**
	 * Writes the whole ModelSet in TriX syntax to the OutputStream.
	 */
	@Override
	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		this.writeTo(out, Syntax.Trix);
	}
	
	/**
	 * Writes the whole ModelSet to the OutputStream. Depending on the Syntax
	 * the context URIs might or might not be serialized. TriX should be able to
	 * serialize contexts.
	 */
	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws IOException, ModelRuntimeException {
		RDFWriter rdfWriter = Rio.createWriter(getRDFFormat(syntax), out);
		this.writeTo(rdfWriter);
	}
	
	/**
	 * Writes the whole ModelSet in TriX syntax to the writer.
	 */
	@Override
	public void writeTo(Writer writer) throws IOException, ModelRuntimeException {
		this.writeTo(writer, Syntax.Trix);
	}
	
	/**
	 * Writes the whole ModelSet to the Writer. Depending on the Syntax the
	 * context URIs might or might not be serialized. TriX should be able to
	 * serialize contexts.
	 */
	@Override
	public void writeTo(Writer writer, Syntax syntax) throws IOException, ModelRuntimeException {
		RDFWriter rdfWriter = Rio.createWriter(getRDFFormat(syntax), writer);
		this.writeTo(rdfWriter);
	}
	
	/**
	 * same as model.assetModel()
	 * 
	 */
	protected void assertModel() {
		if(this.repository == null) {
			throw new ModelRuntimeException("Repository is null");
		}
		if(this.connection == null) {
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
		} catch(OpenRDFException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
}
