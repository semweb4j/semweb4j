package org.ontoware.rdf2go.impl.jena24;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;

import com.hp.hpl.jena.graph.Triple;

/**
 * Define an Iterator over Statements, building the Statements from Jena
 * Triples.
 */

public class TripleIterator implements ClosableIterator<Statement> {

	private com.hp.hpl.jena.util.iterator.ClosableIterator it;

	private long modelModificationCountAtCreationTime;

	private ModelImplJena24 modelImplJena;

	public TripleIterator(com.hp.hpl.jena.util.iterator.ClosableIterator it,
			long modelModificationCountAtCreationTime,
			ModelImplJena24 jenaModel) {
		this.it = it;
		this.modelModificationCountAtCreationTime = modelModificationCountAtCreationTime;
		this.modelImplJena = jenaModel;
	}

	public boolean hasNext() {
		assert modelImplJena.getModificationCount() == modelModificationCountAtCreationTime : "concurrent modification for iterator ("
				+ modelModificationCountAtCreationTime
				+ " but model is "
				+ modelImplJena.getModificationCount() + ")";
		return it.hasNext();
	}

	public Statement next() {
		assert modelImplJena.getModificationCount() == modelModificationCountAtCreationTime;
		Triple t = (Triple) it.next();
		return new StatementJena24Impl(this.modelImplJena, t.getSubject(), t.getPredicate(), t
				.getObject());
	}

	public void remove() {
		// it.remove();
		 assert modelImplJena.getModificationCount() ==
		 modelModificationCountAtCreationTime;
		 it.remove();
	}

	public void close() {
		it.close();
	}

}
