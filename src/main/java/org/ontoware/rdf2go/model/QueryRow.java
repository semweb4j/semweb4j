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
package org.ontoware.rdf2go.model;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.Node;

/**
 * @author voelkel
 */
public interface QueryRow {

	public Node getValue(String varname);

	/**
	 * Convenience for <code>
	 * getValue("x").asString()
	 * </code>
	 */
	public String getLiteralValue(String varname) throws ModelRuntimeException;

}
