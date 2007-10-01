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
