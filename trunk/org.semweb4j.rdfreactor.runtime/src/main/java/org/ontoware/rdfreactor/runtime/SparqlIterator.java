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
		return this.rdf2goIterator.hasNext();
	}


	public OOQueryRow next() {
		if (hasNext()) {
			QueryRow rdf2go = this.rdf2goIterator.next();
			OOQueryRow ooRow = new OOQueryRowImpl( this.ooQueryResultTable, rdf2go );
			return ooRow;
		}
		else {
			this.rdf2goIterator.close();
			this.rdf2goIterator = null;
			return null;
		}
	}

	public void remove() {
		this.rdf2goIterator.remove();
	}

	public void close() {
			this.rdf2goIterator.close();
	}

}
