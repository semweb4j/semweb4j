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

import java.io.IOException;
import java.util.Iterator;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;


/**
 * 
 * @author voelkel
 */
public interface ModelWriter {
	
	/**
	 * adds a statement to this model
	 * 
	 * @param statement the statement to add
	 * @throws ModelRuntimeException if there is either an error with the
	 *             underlying model or an {@link IOException}
	 */
	public void addStatement(Statement statement) throws ModelRuntimeException;
	
	/**
	 * Add all statements contained in 'other' to this model = 'union'
	 * 
	 * @param other another RDF2GO model
	 * @throws ModelRuntimeException if there is either an error with the
	 *             underlying model or an {@link IOException}
	 */
	public void addAll(Iterator<? extends Statement> other) throws ModelRuntimeException;
	
	/**
	 * adds a (subject, property ,object)-statement to this model
	 * 
	 * @param subject of the statement
	 * @param predicate of the statement
	 * @param object of the statement
	 * @throws ModelRuntimeException if there is either an error with the
	 *             underlying model or an {@link IOException}
	 */
	public void addStatement(Resource subject, URI predicate, Node object)
	        throws ModelRuntimeException;
	
	/**
	 * adds a (subject, property, liteal, language-tag)-statement to the model.
	 * This method is intended to give the user convenience and allows the
	 * underlying implementation to convert directly to native objects without
	 * converting to RDF2Go objects first.
	 * 
	 * @param subject URI or Object (= blankNode)
	 * @param predicate of the statement
	 * @param literal main part of literal value of the statement
	 * @param languageTag RDF language tag
	 * @throws ModelRuntimeException if there is either an error with the
	 *             underlying model or an {@link IOException}
	 */
	public void addStatement(Resource subject, URI predicate, String literal, String languageTag)
	        throws ModelRuntimeException;
	
	/**
	 * adds a (subject, property, literal ,datatype)-statement to the model.
	 * This method is intended to give the user convenience and allows the
	 * underlying implementation to convert directly to native objects without
	 * converting to RDF2Go objects first.
	 * 
	 * datatype normally is an URI for a XML schema datatype (XSD)
	 * 
	 * @param subject of the statement
	 * @param predicate of the statement
	 * @param literal main part of the literal of the statement
	 * @param datatypeURI for the datatype part of the literal
	 * @throws ModelRuntimeException if there is either an error with the
	 *             underlying model or an {@link IOException}
	 */
	public void addStatement(Resource subject, URI predicate, String literal, URI datatypeURI)
	        throws ModelRuntimeException;
	
	/**
	 * adds a (subject, property, literal)-statement to the model. This method
	 * is intended to give the user convenience and allows the underlying
	 * implementation to convert directly to native objects without converting
	 * to RDF2Go objects first.
	 * 
	 * @param subject of the statement
	 * @param predicate of the statement
	 * @param literal of the statement. A {@link PlainLiteral} will be created.
	 * @throws ModelRuntimeException if there is either an error with the
	 *             underlying model or an {@link IOException}
	 */
	public void addStatement(Resource subject, URI predicate, String literal)
	        throws ModelRuntimeException;
	
	/**
	 * adds a (subject, property, liteal, language-tag)-statement to the model.
	 * This method is intended to give the user convenience and allows the
	 * underlying implementation to convert directly to native objects without
	 * converting to RDF2Go objects first.
	 * 
	 * @param subjectURIString interpreted as a URI
	 * @param predicate of the statement
	 * @param literal main part of the {@link LanguageTagLiteral}
	 * @param languageTag RDF language tag of the {@link LanguageTagLiteral}
	 * @throws ModelRuntimeException if there is either an error with the
	 *             underlying model or an {@link IOException}
	 */
	public void addStatement(String subjectURIString, URI predicate, String literal,
	        String languageTag) throws ModelRuntimeException;
	
	/**
	 * adds a (subject, property, literal, data-type)-statement to the model.
	 * This method is intended to give the user convenience and allows the
	 * underlying implementation to convert directly to native objects without
	 * converting to RDF2Go objects first.
	 * 
	 * data-type normally is an URI for a XML schema datatype (XSD)
	 * 
	 * @param subjectURIString interpreted as URI of the statement
	 * @param predicate of the statement
	 * @param literal for the {@link DatatypeLiteral}
	 * @param datatypeURI for the {@link DatatypeLiteral}
	 * @throws ModelRuntimeException if there is either an error with the
	 *             underlying model or an {@link IOException}
	 */
	public void addStatement(String subjectURIString, URI predicate, String literal, URI datatypeURI)
	        throws ModelRuntimeException;
	
	/**
	 * adds a (subject, property, literal)-statement to the model. This method
	 * is intended to give the user convenience and allows the underlying
	 * implementation to convert directly to native objects without converting
	 * to RDF2Go objects first.
	 * 
	 * @param subjectURIString interpreted as URI of the statement
	 * @param predicate of the statement
	 * @param literal for the {@link PlainLiteral}
	 * @throws ModelRuntimeException if there is either an error with the
	 *             underlying model or an {@link IOException}
	 */
	public void addStatement(String subjectURIString, URI predicate, String literal)
	        throws ModelRuntimeException;
	
}
