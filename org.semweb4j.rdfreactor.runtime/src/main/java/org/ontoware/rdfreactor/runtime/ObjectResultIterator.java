/**
 * 
 */
package org.ontoware.rdfreactor.runtime;

import java.util.Iterator;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;

/**
 * Wrapper class for an Iterator over URI or BlankNode or Literal objects, 
 * which are objects of triples in the model. 
 * Only RDF Reactor compatible subjects can be extracted from the Iterator.  
 * 
 * @author $Author: xamde $
 * @version $Id: ObjectResultIterator.java,v 1.7 2006/10/16 17:31:58 xamde Exp $
 * 
 */

public class ObjectResultIterator<E> implements Iterator<E> {

	private Iterator<Node> it;

	private Class<?> returnType;

	private Model m;

	/**
	 * Constrcutor for the wrapper around an Iterator over URI or BlankNode or Literal triple objects.
	 * 
	 * @param m - 
	 * 			the underlying RDF2Go model
	 * @param it - 
	 * 			Iterator over Statements
	 * @param returnType - 
	 * 			the desired Java return type for extracting objects from the Iterator  
	 */
	public ObjectResultIterator(Model m, Iterator<Node> it,
			Class<?> returnType) {
		this.it = it;
		this.returnType = returnType;
		this.m = m;
	}

	public boolean hasNext() {
		return it.hasNext();
	}

	@SuppressWarnings(value = "unchecked")
	public E next() {
		Node node = it.next();
		Object typedObject;
		try {
			typedObject = RDFReactorRuntime.node2javatype(this.m, node, returnType);
			// TODO: can this be done better in Java 1.5?
			return (E) typedObject;
		} catch (ModelRuntimeException e) {
			throw new ModelRuntimeException( e );
		}
	}

	public void remove() {
		it.remove();
	}

}
