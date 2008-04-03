package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;

public class ProjectingIterator<T> implements ClosableIterator<T> {

	public static enum projection {
		Subject, Predicate, Object, Context
	}

	private ClosableIterator<Statement> it;
	private projection p;

	public ProjectingIterator(ClosableIterator<Statement> it, projection p) {
		this.it = it;
		this.p = p;
	}

	public void close() {
		it.close();
	}

	public boolean hasNext() {
		return it.hasNext();
	}

	@SuppressWarnings("unchecked")
	public T next() {
		Statement statement = it.next();
		switch (p) {
		case Subject:
			return (T) statement.getSubject();
		case Predicate:
			return (T) statement.getPredicate();
		case Object:
			return (T) statement.getObject();
		case Context:
			return (T) statement.getContext();
		}
		throw new AssertionError("never happens");
	}

	public void remove() {
		it.remove();
	}

}
