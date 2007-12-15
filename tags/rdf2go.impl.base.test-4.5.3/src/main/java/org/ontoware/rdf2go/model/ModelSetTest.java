package org.ontoware.rdf2go.model;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;

/**
 * Tests the implementation found on the classpath
 * @author voelkel
 *
 */
public class ModelSetTest extends AbstractModelSetTest {

	@Override
	public ModelFactory getModelFactory() {
		return RDF2Go.getModelFactory();
	}

}
