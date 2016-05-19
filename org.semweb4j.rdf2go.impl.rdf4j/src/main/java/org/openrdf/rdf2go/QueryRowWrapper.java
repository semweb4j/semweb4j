/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

/**
 * Wrapper that takes an OpenRDF BindingSet and converts it to a RDF2Go
 * QueryRow.
 */
public class QueryRowWrapper implements QueryRow {

	private BindingSet solution;

	public QueryRowWrapper(BindingSet solution) {
		this.solution = solution;
	}

	public Node getValue(String varName) {
		Value value = this.solution.getValue(varName);
		return ConversionUtil.toRdf2go(value);
	}

	public String getLiteralValue(String varName)
		throws ModelRuntimeException
	{
		Node node = getValue(varName);
		if (node instanceof Literal) {
			return ((Literal)node).getValue();
		}
		else {
			throw new ModelRuntimeException("Node is not a literal: " + node);
		}
	}
}
