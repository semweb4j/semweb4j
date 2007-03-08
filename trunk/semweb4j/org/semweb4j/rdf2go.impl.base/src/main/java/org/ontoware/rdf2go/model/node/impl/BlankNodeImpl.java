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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BlankNodeImpl extends ResourceImpl implements BlankNode {

	private static final Log log = LogFactory.getLog(Variable.class);

	private Object underlyingBlankNode;

	/** This method should only be called by RDF2Go implmentations */
	public BlankNodeImpl(Object underlyingBlankNode) {
		this.underlyingBlankNode = underlyingBlankNode;
	}

	/** This method should only be called by RDF2Go implmentations */
	public Object getUnderlyingBlankNode() {
		return this.underlyingBlankNode;
	}

	public boolean equals(Object other) {
		return other instanceof BlankNodeImpl
				&& this.getUnderlyingBlankNode().equals(
						((BlankNodeImpl) other).getUnderlyingBlankNode());
	}
	
	public String toString()
	{
		return underlyingBlankNode.toString();
	}
	
	public URI asURI() throws ClassCastException {
		throw new ClassCastException("Cannot cast a BlankNode to a URI");

	}

	public BlankNode asBlankNode() throws ClassCastException {
		return this;
	}

	public int hashCode() {
		return underlyingBlankNode.hashCode();
	}

	public int compareTo( Node other ) {
		if (other instanceof BlankNode) {
			if (this.equals(other)) return 0;
			else {
				// define somehow a sorting order on blank nodes
				return this.hashCode() - other.hashCode();
			}
		}
		else {
			// sort by type
			return NodeUtils.compareByType(this, other);
		}
	}

	public String toSPARQL() throws NotImplementedException {
		log.warn("Variable (Singleton) should not be used for SPARQL queries");
		throw new NotImplementedException();
	}
}
