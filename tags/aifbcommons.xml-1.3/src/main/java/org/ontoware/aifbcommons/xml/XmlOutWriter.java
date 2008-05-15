package org.ontoware.aifbcommons.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Stack;

public class XmlOutWriter implements XmlOut {

	private Writer w;

	private boolean firstLine = true;

	private boolean inElement = false;

	private boolean inProcessingInstruction = false;

	private Stack<String> openThings = new Stack<String>();

	public XmlOutWriter(Writer w) throws IOException {
		this.w = w;
		w.write(XmlOut.XML_DECLARATION);
	}

	public String getOpentags() {
		String s = "";
		Iterator<String> it = openThings.iterator();
		while (it.hasNext()) {
			s += "/" + it.next();
		}
		return s;
	}

	public void open(String elementName) throws IOException {
		openThings.push(elementName);
		if (inElement) {
			w.write(">");
			inElement = false;
		}
		if (firstLine) {
			firstLine = false;
		} else {
			w.write("\n");
		}
		w.write("<" + elementName);
		inElement = true;
	}

	public void attribute(String name, String value) throws IOException {

		if (!(inElement | inProcessingInstruction))
			throw new IllegalStateException("Atributes are only allowed in elements. We are here "
					+ getOpentags()+" so cannot add "+name+"="+value);

		w.write(" ");
		w.write(name);
		w.write("=\"");
		w.write(XMLUtils.xmlencode(value));
		w.write("\"");
	}

	public void content(String rawContent) throws IOException {
		if (inElement) {
			w.write(">");
			inElement = false;
		}
		w.write(XMLUtils.xmlencode(rawContent));
	}

	public void close(String elementName) throws IOException {
		String open = openThings.pop();
		assert open.equals(elementName) : "trying to close '" + elementName
				+ "' but last opened was '" + open + "'";

		if (inElement) {
			w.write(">");
			inElement = false;
		}
		w.write("</" + elementName + ">");
	}

	public void comment(String comment) throws IOException {
		if (inElement) {
			w.write(">");
			inElement = false;
		}
		w.write("\n<!-- " + comment + " -->");
	}

	public void write(String s) throws IOException {
		w.write(s);
	}

	public void openProcessingInstruction(String processingInstruction) throws IOException {
		inProcessingInstruction = true;
		if (firstLine) {
			firstLine = false;
		} else {
			w.write("\n");
		}
		w.write("<?" + processingInstruction);
	}

	public void closeProcessingInstruction() throws IOException {
		inProcessingInstruction = false;
		w.write("?>");
	}

	public void doctype(String doctype, String publicID, String url) throws IOException {
		if (firstLine) {
			firstLine = false;
		} else {
			w.write("\n");
		}
		w.write("<!DOCTYPE " + doctype + " PUBLIC " + publicID + " " + url + ">");
	}

	public void flush() throws IOException {
		w.flush();
	}

	public void close() throws IOException {
		w.flush();
	}
}
