package org.semweb4j.sesame;

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
		
		URI m = new URIImpl("urn:rel:m");
		URI minusM = new URIImpl("urn:rel:-m");
		URI nrlInverse = ForwardChainingRDFSPlusInverseInferencerConnection.NRL_InverseProperty;
		URI defaultContext = null; // new Resource[0]

		RepositoryConnection con = repository.getConnection();

		con.add(m, nrlInverse, minusM, defaultContext);
		assert con.hasStatement(m, nrlInverse, minusM, true, defaultContext);
		assert con.hasStatement(minusM, nrlInverse, m, true, defaultContext);
		con.add(minusM, nrlInverse, m, defaultContext);
		assert con.hasStatement(m, nrlInverse, minusM, true, defaultContext);
		assert con.hasStatement(minusM, nrlInverse, m, true, defaultContext);

	}


}
