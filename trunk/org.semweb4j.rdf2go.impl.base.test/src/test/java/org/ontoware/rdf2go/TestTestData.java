package org.ontoware.rdf2go;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.ontoware.rdf2go.testdata.TestData;

public class TestTestData {
	
	@Test
	public void testTestData() {
		InputStream in = TestData.getFoafAsStream();
		Assert.assertNotNull(in);
		try {
			Assert.assertTrue( in.read() != -1);
		} catch (IOException e) {
			Assert.fail();
		}
	}

}
