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

package org.ontoware.aifbcommons.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * An <b>closabel</b> iterator over a collection. Iterator takes the place of
 * Enumeration in the Java collections framework.
 * 
 * @author Josh Bloch, adapter by Max Voelkel
 * @see java.util.Collection
 * @see java.util.ListIterator
 * @see java.util.Enumeration
 */
public interface ClosableIterator<E> extends Iterator<E> {
	
	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> would return an element
	 * rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the iterator has more elements.
	 */
	@Override
    boolean hasNext();
	
	/**
	 * Returns the next element in the iteration. Calling this method repeatedly
	 * until the {@link #hasNext()} method returns false will return each
	 * element in the underlying collection exactly once.
	 * 
	 * @return the next element in the iteration.
	 * @exception NoSuchElementException iteration has no more elements.
	 */
	@Override
    E next();
	
	/**
	 * 
	 * Removes from the underlying collection the last element returned by the
	 * iterator (optional operation). This method can be called only once per
	 * call to <tt>next</tt>. The behavior of an iterator is unspecified if the
	 * underlying collection is modified while the iteration is in progress in
	 * any way other than by calling this method.
	 * 
	 * @exception UnsupportedOperationException if the <tt>remove</tt> operation
	 *                is not supported by this Iterator.
	 * 
	 * @exception IllegalStateException if the <tt>next</tt> method has not yet
	 *                been called, or the <tt>remove</tt> method has already
	 *                been called after the last call to the <tt>next</tt>
	 *                method.
	 */
	@Override
    void remove();
	
	/**
	 * The uderlying implementation frees resources. For some it is absolutely
	 * necessary to call this method.
	 */
	void close();
}
