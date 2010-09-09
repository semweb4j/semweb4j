/**
 * 
 */
package org.ontoware.rdfreactor.runtime;

/**
 * A <b>CardinalityException</b> is thrown e.g. when a removing or adding a
 * value from a property violates the maximal or minima cardinality of that
 * property.
 * 
 * @author $Author: behe $
 * @version $Id: CardinalityException.java,v 1.2 2006/05/20 16:55:20 behe Exp $
 * 
 */
@Patrolled
public class CardinalityException extends Exception {
	
	private static final long serialVersionUID = -2987602905455854146L;
	
	public CardinalityException(String msg) {
		super(msg);
	}
	
}
