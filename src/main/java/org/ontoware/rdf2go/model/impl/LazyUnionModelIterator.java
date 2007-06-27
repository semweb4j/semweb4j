package org.ontoware.rdf2go.model.impl;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.TriplePattern;

/**
 * Returns all statements matching the pattern. Looks into each model.
 * TODO test me
 * @author voelkel
 */
public class LazyUnionModelIterator implements ClosableIterator<Statement> {

	private TriplePattern pattern;

	private Iterator<? extends Model> modelit;

	private ClosableIterator<? extends Statement> stmtit;

	public LazyUnionModelIterator(AbstractModelSetImpl impl,
			TriplePattern pattern) {
		this.pattern = pattern;
		// create iterator over models
		this.modelit = impl.getModels();
	}

	public void close() {
		this.stmtit.close();
	}

	public boolean hasNext() {
		getNonEmptyStmtIterator();
		return this.stmtit.hasNext();
	}

	public Statement next() {
		getNonEmptyStmtIterator();
		return this.stmtit.next();
	}

	private void getNonEmptyStmtIterator() {
		// make sure we have a statement iterator
		if (this.stmtit == null) {
			Model m = modelit.next();
			m.open();
			this.stmtit = m.findStatements(pattern);
		}
		// make sure we have a statement iterator, which contains statements
		while (modelit.hasNext() && !this.stmtit.hasNext()) {
			this.stmtit.close();
			Model m = modelit.next();
			m.open();
			this.stmtit = m.findStatements(pattern);
		}
	}

	public void remove() {
		getNonEmptyStmtIterator();
		this.stmtit.remove();
	}

}
