package org.ontoware.rdf2go;


import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.ontoware.rdf2go.testdata.TestData;

public class TestTestData {
	
	@Test
	public void testTestData() {
		InputStream in = TestData.getFoafAsStream();
		Assert.assertNotNull("test data stream should not be null",in);
		try {
			Assert.assertTrue( in.read() != -1);
		} catch (IOException e) {
			Assert.fail();
		}
	}

}
