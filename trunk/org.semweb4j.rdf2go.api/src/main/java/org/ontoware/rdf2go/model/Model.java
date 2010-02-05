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
import org.ontoware.rdf2go.model.node.URI;

/**
 * Defines an RDF Model, and the functions to add and delete statements of all
 * possible kinds to this model. Query functions are also provided.
 * 
 * IMPROVE: add support for union, intersection
 * 
 * @author Max Voelkel <dev@xam.de>
 * @author Benjamin Heitmann <benjamin@lauschangriff.org>
 * @author Leo Sauermann
 */
public interface Model extends ModelValueFactory, ModelAddRemove,
		ModelRemovePatterns, QueryableModel, ModelIO, Commitable,
		ReificationSupport, NamespaceSupport {

	/**
	 * @return the context URI or null
	 */
	URI getContextURI();

	/**
	 * Open connection to defined, underlying implementation
	 * @return the model itself (now open) ready for further operations
	 */
	Model open();

	/**
	 * Close connection to defined, underlying implementation. commit() is
	 * called internally.
	 * 
	 * @return the just opened Model
	 */
	void close();

	/**
	 * @return true, if model has been opened and not yet closed.
	 */
	boolean isOpen();

	/**
	 * For plain models without any inference, this is the exact number of
	 * explicit statements.
	 * 
	 * For models with inference, this number may be anything between the number
	 * of explicit statements up to the number of all inferrable statements plus
	 * the number of explicit statements.
	 * 
	 * For both cases: If one new triple is added to the model, the size must
	 * increase at least one.
	 * 
	 * @return the (imprecise?) number of statements in the model (see comment)
	 * @throws ModelRuntimeException
	 */
	long size() throws ModelRuntimeException;

	/**
	 * @return true if the model is empty, i.e. contains no statements. This is
	 *         the same as size() == 0, but might be faster.
	 */
	boolean isEmpty();

	/**
	 * Adds a complete {@link Model} to this Model.
	 * The context URI of the other model is ignored, if present.
	 * 
	 * Note: this might be faster than addAll( model.iterator() );
	 * 
	 * @param model
	 * @throws ModelRuntimeException
	 *             if any internal (I/O related) exception occurs
	 * @since 4.6
	 */
	void addModel(Model model) throws ModelRuntimeException;

	
	// //////////////
	// Manipulate underlying impl

	/**
	 * @return the native implementation (e.g. a Jena Model). Using this method
	 *         breaks strict triple store independence, but exposes the full
	 *         power and <b>reduces</b> API dependence. This method is part of
	 *         the main API.
	 */
	Object getUnderlyingModelImplementation();

	// ///////////////////
	// eases integration of RDF2Go models in many settings

	/**
	 * Add an arbitrary runtime property, this will not be persisted and is only
	 * available at runtime. This allows Model to serve as a central data model
	 * in larger applications (like SemVersion.ontoware.org)
	 * 
	 * @param propertyURI
	 * @param value
	 */
	void setProperty(URI propertyURI, Object value);

	/**
	 * @param propertyURI
	 * @return stored runtime property value or null
	 */
	Object getProperty(URI propertyURI);

	/**
	 * Dumps the whole content of the model via System.out
	 */
	void dump();

	/**
	 * Two models can be equal even if they do not contain the same statements.
	 * We owe this problem mainly to blank nodes.
	 * 
	 * See http://www.w3.org/TR/2002/WD-rdf-concepts-20020829/#xtocid103648 for
	 * the official definition of graph equality (which is based on graph
	 * isomorphism). See also http://www.w3.org/TR/rdf-mt/.
	 * 
	 * @param other
	 * @return true if the two models are isomorphic as defined in
	 *         http://www.w3.org/TR/rdf-mt/
	 */
	boolean isIsomorphicWith(Model other);

}
