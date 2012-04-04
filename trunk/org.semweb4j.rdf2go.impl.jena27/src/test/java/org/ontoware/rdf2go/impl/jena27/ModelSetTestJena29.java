package org.ontoware.rdf2go.impl.jena27;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.AbstractModelSetTest;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public class ModelSetTestJena29 extends AbstractModelSetTest {

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
	@Test
	public void testWriteToOutputStreamSyntax() throws Exception {
		ModelSet modelSet = this.getModelFactory().createModelSet();
		modelSet.open();

		this.addTestDataToModelSet();
		
		try {
			modelSet.writeTo(new OutputStream() {
				@Override
				public void write(int b) {
				}
			}, Syntax.Nquads);
		} catch(ModelRuntimeException e) {
			fail();
		} catch(IOException e) {
			fail();
		}
	}

	@Test
	public void testRemoveAll2() {
		ModelSet modelSet = this.getModelFactory().createModelSet();
		modelSet.open();
		URI context1 = new URIImpl("uri:context1");
		URI context2 = new URIImpl("uri:context2");
		modelSet.addStatement(context1, new URIImpl("uri:r1"), new URIImpl(
				"uri:p1"), new URIImpl("uri:r2"));
		modelSet.addStatement(context1, new URIImpl("uri:r1"), new URIImpl(
				"uri:p1"), new URIImpl("uri:r3"));
		modelSet.addStatement(context2, new URIImpl("uri:r4"), new URIImpl(
				"uri:p2"), new URIImpl("uri:r5"));
		modelSet.addStatement(context2, new URIImpl("uri:r4"), new URIImpl(
				"uri:p2"), new URIImpl("uri:r6"));
		Model model1 = modelSet.getModel(context1);
		model1.open();
		Model model2 = modelSet.getModel(context2);
		model2.open();
		assertEquals(4, modelSet.size());
		assertEquals(2, model1.size());
		assertEquals(2, model2.size());

		model2.removeAll();

		assertEquals(2, modelSet.size());
		assertEquals(2, model1.size());
		assertEquals(0, model2.size());
		model1.close();
		model2.close();
	}

	
}
