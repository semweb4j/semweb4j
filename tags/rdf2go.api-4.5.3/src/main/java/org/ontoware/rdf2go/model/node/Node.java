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
 * Marker interface for RDF types (URI, BlankNode, Literal)
 * 
 * The following sorting order is defined by different Node types: URI >
 * BlankNode > PlainLiteral > LanguageTaggedLiteral > DatatypedLiteral
 * 
 * Nodes are expected to have correct implementations of equals(Object other)
 * and hashcode()
 * 
 * @author voelkel
 * 
 */
public interface Node extends NodeOrVariable, Comparable<Node> {

	/**
	 * A convenience method for a cast to
	 * org.ontoware.rdf2go.model.node.Resource
	 * 
	 * @return this Node casted as a Resource
	 * @throws ClassCastException
	 *             if the node is not a Resource
	 */
	public Resource asResource() throws ClassCastException;

	/**
	 * A convenience method for a cast to org.ontoware.rdf2go.model.node.Literal
	 * 
	 * @return this Node casted as a Literal
	 * @throws ClassCastException
	 *             if the node is not a Literal
	 */
	public Literal asLiteral() throws ClassCastException;;

	/**
	 * A convenience method for a cast to
	 * org.ontoware.rdf2go.model.node.DatatypeLiteral
	 * 
	 * @return this Node casted as a DatatypeLiteral
	 * @throws ClassCastException
	 *             if the node is not a DatatypeLiteral
	 */
	public DatatypeLiteral asDatatypeLiteral() throws ClassCastException;;

	/**
	 * A convenience method for a cast to
	 * org.ontoware.rdf2go.model.node.LanguageTagLiteral
	 * 
	 * @return this Node casted as a LanguageTagLiteral
	 * @throws ClassCastException
	 *             if the node is not a LanguageTagLiteral
	 */
	public LanguageTagLiteral asLanguageTagLiteral() throws ClassCastException;;

	/**
	 * A convenience method for a cast to org.ontoware.rdf2go.model.node.URI
	 * 
	 * @return this Node casted as a URI
	 * @throws ClassCastException
	 *             if the node is not a URI
	 */
	public URI asURI() throws ClassCastException;

	/**
	 * A convenience method for a cast to
	 * org.ontoware.rdf2go.model.node.BlankNode
	 * 
	 * @return this Node casted as a BlankNode
	 * @throws ClassCastException
	 *             if the node is not a BlankNode
	 */
	public BlankNode asBlankNode() throws ClassCastException;

	/**
	 * A convenience function to create SPARQL queries from nodes
	 * 
	 * @return this node in SPARQL syntax. A URI is wrapped in '&lt;' and
	 *         '&gt;'. A literal is wrapped in '"' chars and escaped.
	 */
	public String toSPARQL();

}
