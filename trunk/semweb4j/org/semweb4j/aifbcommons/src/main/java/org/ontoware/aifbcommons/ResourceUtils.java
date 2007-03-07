package org.ontoware.aifbcommons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResourceUtils {

	public static final Log log = LogFactory.getLog(ResourceUtils.class);

	/**
	 * Maven2 does not resolve URLs correctly, when called from a test case. The
	 * bug is descirbed at
	 * http://mail-archives.apache.org/mod_mbox/maven-users/200411.mbox/%3c20041111115116.B764976E3E@mail.xmatrix.ch%3e
	 * 
	 * So solve this, we patch the URL.
	 * 
	 * @param filename
	 * @param mustBePresent -
	 *            if false, return null. if true, throw FileNotFoundException
	 * @return
	 * @throws FileNotFoundException
	 * @deprecated use loadResourceAsStream instead
	 */
	public static File findFileAsResource(String filename, boolean mustBePresent)
			throws FileNotFoundException {

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		// ResourceUtils.class.getClassLoader()
		
		URL fileURL = classloader.getResource(filename);

		log.debug("loading resource URL = " + fileURL + " from " + filename);

		if (fileURL != null) {
			try {
				String urlString = fileURL.toString();
				
				urlString = urlString.replace("jar:file:/","jar:file:///");
				
				URI uri = URI.create(urlString);
				log.debug("URI = " + uri);

				
				// jar URL syntax: jar:<url>!/{entry}
				// example for a problematic URL:
				// jar:file:/P:/semweb4j-svn/trunk/maven-repo/org/semweb4j-apps/cds-core/4.3-SNAPSHOT/cds-core-4.3-SNAPSHOT.jar!/cds-v5.n3
				if (uri.toASCIIString().startsWith("jar:file:")) {
					// fix it
				}

				File f = new File(fileURL.toURI());
				if (!f.exists())
					log.warn("File for URL '" + fileURL.toURI()
							+ "' not found. file = " + f.getAbsolutePath());

				return f;
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		} else {
			log
					.info("URL constructed for loading '" + filename
							+ "' was null.");
			if (!mustBePresent)
				return null;
			else {
				throw new FileNotFoundException("No resource named " + filename
						+ " could be found.");
			}
		}
	}

	public static InputStream findResourceAsStream(String resourceName) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		// ResourceUtils.class.getClassLoader()
		
		return classloader.getResourceAsStream(resourceName);
	}
}
