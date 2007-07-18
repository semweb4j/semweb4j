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
		ModelRemovePatterns, QueryableModel, ModelIO {

	/**
	 * @return the context URI or null
	 */
	public URI getContextURI();

	/**
	 * Open connection to defined, unterlying implementation
	 */
	public void open();

	/**
	 * Close connection to defined, unterlying implementation. commit() is
	 * called internally.
	 */
	public void close();

	/**
	 * @return true, if model has been opened and not yet closed.
	 */
	public boolean isOpen();

	/**
	 * The number of explicit statements. Statements that are inferred using
	 * backward-chaining are per definition not in this number, and also some
	 * inferred stateemtns may or may be not part of ths size, depending on the
	 * implementation of the model. If a new triple is added to the model, the
	 * size must increase.
	 * 
	 * @return the number of statements in the model
	 * @throws ModelRuntimeException
	 */
	public long size() throws ModelRuntimeException;

	// //////////////
	// Manipulate underlying impl

	/**
	 * @return the native implementation (e.g. a Jena Model). Using this method
	 *         breaks strict triple store independence, but exposes the full
	 *         power and <b>reduces</b> API dependence. This method is part of
	 *         the main API.
	 */
	public Object getUnderlyingModelImplementation();

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
	public void setProperty(URI propertyURI, Object value);

	/**
	 * @param propertyURI
	 * @return stored runtime property value or null
	 */
	public Object getProperty(URI propertyURI);

	/**
	 * Dumps the whole content of the model via System.out
	 */
	public void dump();

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
	public boolean isIsomorphicWith(Model other);

}
