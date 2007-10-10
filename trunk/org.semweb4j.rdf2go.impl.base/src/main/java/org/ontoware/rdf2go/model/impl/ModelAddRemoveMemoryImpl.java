/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.model.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.ModelAddRemove;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

public class ModelAddRemoveMemoryImpl extends AbstractModelAddRemove implements ModelAddRemove {

	protected Set<Statement> set = new HashSet<Statement>();

	@Override
	public void addStatement(Resource subject, URI predicate, Node object) throws ModelRuntimeException {
		// TODO: is it OK to set the context to null?
		Statement s = new StatementImpl(null, subject, predicate, object);
		this.set.add(s);
	}

	@Override
	public void removeStatement(Resource subject, URI predicate, Node object) throws ModelRuntimeException {
		// TODO: is it OK to set the context to null?
		Statement s = new StatementImpl(null, subject, predicate, object);
		this.set.remove(s);
	}
	
	public Set<Statement> getSet() {
		return this.set;
	}

	public void lock() throws LockException {
		throw new UnsupportedOperationException();
	}

	public boolean isLocked() {
		throw new UnsupportedOperationException();
	}

	public void unlock() {
		throw new UnsupportedOperationException();
	}

	public Diff getDiff(Iterator<Statement> statements) throws ModelRuntimeException {
		ModelAddRemoveMemoryImpl other = new ModelAddRemoveMemoryImpl();
		other.addAll(statements);

		ModelAddRemoveMemoryImpl add = new ModelAddRemoveMemoryImpl();
		add.addAll(other.iterator());
		add.removeAll(this.iterator());

		ModelAddRemoveMemoryImpl removed = new ModelAddRemoveMemoryImpl();
		removed.addAll(this.iterator());
		removed.removeAll(other.iterator());

		return new DiffImpl(add.iterator(), removed.iterator());
	}

	public ClosableIterator<Statement> iterator() {
		Iterator<Statement> it = this.set.iterator();
		assert it != null;
		return new PseudoClosableIterator<Statement>( it );
	}

	public long size() {
		return this.set.size();
	}

}
