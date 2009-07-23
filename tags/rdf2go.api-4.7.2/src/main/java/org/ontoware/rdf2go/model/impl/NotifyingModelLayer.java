/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go.model.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.DiffReader;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelChangedListener;
import org.ontoware.rdf2go.model.NotifyingModel;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.TriplePattern;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;

/**
 * Adds notifying capabilites to existing models.
 * @author voelkel
 *
 */
public class NotifyingModelLayer extends DelegatingModel implements
		NotifyingModel {

	public NotifyingModelLayer( Model model ) {
		super(model);
	}

	// /////////////////////////////////
	// override methods to be able to notify

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelWriter#addAll(org.ontoware.rdf2go.core.common.CommonModelReader)
	 */
	@Override
	public void addAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		while (other.hasNext()) {
			this.addStatement(other.next());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI,
	 *      org.ontoware.rdf2go.core.node.Node)
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		this.addStatement(super.getDelegatedModel().createStatement(subject, predicate, object));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		this.addStatement(getDelegatedModel().createStatement(subject, predicate, getDelegatedModel()
				.createLanguageTagLiteral(literal, languageTag)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		this.addStatement(subject, predicate, getDelegatedModel()
				.createDatatypeLiteral(literal, datatypeURI));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		this.addStatement(getDelegatedModel().createStatement(subject, predicate, getDelegatedModel()
				.createPlainLiteral(literal)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelWriter#addStatement(S)
	 */
	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		for (ModelChangedListener listener : this.modelChangeListener.keySet()) {
			TriplePattern pattern = this.modelChangeListener.get(listener);
			if (pattern == null || pattern.matches(statement)) {
				listener.addedStatement(statement);
			}
		}
		getDelegatedModel().addStatement(statement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		this.addStatement(getDelegatedModel().createStatement(getDelegatedModel()
				.createURI(subjectURIString), predicate, getDelegatedModel()
				.createLanguageTagLiteral(literal, languageTag)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		this.addStatement(getDelegatedModel().createStatement(getDelegatedModel()
				.createURI(subjectURIString), predicate, getDelegatedModel()
				.createDatatypeLiteral(literal, datatypeURI)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		this.addStatement(getDelegatedModel().createStatement(getDelegatedModel()
				.createURI(subjectURIString), predicate, getDelegatedModel()
				.createPlainLiteral(literal)));
	}

	/////////////// remove
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelWriter#removeAll(org.ontoware.rdf2go.core.common.CommonModelReader)
	 */
	@Override
	public void removeAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		while (other.hasNext()) {
			this.removeStatement(other.next());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI,
	 *      org.ontoware.rdf2go.core.node.Node)
	 */
	@Override
	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		this.removeStatement(getDelegatedModel().createStatement(subject, predicate, object));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void removeStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		this.removeStatement(getDelegatedModel().createStatement(subject, predicate, getDelegatedModel()
				.createLanguageTagLiteral(literal, languageTag)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	@Override
	public void removeStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		this.removeStatement(subject, predicate, getDelegatedModel()
				.createDatatypeLiteral(literal, datatypeURI));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	@Override
	public void removeStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		this.removeStatement(getDelegatedModel().createStatement(subject, predicate, getDelegatedModel()
				.createPlainLiteral(literal)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelWriter#removeStatement(S)
	 */
	@Override
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		for (ModelChangedListener listener : this.modelChangeListener.keySet()) {
			TriplePattern pattern = this.modelChangeListener.get(listener);
			if (pattern == null || pattern.matches(statement)) {
				listener.removedStatement(statement);
			}
		}
		getDelegatedModel().removeStatement(statement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		this.removeStatement(getDelegatedModel().createStatement(getDelegatedModel()
				.createURI(subjectURIString), predicate, getDelegatedModel()
				.createLanguageTagLiteral(literal, languageTag)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	@Override
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		this.removeStatement(getDelegatedModel().createStatement(getDelegatedModel()
				.createURI(subjectURIString), predicate, getDelegatedModel()
				.createDatatypeLiteral(literal, datatypeURI)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	@Override
	public void removeStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		this.removeStatement(getDelegatedModel().createStatement(getDelegatedModel()
				.createURI(subjectURIString), predicate, getDelegatedModel()
				.createPlainLiteral(literal)));
	}
	
	@Override
	public void removeStatements(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		ModelAddRemoveMemoryImpl toBeRemoved = new ModelAddRemoveMemoryImpl();
		toBeRemoved.addAll(this.findStatements(subject, predicate, object));
		this.removeAll(toBeRemoved.iterator());
		
		this.getDelegatedModel().removeAll(toBeRemoved.iterator());
	}


	//////////// diff
	
	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
		for (ModelChangedListener listener : this.modelChangeListener.keySet()) {
				listener.performedUpdate(diff);
		}
		getDelegatedModel().update(diff);
	}

	
	// //////////////////////////////////
	// implement NotifyingModel

	private Map<ModelChangedListener, TriplePattern> modelChangeListener = new HashMap<ModelChangedListener, TriplePattern>();

	public void addModelChangedListener(ModelChangedListener listener,
			TriplePattern pattern) {
		this.modelChangeListener.put(listener, pattern);
	}

	public void addModelChangedListener(ModelChangedListener listener) {
		this.modelChangeListener.put(listener, null);
	}

	public void removeModelChangedListener(ModelChangedListener listener) {
		this.modelChangeListener.remove(listener);
	}

}
