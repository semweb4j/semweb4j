package org.ontoware.rdf2go.model;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.AbstractModelTest;

/**
 * Tests the implementation found on the classpath
 * @author voelkel
 *
 */
public class ModelTest extends AbstractModelTest {

	@Override
	public ModelFactory getModelFactory() {
		return RDF2Go.getModelFactory();
	}

}
