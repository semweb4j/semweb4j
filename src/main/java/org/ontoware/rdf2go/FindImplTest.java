package org.ontoware.rdf2go;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.testdata.TestData;

public class FindImplTest {

	@Test
	public void testFindImplementation() throws ModelRuntimeException,
			IOException {
		ModelFactory modelFactory = RDF2Go.getModelFactory();
		Assert.assertNotNull(modelFactory);
		Model model = modelFactory.createModel();
		Assert.assertNotNull(model);
		Assert.assertFalse( model.isOpen() );
		model.open();
		Assert.assertTrue( model.isOpen() );
		
		// use the model a bit
		InputStream foafStream = TestData.getFoafAsStream();
		Assert.assertNotNull(foafStream);
		Assert.assertEquals(0, model.size());
		model.readFrom(foafStream);
		Assert.assertTrue(model.size() > 0);
		model.close();
		Assert.assertFalse( model.isOpen() );
	}

}
