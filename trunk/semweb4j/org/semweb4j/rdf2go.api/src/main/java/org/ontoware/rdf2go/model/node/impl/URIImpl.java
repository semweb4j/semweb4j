/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.model.node.impl;

import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

public class URIImpl extends ResourceImpl implements URI {

	private static final Log log = LogFactory.getLog(URIImpl.class);

	private String uriString;

	/**
	 * Checks uri for validity and creates
	 * 
	 * @param uriString
	 * @throws IllegalArgumentException
	 *             if the uri is not valid and createURIWithChecking is true
	 */
	public URIImpl(String uriString) {
		this(uriString, true);
	}

	/**
	 * @param uriString
	 * @param createURIWithChecking
	 *            if true, checks for valid uri
	 * @throws IllegalArgumentException
	 *             if the uri is not valid and createURIWithChecking is true
	 */
	public URIImpl(String uriString, boolean createURIWithChecking) {
		if (createURIWithChecking) {
			try {
				new java.net.URI(uriString);
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(e);
			}
		}
		this.uriString = uriString;
	}

	/**
	 * This method is deprecated. Just use new URIImpl(uriString) instead.
	 * @deprecated use the constructors instead
	 * @param uriString
	 * @return a URI
	 */
	public static URI create(String uriString) {
		return new URIImpl(uriString);
	}

	/**
	 * This method is deprecated. Just use new URIImpl(uriString,false) instead.
	 * @deprecated use the constructors instead
	 * @param uriString
	 * @return a URI
	 */
	public static URI createURIWithoutChecking(String uriString) {
		return new URIImpl(uriString,false);
	}

	public String toString() {
		return this.uriString;
	}

	public URI asURI() throws ClassCastException {
		return this;
	}

	public BlankNode asBlankNode() throws ClassCastException {
		throw new ClassCastException("Cannot cast a URI to a BlankNode");
	}

	public boolean equals(Object other) {

		if (other == null)
			return false;
		
		log.debug("comparing for equal: " + this + " and " + other
				+ " of type " + other.getClass());

		if (other instanceof URI) {
			boolean equal = ((URI) other).toString().equals(this.toString());

			log.debug("they are equal? " + equal);

			return equal;
		} else
			log.debug("URIImpl cannot compare with type " + other.getClass()
					+ " so this is false");

		return false;
	}

	public java.net.URI toJavaURI() throws URISyntaxException {
		return new java.net.URI(this.uriString);
	}

	public int hashCode() {
		log.debug("my hashcode is " + this.uriString.hashCode());
		return this.uriString.hashCode();
	}

	public int compareTo(Node other) {
		if (other instanceof URI) {
			return this.uriString.compareTo(((URI) other).toString());
		} else {
			// sort by type
			return NodeUtils.compareByType(this, other);
		}
	}

	public String toSPARQL() {
		return "<" + uriString + ">";
	}

}
