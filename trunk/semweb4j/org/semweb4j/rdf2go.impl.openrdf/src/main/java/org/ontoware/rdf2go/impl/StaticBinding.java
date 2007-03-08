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

import org.ontoware.rdf2go.ModelFactory;

/**
 * Static binding to OpenRDF.
 * @author voelkel
 */
public class StaticBinding {

	public static ModelFactory getModelFactory() {
		return new org.ontoware.rdf2go.impl.sesame2.ModelFactoryImpl();
	}
	
}
