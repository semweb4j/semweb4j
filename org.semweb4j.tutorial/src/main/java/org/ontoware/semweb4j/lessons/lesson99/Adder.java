package org.ontoware.semweb4j.lessons.lesson99;

import org.ontoware.jrest.RestParams;
import org.ontoware.jrest.RestServer;
import org.ontoware.jrest.annotation.RestAddressByParameter;
import org.ontoware.jrest.response.RestResponse;

public class Adder {

	public static void main(String[] args) throws Exception {
		RestServer rs = new RestServer(8888);
		Adder adder = new Adder();
		rs.registerRestlet("add", adder);
		rs.start();
	}

	public RestResponse get(@RestAddressByParameter("one")
	int a, @RestAddressByParameter("two")
	int b) {
		return new RestResponse("<html><h2>" + (a + b)+"</h2>", RestParams.MIME_TEXT_HTML, 200,
				null, null);
	}
	
	

}
