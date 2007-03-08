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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

public class DiffImpl extends AbstractModelAddRemove implements Diff {

	private static final Log log = LogFactory.getLog(DiffImpl.class);

	private Set<Statement> added;

	private Set<Statement> removed;

	public DiffImpl() {
		this.added = new HashSet<Statement>();
		this.removed = new HashSet<Statement>();
	}

	public DiffImpl(Iterator<? extends Statement> added, Iterator<? extends Statement> removed) {
		this();
		while (added.hasNext())
			this.added.add(added.next());
		while (removed.hasNext())
			this.removed.add(removed.next());

	}

	public Diff create(Iterator<? extends Statement> added, Iterator<? extends Statement> removed) {
		return new DiffImpl(added, removed);
	}

	public Iterable<? extends Statement> getAdded() {
		return this.added;
	}

	public Iterable<? extends Statement> getRemoved() {
		return this.removed;
	}

	public void removeStatement(Statement statement) throws ModelRuntimeException {
		removed.remove(statement);
	}

	public void removeAll(Iterator<? extends Statement> other) throws ModelRuntimeException {
		while (other.hasNext()) {
			removeStatement(other.next());
		}
	}

	public void removeAll() throws ModelRuntimeException {
		throw new UnsupportedOperationException("It doesn't make sense to 'remove all' on a Diff");
	}

	public ClosableIterator<Statement> iterator() {
		throw new UnsupportedOperationException(
				"Please iterate over getAdded or getRemoved instead");
	}

	public void addStatement(Statement statement) throws ModelRuntimeException {
		added.add(statement);
	}

	public void addAll(Iterator<? extends Statement> other) throws ModelRuntimeException {
		while (other.hasNext()) {
			addStatement(other.next());
		}
	}

	public Diff getDiff(Iterator<? extends Statement> statements) throws ModelRuntimeException {
		throw new UnsupportedOperationException("Doens't make sense for a diff");
	}

	public void update(Diff diff) throws ModelRuntimeException {
		addAll(diff.getAdded().iterator());
		removeAll(diff.getRemoved().iterator());
	}

	public void lock() throws LockException {
		throw new UnsupportedOperationException("Doens't make sense for a diff");
	}

	public boolean isLocked() {
		throw new UnsupportedOperationException("Doens't make sense for a diff");
	}

	public void unlock() {
		throw new UnsupportedOperationException("Doens't make sense for a diff");
	}

	@Override
	public void addStatement(Resource subject, URI predicate, Node object) throws ModelRuntimeException {
		addStatement(new StatementImpl(null, subject, predicate, object));
	}

	@Override
	public void removeStatement(Resource subject, URI predicate, Node object) throws ModelRuntimeException {
		removeStatement(new StatementImpl(null, subject, predicate, object));
	}

	public void dump() {
		log.debug("Dumping diff to System.out");
		List<Statement> added = new ArrayList<Statement>();
		for (Statement s : getAdded()) {
			added.add(s);
		}
		Collections.sort(added);
		for (Statement s : added) {
			System.out.println("[+] " + s);
		}

		// removed
		List<Statement> removed = new ArrayList<Statement>();
		for (Statement s : getRemoved()) {
			removed.add(s);
		}
		Collections.sort(removed);
		for (Statement s : removed) {
			System.out.println("[-] " + s);
		}
	}

	public static final boolean equals(Set<Statement> a, Set<Statement> b) {
		if (a.size() == b.size()) {
			for (Statement s : a) {
				if (b.contains(s)) {
					// fine
				} else
					return false;
			}
			return true;

		} else
			return false;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Diff) {
			Diff diff = (Diff) other;

			try {
				log.debug("Comparing two diffs");
				// IMPROVE compare less expensive
				ModelAddRemoveMemoryImpl otherAdded = new ModelAddRemoveMemoryImpl();
				otherAdded.addAll(diff.getAdded().iterator());

				ModelAddRemoveMemoryImpl otherRemoved = new ModelAddRemoveMemoryImpl();
				otherRemoved.addAll(diff.getRemoved().iterator());

				log.debug("This diff: " + added.size() + " added and " + removed.size()
						+ " removed");
				log.debug("Other diff: " + otherAdded.set.size() + " added and "
						+ otherRemoved.set.size() + " removed");

				// now compare the sets of statements

				return equals(added, otherAdded.getSet()) && equals(removed, otherRemoved.getSet());

			} catch (ModelRuntimeException e) {
				throw new RuntimeException(e);
			}
		} else {
			log.debug("other is not a diff but " + other.getClass());
			return false;
		}
	}

}
