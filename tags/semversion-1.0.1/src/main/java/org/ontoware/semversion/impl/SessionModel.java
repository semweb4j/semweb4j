/*
 * Created on 06.09.2005
 *
 */
package org.ontoware.semversion.impl;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.impl.DelegatingModel;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.semversion.Session;

public class SessionModel extends DelegatingModel implements Model {
	
	private final Session session;

	public SessionModel(Session sess, Model model) {
		super(model);
		this.session = sess;
	}

	/**
	 * @return an new blank node, annotaded with an IFP
	 */
	@Override
	public BlankNode createBlankNode() {
		// blank node enrichment
		BlankNode blankNode = getDelegatedModel().createBlankNode();
		getDelegatedModel().addStatement(
				blankNode,
				SemVersionImpl.BLANK_NODE_ID,
				session.getSemVersion().getTripleStore().getModelSet()
						.newRandomUniqueURI());
		return blankNode;
	}

	public Session getSession() {
		return this.session;
	}

}
