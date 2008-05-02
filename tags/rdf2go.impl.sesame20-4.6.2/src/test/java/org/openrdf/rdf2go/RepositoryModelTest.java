/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.model.AbstractModelTest;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

public class RepositoryModelTest extends AbstractModelTest {

	private static Logger log = LoggerFactory.getLogger(RepositoryModelTest.class);
	
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
	public void testDirectRepositoryAccess()
		throws Exception
	{
		Model model = getModelFactory().createModel();
		model.open();

		// fetch the Repository, a Connection and a ValueFactory
		Repository repository = (Repository)model.getUnderlyingModelImplementation();
		RepositoryConnection connection = repository.getConnection();
		ValueFactory factory = repository.getValueFactory();

		// add a statement
		model.addStatement(subject, predicate, object);

		// convert the statement parts to OpenRDF data types
		Resource openRdfSubject = ConversionUtil.toOpenRDF(subject, factory);
		org.openrdf.model.URI openRdfPredicate = ConversionUtil.toOpenRDF(predicate, factory);
		Value openRdfObject = ConversionUtil.toOpenRDF(object, factory);
		org.openrdf.model.URI context = RepositoryModel.DEFAULT_OPENRDF_CONTEXT;

		// make sure this statement is contained in this model
		assertTrue(connection.hasStatement(openRdfSubject, openRdfPredicate, openRdfObject, false, context));

		// make sure this statement can also be found through the Model API
		ClosableIterator<? extends Statement> sit = model.findStatements(subject, predicate, object);
		assertNotNull(sit);
		assertTrue(sit.hasNext());

		Statement s2 = sit.next();
		URI s2s = (URI)s2.getSubject();
		URI s2p = (URI)s2.getPredicate();
		URI s2o = (URI)s2.getObject();

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
