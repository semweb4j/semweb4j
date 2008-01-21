package org.ontoware.rdf2go.impl;

import org.ontoware.rdf2go.ModelFactory;

/**
 * Static binding to Jena24.
 * @author voelkel
 *
 */
public class StaticBinding {

	public static ModelFactory getModelFactory() {
		return new org.ontoware.rdf2go.impl.jena24.ModelFactoryImpl();
	}
	
}
