/**
 * 
 */
package org.ontoware.jrest;

/**
 * Maybe not used ...
 * 
 * @author $Author: xamde $
 * @version $Id: ReturnType.java,v 1.1 2006/09/15 09:51:05 xamde Exp $
 * 
 */

public enum ReturnType {
	/** default: wrap in result-tags */
	XML,

	/** no wrapping in result-tags, no XML for Strings and Readers */
	PLAIN,
	
	/** wrap in XHTML document */
	XHTML

}
