package org.ontoware.rdf2go.model;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;


/** Utils for test, not testing utils */
public class TestUtils {
	
	/**
	 * Copy data from the source modelset to the target modelset. Iterates
	 * through all named models of the source and adds each to the target. If a
	 * named graph already exists in the target, the data will be added to it,
	 * target models will not be replaced.
	 * 
	 * @param source the source, data from here is taken
	 * @param target the target, data will be put here.
	 * @throws ModelRuntimeException if the copying process has an error
	 */
	public static void copy(ModelSet source, ModelSet target) throws ModelRuntimeException {
		for(Iterator<? extends Model> i = source.getModels(); i.hasNext();) {
			Model m = i.next();
			m.open();
			Model tm = target.getModel(m.getContextURI());
			tm.open();
			tm.addAll(m.iterator());
			m.close();
			tm.close();
		}
		// copy default model
		Model m = source.getDefaultModel();
		m.open();
		Model tm = target.getDefaultModel();
		tm.open();
		tm.addAll(m.iterator());
		m.close();
		tm.close();
	}
	
	/**
	 * Runs through the iterator of the iterable, closes it and returns number
	 * of items.
	 * 
	 * @author clemente
	 * 
	 * @param iterable Any type of iterator, not null
	 * @returns Number of items
	 */
	public static int countAndClose(ClosableIterable<?> iterable) {
		if(iterable == null)
			throw new IllegalArgumentException("Cannot count a null-iterable");
		ClosableIterator<?> it = iterable.iterator();
		return countAndClose(it);
	}
	
	/**
	 * Runs through the iterator of the iterable, closes it and returns number
	 * of items.
	 * 
	 * @author clemente
	 * 
	 * @param it Any type of iterator, not null
	 * @returns Number of items
	 */
	public static int countAndClose(ClosableIterator<?> it) {
		if(it == null)
			throw new IllegalArgumentException("Cannot count a null-iterable");
		int count = 0;
		while(it.hasNext()) {
			count++;
			it.next();
		}
		it.close();
		return count;
	}
	
}
