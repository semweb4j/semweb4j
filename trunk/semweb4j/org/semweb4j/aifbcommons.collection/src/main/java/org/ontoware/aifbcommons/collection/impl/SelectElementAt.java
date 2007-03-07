/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.aifbcommons.collection.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Return i-th element from an iterator. Throws NoSuchElementException if index
 * doens match.
 * 
 * @author voelkel
 * 
 */
public class SelectElementAt<T> {

	public T elementAt(int i, Iterator<T> it) throws NoSuchElementException {
		
		int pos = 0;
		while (it.hasNext()) {
			T current = it.next();
			if (i == pos)
				return current;
			pos++;
		}
		throw new NoSuchElementException("No element at pos " + i + ", iterator ends at " + (pos));
	}

}
