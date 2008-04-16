/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.rdf2go.model.node.impl.AbstractBlankNodeImpl;

import org.openrdf.model.BNode;


/**
 *
 * @author voelkel
 */
public class OpenrdfBlankNode extends AbstractBlankNodeImpl {

	/**
	 * @param underlyingBlankNode
	 */
	public OpenrdfBlankNode(BNode underlyingBlankNode) {
		super(underlyingBlankNode);
	}

	@Override
	public String getInternalID() {
		return ((BNode) getUnderlyingBlankNode()).getID();
	}

}
