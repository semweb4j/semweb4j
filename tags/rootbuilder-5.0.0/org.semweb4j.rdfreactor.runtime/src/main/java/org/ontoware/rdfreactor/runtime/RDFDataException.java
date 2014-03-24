/*
 * Created on 17.04.2005
 *
 */
package org.ontoware.rdfreactor.runtime;

/**
 * A <b>RDFDataException</b> is thrown e.g. when doing a (single value) get, 
 * but there are multiple values for the property.
 * 
 * @author mvo 
 */
public class RDFDataException extends RuntimeException {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257565109529358387L;

	/**
	 * @param message
	 */
	public RDFDataException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RDFDataException(String message, Throwable cause) {
		super(message, cause);
	}

}
