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

package org.ontoware.rdf2go.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.DiffReader;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelChangedListener;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.NotifyingModelSet;
import org.ontoware.rdf2go.model.QuadPattern;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;

/**
 * Adds notifying capabilites to existing models.
 * 
 * Reads from streams and readers are not detectable.
 * 
 * @author voelkel
 * 
 */
public class NotifyingModelSetLayer extends DelegatingModelSet implements
		NotifyingModelSet {

	/**
     * 
     */
    private static final long serialVersionUID = 5673118697010013053L;

	public NotifyingModelSetLayer(ModelSet modelset) {
		super(modelset);
	}

	// //////////////////////////////////
	// implement NotifyingModelSet

	private Map<ModelChangedListener, QuadPattern> modelsetChangeListener = new HashMap<ModelChangedListener, QuadPattern>();

	@Override
    public void addModelSetChangedListener(ModelChangedListener listener) {
		this.modelsetChangeListener.put(listener, new QuadPatternImpl(Variable.ANY,
				Variable.ANY, Variable.ANY, Variable.ANY));
	}

	@Override
    public void addModelSetChangedListener(ModelChangedListener listener,
			QuadPattern pattern) {
		this.modelsetChangeListener.put(listener, pattern);
	}

	@Override
    public void removeModelSetChangedListener(ModelChangedListener listener) {
		this.modelsetChangeListener.remove(listener);
	}

	// //////////////////////////
	// override ModelSet methods to enalbe listening

	@Override
	public boolean addModel(Model model) {
		// first check for listeners
		for (Map.Entry<ModelChangedListener, QuadPattern> entry : this.modelsetChangeListener
				.entrySet()) {
			// if pattern.context matches model.context
			if (entry.getValue().getContext() == Variable.ANY
					|| entry.getValue().getContext().equals(
							model.getContextURI())) {
				// filter model
				for (Statement stmt : model) {
					if (entry.getValue().matches(stmt)) {
						entry.getKey().addedStatement(stmt);
					}
				}
			}
		}
		// then add
		return super.addModel(model);
	}

	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		// inspect
		for (Map.Entry<ModelChangedListener, QuadPattern> entry : this.modelsetChangeListener
				.entrySet()) {
			if (entry.getValue().matches(statement)) {
				entry.getKey().addedStatement(statement);
			}
		}
		// delegate
		super.addStatement(statement);
	}

	@Override
	public Model getDefaultModel() {
		Model model = super.getDefaultModel();
		NotifyingModelLayer notifyingModel = new NotifyingModelLayer(model);
		for (Map.Entry<ModelChangedListener, QuadPattern> entry : this.modelsetChangeListener
				.entrySet()) {
			// only if listening for (*, x,y,z) changes in default model are
			// detected
			if (entry.getValue().getContext() == Variable.ANY) {
				notifyingModel.addModelChangedListener(entry.getKey(), entry
						.getValue());
			}
		}
		return notifyingModel;
	}

	@Override
	public Model getModel(URI contextURI) {
		Model model = super.getModel(contextURI);
		NotifyingModelLayer notifyingModel = new NotifyingModelLayer(model);
		for (Map.Entry<ModelChangedListener, QuadPattern> entry : this.modelsetChangeListener
				.entrySet()) {
			if (entry.getValue().getContext() == Variable.ANY
					|| entry.getValue().getContext().equals(contextURI)) {
				notifyingModel.addModelChangedListener(entry.getKey(), entry
						.getValue());
			}
		}
		return notifyingModel;
	}

	@Override
	public ClosableIterator<Model> getModels() {
		List<Model> models = new ArrayList<Model>();
		ClosableIterator<? extends Model> it = super.getModels();
		// wrap all into NotifyingModels
		while (it.hasNext()) {
			Model model = it.next();
			NotifyingModelLayer notifyingModel = new NotifyingModelLayer(model);
			for (Map.Entry<ModelChangedListener, QuadPattern> entry : this.modelsetChangeListener
					.entrySet()) {
				notifyingModel.addModelChangedListener(entry.getKey(), entry
						.getValue());
			}
			models.add(notifyingModel);
		}
		it.close();
		return new PseudoClosableIterator<Model>(models.iterator());
	}

	@Override
	public void removeStatement(Statement statement)
			throws ModelRuntimeException {
		// inspect
		for (Map.Entry<ModelChangedListener, QuadPattern> entry : this.modelsetChangeListener
				.entrySet()) {
			if (entry.getValue().matches(statement)) {
				entry.getKey().removedStatement(statement);
			}
		}
		// delegate
		super.removeStatement(statement);
	}

	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
		// inspect
		for (Map.Entry<ModelChangedListener, QuadPattern> entry : this.modelsetChangeListener
				.entrySet()) {
			entry.getKey().performedUpdate(diff);
		}
		// delegate
		super.update(diff);
	}

}
