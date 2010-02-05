/**
 * LICENSE INFORMATION
 *
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2010
 *
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.exception;

/**
 * This runtime exception is thrown if a given query string could not be parsed
 * and interpreted as a valid query string.
 * 
 * Adapters should include help to refine the query to become valid, e.g. show
 * which part of the query could not be parsed.
 * 
 * @author voelkel
 * 
 */
public class MalformedQueryException extends ModelRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -810711996311935876L;

	public MalformedQueryException() {
		super();
	}

	public MalformedQueryException(Exception e) {
		super(e);
	}

	public MalformedQueryException(String message, Exception e) {
		super(message, e);
	}

	public MalformedQueryException(String message) {
		super(message);
	}
}
