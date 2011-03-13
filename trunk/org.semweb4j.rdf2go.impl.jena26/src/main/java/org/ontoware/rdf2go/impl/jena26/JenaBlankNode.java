package org.ontoware.rdf2go.impl.jena26;

import org.ontoware.rdf2go.model.node.impl.AbstractBlankNodeImpl;

import com.hp.hpl.jena.graph.Node;


public class JenaBlankNode extends AbstractBlankNodeImpl {
	
	private static final long serialVersionUID = 3861356245677277713L;
	
	public JenaBlankNode(Node jenaNode) {
		super(jenaNode);
	}
	
	@Override
	public String getInternalID() {
		return ((Node)this.getUnderlyingBlankNode()).getBlankNodeLabel();
	}
	
}
