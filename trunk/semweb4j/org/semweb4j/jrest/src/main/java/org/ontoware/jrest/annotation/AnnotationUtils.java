/**
 * 
 */
package org.ontoware.jrest.annotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a useful utilitie for Annotations:
 * asMap(java.lang.annotation.Annotation[] annotations), see below.
 * 
 * @author $Author: xamde $
 * @version $Id: AnnotationUtils.java,v 1.1 2006/09/15 09:51:05 xamde Exp $
 * 
 */

public class AnnotationUtils {

	private static Log log = LogFactory.getLog(AnnotationUtils.class);

	/**
	 * Convert annotations array to a Map<Class,Annotation> where Class is the
	 * Annotation class. This makes type-safe handling of annotaitons easier
	 * 
	 * @param annotations
	 * @return a Map<Class,Annotation>
	 */
	public static Map<Class<?>, Annotation> asMap(Annotation[] annotations) {
		Map<Class<?>, Annotation> type2annotation = new HashMap<Class<?>, Annotation>();
		for (int i = 0; i < annotations.length; i++) {
			type2annotation.put(annotations[i].annotationType(), annotations[i]);
			log.debug("Found annotation of type " + annotations[i].annotationType());
		}
		return type2annotation;
	}
}
