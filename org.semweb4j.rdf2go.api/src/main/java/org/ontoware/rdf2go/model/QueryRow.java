/**
 * LICENSE INFORMATION
 *
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2010
 *
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.Node;

/**
 * @author voelkel
 */
public interface QueryRow {

	/**
	 * @param varname without leading questionmark. E.g. use "x" not "?x".
	 * @return the binding for the variable with the name varname
	 */
	public Node getValue(String varname);

	/**
	 * Convenience for <code>
	 * getValue("x").asString()
	 * </code>
	 */
	public String getLiteralValue(String varname) throws ModelRuntimeException;

}
