package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;

public class RDFObjectIterator<T> implements ClosableIterator<T> {

	private ExtractingIterator it;
	private Class<?> returnType;

	public RDFObjectIterator( ExtractingIterator it, Class<?> returnType  ) {
		this.it = it;
		this.returnType = returnType;
	}
	
	public boolean hasNext() {
		return this.it.hasNext();
	}

	@SuppressWarnings("unchecked")
	public T next() {
		try {
			return (T) RDFReactorRuntime.node2javatype( this.it.getModel(), this.it.next(), this.returnType);
		} catch (ModelRuntimeException e) {
			throw new RuntimeException( e );
		}
	}

	public void remove() {
		this.it.remove();
	}

	public void close() {
		this.it.close();
	}


	
	
}
