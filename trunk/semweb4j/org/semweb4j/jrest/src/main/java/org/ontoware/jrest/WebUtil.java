package org.ontoware.jrest;

import java.io.IOException;

import org.ontoware.aifbcommons.xml.XmlOut;

public class WebUtil {

	public static void xmlHeader(XmlOut xo, RestParams params) throws IOException {
		// handled by xo already, TODO: remove this block
//		xo.openProcessingInstruction("xml");
//		xo.attribute("version", "1.0");
//		xo.attribute("encoding", "UTF-8");
//		xo.closeProcessingInstruction();

		if (params.getClientsideXslt() != null) {
			xo.openProcessingInstruction("xml-stylesheet");
			xo.attribute("type", RestParams.MIME_APPLICATION_XSLT_XML);
			xo.attribute("href", params.getClientsideXslt());
			xo.closeProcessingInstruction();
		}
	}

	public static void openXhtmlBody(XmlOut xo, RestParams params) throws IOException {
		xmlHeader(xo, params);
		xo.doctype("html", "\"-//W3C//DTD XHTML 1.0 Strict//EN\"",
				"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"");
		xo.open("html");
		xo.attribute("xmlns", "http://www.w3.org/1999/xhtml");
		xo.attribute("lang", "en");
		xo.open("head");
		xo.open("title");
		xo.content(params.getTitle());
		xo.close("title");
		xo.open("link");
		xo.attribute("href", params.getCss());
		xo.attribute("rel", "stylesheet");
		xo.attribute("type", "text/css");
		xo.close("link");
		xo.close("head");
		xo.open("body");
	}

	public static void closeXhtmlBody(XmlOut xo, RestParams params) throws IOException {
		xo.close("body");
		xo.close("html");
	}

}
