/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2010
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;


public class QueryRowImpl extends HashMap<String,Node> implements QueryRow {
	
	private static final long serialVersionUID = 1496910590032007736L;
	
	@Override
    public Node getValue(String varname) {
		return super.get(varname);
	}
	
	@Override
    public String getLiteralValue(String varname) throws ModelRuntimeException {
		Node n = super.get(varname);
		if(n instanceof Literal)
			return ((Literal)n).getValue();
		// else
		throw new ModelRuntimeException("Node is not a literal");
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for(Map.Entry<String,Node> entry : this.entrySet()) {
			buf.append(entry.getKey()).append(":").append(entry.getValue());
			buf.append(", ");
		}
		
		return buf.toString();
	}
	
}
