package org.ontoware.semversion;

public class DuplicateLabelException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4794982367192651575L;

	public DuplicateLabelException() {
		super();
	}

	public DuplicateLabelException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateLabelException(String message) {
		super(message);
	}

	public DuplicateLabelException(Throwable cause) {
		super(cause);
	}
	

}
