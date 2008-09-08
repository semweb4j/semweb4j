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
package org.ontoware.rdf2go.model.node.impl;

import java.util.Date;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;

public abstract class ResourceImpl implements Resource {

	@Override
	public abstract boolean equals( Object other );
	
	@Override
	public abstract int hashCode();

	public Resource asResource() throws ClassCastException {
		return this;
	}

	public Literal asLiteral() throws ClassCastException {
		throw new ClassCastException("Cannot call this on a resource");
	}

	public DatatypeLiteral asDatatypeLiteral() throws ClassCastException {
		throw new ClassCastException("Cannot call this on a resource");
	}

	public LanguageTagLiteral asLanguageTagLiteral() throws ClassCastException {
		throw new ClassCastException("Cannot call this on a resource");
	}

	public String asString() throws ModelRuntimeException {
		throw new ModelRuntimeException("Cannot call this on a resource");
	}

	public int asInt() throws ModelRuntimeException {
		throw new ModelRuntimeException("Cannot call this on a resource");
	}

	public boolean asBoolean() throws ModelRuntimeException {
		throw new ModelRuntimeException("Cannot call this on a resource");
	}

	public Date asDate() throws ModelRuntimeException {
		throw new ModelRuntimeException("Cannot call this on a resource");
	}
	

}
