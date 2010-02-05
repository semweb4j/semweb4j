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

package org.ontoware.rdf2go.model.persistent;


import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelRuntimeException;

/**
 * A persistent entity
 * 
 * @author voelkel
 * 
 */
public interface Persistent {

	/**
	 * persist all unsaved changes
	 * 
	 * @throws ModelRuntimeException, e.g. IOException
	 */
	public void save() throws ModelRuntimeException, IOException;

	/**
	 * load content from storage
	 * 
	 * @throws ModelRuntimeException, e.g. FileNotFoundException,IOException
	 * @throws IOException 
	 */
	public void load() throws ModelRuntimeException, IOException;

}
