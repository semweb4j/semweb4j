package org.ontoware.jrest.response;

import org.ontoware.jrest.RestParams;


/**
 * Makes an error response
 *
 */
public class RestError extends RestResponse {

	public RestError(String target) {
		super("Error: "+target, RestParams.MIME_TEXT_PLAIN, 303,null,null);
	}

}
