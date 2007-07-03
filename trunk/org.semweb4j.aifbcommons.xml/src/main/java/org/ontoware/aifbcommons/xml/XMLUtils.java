/*
 * Created on 16.05.2005
 *
 */
package org.ontoware.aifbcommons.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.XPP3Reader;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Helper class for XMl handling
 * 
 * @author mvo
 * 
 */
public class XMLUtils {

	private static Log log = LogFactory.getLog(XMLUtils.class);

	public static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

	/** loads from string as URL */
	public static Document getAsXML(String url) {
		assert url != null;
		log.debug("loading xml from URL(?) '"+url+"'");
		try {
			return getAsXML(new URL(url));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static Document getAsXML(URL url) {
		SAXReader reader = new SAXReader();
		try {
			return reader.read(url);
		} catch (DocumentException e) {
			log.info("Troublesome URL: "+url);
			throw new RuntimeException(e);
		}
	}

	public static Document getAsXML(File file) {
		assert file.exists() : file.getAbsoluteFile() + " not found";
		try {
			SAXReader xmlReader = new SAXReader();
			return xmlReader.read(file);
		} catch (DocumentException e) {
			throw new RuntimeException(file.getAbsoluteFile() + " threw " + e);
		}
	}

	public static Document getAsXML(InputStream is) throws DocumentException, IOException,
			XmlPullParserException {
		XPP3Reader xr = new XPP3Reader();
		// SAXReader sr = new SAXReader();
		// sr.setEncoding("ISO-8859-1");
		return xr.read(is);
	}

	public static Document getAsXML(Reader r) throws DocumentException, IOException,
			XmlPullParserException {
		XPP3Reader xr = new XPP3Reader();
		// SAXReader sr = new SAXReader();
		// sr.setEncoding("ISO-8859-1");
		return xr.read(r);
	}

	public static Document loadXML(String xmlAsString) {
		SAXReader reader = new SAXReader();
		StringReader sr = new StringReader(xmlAsString);
		try {
			return reader.read(sr);
		} catch (DocumentException e) {
			log.warn("Could not parse: " + xmlAsString);
			throw new RuntimeException(e);
		}
	}

	public static Document loadXMLFile(String filename) {
		return loadXMLFile(new File(filename));
	}

	public static Document loadXMLFile(File file) {
		SAXReader reader = new SAXReader();
		try {
			FileReader fr = new FileReader(file);
			return reader.read(fr);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * procudes correct UTF8
	 * 
	 * @param d
	 * @param f
	 * @throws IOException
	 */
	public static void write(Document d, File f) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf8");
		write(d, osw);
	}

	public static void write(Document d, Writer w) throws IOException {
		// TODO: check correct utf8 output
		// prepare xml writer
		OutputFormat fo = OutputFormat.createPrettyPrint();
		fo.setEncoding("utf-8"); // XML encoding name
		fo.setIndent(true);
		fo.setIndent("  ");
		fo.setTrimText(false);
		fo.setXHTML(true);
		XMLWriter xw = new XMLWriter(fo);
		xw.setWriter(w);
		xw.write(d);
		xw.flush();
	}

	public static void write(Document d, OutputStream out, String outputCharset) throws IOException {
		OutputStreamWriter outw = new OutputStreamWriter(out, outputCharset);
		write(d, outw);
	}

	public static String xmldecode(String in) {
		String result = in;
		result = result.replace("&amp;", "&");
		result = result.replace("&lt;", "<");
		result = result.replace("&gt;", ">");
		result = result.replace("&apos;", "'");
		result = result.replace("&quot;", "\"");
		return result;

	}

	public static String xmlencode(String in) {
		String result = in;
		result = result.replace("&", "&amp;");
		result = result.replace("<", "&lt;");
		result = result.replace(">", "&gt;");
		result = result.replace("'", "&apos;");
		result = result.replace("\"", "&quot;");
		return result;
	}

	/**
	 * Streaming transformation
	 * @param in
	 * @param xslt
	 * @param out
	 * @throws TransformerException
	 */
	public static void transform(Reader in, Document xslt, Writer out) throws TransformerException {
		
		// create transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		DocumentSource styleSource = new DocumentSource(xslt);
		Transformer transformer = factory.newTransformer(styleSource);
		
		// perform transformation
		StreamSource source = new StreamSource(in);
		StreamResult result = new StreamResult(out);
		
		transformer.transform(source, result);
	}

	/**
	 * 
	 * @param in
	 * @param xslt
	 * @return 'in' styled with 'xslt'
	 * @throws TransformerException
	 * @throws IOException
	 */
	public static Document transform(Document in, Document xslt) throws TransformerException,
			IOException {
		// create transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		DocumentSource styleSource = new DocumentSource(xslt);
		Transformer transformer = factory.newTransformer(styleSource);
		// perform transformation
		DocumentSource source = new DocumentSource(in);
		DocumentResult result = new DocumentResult();
		transformer.transform(source, result);
		return result.getDocument();
	}

	public static String transformToString(Document in, Document xslt) throws TransformerException,
			IOException {
		// create transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		DocumentSource styleSource = new DocumentSource(xslt);
		Transformer transformer = factory.newTransformer(styleSource);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		// perform transformation
		DocumentSource source = new DocumentSource(in);

		// StringWriter sw = new StringWriter();
		// StreamResult result = new StreamResult(sw);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter outw = new OutputStreamWriter(out, "UTF-8");
		StreamResult result = new StreamResult(outw);
		transformer.transform(source, result);

		// return sw.getBuffer().toString();
		return out.toString("UTF-8");

	}

	public static Document toDom4jDocument(org.w3c.dom.Document w3cDomDocument) {
		DOMReader xmlReader = new DOMReader();
		return xmlReader.read(w3cDomDocument);
	}

}
