/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.ontoware.rdf2go.impl;

import org.ontoware.rdf2go.ModelFactory;

import org.openrdf.rdf2go.RepositoryModelFactory;

/**
 * Static binding for RDF2Go.
 */
public class StaticBinding {

	public static ModelFactory getModelFactory() {
		return new RepositoryModelFactory();
	}

}
