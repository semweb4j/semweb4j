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

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * A ModelSet is like a Graph Data Set in SPARQL. It contains a number of named
 * graphs (Models), which might be empty. Additionally, one special unnamed
 * model, the 'Default Model' belongs to a ModelSet.
 * 
 * ModelSet offers a number of methods that make it behave like a quad store,
 * however, when e.g. removing all statements with a certain context, the model
 * named with that context still remains in the ModelSet.
 * 
 * <p>
 * <code>ModelSet</code> can be read from a named graph aware serialization
 * such as TRIG or TRIX using the read/write methods. If you read a
 * serialization that supports only one graph (like RDF/XML), a default model
 * will be created.
 * </p>
 * 
 * The context URI of the default modell is 'null'.
 * 
 * @author max
 * @author sauermann
 */
public interface ModelSet extends Sparqlable, ModelSetIO, FindableModelSet,
		ModelSetAddRemove, ModelValueFactory {

	/**
	 * Open connection to defined, unterlying implementation
	 */
	void open();

	/**
	 * @return true if ModelSet is open
	 */
	boolean isOpen();

	/**
	 * Close connection to defined, unterlying implementation
	 */
	void close();

	/**
	 * The number of explicit statements. Statements that are inferred e.g.
	 * using backward-chaining are per definition not in this number, and also
	 * some inferred stateemtns may or may be not part of ths size, depending on
	 * the implementation of the model. If a new triple is added to the model,
	 * the size must increase.
	 * 
	 * @return the number of statements in the modelset.
	 * @throws ModelRuntimeException
	 */
	long size() throws ModelRuntimeException;

	/**
	 * @return true if all models in this ModelSet contain zero statements. This
	 *         method might be faster than calling size() == 0 on each model.
	 *         Note: This method can return isEmpty == true, even if the
	 *         ModelSet contains a number of models.
	 */
	boolean isEmpty();

	/**
	 * Creates an RDF2Go URI. This allows a user to create a model via
	 * 'getModel( URI )'.
	 * 
	 * @param uriString
	 * @return an RDF2Go URI
	 * @throws ModelRuntimeException
	 *             if URI has not a valid URI fomat - according to the adapter
	 */
	URI createURI(String uriString) throws ModelRuntimeException;

	/**
	 * Creates a statement with a context URI.
	 * 
	 * @param context
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 */
	Statement createStatement(URI context, Resource subject, URI predicate,
			Node object);

	/**
	 * Get the Model with the passed URI. If the model does not exist yet, an
	 * empty model will be created and returned.
	 * 
	 * Note that the returned model is tied to this modelset, and any changes in
	 * the model will be reflected here.
	 * 
	 * @param contextURI
	 *            the URI of the context. This is the same as the name of the
	 *            named graph.
	 * @return the model identified by this context. May have a size of 0, if no
	 *         data was added to it yet. Never returns null.  It has to be isOpen() == true.
	 */
	Model getModel(URI contextURI);

	/**
	 * Removes the Model (NamedGraph) denoted by contextURI from this modelset.
	 * If the model contains statements, they are deleted.
	 * 
	 * @param contextURI
	 * @return true if successful
	 */
	boolean removeModel(URI contextURI);

	/**
	 * Adds a model to this ModelSet. Creating the named-graph if needed, adding
	 * the triples to it if not.
	 * 
	 * This method might be much quicker than addAll(model.iterator()) depending
	 * on the implementation.
	 * 
	 * @param model
	 * @return true if successful
	 */
	boolean addModel(Model model);

	/**
	 * @param contextURI
	 * @return true if a Model (NamedGraph) named 'contextURI' is known. The
	 *         model might be empty. Some implementations don't manage models
	 *         explicitly, here empty models cna never exist.
	 */
	boolean containsModel(URI contextURI);

	/**
	 * Removes all models, which is
	 * <em>not</emp> the same as removing all statements from all
	 * models in this ModelSet.
	 * 
	 * @throws ModelRuntimeException
	 */
	void removeAll() throws ModelRuntimeException;

	/**
	 * The default model is used when the modelset is loaded from a
	 * serialization that has no context. The default model has a context of
	 * 'null'
	 * 
	 * @return the default model. It has to be isOpen() == true.
	 */
	Model getDefaultModel();

	/**
	 * @return an Interator over <em>all</em> models within this ModelSet.
	 *         Some models might be empty.
	 * 
	 * Since 4.4.10: Each model has to be isOpen() == true.
	 * 
	 * For some implementations: While you read the iterator you may not WRITE
	 * to the models.
	 * 
	 */
	ClosableIterator<Model> getModels();

	/**
	 * @return an iterator over all URIs used as model URIs.
	 * 
	 * For some implementations: While you read the iterator you may not WRITE
	 * to the models.
	 */
	ClosableIterator<URI> getModelURIs();

	/**
	 * @return the native implementation (e.g. a Jena Model). Using this method
	 *         breaks strict triple store independence, but exposes the full
	 *         power and <b>reduces</b> API dependence. This method is part of
	 *         the main API.
	 */
	Object getUnderlyingModelSetImplementation();

	/**
	 * Print the whole content of this ModelSet to System.out.
	 */
	void dump();

}