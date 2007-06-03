package org.ontoware.rdf2go.impl.jena24;

import org.ontoware.rdf2go.model.ModelSetTest;
import org.ontoware.rdf2go.model.ModelTest;

import junit.framework.Test;
import junit.framework.TestSuite;



public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(
				"Test for org.ontoware.rdf2go.impl.jena24");

		// impl.jena24
		suite.addTestSuite(ModelTest.class);
		suite.addTestSuite(ModelSetTest.class);
		suite.addTestSuite(TransactionTest.class);
		suite.addTestSuite(DataTypeTesting.class);
		return suite;
	}

}
