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

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.ModelSetTest;
import org.ontoware.rdf2go.model.ModelTest;

import junit.framework.TestSuite;

/**
 * JUnit 4 seems not to be able to run JUnit 3.8 test in a test suite
 * @author voelkel
 *
 */
public class AllTests extends TestSuite {

	public static TestSuite suite() {


		RDF2Go.register(new org.ontoware.rdf2go.impl.sesame2.ModelFactoryImpl());

		TestSuite suite = new TestSuite(
				"Test for org.semweb4j.rdf2go.impl.sesame2");
		// $JUnit-BEGIN$
		suite.addTestSuite(ModelTest.class);
		suite.addTestSuite(ModelSetTest.class);
		suite.addTestSuite(ModelSetImplSesameTest.class);
		suite.addTestSuite(TestsAgainstReportedBugs.class);
		suite.addTestSuite(ModelSetImplSesameFilebasedTest.class);
		suite.addTestSuite(ModelImplSesameUniqueTest.class);
		//$JUnit-END$
		return suite;
	}

}
