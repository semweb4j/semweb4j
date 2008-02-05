package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;

public class ConvertingClosableIterator<T> implements ClosableIterator<T>{

	private ClosableIterator<Node> it;
	private Model model;
	private Class<T> returnType;

	public ConvertingClosableIterator(ClosableIterator<Node> it, Model model,
			Class<T> returnType) {
		this.model = model;
		this.it = it;
		this.returnType = returnType;
	}

	public void close() {
		it.close();
	}

	public boolean hasNext() {
		return it.hasNext();
	}

	@SuppressWarnings("unchecked")
	public T next() {
		Node node = it.next();
		return (T) RDFReactorRuntime.node2javatype(model, node, returnType);
	}

	public void remove() {
		it.remove();
	}

}
