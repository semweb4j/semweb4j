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
package org.ontoware.rdf2go.impl;

import java.util.Properties;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.ReasoningNotSupportedException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;

/**
 * Implmentors musts implement Model getModel(Properties p) and ModelSet
 * getModelSet(Properties p)
 * 
 * @author voelkel
 * 
 */
public abstract class AbstractModelFactory implements ModelFactory {

	// ////////////////////////
	// implement convenience functions by delegating to the worker methods

	public Model createModel() throws ModelRuntimeException {
		Properties properties = new Properties();
		properties.put(ModelFactory.REASONING, Reasoning.none.toString());
		properties.put(ModelFactory.STORAGE, ModelFactory.STORAGE_VALUE_MEMORY);
		return createModel(properties);
	}

	public Model createModel(Reasoning reasoning) throws ModelRuntimeException,
			ReasoningNotSupportedException {
		Properties properties = new Properties();
		properties.put(ModelFactory.REASONING, reasoning.toString());
		properties.put(ModelFactory.STORAGE, ModelFactory.STORAGE_VALUE_MEMORY);
		return createModel(properties);
	}

	public ModelSet createModelSet() throws ModelRuntimeException {
		Properties properties = new Properties();
		properties.put(ModelFactory.REASONING, Reasoning.none.toString());
		properties.put(ModelFactory.STORAGE, ModelFactory.STORAGE_VALUE_MEMORY);
		return createModelSet(properties);
	}

	public ModelSet createModelSet(Reasoning reasoning) throws ModelRuntimeException,
			ReasoningNotSupportedException {
		Properties properties = new Properties();
		properties.put(ModelFactory.REASONING, reasoning.toString());
		properties.put(ModelFactory.STORAGE, ModelFactory.STORAGE_VALUE_MEMORY);
		return createModelSet(properties);
	}
	
	/**
	 * Utility function to help using the properties
	 */
	public static Reasoning getReasoning(Properties p) throws ModelRuntimeException {

		String reasoningString = (String) p.get(ModelFactory.REASONING);
		
		if (reasoningString == null) 
			return Reasoning.none;
		
		// else
		Reasoning reasoning = Reasoning.valueOf(reasoningString);
		if (reasoning == null)
			throw new IllegalArgumentException("Illegal inferencing type: " + reasoningString);

		// else
		return reasoning;
	}
	

}
