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

package org.ontoware.rdf2go.model;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;


/**
 * Interface for SPARQL and other query languages.
 * 
 * @author voelkel
 * 
 */
public interface Sparqlable {
	
	/**
	 * @return results for other construct-like queries, as supported by
	 *         underlying implementation.
	 * 
	 *         Iterator is auto-close, i.e. when last element is fetched, the
	 *         implementation must call close().
	 * @param query
	 * @param querylanguage
	 * @throws ModelRuntimeException if the execution throws an exception
	 * @throws QueryLanguageNotSupportedException if the adapter can't
	 *             understand the given query language
	 * @throws MalformedQueryException if the query is not a valid construct
	 *             query in the given query language
	 */
	ClosableIterable<Statement> queryConstruct(String query, String querylanguage)
	        throws QueryLanguageNotSupportedException, MalformedQueryException,
	        ModelRuntimeException;
	
	/**
	 * returns results for queries in other query languages as a
	 * QueryResultTable as supported by underlying implementation. For some
	 * implementations (Sesame) it is urgently necessary to iterate over all
	 * statements of the returned iterator in order to close resources properly.
	 * 
	 * Iterator must be auto-close, i.e. when last element is fetched, the
	 * implementation must call close().
	 * 
	 * @param queryString The select query string
	 * @return a QueryResultTable
	 * @throws ModelRuntimeException if the execution throws an exception
	 * @throws QueryLanguageNotSupportedException if the given query langauge is
	 *             not supported
	 * @throws MalformedQueryException if the query is not a valid query in the
	 *             given query language
	 */
	QueryResultTable querySelect(String query, String querylanguage)
	        throws QueryLanguageNotSupportedException, MalformedQueryException,
	        ModelRuntimeException;
	
	/**
	 * SPARQL ask queries
	 * 
	 * @param query a SPARQL AKS query
	 * @return the query result as true or false
	 * @throws ModelRuntimeException if the execution throws an exception
	 * @throws MalformedQueryException if the query is not a valid SPARQL ASK
	 *             query
	 */
	boolean sparqlAsk(String query) throws ModelRuntimeException, MalformedQueryException;
	
	/**
	 * @return results for SPARQL Construct queries, as supported by underlying
	 *         implementation.
	 * 
	 *         Iterator is auto-close, i.e. when last element is fetched, the
	 *         implementation must call close().
	 * 
	 * @throws ModelRuntimeException if the execution throws an exception
	 * @throws MalformedQueryException if the query is not a valid SPARQL
	 *             CONSTRUCT query
	 */
	ClosableIterable<Statement> sparqlConstruct(String query) throws ModelRuntimeException,
	        MalformedQueryException;
	
	/**
	 * Iterator must be auto-close, i.e. when last element is fetched, the
	 * implementation must call close(). This means a regular user can call this
	 * method without calling close(), if all elements are consumed.
	 * 
	 * @param query
	 * @return a ClosableIterable over all statements returned by this query
	 * @throws ModelRuntimeException if the execution throws an exception
	 * @throws MalformedQueryException if the query is not a valid SPARQL
	 *             DESCRIBE query
	 */
	ClosableIterable<Statement> sparqlDescribe(String query) throws ModelRuntimeException;
	
	/**
	 * returns results for SPARQL Select queries, as supported by underlying
	 * implementation. For some implementations (Sesame) it is urgently
	 * necessary to iterate over all statements of the returned iterator in
	 * order to close resources properly.
	 * 
	 * Iterator must be auto-close, i.e. when last element is fetched, the
	 * implementation must call close().
	 * 
	 * @param queryString The SPARQL select query string
	 * @return a QueryResultTable
	 * @throws ModelRuntimeException if an error happens when executing the
	 *             query
	 * @throws MalformedQueryException if the query is not a valid SPARQL SELECT
	 *             query
	 */
	QueryResultTable sparqlSelect(String queryString) throws MalformedQueryException,
	        ModelRuntimeException;
	
	// TODO proposed on 2011-03-13 on SemanticOverflow
	
	// /**
	// * @param queryString
	// * @return the answer of the underlying store in the default format. This
	// is
	// * often RDF/XML.
	// * @throws MalformedQueryException if the query could not be parsed
	// * @throws ModelRuntimeException if an error happens when executing the
	// * query
	// */
	// String sparql(String queryString) throws MalformedQueryException,
	// ModelRuntimeException;
	
}
