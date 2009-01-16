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
		this.it.close();
	}

	public boolean hasNext() {
		return this.it.hasNext();
	}

	@SuppressWarnings("unchecked")
	public T next() {
		Node node = this.it.next();
		return (T) RDFReactorRuntime.node2javatype(this.model, node, this.returnType);
	}

	public void remove() {
		this.it.remove();
	}

}
