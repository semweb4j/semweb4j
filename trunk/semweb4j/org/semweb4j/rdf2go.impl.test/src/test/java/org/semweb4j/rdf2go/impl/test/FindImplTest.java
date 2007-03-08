package org.semweb4j.rdf2go.impl.test;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.testdata.TestData;

public class FindImplTest {

	/**
	 * TODO refactor to a real JUnit test case that runs via OSGI
	 * 
	 * @param args
	 * @throws ModelRuntimeException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ModelRuntimeException, IOException {
		ModelFactory modelFactory = RDF2Go.getModelFactory();
		Model model = modelFactory.createModel();
		InputStream foafStream = TestData.getFoafAsStream();
		assert foafStream != null;
		model.readFrom(foafStream);
		model.dump();
	}

}
