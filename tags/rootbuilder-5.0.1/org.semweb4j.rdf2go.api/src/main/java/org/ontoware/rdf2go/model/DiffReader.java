/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2010
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model;

/**
 * A read-only diff.
 * 
 * Implementations should first update the model in a way to first remove all
 * statements given in the DiffReader, then add the added statements. This
 * allows handling overlapping sets.
 * 
 * @author voelkel
 */
public interface DiffReader {
	
	public Iterable<Statement> getAdded();
	
	public Iterable<Statement> getRemoved();
	
}
