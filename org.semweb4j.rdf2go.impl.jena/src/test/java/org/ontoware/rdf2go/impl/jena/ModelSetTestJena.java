package org.ontoware.rdf2go.impl.jena;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.AbstractModelSetTest;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.testdata.TestData;

public class ModelSetTestJena extends AbstractModelSetTest {

	private final ModelFactory modelFactory = new ModelFactoryImpl();
	
	@Override
	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	/**
	 * Overwrite a test from {@linkplain AbstractModelSetTest} because
	 * Jena does not support writing any quad serialization which is
	 * also supported by Sesame. No commonly inherited test is currently
	 * possible. We will test only {@link Syntax.Nquads} for Jena.
	 */
	@Override
	@Test
	public void testWriteToOutputStream() throws Exception {
		this.addTestDataToModelSet();
		
		try {
			getModelSet().writeTo(new OutputStream() {
				@Override
				public void write(int b) {
				}
			}, Syntax.Nquads);
		} catch(ModelRuntimeException e) {
			fail(e.getMessage());
		} catch(IOException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Overwrite a test from {@linkplain AbstractModelSetTest} because
	 * Jena does not support writing any quad serialization which is
	 * also supported by Sesame. No commonly inherited test is currently
	 * possible. We will test only {@link Syntax.Nquads} for Jena.
	 */
	@Override
	@Test
	public void testWriteToOutputStreamSyntax() throws Exception {
		this.addTestDataToModelSet();
		
		try {
			getModelSet().writeTo(new OutputStream() {
				@Override
				public void write(int b) {
				}
			}, Syntax.Nquads);
		} catch(ModelRuntimeException e) {
			fail(e.getMessage());
		} catch(IOException e) {
			fail(e.getMessage());
		}
	}

	@Override
	@Test
	public void testWriteToWriter() throws Exception {
		this.addTestDataToModelSet();
		
		StringWriter sw = new StringWriter();
		try {
			this.getModelSet().writeTo(sw, Syntax.Nquads);
		} catch(ModelRuntimeException e) {
			fail(e.getMessage());
		} catch(IOException e) {
			fail(e.getMessage());
		}
		assertTrue(sw.getBuffer().toString().length() > 1000);
		sw.close();
	}

	/**
	 * Overwrite a test from {@linkplain AbstractModelSetTest} because
	 * Jena does not support writing any quad serialization which is
	 * also supported by Sesame. No commonly inherited test is currently
	 * possible. We will test only {@link Syntax.Nquads} for Jena.
	 */
	@Override
	@Test
	public void testSerialize() throws Exception {
		getModelSet().readFrom(TestData.getFoafAsStream(), Syntax.RdfXml);
		String serialize = getModelSet().serialize(Syntax.Nquads);
		ModelSet m1 = getModelFactory().createModelSet();
		m1.open();
		m1.readFrom(new StringReader(serialize), Syntax.Nquads);
		assertEquals(getModelSet().size(), m1.size());
		m1.close();
	}

}
