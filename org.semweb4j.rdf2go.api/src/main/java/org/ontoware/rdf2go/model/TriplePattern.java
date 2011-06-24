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

package org.ontoware.rdf2go.model;

import java.io.Serializable;

import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.UriOrVariable;


/**
 * Triple match representation in rdf2go
 * 
 * Implementations are expected to have valid impl
 * 
 * @author mvo
 */
public interface TriplePattern extends Serializable {
	
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
	
	/**
	 * @param statement with subject, predicated and object defined.
	 * @return true if this pattern matches the given statement. Ignores
	 *         context.
	 * @since RDF2Go 4.4.2
	 */
	public boolean matches(Statement statement);
	
}
