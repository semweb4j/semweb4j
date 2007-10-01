package org.ontoware.rdf2go;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.testdata.TestData;

/**
 * make some basic functionality tests with the model implementation
 * 
 * assure the implementation is found by the factory,
 * open and close the model, insert some elements, etc.
 */
public class FindImplTest {

	@Test
	public void testFindImplementation() throws ModelRuntimeException,
			IOException {
		
		//test basic model functionalities
		//grab the model factory
		//let it create a model
		//check if the model is closed by default
		//and truly open if it was opened
		ModelFactory modelFactory = RDF2Go.getModelFactory();
		Assert.assertNotNull(modelFactory);
		Model model = modelFactory.createModel();
		Assert.assertNotNull(model);
		Assert.assertFalse( model.isOpen() );
		model.open();
		Assert.assertTrue( model.isOpen() );
		
		// use the model a bit
		//load foaf test data
		//make sure the file is loaded
		//assure the model form above has no entries
		//put the foaf file into the model and make sure the model now
		//has elements
		//finally close the model and find out if it truly is closed
		InputStream foafStream = TestData.getFoafAsStream();
		Assert.assertNotNull(foafStream);
		Assert.assertEquals(0, model.size());
		model.readFrom(foafStream);
		Assert.assertTrue(model.size() > 0);
		model.close();
		Assert.assertFalse( model.isOpen() );
	}

}
