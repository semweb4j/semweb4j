package org.ontoware.jrest;

import java.net.URL;
import java.util.HashMap;

/**
 * Rest request and response parameters
 * 
 * Modeled as key-value pais
 * 
 * @author voelkel
 * 
 */
public class RestParams extends HashMap<String, String> {

	// Mime types

	/**
	 * 
	 */
	private static final long serialVersionUID = -1800385809700046582L;

	/** text/plain */
	public static final String MIME_TEXT_PLAIN = "text/plain";

	/** text/html */
	public static final String MIME_TEXT_HTML = "text/html";

	/** application/xhtml+xml */
	public static final String MIME_APPLICATION_XHTML = "application/xhtml+xml";

	/** application/xml */
	public static final String MIME_APPLICATION_XML = "application/xml";

	/** application/rdf+xml */
	public static final String MIME_APPLICATION_RDF_XML = "application/rdf+xml";

	public static final String MIME_APPLICATION_XSLT_XML = "text/xsl"; //"application/xslt+xml";
	
	
	/** application/sparql-results+xml */
	public static final String MIME_SPARQL_RESULTS_XML = "application/sparql-results+xml";

	/** application/sparql-results+json */
	public static final String MIME_SPARQL_RESULTS_JSON = "application/sparql-results+json";

	// 

	/** application/sparql-results+json */
	public static final String KEY_RESPONSE_CODE = "responseCode";

	public static final String KEY_XSLT = "xslt";

	public static final String KEY_CSS = "css";

	public static final String KEY_MIMETYPE = "mimetype";

	public static final String KEY_MODE = "mode";

	public static final String KEY_HTML_TITLE = "title";

	public static final String KEY_CLIENTSIDE_XSLT = "clientside-xslt";


	/**
	 * returns the mimtype
	 * 
	 * @return the mimtype
	 */
	public String getMimetype() {
		return get(KEY_MIMETYPE);
	}

	/**
	 * returns the css
	 * 
	 * @return the css
	 */
	public String getCss() {
		return get(KEY_CSS);
	}

	/**
	 * returns the xslt
	 * 
	 * @return the xslt
	 */
	public String getXslt() {
		return get(KEY_XSLT);
	}

	/**
	 * set the css
	 */
	public void setCss(String css) {
		if (css == null)
			remove(KEY_CSS);
		else
			put(KEY_CSS, css);
	}

	/**
	 * set the xslt
	 */
	public void setXslt(String xslt) {
		if (xslt == null)
			remove(KEY_XSLT);
		else
			put(KEY_XSLT, xslt);
	}

	/**
	 * set the mimetype
	 */
	public void setMimetype(String mimetype) {
		put(KEY_MIMETYPE, mimetype);
	}

	/**
	 * set the responseCode
	 */
	public void setResponseCode(int responseCode) {
		put(KEY_RESPONSE_CODE, "" + responseCode);
	}

	/**
	 * returns the responseCode
	 * 
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return Integer.parseInt(get(KEY_RESPONSE_CODE));
	}

	public void setCss(URL cssURL) {
		if (cssURL == null)
			setCss((String) null);
		else
			setCss("" + cssURL);
	}

	public void setXslt(URL xsltURL) {
		if (xsltURL == null)
			setXslt((String) null);
		else
			setXslt("" + xsltURL);
	}

	public String getMode() {
		return get(KEY_MODE);
	}

	public void setMode(String mode) {
		put(KEY_MODE, mode);
	}

	public String toString() {
		String s = "";
		for (String key : keySet()) {
			s += "\n" + key + " : " + get(key);
		}
		return s;
	}

	public boolean hasMode(String desiredMode) {
		return getMode() != null && getMode().equals(desiredMode);
	}

	/** title of html page */
	public void setTitle(String title) {
		put(KEY_HTML_TITLE, title);
	}

	public String getTitle() {
		return get(KEY_HTML_TITLE);
	}

	public void setClientsideXslt(String xsltURL) {
		put(KEY_CLIENTSIDE_XSLT, xsltURL );
	}
	
	public String getClientsideXslt() {
		return get(KEY_CLIENTSIDE_XSLT);
	}

}
