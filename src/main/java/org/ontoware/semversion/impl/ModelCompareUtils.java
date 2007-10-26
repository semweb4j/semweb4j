package org.ontoware.semversion.impl;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;

public class ModelCompareUtils {

	public static boolean equals(Model a, Model b) {
		ClosableIterator<Statement> it = b.iterator();
		org.ontoware.rdf2go.model.Diff diff = a.getDiff(it);
		it.close();
		boolean added = diff.getAdded().iterator().hasNext();
		boolean removed = diff.getRemoved().iterator().hasNext();
		return !added & !removed;
	}

}
