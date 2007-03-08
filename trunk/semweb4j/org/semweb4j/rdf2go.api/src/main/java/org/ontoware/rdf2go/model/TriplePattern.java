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
package org.ontoware.rdf2go.model;

import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.UriOrVariable;

/**
 * Triple match representation in rdf2go
 * 
 * Implementations are expected to have valid implem
 * 
 * @author mvo
 */
public interface TriplePattern {

	/**
	 * 
	 * @return URI or BlankNode
	 */
	public ResourceOrVariable getSubject();

	/**
	 * @return The URI representing the predicate (property)
	 */
	public UriOrVariable getPredicate();

	/**
	 * @return URI, String, TypedLiteral, LanguageTaggedLiteral or BlankNode
	 */
	public NodeOrVariable getObject();

}
