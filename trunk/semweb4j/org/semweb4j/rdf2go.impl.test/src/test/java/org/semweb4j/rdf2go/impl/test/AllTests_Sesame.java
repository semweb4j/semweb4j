package org.semweb4j.rdf2go.impl.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.impl.sesame2.ModelFactoryImpl;
import org.ontoware.rdf2go.model.ModelSetTest;
import org.ontoware.rdf2go.model.ModelTest;

public class AllTests_Sesame {

	public static Test suite() {

		RDF2Go.register(new ModelFactoryImpl());

		TestSuite suite = new TestSuite(
				"Test for org.semweb4j.rdf2go.impl.test");
		// $JUnit-BEGIN$
		suite.addTestSuite(ModelTest.class);
		suite.addTestSuite(ModelSetTest.class);
		// $JUnit-END$
		return suite;
	}

}
