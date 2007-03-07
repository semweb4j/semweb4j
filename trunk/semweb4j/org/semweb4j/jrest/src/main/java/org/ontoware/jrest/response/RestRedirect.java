package org.ontoware.jrest.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.jrest.RestParams;

/**
 * 
 *
 */
public class RestRedirect extends RestResponse {

	private static final Log log = LogFactory.getLog( RestRedirect.class );
	/**
	 * 
	 * @param redirectURL
	 * @param xslt
	 * @param css
	 */
	public RestRedirect(String redirectURL, String xslt, String css) {
		super("Redirecting to "+redirectURL+"...", RestParams.MIME_TEXT_PLAIN, 303, xslt, css);
		super.addHeader("Location", ""+redirectURL);
		log.info("Redirect to "+redirectURL);
	}

}
