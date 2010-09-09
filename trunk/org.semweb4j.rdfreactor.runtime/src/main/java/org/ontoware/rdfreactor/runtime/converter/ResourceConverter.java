package org.ontoware.rdfreactor.runtime.converter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdfreactor.runtime.INodeConverter;


/**
 * Converting RDF2Go {@link Node} instances to RDF2Go {@link Resource} objects.
 * 
 * Warning: This throws a {@link ClassCastException} if the given {@link Node}
 * is not a {@link Resource} (i.e. if it is a {@link Literal}).
 * 
 * Node or BlankNode
 * 
 * @author voelkel
 */
public class ResourceConverter implements INodeConverter<Node> {
	
	// might go wrong, if node is a literal
	public Resource toJava(Node node) {
		return node.asResource();
	}
	
	public Node toNode(Model model, Object javaValue) {
		// a resource is a node, fine.
		return (Node)javaValue;
	}
	
}
