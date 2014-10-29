package org.ontoware.rdfreactor.runtime;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;


/**
 * An {@link Iterator} over nodes, based on an iterator over {@link QueryRow},
 * such as returned by sparqlSelect(..).
 * 
 * Extracts a single variable from a {@link QueryRow}.
 * 
 * @author voelkel
 */
@Patrolled
public class ExtractingIterator implements ClosableIterator<Node> {
	
	private ClosableIterator<QueryRow> it;
	
	private Model model;
	
	private String extractVariable;
	
	public ExtractingIterator(Model model, ClosableIterator<QueryRow> it, String extractVariable) {
		this.model = model;
		this.it = it;
		this.extractVariable = extractVariable;
	}
	
	public boolean hasNext() {
		return this.it.hasNext();
	}
	
	public Node next() {
		return this.it.next().getValue(this.extractVariable);
	}
	
	public void remove() {
		this.it.remove();
	}
	
	public void close() {
		this.it.close();
	}
	
	public Model getModel() {
		return this.model;
	}
	
}
