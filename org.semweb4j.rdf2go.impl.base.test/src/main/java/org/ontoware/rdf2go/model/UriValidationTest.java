package org.ontoware.rdf2go.model;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

public class UriValidationTest extends TestCase {

	String[] validURIs = { "http://xam.de", "urn:x-cds:item-1" };

	/**
	 * urn:gunnar is not a valid URI, because it pretends to be a URN, but those
	 * have the syntax ns:part:part. 
	 */
	String[] invalidURIs = { "123","urn:gunnar", "Ping",
			"http://300.300.300.300/%1234", "���", "&xtc;" };

	public void testJavaNetUri() {
		// expect no error
		for (String s : this.validURIs) {
			try {
				@SuppressWarnings("unused")
				URI uri = new URI(s);
			} catch (URISyntaxException e) {
				assertFalse("No exceptions (at least on my machine)", true);
				throw new RuntimeException(e);
			}
		}

		// TODO (wth, 15.08.2007)  find out if this test works
		//there was a todo that stated that it doesn't
		
//		// expect errors
//		for (String s : invalidURIs) {
//			boolean error = false;
//			try {
//				@SuppressWarnings("unused")
//				URI uri = new URI(s);
//				
//			
//				// exception should be thrown
////				assertFalse("Expect exception because '" + s
////						+ "' is not a valid URI", true);
//			} catch (URISyntaxException e) {
//				// ok
//				error = true;
//			}
//			assertTrue("got no URISyntaxException", error);
//		}
	}
}
