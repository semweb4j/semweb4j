package org.ontoware.rdfreactor.generator.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <b>JProperty</b> represents a property of a JClass in the JModel.
 * (A JProperty can be thought of as a property in the Java Bean sense.)
 * 
 * Every JProperty has a name, a comment, a URI to which it is Mapped in the JModel, 
 * a List of JClasses from which it is a property, an inverse property, as well as 
 * a minimum and maximum cardinality.  
 * 
 * @author $Author: xamde $
 * @version $Id: JProperty.java,v 1.9 2006/09/11 10:07:57 xamde Exp $
 * 
 */
public class JProperty extends JMapped {

	/** magic number representing a value, which is not set */
	public static final int NOT_SET = -1;

	private static Logger log = LoggerFactory.getLogger(JProperty.class);

	
	/** the List of ranges of a JProperty */
	private Collection<JClass> types = new HashSet<JClass>();

	/** the minimum cardinality of the JProperty */
	private int minCardinality = NOT_SET;
	
	/** the maximum cardinality of the JProperty */
	private int maxCardinality = NOT_SET;

	/**
	 * if a property has an inverse property, that label is used instead of
	 * propertyname+"_Inverse".
	 */
	private JProperty inverse = null;

	/** the domain of this property: the resource to which this property belongs */ 
	private JClass clazz;

	/**
	 * constructor with all parameters 
	 * @param clazz - JClass which is the domain of this JProperty
	 * @param name of the JProperty
	 * @param mappedTo is the URI to which this JProperty is mapped to
	 * @param minCardinality of the JProperty
	 * @param maxCardinality of the JProperty
	 */
	public JProperty(JClass clazz, String name, URI mappedTo, int minCardinality, int maxCardinality) {
		super(name, mappedTo);
		this.minCardinality = minCardinality;
		this.maxCardinality = maxCardinality;
		this.clazz = clazz;
	}

	/**
	 * constructor without cardinalities
	 * @param clazz - JClass which is the domain of this JProperty 
	 * @param name of the JProperty
	 * @param mappedTo is the URI to which this JProperty is mapped to
	 */
	public JProperty(JClass clazz, String name, URI mappedTo) {
		this(clazz, name, mappedTo, NOT_SET, NOT_SET);
	}

	/**
	 * @return the JClasses of which this instance is a property  
	 */
	public Collection<JClass> getTypes() {
		return this.types;
	}

	/**
	 * @param type is added to the list of JClasses having this JProperty as a range 
	 */
	public void addType(JClass type) {
		assert type != null;
		this.types.add(type);
		log.debug("added a type, have " + this.types.size() + " now");
	}

	/**
	 * for veloctiy
	 * @return a random JClass of the current types
	 */
	public JClass getFirstType() {
		if (this.types.iterator().hasNext())
			return this.types.iterator().next();
		else
			return null;
	}

	public int getMaxCardinality() {
		return this.maxCardinality;
	}

	public int getMinCardinality() {
		return this.minCardinality;
	}

	public void setMinCardinality(int min) {
		this.minCardinality = min;
	}

	public void setMaxCardinality(int max) {
		log.debug("Setting max cardiality for " + getName() + " to " + max);
		this.maxCardinality = max;
	}

	@Override
	public String toDebug() {
		StringBuffer buf = new StringBuffer();
		buf.append("      " + getName() + " (" + this.minCardinality + "/" + this.maxCardinality + ") "
				+ " -> " + getMappedTo() + ", types: ");
		for (JClass jc : this.types) {
			buf.append("'" + jc.getName() + "'");
			buf.append(",");
		}
		if (this.hasInverse())
			buf.append(". Inverse: " + getInverse().getName());
		buf.append(".\n");

		return buf.toString();
	}

	/**
	 * apply consistency checks to this JProperty. 
	 * 
	 * Every JProperty has to belong to at least one JClass. 
	 * 
	 * @return true if this JProperty is consistent
	 */
	public boolean isConsistent() {
		boolean result = true;
		if (this.types.size() == 0) {
			log.warn("property " + getName() + " has no types");
			result &= false;
		}

//		// FIXME if inverse: class should know it
//		if (inverse != null) {
//			inverse.getFirstType().equals(this.getJClass());
//		}

		return result;
	}

	/**
	 * @return JClass which is the domain of this JProperty
	 */
	public JClass getJClass() {
		return this.clazz;
	}

	/**
	 * fix the list of JClasses which use this property, in case it is out of date
	 * 
	 * @param jm is the JModel in which JClasses using this JProperty are searched for 
	 */
	public void fixRanges(JModel jm) {
		if (this.types.size() == 0) {
			log.debug("range is null, default to root");
			this.addType(jm.getRoot());
		} else {
			// change Literal to String
			List<JClass> allTypes = new ArrayList<JClass>();
			allTypes.addAll(this.types);
			for (JClass type : allTypes) {
				assert type != null;
				if (type.getMappedTo().equals(RDFS.Literal)) {
					this.types.remove(type);
					this.types.add(JClass.STRING);
				}
			}

			// remove all redundant superclasses
			// which might be circular linked
			// keep superclass with greater distance from root
			// and among equal ones the one with the first name
			// in lexicographical order
			allTypes = new ArrayList<JClass>();
			allTypes.addAll(this.types);

			// determine maximally nested classes
			int maxDepth = 0;
			List<JClass> maximallyNested = new ArrayList<JClass>();
			for (JClass type : allTypes) {
				int currentDepth = type.getInheritanceDistanceFrom(jm.getRoot());
				if (currentDepth > maxDepth) {
					maxDepth = currentDepth;
					maximallyNested.clear();
					maximallyNested.add(type);
				} else if (currentDepth == maxDepth) {
					maximallyNested.add(type);
				} 
				// else nothing to do
			}

			// sort lexicographically
			class JClassComparator implements Comparator<JClass> {

				public int compare(JClass o1, JClass o2) {
					return o1.getName().compareTo(o2.getName());
				}

			}

			Collections.sort(maximallyNested, new JClassComparator());

			// take first
			this.types.clear();
			this.types.add(maximallyNested.get(0));
		}
	}

	public JProperty getInverse() {
		return this.inverse;
	}

	public boolean hasInverse() {
		return getInverse() != null;
	}

	public void setInverse(JProperty inverse) {
		this.inverse = inverse;
	}

	/** generate a verbose report of this JProperty */
	public String toReport() {
		StringBuffer buf = new StringBuffer();
		buf.append("* " + getName() + " (" + this.minCardinality + " / " + this.maxCardinality + ") "
				+ " URI " + getMappedTo());
		// + ", types: ");
		buf.append("\n");
		for (JClass jc : this.types) {
			buf.append("** range: " + jc.getName() + "");
			buf.append("\n");
		}

		return buf.toString();
	}

}
