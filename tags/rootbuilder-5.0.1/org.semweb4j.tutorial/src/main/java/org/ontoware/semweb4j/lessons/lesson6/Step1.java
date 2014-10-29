package org.ontoware.semweb4j.lessons.lesson6;

import org.ontoware.jrest.RestParams;
import org.ontoware.jrest.RestServer;
import org.ontoware.jrest.annotation.RestAddressByParameter;
import org.ontoware.jrest.response.RestResponse;


/*
 * implements a RESTful web server with an integer adding restlet
 */
public class Step1 {
	
	private static Step1 instance;
	
	//TODO why do we need "Exception" here?
	/*
	 * initializing RestServer, registering Restlet for integer addition, starting RestServer
	 * look at
	 *   http://localhost:8888/rest/__main
	 *   http://localhost:8888/rest/add?a=5&b=15
	 * after running this program. 
	 */
	public static void main(String[] args) throws Exception {
		//TODO explain in web we use Jetty here (what is jetty? what is a restful web server?)
		//TODO understand & explain what this "webroot" is & what so setup elsewhere to get jRest working ...
		RestServer rs = new RestServer(8888, "./step1");
		// default is RestServer rs = new RestServer(5555);
		// which stores content in "./web"
		
		// creating singleton instance:
		instance = new Step1();
		
		//TODO explain the concept Restlet
		rs.registerRestlet("add", instance);
		// to unregister Restlet: rs.unregisterRestlet("step1");
		rs.start();
		// to stop the RestServer: rs.stop();
		
		//TODO clear confusion about rs.run() & rs.start()
	}
	
	//TODO explain @annotations - list of annotations
	/*
	 * HTTP GET handler
	 */
	public RestResponse get(@RestAddressByParameter("a") int a,
							@RestAddressByParameter("b") int b) {
		
		//TODO explain what a,b are
		
		//TODO explain RestResponse
		
		//TODO explain RestParams (it contains constants for mimetypes & keys - what are keys??)
		
		// setting response to a HTML String containing (a+b):
		String responseString = "<html><h2>" + (a + b)+"</h2>";
		// setting MIME-type to text/HTML:
		String mimetype = RestParams.MIME_TEXT_HTML;
		// setting responsecode to 200 (HTTP OK):
		int responsecode = 200;
		// ( ',null,null' here means we don't use xslt or css here)
		RestResponse response = new RestResponse(responseString, mimetype, responsecode, null, null);
		return response;
	}
	
}
