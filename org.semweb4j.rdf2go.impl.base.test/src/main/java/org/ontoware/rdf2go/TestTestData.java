package org.ontoware.rdf2go;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.testdata.TestData;

import junit.framework.TestCase;

public class TestTestData extends TestCase {
	
	public void testTestData() {
		InputStream in = TestData.getFoafAsStream();
		try {
			assertTrue( in.read() != -1);
		} catch (IOException e) {
			fail();
		}
	}

}
