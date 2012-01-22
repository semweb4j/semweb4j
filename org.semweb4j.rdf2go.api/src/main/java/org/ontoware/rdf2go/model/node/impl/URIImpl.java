/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2010
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model.node.impl;

import java.net.URISyntaxException;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;


public class URIImpl extends ResourceImpl implements URI {
	
	private static final long serialVersionUID = -73757322163415742L;
	private String uriString;
	
	/**
	 * Checks uri for validity and creates
	 * 
	 * @param uriString should be in valid URI syntax and not null
	 * @throws IllegalArgumentException if the uri is not valid and
	 *             createURIWithChecking is true
	 */
	public URIImpl(String uriString) {
		this(uriString, true);
	}
	
	/**
	 * @param uriString should be in valid URI syntax and not null
	 * @param createURIWithChecking if true, checks for valid uri
	 * @throws IllegalArgumentException if the uri is not valid and
	 *             createURIWithChecking is true
	 */
	public URIImpl(String uriString, boolean createURIWithChecking) {
		if(createURIWithChecking) {
			try {
				new java.net.URI(uriString);
			} catch(URISyntaxException e) {
				throw new IllegalArgumentException(e);
			}
		}
		this.uriString = uriString;
	}
	
	/**
	 * This method is deprecated. Just use new URIImpl(uriString) instead.
	 * 
	 * @deprecated use the constructors instead
	 * @param uriString should be in valid URI syntax and not null
	 * @return a URI
	 */
	@Deprecated
	public static URI create(String uriString) {
		return new URIImpl(uriString);
	}
	
	/**
	 * This method is deprecated. Just use new URIImpl(uriString,false) instead.
	 * 
	 * @deprecated use the constructors instead
	 * @param uriString should be in valid URI syntax and not null
	 * @return a URI
	 */
	@Deprecated
	public static URI createURIWithoutChecking(String uriString) {
		return new URIImpl(uriString, false);
	}
	
	@Override
	public String toString() {
		return this.uriString;
	}
	
	@Override
    public URI asURI() throws ClassCastException {
		return this;
	}
	
	@Override
    public BlankNode asBlankNode() throws ClassCastException {
		throw new ClassCastException("Cannot cast a URI to a BlankNode");
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null)
			return false;
		if(other instanceof URI) {
			boolean equal = this == other || ((URI)other).toString().equals(this.toString());
			return equal;
		} else
			return false;
	}
	
	public java.net.URI toJavaURI() throws URISyntaxException {
		return new java.net.URI(this.uriString);
	}
	
	@Override
	public int hashCode() {
		return this.uriString.hashCode();
	}
	
	@Override
    public int compareTo(Node other) {
		if(other instanceof URI) {
			return this.uriString.compareTo(((URI)other).toString());
		} else {
			// sort by type
			return NodeUtils.compareByType(this, other);
		}
	}
	
	@Override
    public String toSPARQL() {
		return "<" + this.uriString + ">";
	}
	
	@Override
    public java.net.URI asJavaURI() {
		try {
			return new java.net.URI(toString());
		} catch(URISyntaxException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
}
