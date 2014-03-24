package org.ontoware.semweb4j.lessons.lesson6;

import org.ontoware.jrest.RestParams;
import org.ontoware.jrest.RestServer;
import org.ontoware.jrest.annotation.RestAddressByParameter;
import org.ontoware.jrest.response.RestResponse;

/*
 * implements a RESTful web server with an integer multiplication restlet
 */
public class Step2 {
	
	private static Step2 instance;
	
	/*
	 * run server with restlet
	 */
	public static void main(String[] args) {
		RestServer rs = new RestServer(8889);
		instance = new Step2();
		rs.registerRestlet("mul", instance);
		try {
			rs.start();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/*
	 * HTTP GET handler
	 */
	public RestResponse get(@RestAddressByParameter("x") int a,
							@RestAddressByParameter("y") int b) {
		
		//TODO explain ToXML & XmlOut (maybe later?)
		//TODO learn: mastering anonymous inner types
		
		//TODO explain different constructors: (maybe later?)
		//the one used in lesson6.Step1: new RestResponse(String responseString, String mimetype, int responseCode, String xslt, String css)
		//new RestResponse(Exception e)
		//new RestResponse(ToXML xmlResponse, RestParams params)
		//new RestResponse(Document responseDocument, String mimetype, int responseCode, String xslt, String css)
		//new RestResponse(Reader reader, String mimetype, int responseCode, String xslt, String css)		
		//new RestResponse(ToXML toxml, String mimetype, int responseCode, String xslt, String css)
		
		//TODO explain those:
		//response.addHeader(string, target)
		//response.getHeader()
		//response.getParameter()
		//response.getResponseBody()
		//response.toWriter(writer)
		//response.toStream(stream)
		
		return new RestResponse(String.valueOf(a*b), RestParams.MIME_TEXT_PLAIN, 200, null, null);
	}
	
}
