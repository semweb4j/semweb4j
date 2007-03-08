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
	String[] invalidURIs = { "urn:gunnar", "Ping",
			"http://300.300.300.300/%1234", "���", "&xtc;" };

	public void testJavaNetUri() {
		// expect no error
		for (String s : validURIs) {
			try {
				@SuppressWarnings("unused")
				URI uri = new URI(s);
			} catch (URISyntaxException e) {
				assertFalse("No exceptions (at least on my machine)", true);
				throw new RuntimeException(e);
			}
		}
		// expect error
		for (String s : invalidURIs) {
			try {
				@SuppressWarnings("unused")
				URI uri = new URI(s);
				assertFalse("Expect exception above, as '" + s
						+ "' is not a valid URI", true);
			} catch (URISyntaxException e) {
				fail();
				throw new RuntimeException(e);
			}
		}
	}
}
