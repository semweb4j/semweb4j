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
package org.ontoware.rdf2go.model.node;


/**
 * This variable models wild-cards in triple pattern queries. Only onw instance of
 * this class if allows: ANY. This allows type-safe methods.
 * 
 * 
 * 
 * @author voelkel
 */
public class Variable implements ResourceOrVariable, UriOrVariable,
		NodeOrVariable {

	private Variable() {
	}

	public static final Variable ANY = new Variable();

	public Resource asResource() throws ClassCastException {
		throw new ClassCastException("A Variable cannot be seen as this");
	}

	public Literal asLiteral() throws ClassCastException {
		throw new ClassCastException("A Variable cannot be seen as this");
	}

	public DatatypeLiteral asDatatypeLiteral() throws ClassCastException {
		throw new ClassCastException("A Variable cannot be seen as this");
	}

	public LanguageTagLiteral asLanguageTagLiteral() throws ClassCastException {
		throw new ClassCastException("A Variable cannot be seen as this");
	}

	public URI asURI() throws ClassCastException {
		throw new ClassCastException("A Variable cannot be seen as this");
	}

	public BlankNode asBlankNode() throws ClassCastException {
		throw new ClassCastException("A Variable cannot be seen as this");
	}

	@Override
	public int hashCode() {
		return 0;
	}

	/**
	 * There is only one Variable, so it's equals to itself.
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Variable)
			return true;
		else
			return false;
	}

	public int compareTo(Node other) {
		if (other instanceof Variable) {
			return this.hashCode() - ((Variable) other).hashCode();
		} else {
			return 1;
		}
	}

	public String toSPARQL() {
		throw new RuntimeException(
				"Variable (Singleton) can not be used for SPARQL queries");
	}
	
	/* Always returns '*' */
	@Override
	public String toString() {
		return "*";
	}

}
