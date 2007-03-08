/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.impl.sesame2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.impl.AbstractModelSetImpl;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.ConversionException;
import org.ontoware.rdf2go.util.Converter;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.querylanguage.MalformedQueryException;
import org.openrdf.querylanguage.UnsupportedQueryLanguageException;
import org.openrdf.querymodel.QueryLanguage;
import org.openrdf.queryresult.GraphQueryResult;
import org.openrdf.rdf2go.ConversionUtil;
import org.openrdf.rdf2go.RepositoryModel;
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
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.util.iterator.CloseableIterator;

/**
 * A ModelSet implementation for the Sesame 2.0 alpha4.
 * 
 * Every model returned by this implementation should be explicitly closed by
 * the user. Since the ModelSet interface doesn't have any close() method reuse
 * of models is impossible.
 * 
 * @author antheque
 */
public class ModelSetImplSesame extends AbstractModelSetImpl implements ModelSet {
    
	private static final Log log = LogFactory.getLog( ModelSetImplSesame.class );
	
    /**
     * convert a context ID to a model. 
     * needed by getModels()
     */
    private Converter<org.openrdf.model.Resource, Model> contextIDtoModel = new Converter<org.openrdf.model.Resource, Model>()
    {

        public Model convert(org.openrdf.model.Resource source) throws ConversionException {
            URI u = (URI) ConversionUtil.toRdf2go(source);
            
            RepositoryModel model;
            try {
                model = new RepositoryModel(u, repository);
            } catch (ModelRuntimeException e) {
                throw new ConversionException("Cannot create named model during conversion, context: "+source,
                    ModelSetImplSesame.this, Model.class);
            }
            return model;
        }
        
    };


	private Repository repository;
    
    private ValueFactory valueFactory;
    
	// re-use connection
	private Connection connection;

    private boolean inferencing = false;

	public ModelSetImplSesame(Repository repository) throws ModelRuntimeException {
		this.repository = repository;
		init(false);
	}
    
    private void init(boolean createRepository)
    throws ModelRuntimeException
    {
        // create the repository - if requested
        if (createRepository) {
            Sail sail = new MemoryStore();
            

            repository = new RepositoryImpl(sail);
    
            try {
                repository.initialize();
            }
            catch (SailInitializationException e) {
                throw new ModelRuntimeException(e);
            }
        }
    
        // establish a connection
        open();
    
        // setup ValueFactory and context
        valueFactory = repository.getValueFactory();
    }
    
    ///////////////////////////////////////////////////
    // Connection Handling
    ///////////////////////////////////////////////////

    /**
     * Checks connection for non-null and returns it.
     * @throws ModelRuntimeException if connection was null
     */
    protected Connection getConnection() {
        if (connection == null)
            throw new ModelRuntimeException("ModelSet sesame is depending on open/close. Please call open() to use the methods.");
        return connection;
    }

    /**
	 * Ensure we have an open connection. Re-use an already open connection.
	 * Sets auto-commit.
	 */
	public void open() {
		if (this.connection != null) {
			log.warn("Connection was already open when you tried to open it.");
			// re-use the open connection
		} else {
	    	assert this.repository != null;
	        // establish a connection
	        try {
	        	this.connection = repository.getConnection();
	            this.connection.setAutoCommit(true);
	        }
	        catch (Exception e) {
	            throw new ModelRuntimeException(e);
	        }
		}
	}

	/**
	 * Close a connection if there is one
	 */
	public void close() {
	
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SailException e) {
				// there is hardly anything we can do...
				throw new ModelRuntimeException(e);
			} finally {
				this.connection = null;
			}

		}
	}

    
	// //////////////////////////////////////////////////////////////////////
	// /////////////////////// BASIC MODELSET METHODS ///////////////////////
	// //////////////////////////////////////////////////////////////////////

    
	public Model getDefaultModel() {
		try {
			return new RepositoryModel(repository);
		} catch (ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public Model getModel(URI contextURI) {
		Model resultModel = null;

		try {
			resultModel = new RepositoryModel(contextURI, repository);
		} catch (ModelRuntimeException me) {
			throw new RuntimeException("Couldn't create the model", me);
		}

		return resultModel;
	}

	/**
	 * This implementation creates a
	 * 
	 * @see org.ontoware.rdf2go.model.ModelSet#removeModel(org.ontoware.rdf2go.model.Model)
	 */
	public boolean removeModel(Model model) {
		URI contextURI = model.getContextURI();

		Model ourModel = null;

		try {
			ourModel = new RepositoryModel(contextURI, repository);
			ourModel.removeAll();
		} catch (ModelRuntimeException me) {
			throw new RuntimeException("Couldn't remove the model", me);
		} finally {
			if (ourModel != null) {
				ourModel.close();
			}
		}

		return true;
	}

	public void removeAll() throws ModelRuntimeException {
		try {
			//open();
			getConnection().clear();
		} catch (SailException se) {
			throw new RuntimeException("Couldn't remove statements in a model",
					se);
		} finally {
			// close();
		}
	}

    /**
     * 
     * @see org.ontoware.rdf2go.model.ModelSet#getModels()
     */
	@SuppressWarnings("unchecked")
	public Iterator<? extends Model> getModels() {
        CloseableIterator<? extends Resource> it = getConnection().getContextIDs();
        CloseableIterator<Resource> casted = (CloseableIterator<Resource>)it;
        SesameCloseableIterator<Resource, Model> result = 
            new SesameCloseableIterator<Resource, Model>(casted, contextIDtoModel) ;
        return result;
	}

	// //////////////////////////////////////////////////////////////////////
	// /////////////////// ALL-MODELS STATEMENTS METHODS ////////////////////
	// //////////////////////////////////////////////////////////////////////

	public boolean containsStatements(UriOrVariable contextURI, ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException {
	    ClosableIterator i = findStatements(contextURI, subject, predicate, object);
        try {
            return i.hasNext();
        } finally
        {
            i.close();
        }
	}

	@SuppressWarnings("unchecked")
	public ClosableIterator<Statement> findStatements(UriOrVariable contextURI,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
	    //    convert parameters to Sesame data types
        org.openrdf.model.Resource sesameSubject = (org.openrdf.model.Resource)ConversionUtil.toOpenRDF(subject,
                valueFactory);
        org.openrdf.model.URI sesamePredicate = (org.openrdf.model.URI)ConversionUtil.toOpenRDF(predicate,
                valueFactory);
        Value sesameObject = ConversionUtil.toOpenRDF(object, valueFactory);
        org.openrdf.model.URI sesameContext = (org.openrdf.model.URI)ConversionUtil.toOpenRDF(contextURI, valueFactory);        

        try {
            // find the matching statements
            CloseableIterator<? extends org.openrdf.model.Statement> statements;
            if (!contextURI.equals(Variable.ANY))
                statements = connection.getStatements(
                    sesameSubject, sesamePredicate, sesameObject, sesameContext, inferencing);
            else
                statements = connection.getStatements(
                    sesameSubject, sesamePredicate, sesameObject, inferencing);
            // wrap them in a SesameCloseableStatementIterable
            return new SesameCloseableStatementIterator((CloseableIterator<org.openrdf.model.Statement>)statements);
        }
        catch (Exception e) {
            throw new ModelRuntimeException(e);
        }
	}


	public boolean sparqlAsk(String query) throws ModelRuntimeException {
		// TODO: implement
		throw new RuntimeException("Not yet implemented");
	}

	public ClosableIterable<Statement> sparqlConstruct(String query)
			throws ModelRuntimeException {
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

	public ClosableIterable<Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		// TODO: implement
		throw new RuntimeException("Not yet implemented");
	}

	public QueryResultTable sparqlSelect(String queryString)
			throws ModelRuntimeException {
		// TODO: test if this can query against the whole repository
		// do all the dirty work inside of the iterator...
		open();
		return new SesameQueryResultTable(queryString, getConnection());
	}

	/**
	 * @return an org.openrdf.repository.Repository
	 */
	public Object getUnderlyingModelImplementation() {
		return this.repository;
	}

	/**
	 * Expect an org.openrdf.repository.Repository
	 * Not implemented, can cause too much troubles,
     * facing the converter class and possible opened
     * connections.
	 * @param o implementation
	 */
	public void setUnderlyingModelImplementation(Object o) {
        throw new UnsupportedOperationException("Setting the repositories is not supported");
		//this.repository = (Repository) o;
	}

	public URI createURI(String uriString) throws ModelRuntimeException {
		// check if Sesaem would accept this uri
		try {
			repository.getValueFactory().createURI(uriString);
		} catch (IllegalArgumentException e) {
			throw new ModelRuntimeException("Wrong URI format for Sesame", e);
		}
		// return URI if no error occured
		return new URIImpl(uriString);
	}

	public void dump() {
		RDFWriter sesameWriter = new RDFXMLPrettyWriter(new PrintWriter(
				System.out));
		try {
			open();
			getConnection().export(sesameWriter);
		} catch (Exception se) {
			throw new RuntimeException("Couldn't dump a model", se);
		} finally {
			close();
		}

	}


    /**
     * 
     * @see org.ontoware.rdf2go.model.ModelSet#getModelURIs()
     */
	public Iterator<URI> getModelURIs() {
		
		return new OpenRdfResource2Rdf2GoURIIterator( getConnection().getContextIDs() );
		
//   Alternative impl using generic generics:		
//		
//        CloseableIterator<? extends Resource> ids = getConnection().getContextIDs();
//        CloseableIterator<Resource> need = (CloseableIterator<Resource>)ids;
//        
//        Converter<Resource, URI> converter = new Converter<Resource, URI>() {
//        	
//			public URI convert(Resource res) throws ConversionException {
//				return (URI) ConversionUtil.toRdf2go(res);
//			};
//        };
//        
//        SesameCloseableIterator<Resource, URI> result = 
//            new SesameCloseableIterator<Resource, URI>(need,  null);
//        return result;
	}




	public void readFrom(Reader reader) throws IOException, ModelRuntimeException {
		readFrom(reader, Syntax.RdfXml);
	}

	// TODO: find a way to set base URI
	public void readFrom(Reader reader, Syntax syntax) throws IOException,
			ModelRuntimeException {
		try {
			RDFFormat rdfformat = RDFFormat.forMIMEType(syntax.getMimeType());
			if (rdfformat == null) {
				throw new RuntimeException("unknown Syntax: "
						+ syntax.toString());
			} else {
				repository.getConnection().add(reader, "", rdfformat);
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
				repository.getConnection().add(in, "", rdfformat);
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

	public int size() throws ModelRuntimeException {
		return getConnection().size();
	}

	/**
	 * Writes the whole repository in TriX syntax to the writer
	 */
	public void writeTo(Writer writer) throws IOException, ModelRuntimeException {
		writeTo(writer, Syntax.Trix);
	}

	/**
	 * Write the whole repository to a writer. Depending on the Syntax the
	 * context URIs might get lost. TriX should be able to handle contexts.
	 */
	public void writeTo(Writer writer, Syntax syntax) throws IOException,
			ModelRuntimeException {

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
			getConnection().export(sesameWriter);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writes the whole repository in TriX syntax to the output stream.
	 */
	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		writeTo(out, Syntax.Trix);
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
            getConnection().export(sesameWriter);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}
    

	public ClosableIterable<? extends Statement> queryConstruct(String query, String querylanguage) throws QueryLanguageNotSupportedException, ModelRuntimeException {
		try {
			// sesame returns a special result object
			GraphQueryResult graphQueryResult = connection.evaluateGraphQuery(
					ConversionUtil.toOpenRDFQueryLanguage(querylanguage), query);

			return new SesameGraphIterable(graphQueryResult);

		} catch (MalformedQueryException e) {
			throw new ModelRuntimeException(e);
		} catch (UnsupportedQueryLanguageException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public QueryResultTable querySelect(String query, String querylanguage) throws QueryLanguageNotSupportedException, ModelRuntimeException {
		// do all the dirty work inside of the iterator...
		return new SesameQueryResultTable(query, ConversionUtil.toOpenRDFQueryLanguage(querylanguage), connection);
	}

	public void finalize() {
		close();
	}

	public void addStatement(Statement statement) throws ModelRuntimeException {
		org.openrdf.model.Statement openRdfStatement = ConversionUtil.toOpenRDF(statement, this.valueFactory);
		try {
			this.getConnection().add(openRdfStatement);
		} catch (SailException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void removeStatement(Statement statement) throws ModelRuntimeException {
		org.openrdf.model.Statement openRdfStatement = ConversionUtil.toOpenRDF(statement, this.valueFactory);
		try {
			this.getConnection().remove(openRdfStatement);
		} catch (SailException e) {
			throw new ModelRuntimeException(e);
		}
	}

}
