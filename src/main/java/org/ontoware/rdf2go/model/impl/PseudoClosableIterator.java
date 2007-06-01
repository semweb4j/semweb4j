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
package org.ontoware.rdf2go.model.impl;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;


public class PseudoClosableIterator<T> implements ClosableIterator<T> {

	private Iterator<T> it;

	public PseudoClosableIterator(Iterator<T> it) {
		this.it = it;
	}

	public boolean hasNext() {
		return it.hasNext();
	}

	public T next() {
		return it.next();
	}

	public void remove() {
		it.remove();
	}

	public void close() {
		// do nothing
	}

}
