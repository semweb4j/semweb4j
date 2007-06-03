package org.ontoware.rdfreactor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.ReasoningNotSupportedException;
import org.ontoware.rdfreactor.runtime.DatatypeUtilsTest;
import org.ontoware.rdfreactor.runtime.MicroBridgeTest;
import org.ontoware.rdfreactor.runtime.PersonTest;

/**
 * This is a superclass for Test Suites. Different ModelImpls can be tested.
 * @author voelkel
 * 
 */
public class AllTests {
	
	static {
		try {
			AllTests.m = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
			AllTests.m.open();
		} catch (ReasoningNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static org.ontoware.rdf2go.model.Model m;

	public static TestSuite suite;

	public static Test suite() throws Exception {

		suite = new TestSuite("RDFReactor runtime");

		suite.addTestSuite(MicroBridgeTest.class);
		suite.addTestSuite(DatatypeUtilsTest.class);
		suite.addTestSuite(PersonTest.class);

		return suite;
	}
}
