/**
 * 
 */
package org.ontoware.rdfreactor.generator.java;

import org.ontoware.rdf2go.model.node.URI;


/**
 * <b>JMapped</b> is used as the superclass of Objects in a JModel which can be mapped to 
 * an URI. 
 * 
 * Every JMapped instance has a name, a comment and an URI to which it can be mapped in the
 * representation of the JModel.  
 * 
 * @author $Author: xamde $
 * @version $Id: JMapped.java,v 1.4 2006/09/11 10:07:57 xamde Exp $
 */

public class JMapped {

	/** name of the JMapped instance */
	private String name;

	/** comment of this JMapped instance */
	private String comment;
	
	/** the URI to which this JMapped instance is mapped */
	private URI mappedTo;

	/**
	 * the constructor: 
	 * @param name of the JMapped instance
	 * @param mappedTo is the URI to which to map this JMapped instance
	 */
	public JMapped(String name, URI mappedTo) {
		assert mappedTo != null;
		this.name = name;
		this.mappedTo = mappedTo;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public URI getMappedTo() {
		return this.mappedTo;
	}

	public String getName() {
		return this.name;
	}
	
	public String getPlainName() {
		String plain = getName();
		plain = plain.replaceAll("\\W", "_");
		return plain;
	}

	/**
	 * @return the last part of the name (starting after the last dot in the name)
	 */
	public String getN3Name() {
		if (getName().contains(".")) {
			return getName().substring(getName().lastIndexOf(".")+1);
		} else
			return getName();
	}

	public String toDebug() {
		return this.getName();
	}

	/**
	 * Two JMapped are equal, if they are mapped to the same URI OR have the
	 * same name
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof JMapped && (
		// same URI
				((JMapped) other).getMappedTo().equals(getMappedTo()) || // same
				// name
				((JMapped) other).getName().equals(this.getName()));
	}
	
	/** for velocity: return a dot-free version of the name */
	public String dotfree() {
		return this.getName().replace(".","_");
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
