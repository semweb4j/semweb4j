package org.ontoware.jrest.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyberneko.html.parsers.SAXParser;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.ontoware.aifbcommons.xml.XMLUtils;
import org.ontoware.jrest.RestServer;
import org.ontoware.jrest.annotation.DefaultValue;
import org.ontoware.jrest.annotation.RestAddressByParameter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A sample application. 
 * @author $Author: xamde $
 * @version $Id: Cleaner.java,v 1.2 2006/09/15 09:51:06 xamde Exp $
 * 
 */

public class Cleaner {

	// /////////////
	// charsets from jdk

	/**
	 * Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the
	 * Unicode character set
	 */
	public static final String US_ASCII = "US-ASCII";

	/** ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1 */
	public static final String ISO_8859_1 = "ISO-8859-1";

	/** Eight-bit UCS Transformation Format */
	public static final String UTF_8 = "UTF-8";

	/** Sixteen-bit UCS Transformation Format, big-endian byte order */
	public static final String UTF_16BE = "UTF-16BE";

	/** Sixteen-bit UCS Transformation Format, little-endian byte order */
	public static final String UTF_16LE = "UTF-16LE";

	/**
	 * Sixteen-bit UCS Transformation Format, byte order identified by an
	 * optional byte-order mark
	 */
	public static final String UFT_16 = "UTF-16";

	// Windows-1252

	private static final Log log = LogFactory.getLog(Cleaner.class);

	private static boolean isRedirect(HttpMethod m) {
		return m.getStatusCode() >= 300 && m.getStatusCode() <= 399;
	}

	private static GetMethod getRedirect(HttpClient hc, HttpMethod m) throws HttpException,
			IOException {
		GetMethod get;
		assert isRedirect(m);

		String redirectLocation;
		Header locationHeader = m.getResponseHeader("location");
		if (locationHeader != null) {
			redirectLocation = locationHeader.getValue();
			get = new GetMethod(redirectLocation);
			hc.executeMethod(get);
			return get;
		} else {
			assert false : "The response is invalid and did not provide the new location for the resource."
					+ " Report an error or possibly handle the response like a 404 Not Found error.";
			return null;
		}
	}

	/**
	 * 
	 * @param login -
	 *            .htaccess login
	 * @param pass -
	 *            .htaccess pass
	 * @return a HttpClient configured
	 */
	public static HttpClient getHttpClient(String login, String pass) {
		HttpClient hc = new HttpClient();
		if (login != null && !login.equals("")) {
			hc.getParams().setAuthenticationPreemptive(true);
			Credentials defaultcreds = new UsernamePasswordCredentials(login, pass);
			hc.getState().setCredentials(AuthScope.ANY, defaultcreds);
		}
		return hc;
	}

	/**
	 * load document from the web
	 * 
	 * @param source source URL
	 * @param login for HTTP basic Authentication
	 * @param pass for HTTP basic Authentication
	 * @return an executed GetMethod
	 * @throws HttpException
	 * @throws IOException
	 */
	public static GetMethod getWithHttpClient(URL source, String login, String pass)
			throws HttpException, IOException {
		HttpClient hc = getHttpClient(login, pass);
		return getWithHttpClient(hc, source);
	}

	public static GetMethod getWithHttpClient(HttpClient hc, URL source) throws HttpException,
			IOException {
		GetMethod get = new GetMethod(source + "");
		get.setRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
		get.setFollowRedirects(true);
		hc.executeMethod(get);

		if (isRedirect(get))
			get = getRedirect(hc, get);
		return get;
	}

	/**
	 * 
	 * @param hc HttpClient pre-configured
	 * @param url to POST to
	 * @param requestParam for the POST-body
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static HttpMethod postWithHttpClient(HttpClient hc, URL url, String[][] requestParam)
			throws HttpException, IOException {

		log.debug("posting to " + url);
		PostMethod post = new PostMethod(url + "");
		post
				.setRequestHeader("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727; InfoPath.1)");

		List<Part> partList = new ArrayList<Part>();
		for (String[] entry : requestParam) {
			log.debug("adding " + entry[0] + " = " + entry[1]);
			partList.add(new StringPart(entry[0], entry[1]));
		}
		Part[] parts = (Part[]) partList.toArray(new Part[partList.size()]);
		post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));

		hc.executeMethod(post);
		HttpMethod m = post;
		if (isRedirect(post))
			m = getRedirect(hc, post);

		return m;
	}

	/**
	 * 
	 * @param source
	 *            URL of source document
	 * @param xslt
	 *            optional: URL of an XSLT to be applied to result
	 * @return a cleaned version of the HTML page at 'source'
	 * @throws IOException
	 * @throws HttpException
	 * @throws SAXException
	 * @throws TransformerException
	 * @throws DocumentException
	 */
	public static Document get(@RestAddressByParameter("source")
	URL source, @RestAddressByParameter("xslt")
	@DefaultValue("")
	URL xslt, @RestAddressByParameter("xpath")
	@DefaultValue("")
	String xpath, @RestAddressByParameter("login")
	@DefaultValue("")
	String login, @RestAddressByParameter("pass")
	@DefaultValue("")
	String pass) throws HttpException, IOException, SAXException, TransformerException,
			DocumentException {

		// parse it
		Document d = responseAsDocument(getWithHttpClient(source, login, pass));

		// ////////////
		// XSLT ?
		if (xslt != null && !xslt.equals("")) {
			Document xsltDoc = XMLUtils.getAsXML(xslt);
			d = XMLUtils.transform(d, xsltDoc);
		}

		if (xpath != null && !xpath.equals("")) {
			List list = d.selectNodes(xpath);
			log.debug(list.size() + " result nodes");
			d = DocumentHelper.createDocument();
			Element _xpathResult = d.addElement("result");
			for (Object o : list) {
				Node n = (Node) o;
				n.detach();
				_xpathResult.add(n);
			}
		}

		return d;
	}

	public static Document responseAsDocument(HttpMethod httpMethod) throws IOException,
			SAXException, DocumentException {
		InputStream inputstreamIgnoringEncoding = httpMethod.getResponseBodyAsStream();

		// charset handling
		Charset charset = Charset.forName(ISO_8859_1);
		// change charset according to w3c rules
		// header found?
		Header[] contentTypeHeader = httpMethod.getResponseHeaders("Content-Type");
		for (Header h : contentTypeHeader) {
			log.debug("Found Content-type header");
			log.debug(h.getValue());
			String value = h.getValue();
			if (value.contains("8859")) {
				// keep ISO-8859-1
				log.debug("Found evidence for ISO-8859-1 in HTTP response header '" + value + "'");
			} else if (value.contains("UTF")) {
				// danger!
				if (value.contains("8")) {
					charset = Charset.forName(UTF_8);
					log.debug("Found evidenve for UTF-8 in HTTP response header" + value + "'");
				} else
					throw new RuntimeException("Cannot handle the encoding stated in '" + value
							+ "'");
			}
		}

		Reader encodingAwareReader = new InputStreamReader(inputstreamIgnoringEncoding, charset);
		InputSource inputSource = new InputSource(encodingAwareReader);

		SAXParser saxParser = new SAXParser();
		saxParser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
		saxParser.setFeature("http://xml.org/sax/features/namespaces", true);
		saxParser.setFeature("http://cyberneko.org/html/features/insert-namespaces", true);

		// saxParser.parse(inputSource);

		SAXReader sx = new SAXReader();
		sx.setXMLReader(saxParser);
		Document d = sx.read(inputSource);
		return d;
	}

	public static void main(String[] args) throws Exception {
		RestServer rs = new RestServer(5555);
		rs.start();
		rs.registerRestlet("server", new Server());
		rs.registerRestlet("clean", new Cleaner());

		Document ontoviews = get(new URL("http://xamde.blogspot.com"), new URL(
				"http://localhost:5555/rest/server?key=blogspot2firstpost.xslt"), null, null, null);
		System.out.println(ontoviews.asXML());
	}
}
