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

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;


public class UnionIterable<T> implements ClosableIterable<T> {

	private ClosableIterable<T> a;
	private ClosableIterable<T> b;

	public UnionIterable(ClosableIterable<T> a, ClosableIterable<T> b) {
		this.a = a;
		this.b = b;
	}

	public ClosableIterator<T> iterator() {
		return new UnionIterator<T>( a.iterator(), b.iterator() );
	}

}
