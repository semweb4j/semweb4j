package org.ontoware.semversion;

import java.io.File;
import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.semversion.impl.SemVersionImpl;
import org.ontoware.semversion.impl.SyntacticDiffEngine;

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
	 * reads main model into memory
	 * 
	 * @throws IOException
	 */
	public void startup(File storageDir) throws IOException {
		svi.startup(storageDir);
	}

	/**
	 * Writes back all changes
	 * 
	 * @throws IOException
	 */
	public void shutdown() throws IOException {
		svi.shutdown();
	}

	/**
	 * Save without shutdown
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		svi.save();
	}

	public Session login(String username, String password)
			throws LoginException {
		User user = svi.getUser(username, password);
		assert user == null || user.getName().equals(username);
		if (user == null)
			throw new LoginException(
					"Could not login '"+username+"', check username & password");
		return new Session(user, svi);
	}

	// //////////////////////// // public API

	/**
	 * returns the new User.
	 */
	public User createUser(String name, String pass) {
		return svi.createUser(name, pass);
	}

	public SemVersionImpl getSemVersionImpl() {
		return this.svi;
	}

	public Diff getSyntacticDiff(Model model1, Model model2) {
		try {
			return SyntacticDiffEngine.getSyntacticDiff(model1, model2);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * IMPROVE: offer for OWL as well
	 * 
	 * @param model1
	 * @param model2
	 * @return
	 */
	public Diff getSemanticDiff_RDFS(Model model1, Model model2) {
		try {
			return SemanticDiffEngine.getSemanticDiff_RDFS(model1, model2);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * for debugging purposes, should be removed later dumps the whole store
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
