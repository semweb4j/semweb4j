package org.ontoware.semversion;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.SparqlUtil;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.runtime.Bridge;
import org.ontoware.semversion.impl.SyntacticDiffEngine;
import org.ontoware.semversion.impl.TripleStore;
import org.ontoware.semversion.impl.UserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of SemVersion.
 * 
 * @author voelkel
 */
/**
 * @author voelkel
 *
 */
public class SemVersion {

	private Logger log = LoggerFactory.getLogger(SemVersion.class);

	/**
	 * SemVersion.SEMVERSION_NS + "#hasBlankNodeID
	 */
	public static final URI BLANK_NODE_ID = new URIImpl(
			SemVersion.SEMVERSION_NS + "#hasBlankNodeID");

	public static final String MAIN_BRANCH = new String("main");

	public static final URI MAIN_MODEL_URI = new URIImpl(
			SemVersion.SEMVERSION_NS + "#mainModel");

	public static final String SEMVERSION_NS = "http://purl.org/net/semversion";

	 public static final URI SEMVERSIONIMPL = new URIImpl(
	 SemVersion.SEMVERSION_NS + "/property#SemVersionImpl");
	
	 public static final URI TRIPLESTORE = new
	 URIImpl(SemVersion.SEMVERSION_NS
	 + "/property#TripleStore");

	/**
	 * Denote the status of a Version is 'valid'
	 */
	public static final String VALID = new String("valid");

	private org.ontoware.rdf2go.model.Model mainModel;

	private boolean startup = false;

	private TripleStore ts;

	public SemVersion() throws Exception {
	}

	private void checkStartup() {
		if (!startup)
			throw new IllegalStateException("Please call startup(..) first");
	}

	
	/**
	 * Delete all data. CAUTION.
	 */
	public void clear() {
		checkStartup();
		this.ts.deleteStore();
	}

	/**
	 * Admin taks: Create a new user account.
	 * 
	 * @param name
	 * @param pass
	 * @return the new User.
	 */
	public User createUser(String name, String pass) {
		checkStartup();
		try {
			ClosableIterator<? extends Statement> it = mainModel
					.findStatements(Variable.ANY,
							org.ontoware.semversion.impl.generated.User.NAME,
							new PlainLiteralImpl(name));
			if (it.hasNext())
				throw new RuntimeException("User " + name + " already exists");
			return new UserImpl(mainModel, mainModel.newRandomUniqueURI(),
					name, pass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * For testing purposes only: Removes all stored statements
	 * 
	 * @throws ModelException
	 */
	public void deleteStore() throws ModelException {
		getTripleStore().deleteStore();
	}

	/**
	 * For debugging purposes. Dumps the whole store to System.out.
	 */
	public void dump() {

		try {
			getTripleStore().dump();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Model getMainModel() {
		checkStartup();
		return this.mainModel;
	}

	/**
	 * A semantic diff is calculated like this: <code>
	 * A' = transitive closure of (A)
	 * B' = transitive closure of (B)
	 * return diff(A',B')
	 * </code>
	 * 
	 * @param model_A
	 * @param model_B
	 * @return a semantic Diff
	 */
	public Diff getSemanticDiff_RDFS(Model model_A, Model model_B) {
		try {
			return SemanticDiffEngine.getSemanticDiff_RDFS(model_A, model_B);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param model_A
	 * @param model_B
	 * @return a Diff between the two models. The diff will e.g. contain as
	 *         added triples those present in B but not in A.
	 */
	public Diff getSyntacticDiff(Model model_A, Model model_B) {
		try {
			return SyntacticDiffEngine.getSyntacticDiff(model_A, model_B);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.semversion.SemVersion#getTripleStore()
	 */
	public TripleStore getTripleStore() {
		checkStartup();
		return this.ts;
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
			org.ontoware.semversion.impl.generated.User user = (org.ontoware.semversion.impl.generated.User) it
					.next();
			assert !it.hasNext() : "there should be only one user with this name + pass";
			return new UserImpl(user);
		} else {
			return null;
		}
	}

	public User getUser(URI uri) {
		checkStartup();
		return new UserImpl(new org.ontoware.semversion.impl.generated.User(
				mainModel, uri, false));
	}

	/**
	 * Log in to SemVersion.
	 * 
	 * @param username
	 * @param password
	 * @return a Session. All further access to SemVersion happens via the
	 *         Session.
	 * @throws LoginException
	 */
	public Session login(String username, String password)
			throws LoginException {
		User user = getUser(username, password);
		assert user == null || user.getName().equals(username);
		if (user == null)
			throw new LoginException("Could not login '" + username
					+ "', check username & password");
		return new Session(user, this);
	}

	/**
	 * Save without shutdown.
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		checkStartup();
		log.info("SemVersion now uses auto-commit. There is no need to save.");
	}

	/**
	 * Persists changes. Changes might be persisted before already.
	 * 
	 * @throws IOException
	 */

	public void shutdown() throws IOException {
		checkStartup();
		mainModel.close();
	}

	/**
	 * Starts up SemVersion and reads the main model into memory. This is a
	 * small model which knows the relations between other models.
	 * 
	 * @param storageDir
	 * @throws IOException
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

	/**
	 * @return a session with the user "anonymous" logged in. This user is
	 *         created if necessary.
	 */
	public Session createAnonymousSession() {
		if (getUser("anonymous", "") == null)
			createUser("anonymous", "");
		try {
			return login("anonymous", "");
		} catch (LoginException e) {
			throw new AssertionError("This cannot happen as we create the user before logging in");
		}
	}

}
