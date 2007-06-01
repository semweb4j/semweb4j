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
package org.ontoware.rdf2go.util;

import java.util.Iterator;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;

/**
 * Find a lot of convenience functions that are slow but nice and didn't make it
 * to the Model API.
 * 
 * @author voelkel
 * 
 */
public class ModelUtils {

	/**
	 * Count statements in iterable 'it'
	 * @param it the iterable
	 * @return the size
	 */ 
	public static int size(Iterable it) {
		int count = 0;
		Iterator i = it.iterator();
		while( i.hasNext() ) {
			i.next();
			count++;
		}
		return count;
	}
    
    /**
     * Count statements in iterator 'it'
     * @param i the iterator
     * @return the size
     */
    public static int size(Iterator i) {
        int count = 0;
        while( i.hasNext() ) {
            i.next();
            count++;
        }
        return count;
    }
    
    /**
     * Copy data from the source modelset to the target modelset.
     * Iterates through all named models of the source and adds
     * each to the target. 
     * If a named graph already exists in the target, the data
     * will be added to it, target models will not be replaced.
     * @param source the source, data from here is taken
     * @param target the target, data will be put here.
     * @throws ModelRuntimeException if the copying process has an error
     */
    public static void copy(ModelSet source, ModelSet target) throws ModelRuntimeException
    {
        for (Iterator<? extends Model> i = source.getModels(); 
            i.hasNext(); )
        {
            Model m = i.next();
            m.open();
            Model tm = target.getModel(m.getContextURI());
            tm.open();
            tm.addAll(m.iterator());
        }
        // copy default model
        Model m = source.getDefaultModel();
        m.open();
        Model tm = target.getDefaultModel();
        tm.open();
        tm.addAll(m.iterator());
    }
    
    /**
     * Remove data that is listed in the source modelset from the target modelset.
     * Iterates through all named models of the source and removes the listed
     * triples from the target.
     * @param source the source, data from here is evaluated
     * @param target the target, data contained in source and target are removed from here
     * @throws ModelRuntimeException if the deleting process has an error
     */
    public static void removeFrom(ModelSet source, ModelSet target) throws ModelRuntimeException
    {
        // remove
        for (Iterator<? extends Model> i = source.getModels(); 
            i.hasNext(); )
        {
            Model m = i.next();
            target.getModel(m.getContextURI()).removeAll(m.iterator());
        }
        // remove stuff contained in default model
        target.getDefaultModel().removeAll(source.getDefaultModel().iterator());
    }
    
    /**
     * @param a
     * @param b
     * @param result
     * @return the result after executing the intersection
     * @throws ModelRuntimeException
     */
    public static Model intersection( Model a, Model b, Model result) throws ModelRuntimeException {
    	for( Statement s : a ) {
    		if (b.contains(s)) {
    			result.addStatement(s);
    		}
    	}
    	return result;
    }
    
    /**
     * @param a
     * @param b
     * @param result
     * @return the result after executing the union
     * @throws ModelRuntimeException
     */
    public static Model union( Model a, Model b, Model result) throws ModelRuntimeException {
    	result.addAll(a.iterator());
    	result.addAll(b.iterator());
    	return result;
    }
    
    /**
     * @param a
     * @param b
     * @param result
     * @return the result after executing the complement of b in a (a\b)
     * @throws ModelRuntimeException
     */
    public static Model complement( Model a, Model b, Model result) throws ModelRuntimeException {
    	result.addAll(a.iterator());
    	result.removeAll(b.iterator());
    	return result;
    }

}
