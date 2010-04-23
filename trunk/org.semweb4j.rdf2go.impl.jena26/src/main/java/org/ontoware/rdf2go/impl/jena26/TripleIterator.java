package org.ontoware.rdf2go.impl.jena26;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;

import com.hp.hpl.jena.graph.Triple;


/**
 * Define an Iterator over Statements, building the Statements from Jena
 * Triples.
 */

public class TripleIterator implements ClosableIterator<Statement> {
	
	private com.hp.hpl.jena.util.iterator.ClosableIterator<?> it;
	
	private long modelModificationCountAtCreationTime;
	
	private ModelImplJena26 modelImplJena;
	
	public TripleIterator(com.hp.hpl.jena.util.iterator.ClosableIterator<?> it,
	        long modelModificationCountAtCreationTime, ModelImplJena26 jenaModel) {
		this.it = it;
		this.modelModificationCountAtCreationTime = modelModificationCountAtCreationTime;
		this.modelImplJena = jenaModel;
	}
	
	public boolean hasNext() {
		assert this.modelImplJena.getModificationCount() == this.modelModificationCountAtCreationTime : "concurrent modification for iterator ("
		        + this.modelModificationCountAtCreationTime
		        + " but model is "
		        + this.modelImplJena.getModificationCount() + ")";
		return this.it.hasNext();
	}
	
	public Statement next() {
		assert this.modelImplJena.getModificationCount() == this.modelModificationCountAtCreationTime;
		Triple t = (Triple)this.it.next();
		return new StatementJena24Impl(this.modelImplJena, t.getSubject(), t.getPredicate(), t
		        .getObject());
	}
	
	public void remove() {
		// it.remove();
		assert this.modelImplJena.getModificationCount() == this.modelModificationCountAtCreationTime;
		this.it.remove();
	}
	
	public void close() {
		this.it.close();
	}
	
}
