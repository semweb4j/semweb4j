package org.ontoware.semversion;

public class ConsistencyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7768184938706944938L;

	public ConsistencyException() {
		super();
	}

	public ConsistencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConsistencyException(String message) {
		super(message);
	}

	public ConsistencyException(Throwable cause) {
		super(cause);
	}

}
