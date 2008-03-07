package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

/**
 * Wrapper class to iterate over an array of elements returned from a SPARQL
 * Query.
 * 
 * @author mvo
 */
class SparqlIterator implements ClosableIterator<OOQueryRow> {

	private OOQueryResultTableImpl ooQueryResultTable;
	private ClosableIterator<QueryRow> rdf2goIterator;

	/**
	 * Constructor for the wrapper class over the results of a SPARQL Query.
	 */
	public SparqlIterator(OOQueryResultTableImpl impl, ClosableIterator<QueryRow> it) {
		this.ooQueryResultTable = impl;
		this.rdf2goIterator = it;
	}

	public boolean hasNext() {
		return rdf2goIterator.hasNext();
	}


	public OOQueryRow next() {
		if (hasNext()) {
			QueryRow rdf2go = rdf2goIterator.next();
			OOQueryRow ooRow = new OOQueryRowImpl( ooQueryResultTable, rdf2go );
			return ooRow;
		}
		else {
			rdf2goIterator.close();
			rdf2goIterator = null;
			return null;
		}
	}

	public void remove() {
		rdf2goIterator.remove();
	}

	public void close() {
			rdf2goIterator.close();
	}

}
