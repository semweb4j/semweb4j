/**
 * 
 */
package org.ontoware.jrest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author $Author: xamde $
 * @version $Id: RestConstants.java,v 1.1 2006/09/15 09:51:05 xamde Exp $
 * 
 */

public interface RestConstants {

	public static final String METHOD_GET = "get";

	public static final String METHOD_PUT = "put";

	public static final String METHOD_POST = "post";

	public static final String METHOD_DELETE = "delete";

	public static final String METHOD_OPTIONS = "options";

	public static final String METHOD_HEAD = "head";

	public static final String METHOD_TRACE = "trace";

	public static final Set<String> HTTPMETHODS = new HashSet<String>(Arrays
			.asList(new String[] { METHOD_GET, METHOD_PUT, METHOD_POST,
					METHOD_DELETE, METHOD_OPTIONS, METHOD_HEAD, METHOD_TRACE }));

}
