package org.ontoware.rdf2go.impl.sesame2;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.openrdf.queryresult.Solution;
import org.openrdf.queryresult.TupleQueryResult;

/**
 * Iterator over sesame2 query results, which converts them to rdf2go query rows on demand. 
 * 
 * This should close itself if necessary. 
 * 
 *
 * @author Benjamin Heitmann <benjamin.heitmann@deri.org>
 *
 */
public class SesameQueryIterator implements ClosableIterator<QueryRow> {

	private TupleQueryResult queryResult;

	private Iterator<Solution> iteratorSolutions;

	private boolean closed = false;

	public SesameQueryIterator(TupleQueryResult queryResult) {
		this.queryResult = queryResult;
		this.iteratorSolutions = queryResult.iterator();
	}

	public boolean hasNext() {

		if (closed)
			return false;

		boolean b = this.iteratorSolutions.hasNext();
		if (!b) {
			this.close();
		}
		return b;

	}

	public QueryRow next() {
		
		Solution nextSolution = this.iteratorSolutions.next();
		
		if (!(this.iteratorSolutions.hasNext())) {
			this.close();
		}
		
		return new SesameQueryRow(nextSolution);
	}

	public void remove() {
		this.iteratorSolutions.remove();
	}

	public void close() {
		this.queryResult.close();
		closed = true;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (!closed)
			close();
	} 

}
