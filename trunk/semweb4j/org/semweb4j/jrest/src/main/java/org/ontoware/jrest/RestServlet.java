/*
 * GenericServlet.java
 *
 * Created on 2. Februar 2005, 00:06
 */

package org.ontoware.jrest;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.ontoware.jrest.annotation.AnnotationUtils;
import org.ontoware.jrest.annotation.DefaultValue;
import org.ontoware.jrest.annotation.RestAddressByParameter;
import org.ontoware.jrest.annotation.RestAddressByPath;
import org.ontoware.jrest.annotation.RestContent;
import org.ontoware.jrest.converter.JavaStringConverter;
import org.ontoware.jrest.response.RestResponse;

/**
 * This Servlet maps URL+URI combinations to Restlets.
 * 
 * @author Ignacio
 */
public class RestServlet extends HttpServlet {

	public static final String GET = "get";

	public static final String PUT = "put";

	public static final String POST = "post";

	public static final String DELETE = "delete";

	private static final Log log = LogFactory.getLog(RestServlet.class);

	public static final String XREST_METHOD = "xrest-method";

	public static final String XREST_CSS = "xrest-css";

	/** xrest-xslt */
	public static final String XREST_XSLT = "xrest-xslt";

	public static final String XREST_MIMETYPE = "xrest-mime";

	public static final String XREST_STATUSCODE = "xrest-status";
	
	public static final String XREST_HELP = "xrest-help";

	/*
	 * Global variables
	 */

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257565096644195384L;

	private Map<String, Class> path2restlet;

	/**
	 * Initializes the servlet.
	 */
	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.path2restlet = (Map<String, Class>) config.getServletContext().getAttribute(
				RestServer.PATH2RESTLET);

		assert path2restlet != null : "Could not get mapping from context";
	}

	/**
	 * Destroys the servlet.
	 */
	public void destroy() {

	}

	/**
	 * convert restParameter into javaParameter:
	 * 
	 * for each javaParameter:
	 * <ul>
	 * <li>search for restParameter with correct name
	 * <li> if none found, use same default values
	 * <li>try to convert to desired type
	 * </ul>
	 * 
	 * @throws RestMappingException
	 * @throws URISyntaxException 
	 * @throws DecoderException 
	 */
	private JavaMethodCall toMethodCall(HttpServletRequest request) throws ServletException,
			IOException, RestMappingException, URISyntaxException, DecoderException {

		JavaMethodCall jmc = new JavaMethodCall();

		// extract key from uri: warning, double slashes will get normalized
		String requestPath = request.getPathInfo();
		if (requestPath == null)
			return null;

		String[] segments = requestPath.split("/");
		String requestKey = segments[1];
		log.debug("extracted request key '" + requestKey + "' from URI '" + requestPath + "'");

		jmc.restlet = path2restlet.get(requestKey);
		if (jmc.restlet == null)
			throw new RestMappingException("Found no object mapped at key '" + requestKey
					+ "'. See '/rest/__main' for a list.");
		String methodName = request.getMethod().toLowerCase();

		String methodOverride = getSingleParamValue(request, XREST_METHOD);
		if (methodOverride != null) {
			log.info("overriding request method with " + methodOverride);
			methodName = methodOverride;
		}
		
		
		Map<?, ?> restParameter = request.getParameterMap();
		Set<?> restParameterNames = restParameter.keySet();

		// introspection: find method named like HTTP-method

		boolean foundMethodWithGivenName = false;

		for (Method method : jmc.restlet.getClass().getMethods()) {
			// TODO: ignore case ok?
			if (method.getName().equalsIgnoreCase(methodName)) {
				foundMethodWithGivenName = true;
				// assume we have only one
				if (jmc.method != null)
					throw new RestMappingException(jmc.restlet, "Found multiple methods named '"
							+ methodName + "' in '" + jmc.restlet.getClass()
							+ "', this is illegal for jREST");
				jmc.method = method;
			}
		}

		if (!foundMethodWithGivenName) {
			throw new RestMappingException(jmc.restlet, "Requested method '"
					+ methodName.toUpperCase() + "' via HTTP, but could not find a Java method "
					+ methodName.toLowerCase() + "()");
		}

		jmc.params = new Object[jmc.method.getParameterTypes().length];

		for (int i = 0; i < jmc.method.getParameterTypes().length; i++) {
			Class<?> javaParameterType = jmc.method.getParameterTypes()[i];
			log.debug("examining parameter no. " + i + " of type " + javaParameterType);

			Map<Class<?>, Annotation> type2annotation = AnnotationUtils.asMap(jmc.method
					.getParameterAnnotations()[i]);
			RestAddressByParameter restAddressByParameter = (RestAddressByParameter) type2annotation
					.get(RestAddressByParameter.class);
			RestAddressByPath restAddressByPath = (RestAddressByPath) type2annotation
					.get(RestAddressByPath.class);
			RestContent restContent = (RestContent) type2annotation.get(RestContent.class);

			// get java parameters from address and content
			if (restAddressByParameter != null) {
				// convert to java
				String restParameterName = restAddressByParameter.value();
				// do we have a parameter with this name?
				String restValue;
				if (restParameterNames.contains(restParameterName)) {
					log.debug("found required parameter '" + restParameterName
							+ "' in HTTP query string");
					// TODO better param conversion from string arrays
					restValue = ((String[]) restParameter.get(restParameterName))[0];
					log.debug("value = "+restValue);
				} else {
					log.debug("could not find required parameter '" + restParameterName
							+ "' in HTTP query string");
					DefaultValue defaultValue = (DefaultValue) type2annotation
							.get(DefaultValue.class);

					if (defaultValue != null) {
						restValue = defaultValue.value();
						log.debug("using default value from annotation: " + restValue);
					} else {
						// error
						log.warn("param '" + restParameterName
								+ "': found no value AND no default value");
						RestMappingException rme = new RestMappingException(jmc.restlet, "param '"
								+ restParameterName + "': found no value AND no default value");
						assert rme.getRestlet() != null;
						throw rme;
					}
				}
				log.debug("convert restValue to javaType");
				jmc.params[i] = JavaStringConverter.convertToObject(restValue, javaParameterType);
			} else if (restAddressByPath != null) {
				String restValue = request.getPathInfo().substring(1);
				restValue = restValue.substring(restValue.indexOf("/") + 1);

				log.debug("try to set param '" + i + "' of type '" + javaParameterType
						+ "' from path = " + restValue);
				jmc.params[i] = JavaStringConverter.convertToObject(restValue, javaParameterType);
			} else if (restContent != null) {
				log.debug("try to set param '" + i + "' of type '" + javaParameterType
						+ "' from content");

				if (javaParameterType.equals(Reader.class)) {
					jmc.params[i] = request.getReader();
				} else if (javaParameterType.equals(String.class)) {
					Reader r = request.getReader();
					StringBuffer buf = new StringBuffer();
					int c = r.read();
					while (c != -1) {
						buf.append((char) c);
						c = r.read();
					}
					jmc.params[i] = buf.toString();
				} else if (javaParameterType.equals(Document.class)) {
					Reader r = request.getReader();
					// TODO convert from content using standard functions from
					// commons-xxx
					StringBuffer buf = new StringBuffer();
					int c = r.read();
					while (c != -1) {
						buf.append((char) c);
						c = r.read();
					}
					// TODO validation
					String xmlAsString = buf.toString();
					log.debug("content buf = " + xmlAsString);
					Document d = null;
					if (xmlAsString.length() > 0) {
						try {
							SAXReader reader = new SAXReader();
							StringReader sr = new StringReader(xmlAsString);
							d = reader.read(sr);
						} catch (DocumentException e) {
							throw new RuntimeException("Could not parse: " + xmlAsString);
						}
					}
					jmc.params[i] = d;
				} else {
					throw new RestMappingException(jmc.restlet,
							"Cannot convert response body to type " + javaParameterType);
				}
			} else
				throw new RestMappingException(jmc.restlet, jmc.restlet.getClass().getName() + "."
						+ methodName + "( ... " + javaParameterType.getName()
						+ " ...)' is neither annotated with @RestAddress nor with @RestContent");
		}

		return jmc;
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RestResponse restResponse = null;

		try {

			JavaMethodCall jmc = toMethodCall(request);

			Object result;
			if (jmc == null) {
				result = new RestResponse(new RuntimeException(
						"Found no restlet. Use '__main' to get a list. Request path was '"
								+ request.getRequestURI() + "'"));
			} else {
				result = jmc.execute();
			}
			if (result instanceof RestResponse) {
				// ok
				restResponse = (RestResponse) result;
			} else if (result instanceof Document) {
				restResponse = new RestResponse((Document) result,
						RestParams.MIME_APPLICATION_XML, 200, null, null);
			} else if (result instanceof String) {
				restResponse = new RestResponse((String) result, RestParams.MIME_TEXT_PLAIN, 200,
						null, null);
			} else if (result == null) {
				restResponse = new RestResponse("Method returned null",
						RestParams.MIME_TEXT_PLAIN, 500, null, null);

			} else {
				restResponse = new RestResponse("Cannot handle instance of " + result.getClass()
						+ ". Use a RestPesponse instead", RestParams.MIME_TEXT_PLAIN, 500, null,
						null);
			}
			// handle formatting override params

		} catch (RestMappingException rme) {
			Object restlet = rme.getRestlet();
			if (restlet == null) {
				restResponse = new RestResponse(new RuntimeException(
						"Got an exception. Use '__main' to get a list. Request path was '"
								+ request.getRequestURI() + "'"));
			} else {
				Document d = RestletInfo.getDocumentation(restlet);
				d.getRootElement().addElement("error").addText("" + rme);
				restResponse = new RestResponse(d, RestParams.MIME_APPLICATION_XML, 500, null,
						null);
			}
		} catch (RuntimeException re) {
			restResponse = new RestResponse(re);
		} catch (IOException e) {
			restResponse = new RestResponse(e);
		} catch (URISyntaxException e) {
			restResponse = new RestResponse(e);
		} catch (ServletException e) {
			restResponse = new RestResponse(e);
		} catch (DecoderException e) {
			restResponse = new RestResponse(e);
		}

		String help = getSingleParamValue(request, XREST_HELP);
		if (help != null) {
			log.info("help on xrest");
			restResponse = new RestResponse("Available 'xrest' overrides are:\n"
					+ "xrest-method, xrest-css, xrest-xslt, xrest-mimetype, xrest-statuscode",
					RestParams.MIME_TEXT_PLAIN,200,null,null);
		}		
		
		String css = getSingleParamValue(request, XREST_CSS);
		if (css != null) {
			log.info("overriding css with " + css);
			URL cssURL = new URL(css);
			restResponse.getParameter().setCss(cssURL);
		}
		String xslt = getSingleParamValue(request, XREST_XSLT);
		if (xslt != null) {
			log.info("overriding xslt with " + xslt);
			URL xsltURL = new URL(xslt);
			restResponse.getParameter().setXslt(xsltURL);
		}
		String mime = getSingleParamValue(request, XREST_MIMETYPE);
		if (mime != null) {
			log.info("overriding mimetype with " + mime);
			restResponse.getParameter().setMimetype(mime);
		}
		String statuscode = getSingleParamValue(request, XREST_STATUSCODE);
		if (statuscode != null) {
			log.info("overriding statuscode with " + statuscode);
			restResponse.getParameter().setResponseCode(Integer.parseInt(statuscode));
		}

		write(response, restResponse);
	}

	private static String getSingleParamValue(HttpServletRequest request, String paramName) {
		String[] values = request.getParameterValues(paramName);
		if (values == null)
			return null;
		if (values.length > 1) {
			log.warn("Got multiple values for " + paramName + " but onyl one is allowed");
			return null;
		}
		return values[0];
	}

	private void write(HttpServletResponse servletResponse, RestResponse restResponse) {
		log.debug("writing back with xslt = " + restResponse.getParameter().getXslt());
		servletResponse.setCharacterEncoding("UTF8");
		servletResponse.setContentType(restResponse.getParameter().getMimetype());
		servletResponse.setStatus(restResponse.getParameter().getResponseCode());

		for (Map.Entry<String,String> e : restResponse.getHeader().entrySet()) {
			log.debug("setting header "+e.getKey() +":"+ e.getValue());
			servletResponse.setHeader(e.getKey() , e.getValue() );
		}
		
		try {
			restResponse.toWriter(servletResponse.getWriter());
		} catch (UnsupportedEncodingException e) {
			assert false : "encoding should be supported!";
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.error(e);
			write(servletResponse, new RestResponse(e));
		} catch (TransformerException e) {
			log.error(e);
			write(servletResponse, new RestResponse(e));
		}

	}

	class JavaMethodCall implements Runnable {

		Method method;

		Object[] params;

		Object restlet;

		Object result;

		public String toString() {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < params.length; i++) {
				buf.append("'" + (params[i] == null ? "NULL" : params[i].toString()) + "'").append(
						",\n");
			}

			return "method " + method.getName() + " with " + buf.toString();
			//			
			// XStream xs = new XStream();
			// return "method: " + xs.toXML(method) + " params: "
			// + xs.toXML(params) + " restlet: " + xs.toXML(restlet);
		}

		public void run() {
			log.debug("invoking ... " + this);
			try {
				this.result = this.method.invoke(this.restlet, this.params);
				log.debug("... done");
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof RestException) {
					throw new RuntimeException(e.getCause());
				} else
					throw new RuntimeException(e);
			}
		}

		public Object execute() {
			Thread t = new Thread(this);
			t.start();
			while (t.isAlive()) {
				// System.out.print(".");
				Thread.yield();
			}
			return this.result;
		}
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		service(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		service(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 */
	public String getServletInfo() {
		return "This Servlet maps URL+URI combinations to Restlets.";
	}

}

// private void write(HttpServletResponse response, Object result, String
// contentType,
// StyleCommands styleCommands) throws IOException, TransformerException {
// log.debug("writing back with xslt = " + styleCommands.xslt);
// response.setCharacterEncoding("UTF8");
// response.setContentType(contentType);
// OutputFormat fo = OutputFormat.createPrettyPrint();
// XMLWriter xw = new XMLWriter(fo);
//
// if (result instanceof RestResponse) {
// // hard coded answer behaviour
// Document resultDoc = (Document) ((RestResponse) result).getResponseBody();
// // if (styleCommands.xslt != null) {
// // Document xslt = XMLUtils.getAsXML(styleCommands.xslt);
// // resultDoc = XMLUtils.transform(resultDoc,xslt);
// // }
//
// xw.setWriter(response.getWriter());
// xw.write(resultDoc);
// }
// // heuristics
// else if (contentType.equals(MIME_APPLICATION_XHTML) |
// contentType.equals(MIME_TEXT_HTML)) {
// response
// .getWriter()
// .write(
// "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"
// \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
// + "<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'>\n"
// + " <head>\n"
// + " <meta http-equiv='Content-Type' content='text/html; charset=utf8'/>\n"
// + " <meta http-equiv='charset' content='utf8' />\n");
// if (styleCommands.css != null)
// response.getWriter().write(
// " <link rel='stylesheet' href='" + styleCommands.css + "'/>\n");
//
// response.getWriter().write(
// " <title>jREST</title>\n" + " </head>\n" + " <body>\n");
// Document docResult = converter.convertToDocument(result);
//
// xw.setWriter(response.getWriter());
// xw.write(docResult);
//
// response.getWriter().write("</body></html>");
// } else if (result instanceof Reader) {
// Reader reader = (Reader) result;
// int c = reader.read();
// while (c != -1) {
// response.getWriter().write(c);
// c = reader.read();
// }
// } else if (result instanceof Document) {
// Document resultDoc = (Document) result;
// if (styleCommands.xslt != null) {
// Document xslt = XMLUtils.getAsXML(styleCommands.xslt);
// resultDoc = XMLUtils.transform(resultDoc, xslt);
// }
// xw.setWriter(response.getWriter());
// xw.write(resultDoc);
// } else
// response.getWriter().write("" + result);
//
// response.getWriter().flush();
// response.getWriter().close();
// }
