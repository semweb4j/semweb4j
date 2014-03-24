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

import org.ontoware.rdf2go.model.Model;


/**
 * A Model that can be persisted as a file.
 * 
 * @author voelkel
 * 
 * This interface is a shorthand for Persistent and Model.
 */
public interface PersistentModel extends Persistent, Model {

}
