package org.ontoware.rdfreactor.runtime;


/**
 * object-oriented query-row
 * @author voelkel
 */
public interface OOQueryRow {

	/**
	 * @param varname
	 * @return object as the instance of the class according to mapping given in query
	 */
	public Object getValue(String varname);

}
