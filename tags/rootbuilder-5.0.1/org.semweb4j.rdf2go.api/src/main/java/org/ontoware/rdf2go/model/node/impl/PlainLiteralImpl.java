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

import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;


public class PlainLiteralImpl extends LiteralImpl implements PlainLiteral {
	
	/**
     * 
     */
	private static final long serialVersionUID = -9217019607745519710L;
	private String value;
	
	public PlainLiteralImpl(String value) {
		this.value = value;
	}
	
	@Override
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
	@Override
    public DatatypeLiteral asDatatypeLiteral() throws ClassCastException {
		throw new ClassCastException("Cannot call this on a plain literal");
	}
	
	@Override
    public LanguageTagLiteral asLanguageTagLiteral() throws ClassCastException {
		throw new ClassCastException("Cannot call this on a plain literal");
	}
	
	/* Can be equal to other {@link PlainLiteralImpl} and {@link String} */
	@Override
	public boolean equals(Object other) {
		if(other instanceof PlainLiteral) {
			return ((PlainLiteral)other).getValue().equals(this.value);
		} else if(other instanceof String) {
			return this.value.equals(other);
		} else
			return false;
	}
	
	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
	
	@Override
    public int compareTo(Node other) {
		if(other instanceof PlainLiteral) {
			return this.value.compareTo(((PlainLiteral)other).getValue());
		}
		// else sort by type
		return NodeUtils.compareByType(this, other);
	}
	
	@Override
    public String toSPARQL() {
		return "'''" + sparqlEncode(this.value) + "'''";
	}
	
}
