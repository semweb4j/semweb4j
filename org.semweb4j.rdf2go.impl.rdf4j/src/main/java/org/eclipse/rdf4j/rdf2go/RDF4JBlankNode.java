/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2007.
 * 
 * Licensed under the Aduna BSD-style license.
 */
package org.eclipse.rdf4j.rdf2go;

import org.ontoware.rdf2go.model.node.impl.AbstractBlankNodeImpl;
import org.eclipse.rdf4j.model.BNode;


/**
 * 
 * @author voelkel
 */
public class RDF4JBlankNode extends AbstractBlankNodeImpl {
	
	private static final long serialVersionUID = 5148665108422546165L;
	
	/**
	 * @param underlyingBlankNode from OpenRDF
	 */
	public RDF4JBlankNode(BNode underlyingBlankNode) {
		super(underlyingBlankNode);
	}
	
	@Override
	public String getInternalID() {
		return ((BNode)getUnderlyingBlankNode()).getID();
	}
	
}
