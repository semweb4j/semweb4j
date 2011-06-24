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
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;


/**
 * The rdf2go DatatypLiteral implementation
 * 
 * @author mvo
 * 
 */
public class DatatypeLiteralImpl extends LiteralImpl implements DatatypeLiteral {
	
	private static final long serialVersionUID = -356372147459786438L;
	
	// imagine them as final
	private URI datatype;
	
	private String value;
	
	/**
	 * constructs a Datatype Literal
	 * 
	 * @param value The datatype value
	 * @param datatype The xsd URI
	 */
	public DatatypeLiteralImpl(String value, URI datatype) {
		assert value != null;
		assert datatype != null;
		
		this.datatype = datatype;
		this.value = value;
	}
	
	/**
	 * @param turtleEncoded Syntax &lt;literalValue&gt; '@' &lt;languageTag&gt;
	 */
	public DatatypeLiteralImpl(String turtleEncoded) {
		assert turtleEncoded.contains("^^");
		int i = turtleEncoded.lastIndexOf("^^");
		this.value = turtleEncoded.substring(0, i);
		this.datatype = new URIImpl(turtleEncoded.substring(i + 2));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.DatatypeLiteral#getDatatype()
	 */
	public URI getDatatype() {
		return this.datatype;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.RDFLiteral#getValue()
	 */
	@Override
	public String getValue() {
		return this.value;
	}
	
	@Override
	public boolean equals(Object o) {
		return ((o instanceof DatatypeLiteralImpl)
		        && (this.getValue().equals(((DatatypeLiteralImpl)o).getValue())) && (this
		        .getDatatype().equals(((DatatypeLiteralImpl)o).getDatatype())));
	}
	
	@Override
	public String toString() {
		return getValue() + "^^" + getDatatype();
	}
	
	public int asInt() throws ClassCastException, NumberFormatException {
		if(getDatatype().equals(XSD._int) || getDatatype().equals(XSD._nonPositiveInteger)
		        || getDatatype().equals(XSD._long) || getDatatype().equals(XSD._nonNegativeInteger)
		        || getDatatype().equals(XSD._negativeInteger) || getDatatype().equals(XSD._integer)
		        || getDatatype().equals(XSD._short) || getDatatype().equals(XSD._byte)
		        || getDatatype().equals(XSD._unsignedLong)
		        || getDatatype().equals(XSD._unsignedByte)
		        || getDatatype().equals(XSD._unsignedInt)
		        || getDatatype().equals(XSD._unsignedShort)
		        || getDatatype().equals(XSD._positiveInteger)) {
			return Integer.parseInt(getValue());
		}
		// else
		throw new ClassCastException("Datatype is " + getDatatype()
		        + " which is not derrived from xsd:integer");
	}
	
	public boolean asBoolean() throws ClassCastException {
		return Boolean.parseBoolean(getValue());
	}
	
	public DatatypeLiteral asDatatypeLiteral() throws ClassCastException {
		return this;
	}
	
	public LanguageTagLiteral asLanguageTagLiteral() throws ClassCastException {
		throw new ClassCastException("Cannot call this on a DatatypeLiteral");
	}
	
	@Override
	public int hashCode() {
		return this.datatype.hashCode() + this.value.hashCode();
	}
	
	public int compareTo(Node other) {
		if(other instanceof DatatypeLiteral) {
			DatatypeLiteral oLit = (DatatypeLiteral)other;
			
			int diff = this.getValue().compareTo(oLit.getValue());
			if(diff != 0)
				return diff;
			// else
			return this.getDatatype().compareTo(oLit.getDatatype());
		}
		// else sort by type
		return NodeUtils.compareByType(this, other);
	}
	
	public String toSPARQL() {
		return "'''" + sparqlEncode(this.value) + "'''^^<" + this.datatype + ">";
	}
	
}
