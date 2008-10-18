package org.semweb4j.sesame;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

public class TestRDFSInverseInferencer {

	@Test
	public void testRDFSPlusInversesInferencer() throws RepositoryException {
		// create a Sail stack
		Sail sail = new MemoryStore();
		sail = new ForwardChainingRDFSPlusInverseInferencer(sail);

		// create a Repository
		Repository repository = new SailRepository(sail);
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}

		URI a = new URIImpl("urn:test:a");
		URI b = new URIImpl("urn:test:b");
		URI c = new URIImpl("urn:test:c");
		URI d = new URIImpl("urn:test:d");
		URI nrlInverse = ForwardChainingRDFSPlusInverseInferencerConnection.NRL_InverseProperty;

		repository.getConnection().add(a, b, c, new Resource[0]);

		Assert.assertFalse(repository.getConnection().hasStatement(c, d, a,
				true, new Resource[0]));

		repository.getConnection().add(b, nrlInverse, d, new Resource[0]);

		Assert.assertTrue(repository.getConnection().hasStatement(c, d, a,
				true, new Resource[0]));

	}

	@Test
	public void testInferencer_0_directly() throws RepositoryException {
		// create a Sail stack
		Sail sail = new MemoryStore();
		sail = new ForwardChainingRDFSPlusInverseInferencer(sail);

		// create a Repository
		Repository repository = new SailRepository(sail);
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}

		org.openrdf.model.URI a = new org.openrdf.model.impl.URIImpl(
				"urn:test:a");
		org.openrdf.model.URI b = new org.openrdf.model.impl.URIImpl(
				"urn:test:b");
		org.openrdf.model.URI c = new org.openrdf.model.impl.URIImpl(
				"urn:test:c");
		org.openrdf.model.URI d = new org.openrdf.model.impl.URIImpl(
				"urn:test:d");
		org.openrdf.model.URI nrlInverse = new org.openrdf.model.impl.URIImpl(
				"http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#inverseProperty");

		repository.getConnection().add(a, b, c, new Resource[0]);
		Assert.assertTrue("added [a] [b] [c]", repository.getConnection()
				.hasStatement(a, b, c, true, new Resource[0]));

		Assert.assertFalse("expect not [c] [d] [a]", repository.getConnection()
				.hasStatement(c, d, a, true, new Resource[0]));

		// add [b] hasInverse [d]
		repository.getConnection().add(b, nrlInverse, d, new Resource[0]);
		Assert.assertTrue("added [b] nrlInverse [d]", repository
				.getConnection().hasStatement(b, nrlInverse, d, true,
						new Resource[0]));

		Assert.assertTrue("expect [c] [d] [a]", repository.getConnection()
				.hasStatement(c, d, a, true, new Resource[0]));

	}

	@Test
	public void testStrangeBug() throws RepositoryException {
		// create a Sail stack
		Sail sail = new MemoryStore();
		sail = new ForwardChainingRDFSPlusInverseInferencer(sail);

		// create a Repository
		Repository repository = new SailRepository(sail);
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
		
		URI p = new URIImpl("urn:rel:p");
		URI q = new URIImpl("urn:rel:q");
		URI nrlInverse = ForwardChainingRDFSPlusInverseInferencerConnection.NRL_InverseProperty;
		URI defaultContext = null; // new Resource[0]

		RepositoryConnection con = repository.getConnection();

		// add p-hasInverse-q
		con.add(p, nrlInverse, q, defaultContext);
		assertTrue("just added p-haInv-q, should stil be there", 
				con.hasStatement(p, nrlInverse, q, true, defaultContext) );
		assertTrue("expect inferred stmt: q-hasInv-p", 
				con.hasStatement(q, nrlInverse, p, true, defaultContext) );
		
		// add (redundant) inverse stmt: q-hasInv-p
		con.add(q, nrlInverse, p, defaultContext);
		assertTrue("added p-haInv-q, should stil be there", 
				con.hasStatement(p, nrlInverse, q, true, defaultContext) );
		assertTrue( con.hasStatement(p, nrlInverse, q, true, defaultContext) );
		assertTrue("added q-hasInv-p, should still be there", 
				con.hasStatement(q, nrlInverse, p, true, defaultContext) );

	}

	@Test
	public void testInverseTriplesOnRDFDirectly() throws RepositoryException {
		// create a Sail stack
		Sail sail = new MemoryStore();
		sail = new ForwardChainingRDFSPlusInverseInferencer(sail);

		// create a Repository
		Repository repository = new SailRepository(sail);
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
		
		URI a1 = new URIImpl("urn:name:a1");
		URI b1 = new URIImpl("urn:name:b1");
		URI relA = new URIImpl("urn:rel:A");
		URI relAinv= new URIImpl("urn:rel:Ainv");
		URI nrlInverse = ForwardChainingRDFSPlusInverseInferencerConnection.NRL_InverseProperty;
		URI defaultContext = null; // new Resource[0]

		RepositoryConnection con = repository.getConnection();
		con.add(a1,relA,b1);
		assert con.hasStatement(a1,relA,b1, true, defaultContext);
		con.add(relA,nrlInverse,relAinv);
		assert con.hasStatement(b1, relAinv, a1, true, defaultContext);
	}


}
