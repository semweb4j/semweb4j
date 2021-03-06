package org.ontoware.rdf2go.impl.jena;

import java.io.IOException;

import org.junit.Test;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.AbstractModelTest;
import org.ontoware.rdf2go.model.Syntax;

public class ModelTest extends AbstractModelTest {

	@Override
	public ModelFactory getModelFactory() {
		return new ModelFactoryImpl();
	}

	/*
	 * Non-Javadoc:
	 * Overriding a parent test to set the specific set of syntaxes to be tested
	 * with Jena (different from Sesame).
	 */
	@Override
	@Test
	public void testReadFromSyntaxFiles() throws ModelRuntimeException, IOException {
		// Set the syntaxes to be tested specifically for Jena:
	    // Don't overwrite, use all from parent Test:
		//super.readerSyntaxes = new Syntax[] {Syntax.RdfXml, ...};
		
		super.testReadFromSyntaxFiles();
	}
	
	/*
	 * Non-Javadoc:
	 * Overriding a parent test to set the specific set of syntaxes to be tested
	 * with Jena (different from Sesame).
	 */
	@Override
	@Test
	public void testWriteToSyntaxFiles() throws ModelRuntimeException, IOException {
		// Set the syntaxes to be tested specifically for Jena:
	    // FIXME Jena supports all except for TriX
		super.writerSyntaxes.remove(Syntax.Trix);
		
		super.testWriteToSyntaxFiles();
	}
}
