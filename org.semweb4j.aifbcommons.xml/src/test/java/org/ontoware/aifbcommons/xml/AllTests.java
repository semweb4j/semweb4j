package org.ontoware.aifbcommons.xml;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.ontoware.aifbcommons");
		//$JUnit-BEGIN$
		suite.addTestSuite(XMLUtilsTest.class);
		suite.addTestSuite(DocumentComposerTest.class);
		//$JUnit-END$
		return suite;
	}

}
