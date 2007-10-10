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

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * 
 * @author voelkel
 */
public interface ModelWriter  {

    /**
     * adds a statement to this model
     * 
     * @param statement the statement to add
     * @throws ModelRuntimeException
     */
	public void addStatement(Statement statement) throws ModelRuntimeException;

	/**
	 * Add all statements contained in 'other' to this model = 'union'
	 * 
	 * @param other
	 *            another RDF2GO model
	 * @throws ModelRuntimeException
	 */
	public void addAll(Iterator<Statement> other) throws ModelRuntimeException;

	/**
	 * adds a (subject, property ,object)-statement to this model
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @throws ModelRuntimeException
	 */
	public void addStatement(Resource subject, URI predicate, Node object) throws ModelRuntimeException;

	/**
	 * adds a (subject, property, liteal, language-tag)-statement to the model.
	 * This method is intended to give the user convenience and allows the
	 * underlying implementation to convert directly to native objects without
	 * converting to RDF2Go objects first.
	 * 
	 * @param subject
	 *            URI or Object (= blankNode)
	 * @param predicate
	 * @param literal
	 * @param languageTag
	 *            RDF language tag
	 * @throws ModelRuntimeException
	 */
	public void addStatement(Resource subject, URI predicate, String literal, String languageTag)
			throws ModelRuntimeException;

	/**
	 * adds a (subject, property, literal ,datatype)-statement to the model.
	 * This method is intended to give the user convenience and allows the
	 * underlying implementation to convert directly to native objects without
	 * converting to RDF2Go objects first.
	 * 
	 * datatype normaly is an uri for a xml schema datatype (xsd)
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param datatypeURI
	 * @throws ModelRuntimeException
	 */
	public void addStatement(Resource subject, URI predicate, String literal, URI datatypeURI)
			throws ModelRuntimeException;

	/**
	 * adds a (subject, property, literal)-statement to the model. This method
	 * is intended to give the user convenience and allows the underlying
	 * implementation to convert directly to native objects without converting
	 * to RDF2Go objects first.
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @throws ModelRuntimeException
	 */
	public void addStatement(Resource subject, URI predicate, String literal) throws ModelRuntimeException;

	/**
	 * adds a (subject, property, liteal, language-tag)-statement to the model.
	 * This method is intended to give the user convenience and allows the
	 * underlying implementation to convert directly to native objects without
	 * converting to RDF2Go objects first.
	 * 
	 * @param subject -
	 *            interpretded as a URI
	 * @param predicate
	 * @param literal
	 * @param languageTag
	 *            RDF language tag
	 * @throws ModelRuntimeException
	 */
	public void addStatement(String subjectURIString, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException;

	/**
	 * adds a (subject, property, literal ,datatype)-statement to the model.
	 * This method is intended to give the user convenience and allows the
	 * underlying implementation to convert directly to native objects without
	 * converting to RDF2Go objects first.
	 * 
	 * datatype normaly is an uri for a xml schema datatype (xsd)
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param datatypeURI
	 * @throws ModelRuntimeException
	 */
	public void addStatement(String subjectURIString, URI predicate, String literal, URI datatypeURI)
			throws ModelRuntimeException;

	/**
	 * adds a (subject, property, literal)-statement to the model. This method
	 * is intended to give the user convenience and allows the underlying
	 * implementation to convert directly to native objects without converting
	 * to RDF2Go objects first.
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @throws ModelRuntimeException
	 */
	public void addStatement(String subjectURIString, URI predicate, String literal)
			throws ModelRuntimeException;

}