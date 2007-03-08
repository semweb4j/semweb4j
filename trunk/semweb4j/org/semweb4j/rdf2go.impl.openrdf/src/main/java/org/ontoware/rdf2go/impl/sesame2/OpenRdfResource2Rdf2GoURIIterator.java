package org.ontoware.rdf2go.impl.sesame2;

import java.util.Iterator;

import org.openrdf.model.Resource;
import org.openrdf.rdf2go.ConversionUtil;
import org.openrdf.util.iterator.CloseableIterator;

public class OpenRdfResource2Rdf2GoURIIterator implements Iterator<org.ontoware.rdf2go.model.node.URI> {

	private CloseableIterator<? extends Resource> it;

	public OpenRdfResource2Rdf2GoURIIterator(CloseableIterator<? extends Resource> it) {
		this.it = it;
	}

	public boolean hasNext() {
		return it.hasNext();
	}

	public org.ontoware.rdf2go.model.node.URI next() {
		Resource res = it.next();
		return (org.ontoware.rdf2go.model.node.URI) ConversionUtil.toRdf2go( res );
	}

	public void remove() {
		it.remove();
	}

}
