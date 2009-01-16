package org.ontoware.rdfreactor.runtime;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;

/**
 * @author voelkel
 *
 */
public class ReactorWrapperImpl implements ReactorWrapper {

	private Model model;

	private Resource resource;

	/* (non-Javadoc)
	 * @see org.ontoware.rdfreactor.runtime.example.ReactorWrapper#getModel()
	 */
	public Model getModel() {
		return this.model;
	}

	/* (non-Javadoc)
	 * @see org.ontoware.rdfreactor.runtime.example.ReactorWrapper#getResource()
	 */
	public Resource getResource() {
		return this.resource;
	}

}
