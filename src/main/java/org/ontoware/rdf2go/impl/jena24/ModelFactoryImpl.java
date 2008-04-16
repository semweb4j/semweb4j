package org.ontoware.rdf2go.impl.jena24;

import java.util.Properties;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.AbstractModelFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.URI;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelMaker;

public class ModelFactoryImpl extends AbstractModelFactory implements
		ModelFactory {

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

	public Model createModel(Properties p) throws ModelRuntimeException {
		com.hp.hpl.jena.rdf.model.Model model;

		String backend = p.getProperty(BACKEND);

		// default to in-memory model
		if (backend == null)
			backend = MEMORY;

		Reasoning reasoning = AbstractModelFactory.getReasoning(p);

		if (backend == null || backend.equalsIgnoreCase(MEMORY)) {
			model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
			assert model != null;
		} else if (backend.equalsIgnoreCase(DATABASE)) {
			String sql_drivername = p.getProperty(SQL_DRIVERNAME);
			String db_type = p.getProperty(DB_TYPE);
			String connect_string = p.getProperty(DB_CONNECT_STRING);
			String user = p.getProperty(DB_USER);
			String password = p.getProperty(DB_PASSWD);

			if (sql_drivername == null)
				throw new RuntimeException(
						"Please specify a sql_drivername in your property file!");
			if (db_type == null)
				throw new RuntimeException(
						"Please specify a db_type in your property file!");
			if (connect_string == null)
				throw new RuntimeException(
						"Please specify a connect_string in your property file!");
			if (user == null)
				throw new RuntimeException(
						"Please specify a db_user in your property file!");
			if (password == null)
				throw new RuntimeException(
						"Please specify a db_passwd in your property file!");

			ModelMaker maker;
			try {
				maker = getDBModelMaker(sql_drivername, connect_string, user,
						password, db_type);
			} catch (Exception e) {
				throw new ModelRuntimeException(e);
			}

			model = maker.createDefaultModel();

		} else if (backend.equalsIgnoreCase(FILE)) {
			String filename = p.getProperty(FILENAME);
			if (filename == null) {
				throw new RuntimeException(
						"Please specify a filename in your property file!");
			}
			ModelMaker maker = getFileModelMaker(filename);
			model = maker.createDefaultModel();
		} else
			throw new IllegalArgumentException("Illegal back-end type: "
					+ backend);

		// reasoning

		switch (reasoning) {
		case rdfsAndOwl:
		case owl:
			com.hp.hpl.jena.rdf.model.Model owlModel = (com.hp.hpl.jena.rdf.model.Model) com.hp.hpl.jena.rdf.model.ModelFactory
					.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF,
							model);
			return new ModelImplJena24(owlModel);
		case rdfs:
			com.hp.hpl.jena.rdf.model.Model rdfsModel = (com.hp.hpl.jena.rdf.model.Model) com.hp.hpl.jena.rdf.model.ModelFactory
					.createRDFSModel(model);
			return new ModelImplJena24(rdfsModel);
		default:
			return new ModelImplJena24(model);
		}

	}

	public Model createModel(URI contextURI) throws ModelRuntimeException {
		com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory
				.createDefaultModel();
		return new ModelImplJena24(contextURI, model);
	}

	private ModelMaker getDBModelMaker(String SQLDriver,
			String DB_connectString, String user, String password,
			String db_type) throws Exception {
		Class.forName(SQLDriver);
		IDBConnection conn = new DBConnection(DB_connectString, user, password,
				db_type);
		return com.hp.hpl.jena.rdf.model.ModelFactory.createModelRDBMaker(conn);
	}

	private ModelMaker getFileModelMaker(String filename) {
		return com.hp.hpl.jena.rdf.model.ModelFactory
				.createFileModelMaker(filename);
	}

	public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
		// TODO not available in Jena, TODO: implement using NG4J
		throw new UnsupportedOperationException(
				"not available in Jena, TODO: implement using NG4J");
	}

}
