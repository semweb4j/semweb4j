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
package org.ontoware.rdf2go.impl.sesame2;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.model.AbstractModelSetTest;

/**
 * @author sauermann
 */
public class ModelSetImplSesameTest extends AbstractModelSetTest {

	@Override
	public ModelFactory getModelFactory() {
        return new ModelFactoryImpl();
	}


}
