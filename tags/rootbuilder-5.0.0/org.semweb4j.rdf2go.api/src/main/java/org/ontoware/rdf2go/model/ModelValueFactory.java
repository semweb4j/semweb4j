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

package org.ontoware.rdf2go.model;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * Factory-like parts of an RDF2Go-Model
 * 
 * @author voelkel
 * 
 */
public interface ModelValueFactory {

	/**
	 * Create (but do not add) a new blank node
	 * @return a new blank node
	 */
	BlankNode createBlankNode();

	/**
	 * Create a new blank node with the given internal ID. The id should be one
	 *         returned from BlankNode.getInternalID().
	 * @param internalID
	 * @return a BlankNode with the given internal ID. 
	 * @throws UnsupportedOperationException
	 *             if the underlying store cannot create BlankNodes from IDs.
	 * @throws IllegalArgumentException
	 *             if the internalID could not be used
	 */
	BlankNode createBlankNode(String internalID);

	/**
	 * The model must create URIs it would accept itself.
	 * 
	 * @return a new URI from the given String
	 * @throws IllegalArgumentException,
	 *             e.g. if URI is invalid
	 */
	URI createURI(String uriString) throws IllegalArgumentException;

	/**
	 * CHecks URI for syntax errors.
	 * @param uriString
	 * @return true if the URI is valid for the given implementation
	 */
	boolean isValidURI(String uriString);

	/**
	 * Create a new plain literal
	 * @param literal
	 * @return a PlainLiteral
	 */
	PlainLiteral createPlainLiteral(String literal);

	/**
	 * @param literal
	 * @param langugeTag
	 * @return a LanguageTagLiteral
	 * @throws ModelRuntimeException
	 *             e.g. if the language tag is malformed
	 */
	LanguageTagLiteral createLanguageTagLiteral(String literal,
			String langugeTag) throws ModelRuntimeException;

	/**
	 * @param literal
	 * @param datatypeURI
	 * @return a DatatypeLiteral
	 * @throws ModelRuntimeException
	 *             e.g. if the datatype URI causes problems
	 */
	DatatypeLiteral createDatatypeLiteral(String literal, URI datatypeURI)
			throws ModelRuntimeException;

	/**
	 * Create a new statement - but DOES NOT add it to the model
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return a Statement
	 */
	Statement createStatement(Resource subject, URI predicate, Node object);

	/**
	 * Implementations are free to choose if their semantics are unique within
	 * the this model, the ModelSet, or unique in the universe
	 * 
	 * @return a new, unique URI
	 */
	URI newRandomUniqueURI();

}
