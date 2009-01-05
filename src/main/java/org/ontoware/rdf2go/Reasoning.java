/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go;

public enum Reasoning {

	/** as defined by RDF Semantics or as implemented in reality? ;-) */
	rdfs, 
	
	/** OWL-DL */
	owl, 
	
	/** No reasoning */
	none, 
	
	/** an experimental mix */
	rdfsAndOwl
}
