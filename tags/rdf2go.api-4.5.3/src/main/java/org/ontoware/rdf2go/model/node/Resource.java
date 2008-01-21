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
 * Marker interface for RDF resources (BlankNode and URI).
 * 
 * Must have valid implementations of
 * 
 * <pre>
 * public boolean equals(Object other);
 * 
 * public int hashCode();
 * </pre>
 * 
 * @author voelkel
 * 
 */
public interface Resource extends Node, ResourceOrVariable {

}
