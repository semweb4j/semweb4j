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

package org.ontoware.rdf2go.model.impl;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;


public class PseudoClosableIterator<T> implements ClosableIterator<T> {

	private Iterator<T> iterator;

	public PseudoClosableIterator(Iterator<T> it) {
		this.iterator = it;
	}

	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	public T next() {
		return this.iterator.next();
	}

	public void remove() {
		this.iterator.remove();
	}

	public void close() {
		// do nothing
	}

}
