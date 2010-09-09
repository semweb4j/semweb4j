package org.ontoware.rdfreactor.runtime;

import java.util.Iterator;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;


/**
 * 
 * 
 * Wrapper class for an Iterator over URI or BlankNode or Literal objects, which
 * are objects of triples in the RDF model. Only RDF Reactor compatible subjects
 * (that have a type converter registered in {@link RDFReactorRuntime}) can be
 * extracted from the Iterator.
 * 
 * @deprecated use {@link ConvertingClosableIterator} instead. It can do the
 *             same and is also closable.
 */
@Patrolled
@Deprecated
public class ObjectResultIterator<T> implements Iterator<T> {
	
	private Iterator<Node> it;
	
	private Class<T> returnType;
	
	private Model model;
	
	/**
	 * Constructor for the wrapper around an Iterator over URI or BlankNode or
	 * Literal triple objects.
	 * 
	 * @param model - the underlying RDF2Go model
	 * @param it - Iterator over Statements
	 * @param returnType - the desired Java return type for extracting objects
	 *            from the Iterator
	 */
	public ObjectResultIterator(Model model, Iterator<Node> it, Class<T> returnType) {
		this.model = model;
		this.it = it;
		this.returnType = returnType;
	}
	
	public boolean hasNext() {
		return this.it.hasNext();
	}
	
	@SuppressWarnings(value = "unchecked")
	public T next() {
		Node node = this.it.next();
		Object typedObject = RDFReactorRuntime.node2javatype(this.model, node, this.returnType);
		return (T)typedObject;
	}
	
	public void remove() {
		this.it.remove();
	}
	
}
