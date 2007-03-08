/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.impl.sesame2;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.openrdf.model.Value;
import org.openrdf.queryresult.Solution;
import org.openrdf.rdf2go.ConversionUtil;

/**
 * Wrapper which takes a sesame2 solution, which is a part of the result for a
 * SPARQL query, and converts it to a rdf2go QueryRow, which has the same
 * function in rdf2go.
 * 
 * 
 * @author Benjamin Heitmann <benjamin.heitmann@deri.org>
 * 
 */
public class SesameQueryRow implements QueryRow {

	private Solution solution;

	public SesameQueryRow(Solution solution) {
		this.solution = solution;
	}

	public Node getValue(String varname) {
		Value value = this.solution.getValue(varname);
		if (value == null) {
			return null;
		} else {
			return (Node) ConversionUtil.toRdf2go(value);
		}
	}

	public String getLiteralValue(String varname) throws ModelRuntimeException {
		Node n = this.getValue(varname);
		if (n instanceof Literal)
			return ((Literal) n).getValue();
		else
			throw new ModelRuntimeException("Node is not a literal");
	}

}
