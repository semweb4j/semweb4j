package org.ontoware.jrest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.MimeTypes;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.util.MultiException;

/**
 * This class starts a Jetty HttpServer and configure all the main paramaters.
 * 
 */
public class RestServer implements Runnable {

	/** 'org.ontoware.jrest/path2restlet' */
	public static final String PATH2RESTLET = "org.ontoware.jrest/path2restlet";

	/** '__main' */
	public static final String KEY_MAIN = "__main";

	/** 'rest' */
	public static final String PATH_REST = "rest";

	private static Log log = LogFactory.getLog(RestServer.class);

	private Server server;

	private int port;

	Map<String, Object> path2restlet;

	// /////////////////////////////////
	// constructor

	/**
	 * Constructor. Default is port 5555, static web content in './web'
	 */
	public RestServer(int port) {
		this(port, System.getProperty("jetty.home", ".") + "/web");
	}

	/**
	 * Constructor.
	 */
	public RestServer(int port, String webRoot) {

		this.port = port;
		this.path2restlet = new HashMap<String, Object>();

		// tweak jettys logging
		System.setProperty("org.mortbay.log.class", "org.ontoware.jrest.DelegatingLog");
		if (log.isDebugEnabled()) {
			System.setProperty("DEBUG", "true");
		}

		// Create the server
		this.server = new Server(port);

		// TODO: static content
		Context staticc = new Context(server,"/static" );
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setResourceBase(webRoot);
//		HandlerList handlers = new HandlerList();
//		handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
//		server.setHandler(handlers);
		staticc.setHandler(resource_handler);

		// Create servlet container
		Context app = new Context(server, "/", Context.NO_SESSIONS | Context.NO_SECURITY);
		app.setContextPath("/");
		log.debug("setting web root to " + new File(webRoot).getAbsolutePath());
		app.setResourceBase(webRoot);

		MimeTypes mimeTypes = new MimeTypes();
		mimeTypes.addMimeMapping("xml", "application/xml");
		mimeTypes.addMimeMapping("xslt", "application/xslt+xml");
		app.setMimeTypes(mimeTypes);

		// // Serve static content from the context
		app.setWelcomeFiles(new String[] { "index.html" });

		app.setAttribute(PATH2RESTLET, path2restlet);
		// try {
		// FIXME
		// assert app.getResource("/debug.xslt").exists();
		// assert ! app.getResource("/dbg.xslt").exists();
		// } catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		// throw new RuntimeException(e);
		// }

		// add servlets to the servlet containter
		app.addServlet(new ServletHolder(RestServlet.class), "/" + PATH_REST + "/*");

	}

	/**
	 * Register any java instance with methods 'get', 'put', 'post', 'delete'
	 * 
	 * @param path -
	 *            idntifying the last part of the path, should be a pure
	 *            alphabetic keyword
	 * @param restlet
	 *            any java instance with methods 'get', 'put', 'post', 'delete'
	 */
	public void registerRestlet(String path, Object restlet) {
		path2restlet.put(path, restlet);
		log.info("registered a " + restlet.getClass() + " instance at " + "/" + PATH_REST + "/"
				+ path);
	}

	/**
	 * Unregister a instance
	 * 
	 * @param path -
	 *            idntifying the last part of the path, should be a pure
	 *            alphabetic keyword
	 */
	public void unregisterRestlet(String path) {
		path2restlet.remove(path);
		log.info("unregistered instance at " + "/" + PATH_REST + "/" + path);
	}

	/**
	 * Starts the http server
	 * 
	 * @throws Exception
	 * @throws MultiException
	 */
	public void start() throws Exception {
		try {
			server.start();
			registerRestlet(KEY_MAIN, new ServerInfo(this));
			System.out.println("##### RestServer 0.22 #####");
			System.out.println("SYSTEM: Server successfully started at port: " + this.port + ".");
		} catch (org.mortbay.util.MultiException e) {
			log.error("Could (maybe) not start server", e);
		}

	}

	/**
	 * Stop the server
	 * 
	 * @throws Exception
	 * 
	 * @throws InterruptedException
	 * 
	 */
	public void stop() throws Exception {
		try {
			server.stop();
			System.out.println("SYSTEM: Server successfully stopped at port: " + this.port + ".");
		} catch (InterruptedException e) {
			log.error("Could (maybe) not stop server", e);
		}
	}

	/**
	 * Makes exactly the same job as start() except that it has one more Throw
	 * 
	 * @throws Exception
	 * @throws MultiException
	 * @throws RuntimeException
	 */
	public void run() {
		try {
			start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
