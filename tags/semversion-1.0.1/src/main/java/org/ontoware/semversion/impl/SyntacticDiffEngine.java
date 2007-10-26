/*
 * Created on 13-Jul-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ontoware.semversion.impl;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.impl.ModelAddRemoveMemoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mvo,careng
 * 
 */
public class SyntacticDiffEngine {

	private static final Logger log = LoggerFactory
			.getLogger(SyntacticDiffEngine.class);

	public static Diff getSyntacticDiff(Model a, Model b) throws Exception {

		// in b, but not in a
		ModelAddRemoveMemoryImpl added = new ModelAddRemoveMemoryImpl();
		// in a, but not in b
		ModelAddRemoveMemoryImpl removed = new ModelAddRemoveMemoryImpl();

		Iterator<Statement> it;
		it = a.iterator();
		while (it.hasNext()) {
			Statement s = it.next();
			log.debug("removed: " + s);
			if (!b.contains(s.getSubject(), s.getPredicate(), s.getObject()))
				removed.addStatement(s);
		}

		it = b.iterator();
		while (it.hasNext()) {
			Statement s = it.next();
			log.debug("added: " + s);
			if (!a.contains(s.getSubject(), s.getPredicate(), s.getObject()))
				added.addStatement(s);
		}
		return new DiffImpl(added.iterator(), removed.iterator());
	}

	/**
	 * Applies a diff
	 * 
	 * @param ts
	 *            a TripleStore
	 * @param sourceModel
	 *            the source model
	 * @param diff
	 *            contains added and removed triplesets
	 * @param resultURI
	 *            under which a model with source + added - removed is created
	 * @returns a temporary model with the diff applied
	 * @throws Exception
	 *             TODO: uses blank node enrichment here
	 */
	public static Model applyDiff(TripleStore ts, Model sourceModel, Diff diff) {
		try {
			Model result = ts.getTempModel(ts.newRandomUniqueURI());

			ClosableIterator<Statement> sourceIt = sourceModel.iterator();
			result.addAll(sourceIt);
			sourceIt.close();
			
			Iterator<? extends Statement> it;
			it = diff.getRemoved().iterator();
			while (it.hasNext()) {
				Statement s = it.next();
				log.debug("remove "+s);
				result.removeStatement(s);
			}

			it = diff.getAdded().iterator();
			while (it.hasNext()) {
				Statement s = it.next();
				log.debug("add "+s);
				result.addStatement(s);
			}
			sourceModel.close();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
