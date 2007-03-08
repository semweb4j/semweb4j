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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.openrdf.querylanguage.MalformedQueryException;
import org.openrdf.querylanguage.UnsupportedQueryLanguageException;
import org.openrdf.querymodel.QueryLanguage;
import org.openrdf.queryresult.TupleQueryResult;
import org.openrdf.repository.Connection;


/**
 * Table of results for a SPARQL query. 
 * 
 * You can get the names of the variables in the query with getVariables, 
 * then take the .iterator() and for each QueryRow in that, you can ask for the 
 * Values associated with a variable in that specific QueryRow.
 * 
 *
 * @author Benjamin Heitmann <benjamin.heitmann@deri.org>
 *
 */
public class SesameQueryResultTable implements QueryResultTable {

	/** holds an ordered list of variables in the query */
	private List<String> varnames;
	private Connection sesameConnection;
	private TupleQueryResult queryResult;
	private QueryLanguage queryLanguage;

	public SesameQueryResultTable(String queryString, Connection sesameConnection) throws ModelRuntimeException {
		this(queryString, QueryLanguage.SPARQL, sesameConnection);
	}

	public SesameQueryResultTable(String queryString, QueryLanguage language, Connection sesameConnection) {
		if (queryString == null) throw new IllegalArgumentException("querystring was null");
		assert sesameConnection != null;
		this.queryLanguage = language;
		this.sesameConnection = sesameConnection;
		
		try {			
			this.queryResult = this.sesameConnection.evaluateTupleQuery(queryLanguage, queryString);
		} catch (MalformedQueryException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedQueryLanguageException e) {
			throw new RuntimeException(e);
		}		
		varnames = queryResult.getBindingNames();
	}

	public Set<String> getVariables() {
		return new HashSet<String>(varnames);
	}

	public ClosableIterator<QueryRow> iterator() {
		return new SesameQueryIterator(queryResult);	
	}

}
