package org.ontoware.rdf2go.model;

/**
 * Defining how and when changes are commit to underlying persistence layer.
 * 
 * @author voelkel
 * 
 */
public interface Commitable {

	/**
	 * Writes all changes to underlying persistence layer - if such a layer is
	 * used by the implementation. Otherwise nothing happens.
	 */
	public void commit();

	/**
	 * 
	 * The default value is true.
	 * 
	 * @param autocommit
	 *            If true, all changes are immediately written to the underlying
	 *            persistence layer, if any is used.
	 */
	public void setAutocommit(boolean autocommit);

}
