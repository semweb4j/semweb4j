/*
 * Created on 06.09.2005
 *
 */
package org.ontoware.semversion;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.runtime.RDFDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Session represents a User logged in into SemVersion. Al operations are done
 * via a session, so the author (User) of an operation can be tracked.
 * 
 * @author voelkel
 * 
 */
public class Session {

	private static final Logger log = LoggerFactory.getLogger(Session.class);

	private final User user;

	private final SemVersion semVersion;

	private boolean alive = true;

	public Session(User user, SemVersion semVersion) {
		this.user = user;
		this.semVersion = semVersion;
	}

	private void checkAlive() {
		if (!alive) {
			throw new IllegalStateException("session is no longer alive");
		}
	}

	/**
	 * Closes the current session.
	 */
	public void close() {
		alive = false;
	}

	/**
	 * 
	 * @param label
	 * @return a new VersionedModel or null if a VersionedModel with this name
	 *         already exists
	 */
	public VersionedModel createVersionedModel(String label) {
		checkAlive();
		if (getVersionedModel(label) != null) {
			return null;
		}

		return createVersionedModel(semVersion.getMainModel()
				.newRandomUniqueURI(), label);
	}

	/**
	 * @param uri
	 * @param label
	 * @return a new VersionedModel with the given URI and given label. Returns
	 *         null if the label has been used for another VersionedModel
	 *         already.
	 */
	public VersionedModel createVersionedModel(URI uri, String label) {
		checkAlive();
		if (getVersionedModel(label) != null) {
			return null;
		}
		try {
			VersionedModel vmi = new VersionedModel(semVersion.getMainModel(),
					this, uri, true);

			vmi.setLabel(label);
			if (user != null)
				vmi.setUser(user);

			vmi.setCreationTime(Calendar.getInstance());
			return vmi;
		} catch (RDFDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return a new empty, model with a random URI. This can later be commited
	 *         as a Version.
	 */
	public Model getModel() {
		checkAlive();
		URI rnd = semVersion.getMainModel().newRandomUniqueURI();
		log.debug("get model for " + rnd);
		return semVersion.getTripleStore().getTempModel(rnd);
	}

	/**
	 * @return the SemVersion implementation.
	 */
	public SemVersion getSemVersion() {
		checkAlive();
		return semVersion;
	}

	/**
	 * @return the User holding this Session.
	 */
	public User getUser() {
		checkAlive();
		return user;
	}

	/**
	 * @param string
	 * @return the VersionedModel with the specified Label or null if no
	 *         VersionedModel with that label exists.
	 */
	public VersionedModel getVersionedModel(String label) {
		checkAlive();
		try {
			ClosableIterator<? extends Statement> iter = getSemVersion()
					.getMainModel().findStatements(Variable.ANY, RDFS.label,
							new PlainLiteralImpl(label));
			VersionedModel versionedModel = null;
			if (iter.hasNext()) {
				Statement s = iter.next();
				versionedModel = new VersionedModel(getSemVersion()
						.getMainModel(), this,
				(URI) s.getSubject(), false);
				if (iter.hasNext()) {
					log.warn("Multiple versioned models have the same label");
				}
			}
			iter.close();
			return versionedModel;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param uri
	 * @return the VersionedModel identified by the given URI or null if no such
	 *         VersionedModel exists.
	 */
	public VersionedModel getVersionedModel(URI uri) {
		checkAlive();
		return new VersionedModel(getSemVersion()
				.getMainModel(), this, uri, false);
	}

	/**
	 * @return all VersionedModel in the store
	 */
	public VersionedModel[] getVersionedModels() {
		checkAlive();
		Set<VersionedModel> vmis = new HashSet<VersionedModel>();

		try {
			ClosableIterator<? extends Statement> iter = getSemVersion()
					.getMainModel()
					.findStatements(
							Variable.ANY,
							RDF.type,
							org.ontoware.semversion.impl.generated.VersionedModel.RDFS_CLASS);
			while (iter.hasNext()) {
				Statement s = iter.next();
				vmis.add(new VersionedModel(getSemVersion().getMainModel(),this, (URI) s.getSubject(),
						false));
			}
			iter.close();
			return vmis.toArray(new VersionedModel[0]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
