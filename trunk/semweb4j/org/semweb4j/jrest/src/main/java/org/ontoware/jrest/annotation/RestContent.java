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
 * map http request body to variable
 * @author        $Author: xamde $
 * @version       $Id: RestContent.java,v 1.2 2006/09/15 09:51:05 xamde Exp $
 *
 */

@Target({ ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestContent {}
