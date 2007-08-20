package org.ontoware.rdfreactor.runtime;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;

/**
 * Can convert an RDF2Go node (or null) to the type T
 * @author voelkel
 *
 * @param <T>
 */
public interface INodeConverter<T> {

	T toJava( Node node );
	
	/**
	 * @param model TODO
	 * @param javaType must be of type T
	 * @return an RDF2Go nod with the value of javaValue
	 */
	Node toNode( Model model, Object javaValue );
	
}
