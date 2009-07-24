/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go.model.node.impl;

import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;

public class NodeUtils {

	/**
	 * The following sorting order is defined by different Node types: URI >
	 * BlankNode > PlainLiteral > LnaguageTaggedLiteral > DatatypedLiteral
	 */
	public static int compareByType(Node a, Node b) {
		if (a instanceof URI) {
			if (b instanceof URI)
				return 0;
			return 1;
		} else if (a instanceof BlankNode) {
			if (b instanceof URI)
				return -1;
			if (b instanceof BlankNode)
				return 0;
			return 1;

		} else if (a instanceof PlainLiteral) {
			if (b instanceof URI || b instanceof BlankNode)
				return -1;
			if (b instanceof PlainLiteral)
				return 0;
			return 1;
		} else if (a instanceof LanguageTagLiteral) {
			if (b instanceof URI || b instanceof BlankNode || b instanceof PlainLiteral)
				return -1;
			if (b instanceof LanguageTagLiteral)
				return 0;
			return 1;
		} else if (a instanceof DatatypeLiteral) {
			if (b instanceof URI || b instanceof BlankNode || b instanceof PlainLiteral || b instanceof LanguageTagLiteral)
				return -1;
			if (b instanceof DatatypeLiteral)
				return 0;
			throw new IllegalArgumentException();
		} else
			throw new IllegalArgumentException();

	}
}
