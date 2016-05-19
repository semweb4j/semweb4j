/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 * 
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import java.util.Properties;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.AbstractModelFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;


public class RepositoryModelFactory extends AbstractModelFactory {
	
	public Model createModel(Properties properties) throws ModelRuntimeException {
		return new RepositoryModel(createRepository(properties));
	}
	
	public Model createModel(URI contextURI) throws ModelRuntimeException {
		return new RepositoryModel(contextURI, createRepository(null));
	}
	
	public ModelSet createModelSet(Properties properties) throws ModelRuntimeException {
		return new RepositoryModelSet(createRepository(properties));
	}
	
	private static Repository createRepository(Properties properties) throws ModelRuntimeException {
		// find out if we need reasoning
		String reasoningProperty = properties == null ? null : properties.getProperty(REASONING);
		boolean reasoning = Reasoning.rdfs.toString().equalsIgnoreCase(reasoningProperty);
		
		// create a Sail stack
		Sail sail = new MemoryStore();
		
		if(reasoning) {
			sail = new ForwardChainingRDFSInferencer((MemoryStore)sail);
		}
		
		// create a Repository
		Repository repository = new SailRepository(sail);
		try {
			repository.initialize();
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
		
		return repository;
	}
	
	public QueryResultTable sparqlSelect(String endpointURL, String sparqlQuery) {
		HTTPRepository endpoint = new HTTPRepository(endpointURL, "");
		try {
			endpoint.initialize();
			RepositoryConnection connection = endpoint.getConnection();
			
			return new RepositoryQueryResultTable(sparqlQuery, connection);
		} catch(RepositoryException e) {
			throw new ModelRuntimeException(e);
		}
		
	}
}
