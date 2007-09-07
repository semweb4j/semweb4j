package org.ontoware.rdfreactor.runtime;

import org.ontoware.rdf2go.model.Model;

public interface ReactorWrapper extends ResourceEntity {

	public abstract Model getModel();

}