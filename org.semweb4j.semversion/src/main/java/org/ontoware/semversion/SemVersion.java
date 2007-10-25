package org.ontoware.semversion;

import java.io.File;
import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.semversion.impl.SemVersionImpl;
import org.ontoware.semversion.impl.SyntacticDiffEngine;

/**
 * Main class of SemVersion.
 * @author voelkel
 */
public class SemVersion {

	// TODO fixme to PURL
	public static final String SEMVERSION_NS = "http://semversion.ontoware.org/ns/2005/semversion";

	/**
	 * Denote the status of a Version is 'valid'
	 */
	public static final String VALID = new String("valid");

	public static final String MAIN_BRANCH = new String("main");

	protected SemVersionImpl svi;

	public SemVersion() throws Exception {
		svi = new SemVersionImpl();
	}

	/**
	 * Starts up SemVersion and reads the main model into memory. This is a
	 * small model which knows the relations between other models.
	 * 
	 * @param storageDir
	 * @throws IOException
	 */
	public void startup(File storageDir) throws IOException {
		svi.startup(storageDir);
	}

	/**
	 * Persists changes. Changes might be persisted before already.
	 * 
	 * @throws IOException
	 */
	public void shutdown() throws IOException {
		svi.shutdown();
	}

	/**
	 * Save without shutdown.
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		svi.save();
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
		User user = svi.getUser(username, password);
		assert user == null || user.getName().equals(username);
		if (user == null)
			throw new LoginException("Could not login '" + username
					+ "', check username & password");
		return new Session(user, svi);
	}

	// //////////////////////// // public API

	/**
	 * Admin taks: Create a new user account.
	 * 
	 * @param name
	 * @param pass
	 * @return the new User.
	 */
	public User createUser(String name, String pass) {
		return svi.createUser(name, pass);
	}

	/**
	 * @return the internal back-end implementation.
	 */
	public SemVersionImpl getSemVersionImpl() {
		return this.svi;
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

	// TODO IMPROVE: offer for OWL as well

	/**
	 * A semantic diff is calculated like this:
	 * <code>
	 * A' = transitive closure of (A)
	 * B' = transitive closure of (B)
	 * return diff(A',B')
	 * </code>
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
	 * For debugging purposes. Dumps the whole store to System.out.
	 */
	public void dump() {

		try {
			getSemVersionImpl().getTripleStore().dump();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * For testing purposes only: Removes all stored statements
	 * 
	 * @throws ModelException
	 */
	public void deleteStore() throws ModelException {
		svi.getTripleStore().deleteStore();
	}

}
