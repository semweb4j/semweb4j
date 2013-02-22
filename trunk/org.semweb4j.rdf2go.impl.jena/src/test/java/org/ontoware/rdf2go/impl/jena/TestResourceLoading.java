package org.ontoware.rdf2go.impl.jena;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

public class TestResourceLoading {

	@Test
	public void testFIle() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		InputStream in = cl
				.getResourceAsStream("org/ontoware/rdf2go/testdata/foaf.xml");
		Assert.assertNotNull(in);
	}

}
