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

/**
 * Defining how and when changes are commit to underlying persistence layer.
 * 
 * @author voelkel
 * 
 */
public interface Commitable {

	/**
	 * Writes all changes to underlying persistence layer - if such a layer is
	 * used by the implementation. Otherwise nothing happens.
	 * 
	 * @throws ModelRuntimeException
	 *             if the commit could not be executed
	 */
	public void commit() throws ModelRuntimeException;

	/**
	 * 
	 * The default value is true.
	 * 
	 * @param autocommit
	 *            If true, all changes are immediately written to the underlying
	 *            persistence layer, if any is used.
	 */
	public void setAutocommit(boolean autocommit);

}
