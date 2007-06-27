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
 * to remote clients. Get the capabilities of this model via
 * getNotifyingCapabilities()
 * 
 * @author sauermann
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
