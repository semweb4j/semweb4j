package org.ontoware.jrest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * for mime-type 'multipart/form-data', as as outlined in RFC 1521.
 * 
 * More or less key-value pairs in the HTTP request body
 * 
 * 
 * The multipart form data uses the POST method from the HTTP standard which is
 * defined in section 8.3 of RFC1945 and similarly redefined for HTTP 1.1 in
 * section 9.5 of RFC2616.
 * 
 * The multipart/form-data MIME type used to format the body of the request is
 * defined in RFC1867.
 * 
 * For POST requests.
 * @author voelkel
 * 
 */
@Target({ ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestContentPart {

	String value();

}
