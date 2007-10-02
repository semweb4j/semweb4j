package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

/**
 * An iterator over nodes, based on an interator over QueryRows, such as
 * returned by RDF2GO.sparqlSelect(..)
 * 
 * @author voelkel
 */
public class ExtractingIterator implements ClosableIterator<Node> {

	private ClosableIterator<QueryRow> it;

	private Model model;

	private String extractVariable;

	public ExtractingIterator(Model model, ClosableIterator<QueryRow> it,
			String extractVariable) {
		this.model = model;
		this.it = it;
		this.extractVariable = extractVariable;
	}

	public boolean hasNext() {
		return it.hasNext();
	}

	public Node next() {
		return it.next().getValue(extractVariable);
	}

	public void remove() {
		it.remove();
	}

	public void close() {
		it.close();
	}

	public Model getModel() {
		return this.model;
	}

}
