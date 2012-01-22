/**
 * LICENSE INFORMATION
 *
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2010
 *
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model.impl;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.TriplePattern;

/**
 * Returns all statements matching the pattern. Looks into each model.
 * TODO test me - not needed for OpenRDF
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

	@Override
    public void close() {
		this.stmtit.close();
	}

	@Override
    public boolean hasNext() {
		getNonEmptyStmtIterator();
		return this.stmtit.hasNext();
	}

	@Override
    public Statement next() {
		getNonEmptyStmtIterator();
		return this.stmtit.next();
	}

	private void getNonEmptyStmtIterator() {
		// make sure we have a statement iterator
		if (this.stmtit == null) {
			Model m = this.modelit.next();
			m.open();
			this.stmtit = m.findStatements(this.pattern);
		}
		// make sure we have a statement iterator, which contains statements
		while (this.modelit.hasNext() && !this.stmtit.hasNext()) {
			this.stmtit.close();
			Model m = this.modelit.next();
			m.open();
			this.stmtit = m.findStatements(this.pattern);
		}
	}

	@Override
    public void remove() {
		getNonEmptyStmtIterator();
		this.stmtit.remove();
	}

}
