package org.ontoware.rdfreactor.runtime;

import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterable;

public interface OOQueryResultTable extends ClosableIterable<OOQueryRow> {

	/**
	 * Return the set of all variable names useed in the query.
	 * 
	 * @return
	 */
	List<String> getVariables();

}