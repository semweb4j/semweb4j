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
	 * @return a new blank node
	 */
	public BlankNode createBlankNode();

	/**
	 * The model must create URIs it would accept itself.
	 * 
	 * @return a new URI from the given String
	 * @throws ModelRuntimeException,
	 *             e.g. if URI is invalid
	 */
	public URI createURI(String uriString) throws ModelRuntimeException;

	/**
	 * @param uriString
	 * @return true if the URI is valid for the given implementation
	 */
	public boolean isValidURI(String uriString);

	/**
	 * @param literal
	 * @return a PlainLiteral
	 */
	public PlainLiteral createPlainLiteral(String literal);

	/**
	 * @param literal
	 * @param langugeTag
	 * @return a LanguageTagLiteral
	 * @throws ModelRuntimeException e.g. if the language tag is malformed
	 */
	public LanguageTagLiteral createLanguageTagLiteral(String literal, String langugeTag)
			throws ModelRuntimeException;

	/**
	 * @param literal
	 * @param datatypeURI
	 * @return a DatatypeLiteral
	 * @throws ModelRuntimeException e.g. if the datatype URI causes problems
	 */
	public DatatypeLiteral createDatatypeLiteral(String literal, URI datatypeURI)
			throws ModelRuntimeException;

	/**
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return a Statement
	 */
	public Statement createStatement(Resource subject, URI predicate, Node object);

	/**
	 * Implementations are free to choose if their semantics are unique within
	 * the this model, the modelset, or unique in the universe
	 * 
	 * @return a new, unique URI
	 */
	public URI newRandomUniqueURI();

}
