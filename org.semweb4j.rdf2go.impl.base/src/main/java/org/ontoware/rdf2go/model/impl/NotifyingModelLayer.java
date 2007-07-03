package org.ontoware.rdf2go.model.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelChangedListener;
import org.ontoware.rdf2go.model.NotifyingModel;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.TriplePattern;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

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
	public void addStatement(Statement statement) throws ModelRuntimeException {
		for (ModelChangedListener listener : modelChangeListener.keySet()) {
			TriplePattern pattern = modelChangeListener.get(listener);
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
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		for (ModelChangedListener listener : modelChangeListener.keySet()) {
			TriplePattern pattern = modelChangeListener.get(listener);
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
	public void removeStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		this.removeStatement(getDelegatedModel().createStatement(getDelegatedModel()
				.createURI(subjectURIString), predicate, getDelegatedModel()
				.createPlainLiteral(literal)));
	}

	//////////// diff
	
	public void update(Diff diff) throws ModelRuntimeException {
		for (ModelChangedListener listener : modelChangeListener.keySet()) {
				listener.performedUpdate(diff);
		}
		getDelegatedModel().update(diff);
	}

	
	// //////////////////////////////////
	// implement NotifyingModel

	private Map<ModelChangedListener, TriplePattern> modelChangeListener = new HashMap<ModelChangedListener, TriplePattern>();

	public void addModelChangedListener(ModelChangedListener listener,
			TriplePattern pattern) {
		modelChangeListener.put(listener, pattern);
	}

	public void addModelChangedListener(ModelChangedListener listener) {
		modelChangeListener.put(listener, null);
	}

	public void removeModelChangedListener(ModelChangedListener listener) {
		modelChangeListener.remove(listener);
	}

}
