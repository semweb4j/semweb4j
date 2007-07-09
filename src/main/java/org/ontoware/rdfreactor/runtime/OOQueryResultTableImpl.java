package org.ontoware.rdfreactor.runtime;

import java.util.Map;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;

public class OOQueryResultTableImpl implements OOQueryResultTable {

	private Model m;

	private Map<String, Class> returnTypes;

	private QueryResultTable resultTable;

	public OOQueryResultTableImpl(Model m, Map<String, Class> returnTypes, String sparqlSelectQuery)
			throws ModelRuntimeException {

		this.resultTable = m.sparqlSelect(sparqlSelectQuery);

		if (!sparqlSelectQuery.contains("SELECT")) {
			throw new ModelRuntimeException("The given query is not a SELECT query");
		}
		// else
		this.m = m;
		this.returnTypes = returnTypes;
	}

	public Set<String> getVariables() {
		return this.resultTable.getVariables();
	}

	public ClosableIterator<OOQueryRow> iterator() {
		return new SparqlIterator(this, this.resultTable.iterator());
	}

	public Model getModel() {
		return this.m;
	}

	public Class getReturnType(String varname) {
		return returnTypes.get(varname);
	}

}
