package org.ontoware.jrest.response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.ontoware.aifbcommons.xml.XMLUtils;
import org.ontoware.aifbcommons.xml.XmlOut;
import org.ontoware.aifbcommons.xml.XmlOutWriter;
import org.ontoware.jrest.RestParams;
import org.ontoware.jrest.ToXML;
import org.ontoware.jrest.WebUtil;


/**
 * This class represents the Response for REST. A restlet retunrs a RestResponse
 * 
 * @author voelkel
 * 
 */

public class RestResponse {

	private static final Log log = LogFactory.getLog(RestResponse.class);

	private Object response;

	private Map<String, String> header = new HashMap<String, String>();

	private RestParams parameter = new RestParams();

	/**
	 * Constructor.
	 */
	private RestResponse(Object responseObject, String mimetype, int responseCode, String xslt,
			String css) {
		assert responseObject != null;
		this.response = responseObject;
		parameter.setXslt(xslt);
		parameter.setCss(css);
		parameter.setMimetype(mimetype);
		parameter.setResponseCode(responseCode);
	}

	/**
	 * 
	 * @param responseDocument
	 * @param mimetype
	 * @param responseCode
	 * @param xslt
	 * @param css
	 */
	public RestResponse(Document responseDocument, String mimetype, int responseCode, String xslt,
			String css) {
		this((Object) responseDocument, mimetype, responseCode, xslt, css);
	}

	public RestResponse(ToXML xmlResponse, RestParams params) {
		assert xmlResponse != null;
		this.response = xmlResponse;
		this.parameter = (RestParams) params.clone();
	}

	public RestResponse(ToXML toXML, String mimetype, int responseCode, String xslt, String css) {
		this((Object) toXML, mimetype, responseCode, xslt, css);
	}

	/**
	 * 
	 * 
	 * @param reader
	 * @param mimetype
	 * @param responseCode
	 * @param xslt
	 * @param css
	 */
	public RestResponse(Reader reader, String mimetype, int responseCode, String xslt, String css) {
		this((Object) reader, mimetype, responseCode, xslt, css);
	}

	/**
	 * 
	 * @param responseString
	 * @param mimetype
	 * @param responseCode
	 * @param xslt
	 * @param css
	 */
	public RestResponse(String responseString, String mimetype, int responseCode, String xslt,
			String css) {
		this((Object) responseString, mimetype, responseCode, xslt, css);
	}

	/**
	 * Constructor.
	 */
	public RestResponse(Exception e) {
		response = "Exception!  "+e.toString() + " --- " + e.getCause();
		e.printStackTrace();
		log.error(e);

		// FIXME
		// XStream xstream = new XStream();
		// String xml = xstream.toXML(e);
		// this.response = xml;
		parameter.setMimetype(RestParams.MIME_TEXT_PLAIN);
		parameter.setResponseCode(500);
	}

	/**
	 * returns the response
	 * 
	 * @return the response
	 */
	public Object getResponseBody() {
		return response;
	}

	/**
	 * convenience method to dump to a stream
	 * 
	 * @param out
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void toStream(OutputStream stream) throws IOException, TransformerException {
		toWriter(new OutputStreamWriter(stream));
	}

	/**
	 * transform restResponse to Stream
	 */
	public void toWriter(Writer writer) throws IOException, TransformerException {
		Object result = this.getResponseBody();
		if (result instanceof Reader) {
			Reader reader = (Reader) result;
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();
			while (line != null) {
				writer.write(line + "\n");
				line = br.readLine();
			}
		} else if (result instanceof ToXML) {
			ToXML toXML = (ToXML) result;
			// style
			if (parameter.getXslt() != null) {
				log.warn("styling with xslt = " + parameter.getXslt());
				StringWriter sw = new StringWriter();
				toXML.toXML(new XmlOutWriter(sw), getParameter());
				String s = sw.getBuffer().toString();
				Document d = XMLUtils.loadXML(s);
				Document xslt = XMLUtils.getAsXML(parameter.getXslt());
				d = XMLUtils.transform(d, xslt);
				XMLUtils.write(d, writer);
			} else {
				log.debug("Rendering ToXML without XSLT");
				// faster!
				XmlOut xo = new XmlOutWriter(writer);
				WebUtil.xmlHeader(xo, getParameter());
				toXML.toXML(xo, getParameter());
			}
		} else if (result instanceof Document) {

			log.debug("Rendering document");

			// TODO use xsltURL and cssURL
			Document resultDoc = (Document) result;
			// prepare xml writer
			// style
			if (parameter.getXslt() != null) {
				log.debug("xslt = " + parameter.getXslt());
				Document xslt = XMLUtils.getAsXML(parameter.getXslt());
				resultDoc = XMLUtils.transform(resultDoc, xslt);
			} else
				log.debug("No xslt given");

			XMLUtils.write(resultDoc, writer);
			/**
			 * // FIXME there must be anoter way to get rid of the XML
			 * delcaration StringWriter sw = new StringWriter();
			 * XMLUtils.write(resultDoc, sw); StringBuffer res = sw.getBuffer();
			 * String trunc = sw.toString().substring("<?xml version='1.0'
			 * encoding='utf-8'?>".length()); writer.write( trunc );
			 */
		} else {
			log.warn("Rendering via toString()");
			writer.write("" + result);
		}
	}

	public RestParams getParameter() {
		return this.parameter;
	}

	/**
	 * returns the header
	 * 
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		return this.header;
	}

	/**
	 * add header
	 */
	public void addHeader(String string, String target) {
		header.put(string, target);
	}

}
