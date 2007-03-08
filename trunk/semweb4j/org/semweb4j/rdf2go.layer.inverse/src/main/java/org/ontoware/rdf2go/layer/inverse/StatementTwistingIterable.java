package org.ontoware.rdf2go.layer.inverse;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;

/**
 * Simple helper for StatementTwistingIterator
 * @author voelkel
 *
 */
public class StatementTwistingIterable implements ClosableIterable<Statement> {

	private ClosableIterable<Statement> it;
	private Model m;

	public StatementTwistingIterable(Model m, ClosableIterable<Statement> it) {
		this.it = it;
		this.m = m;
	}

	public ClosableIterator<Statement> iterator() {
		return new StatementTwistingIterator( m, it.iterator() );
	}

}
