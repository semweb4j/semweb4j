package org.ontoware.rdf2go.impl.jena27;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.impl.jena29.ModelFactoryImpl;
import org.ontoware.rdf2go.model.AbstractModelTest;

public class ModelTest extends AbstractModelTest {

	@Override
	public ModelFactory getModelFactory() {
		return new ModelFactoryImpl();
	}

	
}
