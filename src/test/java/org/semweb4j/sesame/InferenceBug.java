package org.semweb4j.sesame;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

public class InferenceBug {

	@Test
	@Ignore("due to http://openrdf.org/issues/browse/SES-521")
	public void testAddInferredStatementExplicitly() throws RepositoryException {
		URI a = new URIImpl("urn:rel:a");
		URI b = new URIImpl("urn:rel:b");
		URI c = new URIImpl("urn:rel:c");
		URI defaultContext = null;
		// create a Sail stack
		Sail sail = new MemoryStore();
		sail = new ForwardChainingRDFSInferencer(sail);
		// create a Repository
		Repository repository = new SailRepository(sail);
		repository.initialize();

		RepositoryConnection con = repository.getConnection();

		con.add(a, RDFS.SUBPROPERTYOF, b, defaultContext);
		con.add(b, RDFS.SUBPROPERTYOF, c, defaultContext);
		Assert.assertTrue(con.hasStatement(a, RDFS.SUBPROPERTYOF, c, true,
				defaultContext));
		con.add(a, RDFS.SUBPROPERTYOF, c);
		Assert.assertTrue(con.hasStatement(a, RDFS.SUBPROPERTYOF, c, true,
				defaultContext));
	}
}
