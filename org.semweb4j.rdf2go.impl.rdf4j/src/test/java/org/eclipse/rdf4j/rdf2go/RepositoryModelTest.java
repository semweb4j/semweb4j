/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.eclipse.rdf4j.rdf2go;


import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.model.AbstractModelTest;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryModelTest extends AbstractModelTest {

	private static Logger log = LoggerFactory
			.getLogger(RepositoryModelTest.class);

	@Override
	public ModelFactory getModelFactory() {
		return new RepositoryModelFactory();
	}

	@Test
	public void testLogging() {
		log.debug("Testing logging at DEBUG level");
		log.info("Testing logging at INFO level");
		log.warn("Testing logging at WARN level");
	}

	@Test
	public void testDirectRepositoryAccess() throws Exception {
		Model model = getModelFactory().createModel();
		model.open();

		// fetch the Repository, a Connection and a ValueFactory
		Repository repository = (Repository) model
				.getUnderlyingModelImplementation();
		RepositoryConnection connection = repository.getConnection();
		ValueFactory factory = repository.getValueFactory();

		// add a statement
		model.addStatement(subject, predicate, object);

		// convert the statement parts to OpenRDF data types
		Resource openRdfSubject = ConversionUtil.toOpenRDF(subject, factory);
		org.eclipse.rdf4j.model.IRI openRdfPredicate = ConversionUtil.toOpenRDF(
				predicate, factory);
		Value openRdfObject = ConversionUtil.toOpenRDF(object, factory);
		org.eclipse.rdf4j.model.IRI context = RepositoryModel.DEFAULT_OPENRDF_CONTEXT;

		// make sure this statement is contained in this model
		assertTrue(connection.hasStatement(openRdfSubject, openRdfPredicate,
				openRdfObject, false, context));

		// make sure this statement can also be found through the Model API
		ClosableIterator<? extends Statement> sit = model.findStatements(
				subject, predicate, object);
		assertNotNull(sit);
		assertTrue(sit.hasNext());

		Statement s2 = sit.next();
		URI s2s = (URI) s2.getSubject();
		URI s2p = s2.getPredicate();
		URI s2o = (URI) s2.getObject();

		assertEquals(subject, s2s);
		assertEquals(predicate, s2p);
		assertEquals(object, s2o);

		// make sure that this statement is only returned once
		assertFalse(sit.hasNext());

		// clean-up
		sit.close();
		connection.close();
		model.close();
	}

	@Override
	@Test
	public void testRemoveAll() throws Exception {
		Repository repo = new SailRepository(new MemoryStore());
		repo.initialize();
		RepositoryModelSet modelSet = new RepositoryModelSet(repo);
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

	// @Override
	// public void testRdfsReasoning()
	// throws ReasoningNotSupportedException, ModelRuntimeException
	// {
	// // overridden: inferred statements end up having no context, so the
	// // Model does not see them (findStatements, contains, etc. do not
	// // encounter them). This may change in the future, once
	// // MemoryStoreRDFSInferences starts to support different inferencing
	// // and inferred statement insertion strategies.
	// }
}
