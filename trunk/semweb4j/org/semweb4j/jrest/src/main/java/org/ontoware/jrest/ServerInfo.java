package org.ontoware.jrest;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ontoware.jrest.response.RestResponse;

/**
 * Gives information about this RestServer
 * 
 * @author $Author: xamde $
 * @version $Id: ServerInfo.java,v 1.3 2006/10/11 17:06:43 xamde Exp $
 * 
 */
public class ServerInfo {

	private RestServer restServer;

	public ServerInfo(RestServer restServer) {
		this.restServer = restServer;
	}

	public String toString() {
		return "ServerInfo";
	}

	public RestResponse get() {
		// TODO return correct XHTML document
		Document index = DocumentHelper.createDocument();
		Element _ul = index.addElement("ul");
		for (Map.Entry<String, Object> e : restServer.path2restlet.entrySet()) {
			Element _li = _ul.addElement("li");
			Element _a = _li.addElement("a");
			_a.setText(e.getKey());
			_a.addAttribute("href", e.getKey());
			_li.addText(" (" + e.getValue() + ")");
		}
		return new RestResponse(index, RestParams.MIME_TEXT_HTML, 200, null, null);
	}}
