package org.ontoware.rdfreactor.generator.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfschema.Class;
import org.ontoware.rdfreactor.schema.rdfschema.Resource;

/**
 * A <b>JClass</b> represents a class in a JModel. 
 * 
 * Every JClass has a name, a comment, a List of properties and inverse properties, 
 * a List of superclasses (multiple superclasses are allowed), and an URI to which the JClass
 * is mapped.
 * 
 * Properties are represented by JProperty instances.
 * 
 * @author mvo
 */
public class JClass extends JMapped {

	public static final JClass STRING = new JClass(JPackage.JAVA_LANG, String.class.getName(),
			RDFS.Literal);

	public static final JClass RESOURCE = new JClass(JPackage.RDFSCHEMA, Resource.class.getName(),
			RDFS.Resource);

	public static final JClass RDFS_CLASS = new JClass(JPackage.RDFSCHEMA, Class.class.getName(),
			RDFS.Class);

	public static final JClass OWL_CLASS = new JClass(JPackage.OWL,
			org.ontoware.rdfreactor.schema.owl.Class.class.getName(), OWL.Class);

	public static final JClass REACTOR_BASE_NAMED = new JClass(new JPackage(
			"org.ontoware.rdfreactor.runtime"), "ReactorBaseNamed", new URIImpl("urn:java:org.ontoware.rdfreactor.named"));

	
	private static final Log log = LogFactory.getLog(JClass.class);

	/** List of properties, represented by JProperty instances */
	private List<JProperty> properties = new ArrayList<JProperty>();

	/** List of inverse properties, represented by JProperty instances */
	private List<JProperty> inverseProperties = new ArrayList<JProperty>();

	/** List of superclasses, represented by JClass instances */
	private List<JClass> superclasses = new ArrayList<JClass>();

	/** the JPackage, to which this JClass belongs */
	private JPackage packagge;

	/**
	 * property names need only to be unique within a class
	 */
	public Set<String> usedPropertynames = new HashSet<String>();

	/**
	 * creating a class adds it to the package
	 * the only constructor: 
	 * @param packagge is the JPackage to which this JClass belongs
	 * @param name is the Name identyfing this JClass 
	 * @param mappedTo is an URI to which this JClass should be mapped
	 */
	public JClass(JPackage packagge, String name, URI mappedTo) {
		super(name, mappedTo);
		this.packagge = packagge;
		// wire
		this.packagge.getClasses().add(this);
	}

	public String toString() {
		return this.getName();
	}

	public String toDebug() {
		StringBuffer buf = new StringBuffer();
		buf.append("    JClass " + getName() + " -> " + getMappedTo() + "\n");
		if (superclasses.size() > 0)
			buf.append("      extends: ");
		for (JClass jc : superclasses) {
			buf.append(jc.getName()).append(", ");
		}
		if (superclasses.size() > 0)
			buf.append("\n");

		if (getComment() != null)
			buf.append("      comment: ").append(getComment()).append("\n");

		for (JProperty jp : properties) {
			buf.append(jp.toDebug());
		}
		return buf.toString();
	}

	/**
	 * @return true if this JClass has a JProperty with a cardinality greater then one
	 */
	public boolean hasMultiValuePropeties() {
		for (JProperty jp : properties)
			if (jp.getMaxCardinality() > 1)
				return true;
		return false;
	}

	/**
	 * @return true if the template generates code that throws a RDFDataException
	 */
	public boolean throwsRDFDataException() {
		for (JProperty jp : properties) {
			if (jp.getMaxCardinality() == JProperty.NOT_SET || jp.getMaxCardinality() == 1)
				return true;
		}
		return false;
	}

	/**
	 * @return true if the template generates code that throws a CardinalityException
	 */
	public boolean throwsCardinalityException() {
		for (JProperty jp : properties) {
			if (jp.getMinCardinality() != JProperty.NOT_SET)
				return true;
			if (jp.getMaxCardinality() > 1)
				return true;
		}
		return false;
	}

	public List<JClass> getSuperclasses() {
		return this.superclasses;
	}

	public void addSuperclass(JClass superclass) {
		this.superclasses.add(superclass);
	}

	public List<JProperty> getProperties() {
		return this.properties;
	}

	/**
	 * apply consistency checks to this JClass and all instances 
	 * of JProperty in it.
	 * 
	 * A JClass is not consistent if it has more then one superclass.
	 * 
	 * @return true if the JClass and all its parts are consistent
	 */
	public boolean isConsistent() {
		boolean result = true;
		if (superclasses.size() != 1) {
			log.warn(getName() + " has " + superclasses.size() + " superclasses");
			result &= false;
		}
		for (JProperty jp : properties) {
			if (!jp.isConsistent())
				log.warn(jp.getName() + " is not consistent");
			result &= jp.isConsistent();
		}
		return result;
	}

	/**
	 * @return first superclass, null if none exists
	 */public JClass getSuperclass() {
		return superclasses.get(0);
	}

	private JPackage getPackage() {
		return this.packagge;
	}
	
	/**
	 * Calculate the inheritance distance of this JClass from the given root JClass.
	 * 
	 * @param root is a JClass
	 * @return the number of classes in the inheritance tree between this JClass and the given root JClass
	 */
	public int getInheritanceDistanceFrom(JClass root) {
		return this.getInheritanceDistanceFrom(root, 0, new HashSet<JClass>());
	}

	/**
	 * Calculate the inheritance distance of this JClass from the given root JClass.
	 * The calculation is done through recursion.
	 *  
	 * @param root is the JClass from which the distance should be calculated
	 * @param steps is passed as an argument because it is recursivly counted upwards
	 * @param seen contains all JClasses through which this function passed as part of the recursion
	 * @return the number of steps until the given root JClass is reached recursivly
	 */
	private int getInheritanceDistanceFrom(JClass root, int steps, Set<JClass> seen) {
		log.debug("visiting '" + this.getName() + "' step = " + steps + " seen = " + seen.size()
				+ " root = '" + root + "'");

		if (this.getName().equals(root.getName()))
			return steps;
		else if (seen.contains(this))
			return steps;
		else {
			Set<JClass> localseen = new HashSet<JClass>();
			localseen.addAll(seen);
			localseen.add(this);

			// return maximum
			int max = 0;
			for (JClass superclass : getSuperclasses()) {
				int current = superclass.getInheritanceDistanceFrom(root, steps + 1, localseen);
				if (current > max)
					max = current;
			}
			return steps + max;
		}
	}

	/** change the superclass of this JClass to the given one */
	public void replaceSuperclassesWith(JClass jc) {
		this.superclasses.clear();
		this.superclasses.add(jc);
	}

	public List<JProperty> getInverseProperties() {
		return inverseProperties;
	}

	/** generate a verbose report of this JClass */
	public String toReport() {
		StringBuffer buf = new StringBuffer();
		buf.append("\n" + getName());
		if (superclasses.size() > 0)
			buf.append("  extends: ");
		for (JClass jc : superclasses) {
			buf.append(jc.getN3Name()).append(", ");
		}
		buf.append(" URI: " + getMappedTo() + "\n");
		// if (superclasses.size() > 0)
		// buf.append("\n");

		if (getComment() != null)
			buf.append("''").append(getComment()).append("''\n");

		for (JProperty jp : properties) {
			buf.append(jp.toReport());
		}
		return buf.toString();
	}

	/** two JClass instances are equal if they have the same name and belong to the same JPackage */
	public boolean equals(Object other) {
		if (other instanceof JClass) {
			JClass otherclass = (JClass) other;
			return (this.getName().equals(otherclass.getName()) && this.getPackage().equals(
					otherclass.getPackage()));
		}
		return false;
	}
}
