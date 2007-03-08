package org.ontoware.rdf2go.layer.inverse;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;


public class StatementTwistingIterator implements ClosableIterator<Statement> {

	private ClosableIterator<Statement> it;
	private Model m;

	public StatementTwistingIterator(Model m, ClosableIterator<Statement> it) {
		this.it = it;
		this.m = m;
	}

	public boolean hasNext() {
		return it.hasNext();
	}

	/**
	 * retrieve next statement and 'twist' it (s,p,o) becomes (o,p,s)
	 */
	public Statement next() {
		Statement s = it.next();
		if (s.getObject() instanceof Literal)
			throw new IllegalArgumentException("Iterator returns statements with Literal-objects, those cannot be twisted in RDF");
		return (Statement) new StatementImpl( m.getContextURI(), (Resource) s.getObject(), s.getPredicate(), s.getSubject() );
	}

	public void remove() {
		it.remove();
	}

	public void close() {
		it.close();
	}

}
