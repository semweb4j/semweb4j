/*
 * Created on 06.09.2005
 *
 */
package org.ontoware.semversion.impl;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.impl.DelegatingModel;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.semversion.SemVersion;

/**
 * Annotates each created blank node with a unique URI
 * 
 * @author voelkel
 * 
 */
public class BlankNodeEnrichmentModel extends DelegatingModel implements Model {

	public BlankNodeEnrichmentModel(Model model) {
		super(model);
	}

	/**
	 * @return an new blank node, annotated with an IFP (inverse functional
	 *         property = a unique ID)
	 */
	@Override
	public BlankNode createBlankNode() {
		// blank node enrichment
		BlankNode blankNode = getDelegatedModel().createBlankNode();
		getDelegatedModel().addStatement(blankNode, SemVersion.BLANK_NODE_ID,
				super.newRandomUniqueURI());
		return blankNode;
	}

}
