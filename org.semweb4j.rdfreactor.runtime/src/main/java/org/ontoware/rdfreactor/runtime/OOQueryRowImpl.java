package org.ontoware.rdfreactor.runtime;

import org.ontoware.rdf2go.model.QueryRow;

public class OOQueryRowImpl implements OOQueryRow {

	private QueryRow row;

	private OOQueryResultTableImpl ooQueryResultTable;

	public OOQueryRowImpl(QueryRow rdf2go) {
		this.row = rdf2go;
	}

	public OOQueryRowImpl(OOQueryResultTableImpl ooQueryResultTable, QueryRow rdf2go) {
		this.row = rdf2go;
		this.ooQueryResultTable = ooQueryResultTable;
	}

	public Object getValue(String varname) {
		return RDFReactorRuntime.node2javatype(this.ooQueryResultTable.getModel(), this.row.getValue(varname),
				this.ooQueryResultTable.getReturnType(varname));
	}

}
