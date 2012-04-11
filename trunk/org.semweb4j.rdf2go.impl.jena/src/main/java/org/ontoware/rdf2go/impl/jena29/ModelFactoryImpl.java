package org.ontoware.rdf2go.impl.jena29;

import java.util.Properties;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.AbstractModelFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.node.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;


public class ModelFactoryImpl extends AbstractModelFactory implements ModelFactory {
	
	protected static final Logger log = LoggerFactory.getLogger(ModelFactoryImpl.class);
	
	public static final String BACKEND = "back-end";
	
	public static final String MEMORY = "memory";
	
	public static final String DATABASE = "database";
	
	public static final String SQL_DRIVERNAME = "sql_drivername";
	
	public static final String DB_TYPE = "db_type";
	
	public static final String DB_CONNECT_STRING = "db_connect_string";
	
	public static final String DB_USER = "db_user";
	
	public static final String DB_PASSWD = "db_passwd";
	
	public static final String FILE = "file";
	
	public static final String FILENAME = "filename";
	
	@Override
	public Model createModel(Properties p) throws ModelRuntimeException {
		com.hp.hpl.jena.rdf.model.Model model;
		
		String backend = p.getProperty(BACKEND);
		
		// default to in-memory model
		if(backend == null)
			backend = MEMORY;
		
		Reasoning reasoning = AbstractModelFactory.getReasoning(p);
		
		if(backend.equalsIgnoreCase(MEMORY)) {
			model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
			assert model != null;
		} else if(backend.equalsIgnoreCase(DATABASE)) {
			throw new ModelRuntimeException(
			        "This release of RDF2Go no longer supports Jena database backends. Use RDF2Go-Jena 2.6 or wait until we support SDB or TDB.");
			// gone: return ModelFactoryImpl_RDB.createModel(p);
		} else if(backend.equalsIgnoreCase(FILE)) {
			String filename = p.getProperty(FILENAME);
			if(filename == null) {
				throw new RuntimeException("Please specify a filename in your property file!");
			}
			ModelMaker maker = getFileModelMaker(filename);
			model = maker.createDefaultModel();
		} else
			throw new IllegalArgumentException("Illegal back-end type: " + backend);
		
		// reasoning
		
		switch(reasoning) {
		case rdfsAndOwl:
		case owl:
			com.hp.hpl.jena.rdf.model.Model owlModel = com.hp.hpl.jena.rdf.model.ModelFactory
			        .createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, model);
			return new ModelImplJena26(owlModel);
		case rdfs:
			com.hp.hpl.jena.rdf.model.Model rdfsModel = com.hp.hpl.jena.rdf.model.ModelFactory
			        .createRDFSModel(model);
			return new ModelImplJena26(rdfsModel);
		default:
			return new ModelImplJena26(model);
		}
		
	}
	
	@Override
	public Model createModel(URI contextURI) throws ModelRuntimeException {
		com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory
		        .createDefaultModel();
		return new ModelImplJena26(contextURI, model);
	}
	
	private static ModelMaker getFileModelMaker(String filename) {
		return com.hp.hpl.jena.rdf.model.ModelFactory.createFileModelMaker(filename);
	}
	
	@Override
	public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
		return new ModelSetImplJena29();
	}
	
	@Override
	public QueryResultTable sparqlSelect(String url, String queryString) {
		log.debug("Query " + queryString);
		QueryExecution qe = QueryExecutionFactory.sparqlService(url, queryString);
		return new QueryResultTableImpl(qe);
	}
	
}
