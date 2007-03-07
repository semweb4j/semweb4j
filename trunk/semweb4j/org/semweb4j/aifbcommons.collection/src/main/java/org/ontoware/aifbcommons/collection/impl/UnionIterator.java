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

import org.ontoware.aifbcommons.collection.ClosableIterator;


public class UnionIterator<T> implements ClosableIterator<T> {

	private ClosableIterator<T> a;

	private ClosableIterator<T> b;

	private ClosableIterator<T> last;

	private boolean usingA = true;

	public UnionIterator(ClosableIterator<T> a, ClosableIterator<T> b) {
		this.a = a;
		this.b = b;
		if (!a.hasNext()) {
			usingA = false;
			last = b;
		} else {
			last = a;
		}
	}

	public boolean hasNext() {
		if (usingA)
			return a.hasNext();
		else
			return b.hasNext();
	}

	public T next() {
		if (usingA) {
			if (a.hasNext()) {
				last = a;
				return a.next();
			} else {
				usingA = false;
				last = b;
				return b.next();
			}
		} else
			return b.next();
	}

	public void remove() {
		last.remove();
	}

	public void close() {
		a.close();
		b.close();
	}

}
