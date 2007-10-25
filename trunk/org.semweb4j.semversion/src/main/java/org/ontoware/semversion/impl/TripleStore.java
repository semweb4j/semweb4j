package org.ontoware.semversion.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.DelegatingModel;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.rdf2go.RepositoryModelFactory;
import org.openrdf.rdf2go.RepositoryModelSet;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class determines the storage strategy. Internally an RDF2Go ContextModel
 * is used.
 * 
 * @author $Author: xamde $
 * @version $Id: TripleStore.java,v 1.23 2006/10/16 17:30:25 xamde Exp $
 * 
 */
public class TripleStore {

	private static Logger log = LoggerFactory.getLogger(TripleStore.class);

	/** the real persistent store of SemVersion */
	private ModelSet persistentModelSet;

	public TripleStore(File storageDir) throws IOException {

		// persistent store
		NativeStore ns = new NativeStore(storageDir);
		ns.setParameter("triple-indexes", "spoc, posc, sopc, psoc, ospc, opsc");
		Repository myRepository = new SailRepository(ns);
		try {
			myRepository.initialize();
			persistentModelSet = new RepositoryModelSet(myRepository);
			persistentModelSet.open();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * @return an open RDF2Go model as a view on a certain set of quads in the
	 *         ContextModel
	 */
	public Model getPersistentModel(URI contextURI) {
		if (contextURI == null)
			throw new IllegalArgumentException("null");
		log.debug("Getting model: " + contextURI);
		Model model = persistentModelSet.getModel(contextURI);
		model.open();
		return model;
	}

	/** return an in-memory Model, opened */
	public Model getTempModel(URI uri) {
		// IMPROVE for hardcore scalability use persistent modlsets here
		ModelSet inMemoryModelSet = new RepositoryModelFactory()
				.createModelSet();
		inMemoryModelSet.open();
		Model m = inMemoryModelSet.getModel(uri);
		m.open();
		return new ClosingModel(m,inMemoryModelSet);
	}

	class ClosingModel extends DelegatingModel {
		private ModelSet modelset;

		public ClosingModel(Model model, ModelSet modelset) {
			super(model);
			this.modelset = modelset;
		}

		public void close() {
			super.close();
			modelset.close();
		}
	}

	/**
	 * Adds the contents of an external model to the context model. Expensive
	 * operation, as the model has to be copied.
	 * 
	 * @param m
	 * @return the persistent Model, opened
	 */
	public Model addModelAndPersist(Model m) {
		assert m != null;

		// if m is a persistent model already: copy to memory first
		if (m.getContextURI() != null
				&& persistentModelSet.containsModel(m.getContextURI())) {
			// FIXME

			m.dump();

			Model n = persistentModelSet.getModel(m.getContextURI());
			n.open();
			n.dump();

			ClosableIterator<URI> it = persistentModelSet.getModelURIs();
			while (it.hasNext()) {
				System.out.println("Model URI " + it.next());
			}

			throw new RuntimeException("HAB CIHS MIR DOCHGEDACHT m is "
					+ m.getContextURI());
		} else {
			URI u = persistentModelSet.newRandomUniqueURI();
			Model persistent = getPersistentModel(u);
			ClosableIterator<Statement> it = m.iterator();
			persistent.addAll(it);
			it.close();
			return persistent;
		}

	}

	public void dump() {
		System.out.println("Persistent modeset:");
		persistentModelSet.dump();
	}

	public void deleteStore() {
		persistentModelSet.removeAll();
	}

	/**
	 * Make sure all bnodes have a unique inverse functional property
	 * 
	 * TODO: test this method
	 * 
	 * @param m
	 * @return
	 * @throws Exception
	 */
	public static void bnodeEnrichment(Model m) throws Exception {
		Iterator<Statement> it = m.iterator();
		Map<BlankNode, URI> knownBnodes = new HashMap<BlankNode, URI>();

		DiffImpl diff = new DiffImpl();
		boolean changed = false;
		while (it.hasNext()) {
			Statement s = it.next();
			if (s.getSubject() instanceof BlankNode) {
				changed = true;
				URI u = knownBnodes.get(s.getSubject());
				if (u == null) {
					u = m.newRandomUniqueURI();
					knownBnodes.put((BlankNode) s.getSubject(), u);
				}
				diff.addStatement(s.getSubject(), SemVersionImpl.BLANK_NODE_ID,
						u);
			}
			if (s.getObject() instanceof BlankNode) {
				changed = true;
				URI u = knownBnodes.get(s.getObject());
				if (u == null) {
					u = m.newRandomUniqueURI();
					knownBnodes.put((BlankNode) s.getObject(), u);
				}
				diff.addStatement((Resource) s.getObject(),
						SemVersionImpl.BLANK_NODE_ID, u);
			}
		}
		if (changed) {
			m.addAll(diff.getAdded().iterator());
		}
	}

	public URI newRandomUniqueURI() {
		return this.persistentModelSet.newRandomUniqueURI();
	}

	/**
	 * @return persistent ModelSet
	 */
	ModelSet getModelSet() {
		return this.persistentModelSet;
	}

	/**
	 * return a non-persistent copy of the model stored at the given URI.
	 * Returned model is open
	 */
	public Model getAsTempCopy(URI modelURI) {
		Model p = getPersistentModel(modelURI);
		Model copy = getTempModel(newRandomUniqueURI());
		ClosableIterator<Statement> it = p.iterator();
		copy.addAll(it);
		it.close();
		return copy;
	}

	public void finalize() {
		persistentModelSet.close();
	}

}
