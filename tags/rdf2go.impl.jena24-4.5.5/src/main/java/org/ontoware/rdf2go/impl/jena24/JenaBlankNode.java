package org.ontoware.rdf2go.impl.jena24;

import org.ontoware.rdf2go.model.node.impl.AbstractBlankNodeImpl;

import com.hp.hpl.jena.graph.Node;

public class JenaBlankNode extends AbstractBlankNodeImpl {

	public JenaBlankNode(Node jenaNode) {
		super(jenaNode);
	}

	@Override
	public String getInternalID() {
		return ((Node) this.getUnderlyingBlankNode()).getBlankNodeLabel();
	}

}
