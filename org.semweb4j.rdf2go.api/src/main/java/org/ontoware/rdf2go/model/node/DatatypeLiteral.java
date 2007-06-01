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
package org.ontoware.rdf2go.model.node;

import java.util.Date;

/**
 * RDF Literal representation of a datatype (usually xml schema datatype)
 * 
 * Implementations are expected to have valid implementations of equals( Object )
 * and hashCode()
 * 
 * @author mvo
 * 
 */
public interface DatatypeLiteral extends Literal {

	/**
	 * the URI normally is an URI for a xml schema datatype (xsd)
	 * 
	 * @return the URI of the datatype
	 */
	public URI getDatatype();

	/**
	 * 
	 * @return
	 * @throws NumberFormatException
	 *             if the literal value could not be parsed
	 * @throws ClassCastException
	 *             if the Literal is not an xsd:integer or a subtype of it
	 * @deprecated this should be part of a utility, not the core API. Use
	 *             org.ontoware.rdf2go.util.TypeConverter from rdf2go.base.impl
	 *             instead
	 */
	public int asInt() throws NumberFormatException, ClassCastException;

	/**
	 * @return Parses the string argument as a boolean. The boolean returned
	 *         represents the value true if the string argument is not null and
	 *         is equal, ignoring case, to the string "true".
	 * @throws ClassCastException
	 *             is the literal is not a DatatypeLiteral with a datatype of
	 *             xsd:boolean.
	 * @deprecated this should be part of a utility, not the core API. Use
	 *             org.ontoware.rdf2go.util.TypeConverter from rdf2go.base.impl
	 *             instead
	 */
	public boolean asBoolean() throws ClassCastException;

	/**
	 * @return a java.util.Date representing the literal value
	 * @throws ClassCastException
	 *             if the Literals is not a DatatypeLiteral with datatype of
	 *             xsd:dateTime, xsd:date or xsd:time.
	 * @deprecated this should be part of a utility, not the core API. Use
	 *             org.ontoware.rdf2go.util.TypeConverter from rdf2go.base.impl
	 *             instead
	 */
	public Date asDate() throws ClassCastException;

}