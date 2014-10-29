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

package org.ontoware.rdf2go;

import java.util.Properties;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.ReasoningNotSupportedException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.node.URI;


/**
 * RDF2Go adapters have to implement this interface to be able to create Models
 * and ModelSets.
 * 
 * Property keys are defined in this interface (Reasoning, Storage).
 * 
 * @author voelkel
 * 
 */
public interface ModelFactory {
	
	/**
	 * The Property key to indicate the Reasoning state in the properties. Legal
	 * values are:
	 * <ul>
	 * <li>NONE (default)
	 * <li>RDFS
	 * <li>OWL
	 * </ul>
	 */
	static final String REASONING = "Reasoning";
	
	/**
	 * The Property key to indicate where to store the model or modelset. Legal
	 * values are:
	 * <ul>
	 * <li>MEMORY (default, stores in-memory)
	 * <li>any absolute or relative path, in Java syntax ("/")
	 * </ul>
	 */
	static final String STORAGE = "Storage";
	
	static final String STORAGE_VALUE_MEMORY = "MEMORY";
	
	// ////////////////////////////////
	// Model
	
	/**
	 * Create a default in-memory ModelSet with no inferencing.
	 * 
	 * @return a Model implementation.
	 * @throws ModelRuntimeException if the adapter could not create the model
	 */
	Model createModel() throws ModelRuntimeException;
	
	/**
	 * Create a default in-memory ModelSet with no inferencing and the given
	 * context URI. All statements added to this model will have this context as
	 * the context.
	 * 
	 * @return a Model implementation bound to the given context URI.
	 * @throws ModelRuntimeException if the adapter could not create the model
	 */
	Model createModel(URI contextURI) throws ModelRuntimeException;
	
	/**
	 * Create a new Model with inferencing. Type of reasoning is passed.
	 * 
	 * @param reasoning the type of reasoning that is needed
	 * @return a model with reasoning support
	 * @throws ModelRuntimeException if the adapter could not create the Model
	 * @throws ReasoningNotSupportedException if the passed kind of reasoning is
	 *             not supported.
	 */
	Model createModel(Reasoning reasoning) throws ReasoningNotSupportedException,
	        ModelRuntimeException;
	
	/**
	 * Create a Model configured by the given properties. You have to look at
	 * the adapter documentation to see which properties do what.
	 * 
	 * @param properties configures the to-be-created Model
	 * @return the created Model
	 * @throws ModelRuntimeException if the adapter could not create the Model
	 * @throws ReasoningNotSupportedException if the passed kind of reasoning is
	 *             not supported.
	 */
	Model createModel(Properties properties) throws ModelRuntimeException;
	
	// ///////////////////////////
	// ModelSet
	
	/**
	 * create a default in-memory ModelSet with no inferencing.
	 * 
	 * @return a ModelSet implementation.
	 * @throws ModelRuntimeException
	 */
	ModelSet createModelSet() throws ModelRuntimeException;
	
	/**
	 * Create a default in-memory ModelSet with given inferencing. Type of
	 * reasoning is passed.
	 * 
	 * @param reasoning the type of reasoning that is needed
	 * @return the created ModelSet
	 * @throws ModelRuntimeException
	 * @throws ReasoningNotSupportedException if the passed kind of reasoning is
	 *             not supported.
	 * @throws ModelRuntimeException if the adapter could not create the model
	 */
	ModelSet createModelSet(Reasoning reasoning) throws ReasoningNotSupportedException,
	        ModelRuntimeException;
	
	/**
	 * Create a ModelSet configured by the given properties. You have to look at
	 * the adapter documentation to see which properties do what.
	 * 
	 * @param properties - configures the model creation
	 * @return the created ModelSet
	 * @throws ModelRuntimeException if the adapter could not create the Model
	 * @throws ReasoningNotSupportedException if the passed kind of reasoning is
	 *             not supported.
	 */
	ModelSet createModelSet(Properties p) throws ModelRuntimeException;
	
	/**
	 * @param url SPARQL endpoint URL, never null
	 * @param query SPARQL query, never null
	 * @return the result of executing the given SPARQL query against the remote
	 *         URL.
	 * @throws UnsupportedOperationException if this underlying implemention
	 *             does not support asking remote SPARQL endpoiints.
	 */
	QueryResultTable sparqlSelect(String url, String query);
	
}
