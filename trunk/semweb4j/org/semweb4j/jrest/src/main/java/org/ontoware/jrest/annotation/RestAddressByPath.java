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
 * @version $Id: RestAddressByPath.java,v 1.1 2006/07/01 15:52:40 xamde Exp $
 * 
 */

/**
 * Use request path as parameter (only the part after the servlet)
 */
@Target( { ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestAddressByPath {

}
