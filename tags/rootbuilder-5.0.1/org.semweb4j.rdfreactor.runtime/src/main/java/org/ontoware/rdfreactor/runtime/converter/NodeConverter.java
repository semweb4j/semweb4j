package org.ontoware.rdfreactor.runtime.converter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdfreactor.runtime.INodeConverter;


/**
 * The trivial converter converting RDF2Go nodes just to itself.
 * 
 * @author voelkel
 * 
 */
public class NodeConverter implements INodeConverter<Node> {
	
	public Node toJava(Node node) {
		return node;
	}
	
	public Node toNode(Model model, Object javaValue) {
		return (Node)javaValue;
	}
	
}
