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
package org.ontoware.rdf2go.model.persistent;

import org.ontoware.rdf2go.model.Model;


/**
 * A Model that can be persisted as a file.
 * 
 * @author voelkel
 * 
 */
public interface PersistentModel extends Persistent, Model {
	
	// TODO (xamde from wth, 11.07.2007) WHY DOES THIS EMPTY CLASS EXIST?  
	//if this is fine, please document why

}
