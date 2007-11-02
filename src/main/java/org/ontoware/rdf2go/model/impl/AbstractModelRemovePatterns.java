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

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.ModelRemovePatterns;
import org.ontoware.rdf2go.model.TriplePattern;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.UriOrVariable;

/**
 * The implementation first searches for all matching triples, copies them to
 * memory and then removes them. This is very inefficient. Please override!
 * 
 * @author voelkel
 * 
 */
public abstract class AbstractModelRemovePatterns extends
		AbstractModelAddRemove implements ModelRemovePatterns {

	public void removeStatements(TriplePattern triplePattern)
			throws ModelRuntimeException {
		ModelAddRemoveMemoryImpl toBeRemoved = new ModelAddRemoveMemoryImpl();
		toBeRemoved.addAll(this.findStatements(triplePattern));
		this.removeAll(toBeRemoved.iterator());
	}

	public void removeStatements(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		ModelAddRemoveMemoryImpl toBeRemoved = new ModelAddRemoveMemoryImpl();
		toBeRemoved.addAll(this.findStatements(subject, predicate, object));
		this.removeAll(toBeRemoved.iterator());
	}

}
