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
		this.it.close();
	}

	public boolean hasNext() {
		return this.it.hasNext();
	}

	@SuppressWarnings("unchecked")
	public T next() {
		Statement statement = this.it.next();
		switch (this.p) {
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
		this.it.remove();
	}

}
