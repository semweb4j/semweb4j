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
package org.ontoware.rdf2go;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.AbstractModelFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.URI;

/**
 * assures that the registration of different implementations of the
 * rdf2go framework are made correctly
 * 
 * @author sauermann
 */
public class RDF2GoTest extends TestCase {

	//the classes return null on purpose
	//they are only mock implementations for testing purposes
	
	private static class RDF2GoTestAdapter extends AbstractModelFactory {

		public Model createModel(Properties p) throws ModelRuntimeException {
			return null;
		}

		public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
			return null;
		}

		public Model createModel(URI contextURI) throws ModelRuntimeException {
			return null;
		}
	}

	private static class RDF2GoTestAdapterTwo extends AbstractModelFactory {

		public Model createModel(Properties p) throws ModelRuntimeException {
			return null;
		}

		public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
			return null;
		}

		public Model createModel(URI contextURI) throws ModelRuntimeException {
			return null;
		}
	}

	/**
	 * Testing the RDF2Go default class
	 * 
	 * create two inner classes that implement the missing parts
	 * form the abstract factory
	 * 
	 * register those implementation with the RDF2Go framework
	 */
	@Test
	public void testRDF2GoFactory() {
		// this is protected by design
		RDF2Go.modelFactory = null;

		ModelFactory adapterOne = new RDF2GoTestAdapter();
		RDF2Go.register(adapterOne);
		// twice is allowed
		RDF2Go.register(adapterOne); 
		adapterOne = new RDF2GoTestAdapter(); 
		
		// twice is only allowed with the same class but not with a different
		// one
		
		// TODO (wth, 15.08.2007) warum wird hier nun zum 3. mal der
		//adapter 1 registriert - der ist ja schon und das will man 
		//garnicht mehr testen
		RDF2Go.register(adapterOne);

		ModelFactory adapterTwo = new RDF2GoTestAdapterTwo();

		try {
			RDF2Go.register(adapterTwo);
			fail("cannot register two different RDF2Go classes");
		} catch (RuntimeException e) {
			// ok
		}

	}

}
