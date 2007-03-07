/**
 * 
 */
package org.ontoware.jrest.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.ontoware.aifbcommons.xml.XMLUtils;
import org.ontoware.jrest.RestParams;
import org.ontoware.jrest.RestServer;
import org.ontoware.jrest.annotation.RestAddressByPath;
import org.ontoware.jrest.annotation.RestContent;
import org.ontoware.jrest.response.RestError;
import org.ontoware.jrest.response.RestResponse;

/**
 * Example how to implement a classical file server with jREST.
 * 
 * Defaults to serve from folder './web'
 * 
 * @author $Author: xamde $
 * @version $Id: Server.java,v 1.3 2006/10/11 17:06:44 xamde Exp $
 * 
 */

public class Server {
	
	private static final Log log = LogFactory.getLog(Server.class);

	private String root;

	public Server() {
		this("./web");
	}

	public Server(String root) {
		this.root = root;
	}

	public RestResponse get(@RestAddressByPath
	String path) {
		File f = new File(this.root + "/" + path);
		log.warn("Looking for "+f);
		if (f.exists()) {
			if (path.endsWith("xml"))
				return new RestResponse(XMLUtils.getAsXML(f),RestParams.MIME_APPLICATION_XML, 200, null,null);
			else if (path.endsWith("xslt"))
				return new RestResponse(XMLUtils.getAsXML(f),RestParams.MIME_APPLICATION_XSLT_XML, 200, null,null);
			else if (path.endsWith("html"))
				return new RestResponse(XMLUtils.getAsXML(f),RestParams.MIME_TEXT_HTML, 200, null,null);
			else if (path.endsWith("xhtml"))
				return new RestResponse(XMLUtils.getAsXML(f),RestParams.MIME_APPLICATION_XHTML, 200, null,null);
		else {
				FileReader fr;
				try {
					fr = new FileReader(f);
					return new RestResponse(fr,RestParams.MIME_TEXT_PLAIN,200,null,null);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
			
		} else {
			return new RestError("could not find "+f.getAbsolutePath());
		}
		

	}

	public static void main(String[] args) throws Exception {
		RestServer rs = new RestServer(5555);
		rs.start();
		rs.registerRestlet("server", new Server());
	}

	public void put(@RestAddressByPath
	String path, @RestContent
	Document d) throws IOException {
		File f = new File(this.root +"/" + path);
		OutputFormat of = new OutputFormat();
		of.setEncoding("UTF-8");

		FileWriter fw = new FileWriter(f);
		fw.write(d.asXML());
		fw.close();

		// XMLWriter xw = new XMLWriter( fw);
		// xw.write(d);
		// xw.flush();
	}

}
