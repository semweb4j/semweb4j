package org.ontoware.jrest;

/**
 * an error caused by java objects that do not comply to jrest expectations
 * 
 * @author voelkel
 * 
 */
public class RestMappingException extends RestException {

	private Object restlet;

	/**
	 * Sets the error message and the satus code of RestException
	 * @param errorMsg
	 * @param statusCode
	 */
	public RestMappingException(String errorMsg, int statusCode) {
		super(errorMsg, statusCode);
	}
	
	/**
	 * Sets the error message and the default value of the satus code that is 500 of RestException
	 * @param errorMsg
	 */
	public RestMappingException(String errorMsg) {
		super(errorMsg, 500);
	}

	/**
	 * Sets the error message and the default value of the satus code that is 500 of RestException. 
	 * And Set a restlet that causes a bug
	 * @param restlet
	 * @param errorMsg
	 */
	public RestMappingException(Object restlet, String errorMsg) {
		super(errorMsg, 500);
		assert restlet != null;
		this.restlet = restlet;
	}

	/**
	 * 
	 * @return the restlet that caused the bug
	 */
	public Object getRestlet() {
		return this.restlet;
	}

	private static final long serialVersionUID = 7227632272716147883L;

}
