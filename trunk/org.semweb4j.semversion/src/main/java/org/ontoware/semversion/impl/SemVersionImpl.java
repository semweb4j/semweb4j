package org.ontoware.semversion.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.SparqlUtil;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.runtime.Bridge;
import org.ontoware.semversion.SemVersion;
import org.ontoware.semversion.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemVersionImpl {

	private static final Logger log = LoggerFactory
			.getLogger(SemVersionImpl.class);

	private TripleStore ts;

	private org.ontoware.rdf2go.model.Model mainModel;

	private boolean startup = false;

	public static final URI TRIPLESTORE = new URIImpl(SemVersion.SEMVERSION_NS
			+ "/property#TripleStore");

	public static final URI SEMVERSIONIMPL = new URIImpl(
			SemVersion.SEMVERSION_NS + "/property#SemVersionImpl");

	public static final URI MAIN_MODEL_URI = new URIImpl(
			SemVersion.SEMVERSION_NS + "#mainModel");

	public static final URI BLANK_NODE_ID = new URIImpl(
			SemVersion.SEMVERSION_NS + "#hasBlankNodeID");

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.semversion.SemVersion#getTripleStore()
	 */
	public TripleStore getTripleStore() {
		checkStartup();
		return this.ts;
	}
	
	private void checkStartup() {
		if (!startup)
			throw new IllegalStateException("Please call startup(..) first");
	}

	/**
	 * reads main model into memory
	 * 
	 * @throws Exception
	 * 
	 */
	public void startup(File storageDir) throws IOException {
		// look for property file
		// File propFile =
		// // ResourceUtils.findFileAsResource( ..., true);
		// new File("./etc/semversion.properties");
		// Properties props = new Properties();
		// props.load(new FileInputStream(propFile));
		//
		// // use properties
		// String storageDirName = props.getProperty("StorageDir");
		// log.debug("storage dir = '" + storageDirName + "'");
		// if (storageDirName == null)
		// storageDirName = "./storage";
		// this.storageDir = new File(storageDirName);
		if (!storageDir.exists()) {
			log.warn("Not found, creating " + storageDir);
			storageDir.mkdirs();
		}
		assert storageDir.exists() : storageDir.getAbsoluteFile()
				+ " not found";

		// startup persistence layer
		ts = new TripleStore(storageDir);

		// load main model from store
		mainModel = ts.getPersistentModel(MAIN_MODEL_URI);
		// configure main model
		mainModel.setProperty(SEMVERSIONIMPL, this);
		mainModel.setProperty(TRIPLESTORE, ts);
		startup = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.semversion.SemVersion#shutdown()
	 */
	public void shutdown() throws IOException {
		checkStartup();
		mainModel.close();
	}

	public void save() throws IOException {
		checkStartup();
		log.info("SemVersion now uses auto-commit. There is no need to save.");
	}

	public UserImpl createUser(String name, String pass) {
		checkStartup();
		try {
			ClosableIterator<? extends Statement> it = mainModel
					.findStatements(Variable.ANY,
							org.ontoware.semversion.impl.generated.User.NAME,
							new PlainLiteralImpl(name));
			if (it.hasNext())
				throw new RuntimeException("User " + name + " already exists");
			return new UserImpl(mainModel, mainModel.newRandomUniqueURI(), name,
					pass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public User getUser(URI uri) {
		checkStartup();
		return new UserImpl(new org.ontoware.semversion.impl.generated.User(
				mainModel, uri, false));
	}

	public User getUser(String name, String pass) {
		checkStartup();
		Iterator<? extends Object> it = Bridge
				.getSparqlSelectSingleVariable(
						mainModel,
						org.ontoware.semversion.impl.generated.User.class,
						"SELECT ?x WHERE {"
								+ // .
								"?x <"
								+ RDF.type
								+ "> <"
								+ org.ontoware.semversion.impl.generated.User.RDFS_CLASS
								+ "> ."
								+ // .
								"?x <"
								+ org.ontoware.semversion.impl.generated.User.NAME
								+ "> "
								+ SparqlUtil.toSparqlLiteral(name)
								+ " ."
								+
								// .
								"?x <"
								+ org.ontoware.semversion.impl.generated.User.PASSWORD
								+ "> " + SparqlUtil.toSparqlLiteral(pass)
								+ " ." + // .
								"}");

		if (it.hasNext()) {
			org.ontoware.semversion.impl.generated.User user = (org.ontoware.semversion.impl.generated.User) it.next();
			assert ! it.hasNext() : "there should be only one user with this name + pass";
			return new UserImpl(user);
		} else {
			return null;
		}
	}

	public Model getMainModel() {
		checkStartup();
		return this.mainModel;
	}

	public void clear() {
		checkStartup();
		this.ts.deleteStore();
	}

}
