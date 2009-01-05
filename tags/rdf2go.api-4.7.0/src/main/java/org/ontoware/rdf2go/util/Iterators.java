/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go.util;

import java.util.Collection;
import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;

/**
 * This class consists exclusively of static methods that operate on or return
 * iterators. It is the Iterator-equivalent of <tt>java.util.Collections</tt>.
 * 
 * copied into RDF2Go by Leo Sauemann on 11.1.2007, 
 * it helps to get working quicker.
 */
public class Iterators {

	/**
	 * Adds all elements from the supplied iterator to the specified collection. If the
	 * supplied iterator is an instance of {@link CloseableIterator} it is automatically
	 * closed after consumption.
	 * 
	 * @param iter
	 *        An iterator containing elements to add to the container. If the iterator
	 *        is an instance of {@link CloseableIterator} it is automatically closed
	 *        after consumption.
	 * @param collection
	 *        The collection to add the elements to.
	 * @return The <tt>collection</tt> object that was supplied to this method.
	 */
	public static <E, C extends Collection<E>> C addAll(Iterator<? extends E> iter, C collection) {
		try {
			while (iter.hasNext()) {
				collection.add(iter.next());
			}
		}
		finally {
			closeCloseable(iter);
		}

		return collection;
	}

	/**
	 * Closes the supplied iterator if it is an instance of
	 * {@link CloseableIterator}, otherwise the request is ignored.
	 * 
	 * @param iter
	 *        The iterator that should be closed.
	 */
	public static void closeCloseable(Iterator<?> iter) {
		if (iter instanceof ClosableIterator<?>) {
			((ClosableIterator<?>)iter).close();
		}
	}

	/**
	 * Converts an iterator to a string by concatenating all of the string
	 * representations of objects in the iterator, divided by a separator.
	 * 
	 * @param iter
	 *        An iterator over arbitrary objects that are expected to implement
	 *        {@link Object#toString()}.
	 * @param separator
	 *        The separator to insert between the object strings.
	 * @return A String representation of the objects provided by the supplied
	 *         iterator.
	 */
	public static String toString(Iterator<?> iter, String separator) {
		StringBuilder sb = new StringBuilder();
		toString(iter, separator, sb);
		return sb.toString();
	}

	/**
	 * Converts an iterator to a string by concatenating all of the string
	 * representations of objects in the iterator, divided by a separator.
	 * 
	 * @param iter
	 *        An iterator over arbitrary objects that are expected to implement
	 *        {@link Object#toString()}.
	 * @param separator
	 *        The separator to insert between the object strings.
	 * @param sb
	 *        A StringBuilder to append the iterator string to.
	 */
	public static void toString(Iterator<?> iter, String separator, StringBuilder sb) {
		while (iter.hasNext()) {
			sb.append(iter.next());

			if (iter.hasNext()) {
				sb.append(separator);
			}
		}
	}
	
	/**
	 * Runs through the iterator and returns number of items. It doesn't close it.
	 * 
	 * @author clemente
	 * 
	 * @param it Any type of iterator, not null
	 * @returns Number of items
	 */
	
	public static int count(Iterator<?> it){
		assert it != null;
		
		int count = 0;
		while (it.hasNext()) {
			count++;
			it.next();
		}
		
		return count;

	}
		
}
