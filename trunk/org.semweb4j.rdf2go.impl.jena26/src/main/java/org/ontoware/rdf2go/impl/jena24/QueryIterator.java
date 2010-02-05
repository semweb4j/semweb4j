package org.ontoware.rdf2go.impl.jena24;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.impl.QueryRowImpl;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class QueryIterator implements ClosableIterator<QueryRow> {

	private ResultSet resultSet;

	private QueryResultTableImpl table;

	public QueryIterator(QueryResultTableImpl table, ResultSet results) {
		this.resultSet = results;
		this.table = table;
	}

	public boolean hasNext() {
		return this.resultSet.hasNext();
	}

	@SuppressWarnings("null")
	/*
	 * patch from garpinc against nulls -
	 * http://issues.semweb4j.org/browse/RTGO-65
	 */
	public QueryRow next() {
		QuerySolution qs = this.resultSet.nextSolution();
		QueryRowImpl row = new QueryRowImpl();
		for (String v : this.table.getVariables()) {
			RDFNode node = qs.get(v);
			assert node != null : "null node for varname " + v
					+ ". Do you have unbound variables in the query?";
			try {
				row.put(v, TypeConversion.toRDF2Go((node == null ? null : node
						.asNode())));
			} catch (ModelRuntimeException e) {
				throw new ModelRuntimeException(e);
			}
		}
		return row;
	}

	public void remove() {
		this.resultSet.remove();
	}

	public void close() {
	}

}
