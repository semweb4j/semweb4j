package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;


/**
 * A {@link ClosableIterator} that uses the registered type converters to turn
 * an iterator over RDF2Go {@link Node} objects to a type <T>-specific iterator.
 * 
 * @author voelkel
 * 
 * @param <T>
 */
@Patrolled
public class ConvertingClosableIterator<T> implements ClosableIterator<T> {
	
	private ClosableIterator<Node> it;
	
	private Class<T> returnType;
	
	private Model model;
	
	/**
	 * @param it
	 * @param model
	 * @param returnType given as a java class to allow runtime reflection on it
	 */
	public ConvertingClosableIterator(ClosableIterator<Node> it, Model model, Class<T> returnType) {
		this.model = model;
		this.it = it;
		this.returnType = returnType;
	}
	
	public boolean hasNext() {
		return this.it.hasNext();
	}
	
	public void close() {
		this.it.close();
	}
	
	@SuppressWarnings("unchecked")
	public T next() {
		Node node = this.it.next();
		Object typedObject = RDFReactorRuntime.node2javatype(this.model, node, this.returnType);
		return (T)typedObject;
	}
	
	public void remove() {
		this.it.remove();
	}
	
}
