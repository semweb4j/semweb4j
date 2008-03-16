package org.ontoware.semversion;

import org.ontoware.rdf2go.model.node.URI;


public interface User {

	/**
	 * @return password
	 */
	String getPassword();

	/**
	 * set password
	 */
	void setPassword(String value);

	/**
	 * @return the user name
	 */
	String getName();

	/**
	 * set user name
	 */
	void setName(String value);
	
	/**
	 * @return the uri of this user
	 */
	URI getURI();

}