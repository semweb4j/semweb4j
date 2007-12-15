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

import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * Subclasses must have valid equals() and hashCode() implementations.
 * @author voelkel
 *
 */
public abstract class LiteralImpl implements Literal {

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


}
