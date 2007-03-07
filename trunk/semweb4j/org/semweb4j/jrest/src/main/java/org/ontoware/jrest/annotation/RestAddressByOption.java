package org.ontoware.jrest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EXPERIMENTAL
 * 
 * 
 * Currently not used.
 * @author $Author: xamde $
 * @version $Id: RestAddressByOption.java,v 1.3 2006/10/11 17:41:22 xamde Exp $
 * 
 */

/**
 * If an anotation of this type is present, only given values are legal.
 * Values are given as a Sting-array of keywords.
 */
@Target( { ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestAddressByOption {

	String[] value();

}
