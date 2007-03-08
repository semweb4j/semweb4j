package org.semweb4j.rdf2go.impl.test;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;

/**
 * A stupid test to check what RDF triple stores do, when they should read N3,
 * which is not an RDF syntax.
 * 
 * @author voelkel
 * 
 */
public class N3ProcessingTest extends TestCase {

	public void testN3() throws ModelRuntimeException, IOException {
		Model model = RDF2Go.getModelFactory().createModel();
		Syntax n3 = new Syntax("N3", "text/rdf+n3");

		ClassLoader classloader = Thread.currentThread()
				.getContextClassLoader();
		InputStream in = classloader.getResourceAsStream("test2.n3");
		model.readFrom(in, n3);
	}

}
