package org.ontoware.rdfreactor.example;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdfreactor.runtime.Bridge;
import org.ontoware.rdfreactor.runtime.ExtractingIterator;

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
			return (T) Bridge.uriBlankLiteral2reactor( it.getModel(), it.next(), returnType);
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
