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

import org.ontoware.rdf2go.model.node.UriOrVariable;

/**
 * Quad match representation in rdf2go, used to query a ModelSet.
 * 
 * @author mvo
 * 
 */
public interface QuadPattern extends TriplePattern {


	/**
	 * @return URI or Variable
	 */
	public UriOrVariable getContext();

}
