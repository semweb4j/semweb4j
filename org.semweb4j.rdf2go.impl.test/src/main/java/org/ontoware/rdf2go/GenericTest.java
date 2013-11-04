package org.ontoware.rdf2go;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.AbstractModelFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.testdata.TestData;


/**
 * make some basic functionality tests with the model implementation
 * 
 * assure the implementation is found by the factory, open and close the model,
 * insert some elements, etc.
 */
public class GenericTest {
	
	@Test
	public void testFindImplementation() throws ModelRuntimeException, IOException {
		
		// test basic model functionalities
		// grab the model factory
		// let it create a model
		// check if the model is closed by default
		// and truly open if it was opened
		ModelFactory modelFactory = RDF2Go.getModelFactory();
		Assert.assertNotNull(modelFactory);
		Model model = modelFactory.createModel();
		Assert.assertNotNull(model);
		Assert.assertFalse(model.isOpen());
		model.open();
		Assert.assertTrue(model.isOpen());
		
		// use the model a bit
		// load foaf test data
		// make sure the file is loaded
		// assure the model form above has no entries
		// put the foaf file into the model and make sure the model now
		// has elements
		// finally close the model and find out if it truly is closed
		InputStream foafStream = TestData.getFoafAsStream();
		Assert.assertNotNull(foafStream);
		Assert.assertEquals(0, model.size());
		model.readFrom(foafStream);
		Assert.assertTrue(model.size() > 0);
		model.close();
		Assert.assertFalse(model.isOpen());
	}
	
	// the classes return null on purpose
	// they are only mock implementations for testing purposes
	
	private static class RDF2GoTestAdapter extends AbstractModelFactory {
		
		@Override
		public Model createModel(Properties p) throws ModelRuntimeException {
			return null;
		}
		
		@Override
		public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
			return null;
		}
		
		@Override
		public Model createModel(URI contextURI) throws ModelRuntimeException {
			return null;
		}
		
		@Override
		public QueryResultTable sparqlSelect(String url, String query) {
			return null;
		}
	}
	
	private static class RDF2GoTestAdapterTwo extends AbstractModelFactory {
		
		@Override
		public Model createModel(Properties p) throws ModelRuntimeException {
			return null;
		}
		
		@Override
		public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
			return null;
		}
		
		@Override
		public Model createModel(URI contextURI) throws ModelRuntimeException {
			return null;
		}
		
		@Override
		public QueryResultTable sparqlSelect(String url, String query) {
			return null;
		}
	}
	
	/**
	 * Testing the RDF2Go default class
	 * 
	 * assures that the registration of different implementations of the rdf2go
	 * framework are made correctly
	 * 
	 * create two inner classes that implement the missing parts form the
	 * abstract factory
	 * 
	 * register those implementation with the RDF2Go framework
	 * 
	 * @author sauermann
	 */
	@Test
	public void testRDF2GoFactory() {
		// this is protected by design
		RDF2Go.modelFactory = null;
		
		// register the same twice is allowed
		ModelFactory adapterOne = new RDF2GoTestAdapter();
		RDF2Go.register(adapterOne);
		RDF2Go.register(adapterOne);
		
		// twice is only allowed with the same class but not with a different
		// one
		adapterOne = new RDF2GoTestAdapter();
		RDF2Go.register(adapterOne);
		
		ModelFactory adapterTwo = new RDF2GoTestAdapterTwo();
		
		try {
			RDF2Go.register(adapterTwo);
			fail("cannot register two different RDF2Go classes");
		} catch(RuntimeException e) {
			// ok
		} finally {
			RDF2Go.modelFactory = null;
		}
		
	}
	
	/**
	 * call this test from other code to check if the resource can be found from
	 * within a packaged jar
	 */
	@Test
	public void testTestData() {
		InputStream in = TestData.getFoafAsStream();
		Assert.assertNotNull(in);
		try {
			Assert.assertTrue(in.read() != -1);
		} catch(IOException e) {
			Assert.fail();
		}
	}
	
}
