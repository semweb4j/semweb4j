package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;

public class RDFObjectIterator<T> implements ClosableIterator<T> {

	private ExtractingIterator it;
	private Class returnType;

	public RDFObjectIterator( ExtractingIterator it, Class returnType  ) {
		this.it = it;
		this.returnType = returnType;
	}
	
	public boolean hasNext() {
		return it.hasNext();
	}

	@SuppressWarnings("unchecked")
	public T next() {
		try {
			return (T) RDFReactorRuntime.node2javatype( it.getModel(), it.next(), returnType);
		} catch (ModelRuntimeException e) {
			throw new RuntimeException( e );
		}
	}

	public void remove() {
		it.remove();
	}

	public void close() {
		it.close();
	}


	
	
}
