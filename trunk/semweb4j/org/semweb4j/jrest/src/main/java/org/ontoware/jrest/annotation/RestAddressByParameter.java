/**
 * 
 */
package org.ontoware.jrest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Takes parameters from URL query string (e.g.
 * http://localhost:8888/rest/alice?age=13)
 * 
 * @author $Author: xamde $
 * @version $Id: RestAddressByParameter.java,v 1.2 2006/09/15 09:51:05 xamde Exp $
 * 
 */

/**
 * The web parameter from which to fill the java parameter that is annotated.
 */
@Target( { ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestAddressByParameter {

	String value();

}
