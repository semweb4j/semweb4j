/**
 * 
 */
package org.ontoware.jrest.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

/**
 * Used to convert to and from URL parameters.
 * @author $Author: xamde $
 * @version $Id: JavaStringConverter.java,v 1.4 2006/10/11 17:06:43 xamde Exp $
 * 
 */

public class JavaStringConverter {

	private static final Log log = LogFactory.getLog(JavaStringConverter.class);

	private static final URLCodec urlCodec = new URLCodec();
	
	/**
	*  Converts a string to an object with any type(@romu: to a String ou to String?)
	* @param string
	* @param targetType - Put the name of the wanted type 
	* @return the object
	*/
	public static Object convertToObject(String string, Class<?> targetType)  {

		if (string == null) {
			log.debug("convert to null from null");
			return null;
		}

		if (string.equals("")) {
			log.debug("convert to null from empty String");
			return null;
		}

		if (targetType.equals(String.class)) {
			log.debug("return String");
			return string;
		}

		if (targetType.equals(boolean.class)) {
			log.debug("return boolean");
			return Boolean.parseBoolean(string);
		}

		if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
			log.debug("convert to Integer from String");
			return new Integer(string);
		}

		// TODO more primitive types

		if (targetType.equals(URI.class)) {
			log.debug("convert to URI from String");
			try {
				return new URI(urlCodec.decode(string));
			} catch (URISyntaxException e) {
				try {
					log.warn("Could not parse as URI: "+urlCodec.decode(string));
				} catch (DecoderException e1) {
					assert false;
					throw new RuntimeException( e1 );
				}
				throw new RuntimeException( e );
			} catch (DecoderException e) {
				log.warn("Could not decode: "+string);
				throw new RuntimeException( e );
			}
		}

		if (targetType.equals(URL.class)) {
			log.debug("convert to URI from String");
			try {
				return new URL(string).toExternalForm();
			} catch (MalformedURLException e) {
				log.warn("Could not parse as URL: "+string);
				throw new RuntimeException( e );
			}
		}

		// dom4j.Document
		if (targetType.equals(Document.class)) {
			log.debug("load as XML document (dom4j");
			return org.ontoware.aifbcommons.xml.XMLUtils.loadXML(string);
		}

		// experimental
		try {
			log.debug("try to convert using Constructor( String )");
			Constructor<?> c = targetType
					.getConstructor(new Class[] { String.class });
			try {
				Object result = c.newInstance(new Object[] { string });
				log.debug("created result through constructor(String)");
				return result;
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}

		throw new RuntimeException("cannot convert to " + targetType
				+ " (yet) from " + string);

	}

	/**
	*  Converts an object to a string (@romu: to a String ou to String?)
	* @param object
	* @return the string
	*/
	public static String convertToString(Object o) {

		if (o == null)
			return "";

		if (o instanceof String) {
			log.debug("o is a String");
			return (String) o;
		}

		log.debug("convert using toString()");
		return o.toString();

	}
}
