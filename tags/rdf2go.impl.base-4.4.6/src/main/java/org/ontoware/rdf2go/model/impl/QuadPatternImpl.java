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

import org.ontoware.rdf2go.model.QuadPattern;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.UriOrVariable;

/**
 * A statement with variables
 * @author voelkel
 *
 */
public class QuadPatternImpl extends TriplePatternImpl implements QuadPattern {

	private UriOrVariable context;

	public QuadPatternImpl(UriOrVariable context, ResourceOrVariable subject, UriOrVariable predicate, NodeOrVariable object) {
		super(subject,predicate,object);
		this.context = context;
	}

	public UriOrVariable getContext() {
		return this.context;
	}
	
	public boolean equals(Object o) {
		return ((o instanceof Statement)
				&& (this.getContext().equals( ((Statement) o ).getContext()))
				&& (this.getSubject().equals(((Statement) o).getSubject()))
				&& (this.getPredicate().equals(((Statement) o).getPredicate())) && (this
				.getObject().equals(((Statement) o).getObject())));
	}
	
	public int hashCode() {
		return context.hashCode() + super.hashCode();
	}

}
