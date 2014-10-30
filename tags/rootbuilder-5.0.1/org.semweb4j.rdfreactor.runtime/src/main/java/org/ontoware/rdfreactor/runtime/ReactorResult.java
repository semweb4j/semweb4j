package org.ontoware.rdfreactor.runtime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.Node;

public class ReactorResult<T> {
	
	private Class<T> returnType;
	
	private Model model;

	private TriplePatternImpl triplePattern;
	
	public ReactorResult(Model model, TriplePatternImpl triplePattern, Class<T> returnType) {
		this.model = model;
		this.triplePattern = triplePattern;
		this.returnType = returnType;
	}

	public ClosableIterator<T> asClosableIterator() {
		ClosableIterator<Statement> it = this.model.findStatements(this.triplePattern.getSubject(),
				this.triplePattern.getPredicate(), this.triplePattern.getObject() );
		ProjectingIterator.projection proj = null;
		switch (this.triplePattern.extract) {
		case SUBJECT:
			proj = ProjectingIterator.projection.Subject;
			break;
		case PREDICATE:
			proj = ProjectingIterator.projection.Predicate;
			break;
		case OBJECT:
			proj = ProjectingIterator.projection.Object;
			break;
		}
		return new ConvertingClosableIterator<T>( new ProjectingIterator<Node>(it,
				proj), this.model, this.returnType);
	}
	
	public T firstValue() {
		ClosableIterator<T> it = asClosableIterator();
		T first = null;
		if (it.hasNext()) {
			first = it.next();
		}
		it.close();
		return first;
	}
	
	public List<T> asList() {
		ClosableIterator<T> it = asClosableIterator();
		ArrayList<T> list = new ArrayList<T>();
		while (it.hasNext()) {
			list.add(it.next());
		}
		it.close();
		return list;
	}

	@SuppressWarnings("unchecked")
	public T[] asArray() {
		Object[] resultAsArray = (Object[]) Array.newInstance(this.returnType, 0);
		return (T[]) asList().toArray(resultAsArray);
	}
	
	public long count() {
		ClosableIterator<T> it = asClosableIterator();
		long count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		it.close();
		return count;
	}
	
}
