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
 * @author $Author: xamde $
 * @version $Id: DefaultValue.java,v 1.2 2006/04/15 11:25:14 xamde Exp $
 * 
 */

/**
 * The default value for a parameter 
 */
@Target( { ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultValue {

	String value();

}
