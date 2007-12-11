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

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * Can remove statements and apply diffs in one atomic operation.
 * @author voelkel
 */
public interface ModelAddRemove extends ClosableIterable<Statement>, ModelWriter, Lockable {

	//////////////
	// diffing
	
	/**
	 * Apply the changes given by this diff <emph>in one atomic operation</emph>
	 * 
	 * Implementations must check that all statements to be removed are still in
	 * the Model. Otherwise an exception is thrown.
	 * 
	 * First all triples to be removed are removed, then triples to be added are added.
	 * 
	 * @param diff
	 * @throws ModelRuntimeException
	 */
	void update(DiffReader diff) throws ModelRuntimeException;

	/**
	 * @param statements
	 * @return a Diff between this model and the statements given in the iterator
	 * @throws ModelRuntimeException
	 */
	Diff getDiff(Iterator<? extends Statement> statements) throws ModelRuntimeException;

	
	
	/**
	 * Removes all statements from this model.
	 * 
	 * @throws ModelRuntimeException
	 */
	void removeAll() throws ModelRuntimeException;

	/**
	 * Removes all statements contained in 'other' from this model =
	 * 'difference'
	 * 
	 * @param other
	 *            another RDF2GO model
	 * @throws ModelRuntimeException
	 */
	void removeAll(Iterator<? extends Statement> statements) throws ModelRuntimeException;

	///////////////////
	// remove

	/**
	 * remove a (subject, property ,object)-statement from the model
	 * 
	 * @param subject
	 *            URI or Object (= blankNode)
	 * @param predicate
	 * @param object
	 *            URI or String (=plainLiteral) or BlankNode (=blankNode) or
	 *            TypedLiteral or LanguageTagLiteral
	 * @throws ModelRuntimeException
	 */
	void removeStatement(Resource subject, URI predicate, Node object) throws ModelRuntimeException;

	
	
	void removeStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException;

	/**
	 * remove a (subject, property ,literal, language tag)-statement from the
	 * model
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param languageTag
	 * @throws ModelRuntimeException
	 */
	void removeStatement(Resource subject, URI predicate, String literal, String languageTag)
			throws ModelRuntimeException;

	/**
	 * remove a (subject, property ,literal, datatype)-statement from the model
	 * 
	 * datatype often is an uri for a xml schema datatype (xsd)
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param datatypeURI
	 * @throws ModelRuntimeException
	 */
	void removeStatement(Resource subject, URI predicate, String literal, URI datatypeURI)
			throws ModelRuntimeException;

	/**
	 * remove a rdf2go-statement from the model
	 * 
	 * @param statement
	 * @throws ModelRuntimeException
	 */
	void removeStatement(Statement statement) throws ModelRuntimeException;

	void removeStatement(String subjectURIString, URI predicate, String literal)
			throws ModelRuntimeException;

	/**
	 * remove a (subject, property ,literal, language tag)-statement from the
	 * model
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param languageTag
	 * @throws ModelRuntimeException
	 */
	void removeStatement(String subjectURIString, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException;

	/**
	 * remove a (subject, property ,literal, datatype)-statement from the model
	 * 
	 * datatype often is an uri for a xml schema datatype (xsd)
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param datatypeURI
	 * @throws ModelRuntimeException
	 */
	void removeStatement(String subjectURIString, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException;

}