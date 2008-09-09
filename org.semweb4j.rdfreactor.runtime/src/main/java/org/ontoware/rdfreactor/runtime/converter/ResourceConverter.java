package org.ontoware.rdfreactor.runtime.converter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdfreactor.runtime.INodeConverter;

/**
 * Node or BlankNode
 * @author voelkel
 *
 */
public class ResourceConverter implements INodeConverter<Node> {

	// might go wrong, if node is a literal
	public Resource toJava(Node node) {
		return node.asResource();
	}

	public Node toNode(@SuppressWarnings("unused")
	Model model, Object javaValue) {
		// a resource is a node, fine.
		return (Node) javaValue;
	}

}
