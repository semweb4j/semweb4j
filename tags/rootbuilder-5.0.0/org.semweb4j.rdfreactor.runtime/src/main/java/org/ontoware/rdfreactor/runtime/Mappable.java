package org.ontoware.rdfreactor.runtime;

import java.util.Iterator;
import java.util.Map;


/**
 * Analogous to {@link Iterable} which can return an {@link Iterator}, this
 * interface marks classes that can return a {@link Map}.
 * 
 * @author voelkel
 * 
 *         TODO use in RDFReactor
 * 
 * @param <K>
 * @param <V>
 */
@Patrolled
public interface Mappable<K, V> {
	
	public Map<K,V> map();
	
}
