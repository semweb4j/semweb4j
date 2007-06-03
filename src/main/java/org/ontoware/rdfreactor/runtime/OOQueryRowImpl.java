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
		return BridgeBase.uriBlank2reactor(ooQueryResultTable.getModel(), row.getValue(varname),
				ooQueryResultTable.getReturnType(varname));
	}

}
