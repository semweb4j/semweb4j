package org.ontoware.rdf2go.impl.jena24;

import junit.framework.TestCase;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;

public class TransactionTest extends TestCase {

	private ModelImplJena24 m;

	private URI a = new URIImpl("urn:test:a",false);

	private URI b = new URIImpl("urn:test:b",false);

	protected void setUp() throws Exception {
		super.setUp();
		this.m = new ModelImplJena24(Reasoning.none);
		m.open();
		m.removeAll();
	}

	public void testJenaTransaction() {
		Model m;
		try {
			String className = "org.postgresql.Driver"; // path of driver class
			Class.forName(className); // load driver
			String DB_URL = "jdbc:postgresql://localhost/jena"; // URL of
																// database
																// server
			String DB_USER = "ontoware"; // database user id
			String DB_PASSWD = "ontoware"; // database password
			String DB = "PostgreSQL"; // database type

			// Create database connection
			IDBConnection conn = new DBConnection(DB_URL, DB_USER, DB_PASSWD,
					DB);
			ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
			m = maker.createDefaultModel();
			m.removeAll();

			assertTrue(m.size() == 0);
			m = m.begin();
			m.add(m.createResource(a.toString()), m
					.createProperty(b.toString()), m.createLiteral("Test"));
			m.add(m.createResource(a.toString()), m
					.createProperty(b.toString()), m.createLiteral("Test2"));
			// assertTrue(m.size() == 0);
			m.commit();
			assertTrue(m.size() == 2);

		} catch (ClassNotFoundException cnfe) {

		}
	}
}
