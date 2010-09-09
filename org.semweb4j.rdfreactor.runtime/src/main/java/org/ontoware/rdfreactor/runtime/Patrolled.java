package org.ontoware.rdfreactor.runtime;

/**
 * Marker annotation for a patrolled method or class. This means, I reviewed the
 * code and believe it can go unchanged (if not marked deprecated or has FIXME
 * annotations) into the next release of RDFReactor.
 * 
 * After the next round of recatoring, this annotation and all references to it
 * should be deleted.
 * 
 * @author voelkel
 * 
 */
public @interface Patrolled {
	
}
