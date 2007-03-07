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

import java.util.LinkedList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.aifbcommons.collection.UnionManyIterator;


/**
 * Iterates on an Iterator of Iterators, but uses them one by one
 * @author voelkel
 *
 * @param <T>
 */
public class UnionManyIteratorImpl<T> implements UnionManyIterator<T> {

	private List<ClosableIterator<T>> iteratorList = new LinkedList<ClosableIterator<T>>();

	public void addIterator(ClosableIterator<T> it) {
		iteratorList.add(it);
	}

	public boolean hasNext() {
		return iteratorList.size() > 0 && iteratorList.get(0).hasNext();
	}

	public T next() {
		if (hasNext()) {
			T t = iteratorList.get(0).next();
			manageList();
			return t;
		} else
			return null;
	}

	public void remove() {
		if (iteratorList.size() > 0) {
			iteratorList.get(0).remove();
		}
		manageList();
	}

	private void manageList() {
		if (!iteratorList.get(0).hasNext()) {
			iteratorList.get(0).remove();
		}
	}

	public void close() {
		for (ClosableIterator it : iteratorList) {
			it.close();
		}
	}

}
