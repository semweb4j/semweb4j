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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.DiffReader;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DiffImpl extends AbstractModelAddRemove implements Diff {
	
	/**
     * 
     */
    private static final long serialVersionUID = -8901163929521106031L;

	private static final Logger log = LoggerFactory.getLogger(DiffImpl.class);
	
	private Set<Statement> addedSet;
	
	private Set<Statement> removedSet;
	
	public DiffImpl() {
		this.addedSet = new HashSet<Statement>();
		this.removedSet = new HashSet<Statement>();
	}
	
	public DiffImpl(Iterator<? extends Statement> added, Iterator<? extends Statement> removed) {
		this();
		while(added.hasNext()) {
			this.addedSet.add(added.next());
		}
		while(removed.hasNext()) {
			Statement stmt = removed.next();
			if(this.addedSet.contains(stmt)) {
				// adding and removing the same statement is a
				// do-nothing-operation
				this.addedSet.remove(stmt);
			} else {
				this.removedSet.add(stmt);
			}
		}
	}
	
	public Diff create(Iterator<? extends Statement> added, Iterator<? extends Statement> removed) {
		return new DiffImpl(added, removed);
	}
	
	public Iterable<Statement> getAdded() {
		return this.addedSet;
	}
	
	public Iterable<Statement> getRemoved() {
		return this.removedSet;
	}
	
	@Override
	public void removeStatement(Statement statement) throws ModelRuntimeException {
		this.removedSet.add(statement);
	}
	
	@Override
	public void removeAll(Iterator<? extends Statement> other) throws ModelRuntimeException {
		while(other.hasNext()) {
			removeStatement(other.next());
		}
	}
	
	@Override
	public void removeAll() throws ModelRuntimeException {
		throw new UnsupportedOperationException("It doesn't make sense to 'remove all' on a Diff");
	}
	
	public ClosableIterator<Statement> iterator() {
		throw new UnsupportedOperationException(
		        "Please iterate over getAdded or getRemoved instead");
	}
	
	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		this.addedSet.add(statement);
	}
	
	@Override
	public void addAll(Iterator<? extends Statement> other) throws ModelRuntimeException {
		while(other.hasNext()) {
			addStatement(other.next());
		}
	}
	
	public Diff getDiff(Iterator<? extends Statement> statements) throws ModelRuntimeException {
		throw new UnsupportedOperationException("Doens't make sense for a diff");
	}
	
	@Override
	public void update(DiffReader diff) throws ModelRuntimeException {
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
	public void addStatement(Resource subject, URI predicate, Node object)
	        throws ModelRuntimeException {
		addStatement(new StatementImpl(null, subject, predicate, object));
	}
	
	@Override
	public void removeStatement(Resource subject, URI predicate, Node object)
	        throws ModelRuntimeException {
		removeStatement(new StatementImpl(null, subject, predicate, object));
	}
	
	public void dump() {
		log.trace("Dumping diff to System.out");
		List<Statement> added = new ArrayList<Statement>();
		for(Statement s : getAdded()) {
			added.add(s);
		}
		Collections.sort(added);
		for(Statement s : added) {
			System.out.println("[+] " + s);
		}
		
		// removed
		List<Statement> removed = new ArrayList<Statement>();
		for(Statement s : getRemoved()) {
			removed.add(s);
		}
		Collections.sort(removed);
		for(Statement s : removed) {
			System.out.println("[-] " + s);
		}
	}
	
	public static final boolean equals(Set<Statement> a, Set<Statement> b) {
		if(a.size() == b.size()) {
			for(Statement s : a) {
				if(!b.contains(s))
					return false;
			}
			return true;
		}
		// else
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Diff) {
			
			Diff diff = (Diff)other;
			
			try {
				log.trace("Comparing two diffs");
				// IMPROVE compare less expensive
				ModelAddRemoveMemoryImpl otherAdded = new ModelAddRemoveMemoryImpl();
				otherAdded.addAll(diff.getAdded().iterator());
				
				ModelAddRemoveMemoryImpl otherRemoved = new ModelAddRemoveMemoryImpl();
				otherRemoved.addAll(diff.getRemoved().iterator());
				
				log.trace("This diff: " + this.addedSet.size() + " added and "
				        + this.removedSet.size() + " removed");
				log.trace("Other diff: " + otherAdded.set.size() + " added and "
				        + otherRemoved.set.size() + " removed");
				
				// now compare the sets of statements
				
				return equals(this.addedSet, otherAdded.getSet())
				        && equals(this.removedSet, otherRemoved.getSet());
				
			} catch(ModelRuntimeException e) {
				throw new RuntimeException(e);
			}
		}
		// else
		if(other == null) {
			log.trace("other is null, but not a DiffImpl");
		} else {
			log.trace("other is not a diff but " + other.getClass());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.addedSet.hashCode() + this.removedSet.hashCode();
	}
	
}
