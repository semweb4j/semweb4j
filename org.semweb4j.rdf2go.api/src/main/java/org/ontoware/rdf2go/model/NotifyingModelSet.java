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
 * A ModelSet that can notify listeners when triples are added or removed from the
 * model.
 * 
 * Note that not all models support listening, for example a model hosted on a
 * remote server may not support notifications of operations done on the model
 * to remote clients.
 * 
 * @author sauermann
 * @author voelkel
 */
public interface NotifyingModelSet {

	/**
	 * 
	 * @param listener
	 * @param pattern
	 *            all add or remove statement events matching the pattern will
	 *            be send to the listener. Updates are always send.
	 */
	public void addModelSetChangedListener(ModelChangedListener listener,
			QuadPattern pattern);

	public void addModelSetChangedListener(ModelChangedListener listener);

	public void removeModelSetChangedListener(ModelChangedListener listener);

}
