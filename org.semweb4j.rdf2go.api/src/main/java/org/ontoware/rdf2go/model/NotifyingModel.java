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

/**
 * BSD Licensed.
 */
package org.ontoware.rdf2go.model;

/**
 * A Model that can notify listeners when triples are added or removed from the
 * model.
 * 
 * Note that not all models support listening, for example a model hosted on a
 * remote server may not support notifications of operations done on the model
 * to remote clients. 
 * 
 * Reads from streams and readers are not detectable.
 * 
 * @author sauermann
 * @author voelkel
 */
public interface NotifyingModel {

	/**
	 * 
	 * @param listener
	 * @param pattern
	 *            all add or remove statement events matching the pattern will
	 *            be send to the listener. Updates are always send.
	 */
	public void addModelChangedListener(ModelChangedListener listener,
			TriplePattern pattern);

	public void addModelChangedListener(ModelChangedListener listener);

	public void removeModelChangedListener(ModelChangedListener listener);

}
