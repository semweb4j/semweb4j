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

import org.ontoware.rdf2go.exception.LockException;

public interface Lockable {

	//////////////
	// locking
	
	/**
	 * @return true if a lock is active
	 */
	public boolean isLocked();
	
	/**
	 * Removes a lock. If no lock was set, nothing happens.
	 */
	public void unlock();

	/**
	 * Tries to obtain a lock.
	 * @throws LockException if already locked
	 */
	public void lock() throws LockException;

}
