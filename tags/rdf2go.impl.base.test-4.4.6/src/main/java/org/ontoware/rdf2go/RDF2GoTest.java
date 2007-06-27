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

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.AbstractModelFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.URI;

/**
 * @author sauermann
 */
public class RDF2GoTest extends TestCase {

	private static class RDF2GoTestAdapter extends AbstractModelFactory {

		public Model createModel(Properties p) throws ModelRuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		public Model createModel(URI contextURI) throws ModelRuntimeException {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private static class RDF2GoTestAdapterTwo extends AbstractModelFactory {

		public Model createModel(Properties p) throws ModelRuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		public Model createModel(URI contextURI) throws ModelRuntimeException {
			// TODO Auto-generated method stub
			return null;
		}
	};

	/**
	 * Testing the RDF2Go default class
	 */
	public void testRDF2GoFactory() {
		// this is protected by design
		RDF2Go.modelFactory = null;

		ModelFactory adapterOne = new RDF2GoTestAdapter();
		RDF2Go.register(adapterOne);
		RDF2Go.register(adapterOne); // twice is allowed
		adapterOne = new RDF2GoTestAdapter(); // twice is allowed with the
												// same class
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
