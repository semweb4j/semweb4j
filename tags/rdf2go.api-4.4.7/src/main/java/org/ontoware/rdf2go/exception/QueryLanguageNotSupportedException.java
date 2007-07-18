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
package org.ontoware.rdf2go.exception;

public class QueryLanguageNotSupportedException extends ModelRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5838150544926081712L;

	public QueryLanguageNotSupportedException(Exception e) {
		super(e);
	}

	public QueryLanguageNotSupportedException(String message) {
		super(message);
	}

	public QueryLanguageNotSupportedException(String message, Exception e) {
		super(message,e);
	}

	public QueryLanguageNotSupportedException() {
		super("The given query language is not supported by this adapter.");
	}
}
