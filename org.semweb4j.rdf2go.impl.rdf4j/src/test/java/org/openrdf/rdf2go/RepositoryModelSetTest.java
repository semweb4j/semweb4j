/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.model.AbstractModelSetTest;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public class RepositoryModelSetTest extends AbstractModelSetTest {

	Statement s = new StatementImpl(new URIImpl("urn:testcontext"), new URIImpl("urn:test"), new URIImpl(
			"urn:testpred"), new URIImpl("urn:testobj"));

	RepositoryModelFactory rmodelfactory = new RepositoryModelFactory();

	@Override
	public ModelFactory getModelFactory() {
		return this.rmodelfactory;
	}
	
}
