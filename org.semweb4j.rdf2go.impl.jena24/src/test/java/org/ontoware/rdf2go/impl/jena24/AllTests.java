package org.ontoware.rdf2go.impl.jena24;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ontoware.rdf2go.model.ModelTest;
import org.ontoware.rdf2go.testdata.TestData;



public class AllTests
{

	public static Test suite()
	{
		
		TestData.main(null);
		
		TestSuite suite = new TestSuite(
				"Test for org.ontoware.rdf2go.impl.jena24");

		// impl.jena24
		suite.addTestSuite(ModelTest.class);
		//suite.addTestSuite(ModelSetTest.class);
		suite.addTestSuite(TransactionTest.class);
		suite.addTestSuite(DataTypeTesting.class);
		return suite;
	}

}
