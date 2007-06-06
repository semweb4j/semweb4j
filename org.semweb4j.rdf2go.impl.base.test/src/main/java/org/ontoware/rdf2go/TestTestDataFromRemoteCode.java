package org.ontoware.rdf2go;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.ontoware.rdf2go.testdata.TestData;

/**
 * call this test from other code to check if the resource can be found from
 * within a packaged jar
 */
public class TestTestDataFromRemoteCode {

	@Test
	public void testTestData() {
		InputStream in = TestData.getFoafAsStream();
		Assert.assertNotNull(in);
		try {
			Assert.assertTrue(in.read() != -1);
		} catch (IOException e) {
			Assert.fail();
		}
	}

}
