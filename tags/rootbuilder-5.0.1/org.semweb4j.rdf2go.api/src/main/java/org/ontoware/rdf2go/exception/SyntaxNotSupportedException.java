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
 * A runtime exception thrown if a method offers a choice of syntaxes, but the
 * chosen syntax is not supported by the adapter.
 * 
 * @author voelkel
 * 
 */
public class SyntaxNotSupportedException extends ModelRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5225692503846334223L;

	public SyntaxNotSupportedException() {
		super();
	}

	public SyntaxNotSupportedException(Exception e) {
		super(e);
	}

	public SyntaxNotSupportedException(String message, Exception e) {
		super(message, e);
	}

	public SyntaxNotSupportedException(String message) {
		super(message);
	}

}
