/**
 * LICENSE INFORMATION
 *
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2010
 *
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model.node.impl;

import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;

public class LanguageTagLiteralImpl extends LiteralImpl implements
		LanguageTagLiteral {

	/**
     * 
     */
    private static final long serialVersionUID = 2183810366150271250L;

	private String languageTag;

	private String literalValue;

	public LanguageTagLiteralImpl(String string, String languageTag) {
		assert languageTag != null;
		assert string != null;
		this.languageTag = languageTag.toLowerCase();
		this.literalValue = string;
	}

	/**
	 * @param turtleEncoded
	 *            Syntax &lt;literalValue&gt; '@' &lt;languageTag&gt;
	 */
	public LanguageTagLiteralImpl(String turtleEncoded) {
		assert turtleEncoded.contains("@");
		int i = turtleEncoded.lastIndexOf('@');
		this.literalValue = turtleEncoded.substring(0, i).toLowerCase();
		this.languageTag = turtleEncoded.substring(i + 1);
	}

	@Override
	public String getValue() {
		return this.literalValue;
	}

	@Override
    public String getLanguageTag() {
		return this.languageTag;
	}

	@Override
	public boolean equals(Object o) {
		return ((o instanceof LanguageTagLiteralImpl)
				&& (this.getValue().equals(((LanguageTagLiteralImpl) o)
						.getValue())) && (this.getLanguageTag()
				.equals(((LanguageTagLiteralImpl) o).getLanguageTag())));
	}

	@Override
	public String toString() {
		return getValue() + "@" + getLanguageTag();
	}

	@Override
    public DatatypeLiteral asDatatypeLiteral() throws ClassCastException {
		throw new ClassCastException(
				"Cannot call this on a language tagged literal");
	}

	@Override
    public LanguageTagLiteral asLanguageTagLiteral() throws ClassCastException {
		return this;
	}

	@Override
	public int hashCode() {
		return this.languageTag.hashCode() + this.literalValue.hashCode();
	}

	@Override
    public int compareTo(Node other) {
		if (other instanceof LanguageTagLiteral) {
			LanguageTagLiteral oLit = (LanguageTagLiteral) other;

			int diff = this.getValue().compareTo(oLit.getValue());
			if (diff != 0)
				return diff;
			//else
			return this.getLanguageTag().compareTo(oLit.getLanguageTag());
		}
			// else sort by type
		return NodeUtils.compareByType(this, other);
	}

	@Override
    public String toSPARQL() {
		return "'''" + sparqlEncode(this.literalValue) + "'''@"
				+ this.languageTag;
	}

}
