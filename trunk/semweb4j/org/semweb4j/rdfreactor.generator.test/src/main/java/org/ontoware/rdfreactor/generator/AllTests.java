package org.ontoware.rdfreactor.generator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.ReasoningNotSupportedException;

/**
 * This is a superclass for Test Suites. Different ModelImpls can be tested.
 * @author voelkel
 * 
 */
public class AllTests {
	
	static {
		try {
			AllTests.m = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
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

		suite = new TestSuite("RDFReactor generator");

		suite.addTestSuite(SourceCodeWriterTest.class);
		suite.addTestSuite(TestJModel.class);
		
		return suite;
	}
}
