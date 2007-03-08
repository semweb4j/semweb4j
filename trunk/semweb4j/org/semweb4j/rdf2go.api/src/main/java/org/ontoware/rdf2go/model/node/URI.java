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
package org.ontoware.rdf2go.model.node;


/**
 * Interface for URI implementations.
 * 
 * Implementations must have valid implementations of
 * 
 * <pre>
 * public boolean equals(Object other);
 * 
 * public int hashCode();
 * </pre>
 * 
 * The method "public String toString();" is expected to return a valid URI String
 * 
 * 
 * 
 * 
 * @author voelkel
 * 
 */
public interface URI extends Resource, UriOrVariable {

}
