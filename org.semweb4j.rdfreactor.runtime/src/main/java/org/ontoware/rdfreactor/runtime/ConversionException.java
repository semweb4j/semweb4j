package org.ontoware.rdfreactor.runtime;

/**
 * Thrown if a type conversion was not possible.
 * 
 * @author voelkel
 */
@Patrolled
public class ConversionException extends RuntimeException {
	
	private static final long serialVersionUID = -5653207426106765259L;
	
	public ConversionException() {
		super();
	}
	
	public ConversionException(String message) {
		super(message);
	}
	
	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ConversionException(Throwable cause) {
		super(cause);
	}
}
