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

import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;


/**
 * Subclasses must have valid equals() and hashCode() implementations.
 * 
 * @author voelkel
 * 
 */
public abstract class LiteralImpl implements Literal {
	
	private static final long serialVersionUID = -4860142603655339470L;
	
	public abstract String getValue();
	
	public Resource asResource() throws ClassCastException {
		throw new ClassCastException("Literals are no resources");
	}
	
	public Literal asLiteral() throws ClassCastException {
		return this;
	}
	
	public URI asURI() throws ClassCastException {
		throw new ClassCastException("Literals are no URIs");
	}
	
	public BlankNode asBlankNode() throws ClassCastException {
		throw new ClassCastException("Literals are no BlankNodes");
	}
	
	protected static String sparqlEncode(String raw) {
		String result = raw;
		result = result.replace("\\", "\\\\");
		result = result.replace("'", "\\'");
		result = result.replace("\"", "\\\"");
		return result;
	}
	
}
