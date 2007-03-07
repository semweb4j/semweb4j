/**
 * 
 */
package org.ontoware.jrest;

/**
 * As the name of the class saids it this makes a simple Exception
 * 
 * @author        $Author: romuald $
 * @version       $Id: RestException.java,v 1.3 2006/08/28 08:26:06 romuald Exp $
 *
 */

public class RestException extends RuntimeException {


	private static final long serialVersionUID = 1L;
	private int statusCode;
	private String errorMsg;

	/**
	 * Sets the error message and the satus code
	 * @param errorMsg
	 * @param statusCode
	 */
	public RestException( String errorMsg, int statusCode) {
		this.statusCode = statusCode;
		this.errorMsg = errorMsg;
	}
	
	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return this.statusCode;
	}
	
	/**
	 * @return the errorMsg
	 */
	public String getErrorMessage() {
		return this.errorMsg;
	}

}
