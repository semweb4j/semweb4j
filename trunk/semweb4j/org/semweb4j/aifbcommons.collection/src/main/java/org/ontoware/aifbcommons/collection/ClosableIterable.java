/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 * 
 * IMPROVE: USe Jakarta Commons Collections instead?
 */
package org.ontoware.aifbcommons.collection;


/** Implementing this interface allows an object to be the target of
 *  the "foreach" statement.
 */
public interface ClosableIterable<T> extends Iterable<T> {

    /**
     * Returns an iterator over a set of elements of type T.
     * 
     * @return an Iterator.
     */
    ClosableIterator<T> iterator();
}
