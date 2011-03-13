package org.ontoware.rdfreactor.generator.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A <b>JClass</b> represents a class in a JModel.
 * 
 * Every JClass has a name, a comment, a List of properties and inverse
 * properties, a List of superclasses (multiple superclasses are allowed), and
 * an URI to which the JClass is mapped.
 * 
 * Properties are represented by JProperty instances.
 * 
 * @author mvo
 */
public class JClass extends JMapped implements Comparable<JClass> {
	
	public static final JClass STRING = new JClass(JPackage.JAVA_LANG, String.class.getName(),
	        RDFS.Literal);
	
	public static final JClass RESOURCE = new JClass(JPackage.RDFSCHEMA,
	        org.ontoware.rdfreactor.schema.rdfs.Resource.class.getName(), RDFS.Resource);
	
	public static final JClass RDFS_CLASS = new JClass(JPackage.RDFSCHEMA,
	        org.ontoware.rdfreactor.schema.rdfs.Class.class.getName(), RDFS.Class);
	
	public static final JClass OWL_CLASS = new JClass(JPackage.OWL,
	        org.ontoware.rdfreactor.schema.owl.OwlClass.class.getName(), OWL.Class);
	
	private static Logger log = LoggerFactory.getLogger(JClass.class);
	
	/** List of properties, represented by JProperty instances */
	private List<JProperty> properties = new ArrayList<JProperty>();
	
	/** List of inverse properties, represented by JProperty instances */
	private List<JProperty> inverseProperties = new ArrayList<JProperty>();
	
	/**
	 * List of potentially missing properties, represented by JProperty
	 * instances
	 */
	private List<JProperty> missingProperties = new ArrayList<JProperty>();
	
	/** List of superclasses, represented by JClass instances */
	private List<JClass> superclasses = new ArrayList<JClass>();
	
	/** as used for code generation */
	private JClass javaSuperclass;
	
	/** the JPackage, to which this JClass belongs */
	private JPackage packagge;
	
	private Set<JClass> javaSubclasses = new HashSet<JClass>();
	
	public boolean cardinalityexception = false;
	
	/**
	 * property names need only to be unique within a class
	 * 
	 * FIXME and in all super-classes of that class
	 * 
	 * @return all property names that have been used in this class or a
	 *         super-class
	 */
	public Set<String> getUsedPropertyNames() {
		// look in all sub- and super-classes and collect used names
		Set<String> usedNames = new HashSet<String>();
		
		addUsedPropertyNames(usedNames, this);
		
		JClass superclass = getSuperclass();
		while(superclass != null) {
			addUsedPropertyNames(usedNames, superclass);
			superclass = superclass.getSuperclass();
		}
		addUsedPropertyNamesFromSubClasses(usedNames, this);
		return usedNames;
	}
	
	private void addUsedPropertyNamesFromSubClasses(Set<String> usedNames, JClass jclass) {
		for(JClass subclass : jclass.getSubclasses()) {
			addUsedPropertyNames(usedNames, subclass);
			addUsedPropertyNamesFromSubClasses(usedNames, subclass);
		}
	}
	
	private Set<JClass> getSubclasses() {
		return this.javaSubclasses;
	}
	
	/**
	 * Add all names used by a property in jclass to usedNames
	 * 
	 * @param usedNames
	 * @param jclass
	 */
	private void addUsedPropertyNames(Set<String> usedNames, JClass jclass) {
		for(JProperty jprop : jclass.getProperties()) {
			usedNames.add(jprop.getName());
		}
	}
	
	/**
	 * creating a class adds it to the package the only constructor:
	 * 
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
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	public String getDotfree() {
		return this.getName().replace(".", "_");
	}
	
	@Override
	public String toDebug() {
		StringBuffer buf = new StringBuffer();
		buf.append("    JClass " + getName() + " -> " + getMappedTo() + "\n");
		if(this.superclasses.size() > 0)
			buf.append("      extends: ");
		for(JClass jc : this.superclasses) {
			buf.append(jc.getName()).append(", ");
		}
		if(this.superclasses.size() > 0)
			buf.append("\n");
		
		if(getComment() != null)
			buf.append("      comment: ").append(getComment()).append("\n");
		
		for(JProperty jp : this.properties) {
			buf.append(jp.toDebug());
		}
		return buf.toString();
	}
	
	/**
	 * @return true if this JClass has a JProperty with a cardinality greater
	 *         then one
	 */
	public boolean hasMultiValuePropeties() {
		for(JProperty jp : this.properties)
			if(jp.getMaxCardinality() > 1)
				return true;
		return false;
	}
	
	/**
	 * @return true if the template generates code that throws a
	 *         RDFDataException
	 */
	public boolean throwsRDFDataException() {
		for(JProperty jp : this.properties) {
			if(jp.getMaxCardinality() == JProperty.NOT_SET || jp.getMaxCardinality() == 1)
				return true;
		}
		return false;
	}
	
	// /**
	// * @return true if the template generates code that throws a
	// * CardinalityException
	// */
	// public boolean throwsCardinalityException() {
	// for (JProperty jp : properties) {
	// if (jp.getMinCardinality() != JProperty.NOT_SET)
	// return true;
	// if (jp.getMaxCardinality() > 1)
	// return true;
	// }
	// return false;
	// }
	
	/**
	 * @return all direct super-classes
	 */
	public List<JClass> getSuperclasses() {
		return this.superclasses;
	}
	
	/**
	 * @return all super-classes, and their super-classes, and so on until root
	 */
	public Set<JClass> getTransitiveSuperclasses() {
		Set<JClass> transitiveSuperclasses = new HashSet<JClass>();
		addSuperClasses(transitiveSuperclasses, this);
		return transitiveSuperclasses;
	}
	
	private static void addSuperClasses(Set<JClass> result, JClass clazz) {
		for(JClass superClass : clazz.getSuperclasses()) {
			result.add(superClass);
			addSuperClasses(result, superClass);
		}
	}
	
	public void addSuperclass(JClass superclass) {
		this.superclasses.add(superclass);
	}
	
	/**
	 * Double-link
	 * 
	 * @param javaSuperclass
	 */
	public void setJavaSuperclass(JClass javaSuperclass) {
		this.javaSuperclass = javaSuperclass;
		javaSuperclass.addJavaSubclass(this);
	}
	
	private void addJavaSubclass(JClass javaSubclass) {
		this.javaSubclasses.add(javaSubclass);
		
	}
	
	public List<JProperty> getProperties() {
		// sort properties
		Collections.sort(this.properties, new Comparator<JProperty>() {
			
			public int compare(JProperty a, JProperty b) {
				return a.getName().compareTo(b.getName());
			}
			
		});
		
		// return
		return this.properties;
	}
	
	/**
	 * apply consistency checks to this JClass and all instances of JProperty in
	 * it.
	 * 
	 * A JClass is not consistent if it has more then one superclass.
	 * 
	 * @return true if the JClass and all its parts are consistent
	 */
	public boolean isConsistent() {
		boolean result = true;
		if(this.javaSuperclass == null) {
			log.warn(getName() + " has no superclass to inherit from");
			result &= false;
		}
		for(JProperty jp : this.properties) {
			if(!jp.isConsistent())
				log.warn(jp.getName() + " is not consistent");
			result &= jp.isConsistent();
		}
		return result;
	}
	
	/**
	 * @return first superclass, null if none exists
	 */
	public JClass getSuperclass() {
		return this.javaSuperclass;
	}
	
	private JPackage getPackage() {
		return this.packagge;
	}
	
	/**
	 * Calculate the inheritance distance of this JClass from the given root
	 * JClass.
	 * 
	 * @param root is a JClass
	 * @return the number of classes in the inheritance tree between this JClass
	 *         and the given root JClass
	 */
	public int getInheritanceDistanceFrom(JClass root) {
		return this.getInheritanceDistanceFrom(root, 0, new HashSet<JClass>());
	}
	
	/**
	 * Calculate the inheritance distance of this JClass from the given root
	 * JClass. The calculation is done through recursion.
	 * 
	 * @param root is the JClass from which the distance should be calculated
	 * @param steps is passed as an argument because it is recursivly counted
	 *            upwards
	 * @param seen contains all JClasses through which this function passed as
	 *            part of the recursion
	 * @return the number of steps until the given root JClass is reached
	 *         recursivly
	 */
	private int getInheritanceDistanceFrom(JClass root, int steps, Set<JClass> seen) {
		log.debug("visiting '" + this.getName() + "' step = " + steps + " seen = " + seen.size()
		        + " root = '" + root + "'");
		
		if(this.getName().equals(root.getName()))
			return steps;
		else if(seen.contains(this))
			return steps;
		else {
			Set<JClass> localseen = new HashSet<JClass>();
			localseen.addAll(seen);
			localseen.add(this);
			
			// return maximum
			int max = 0;
			for(JClass superclass : getSuperclasses()) {
				int current = superclass.getInheritanceDistanceFrom(root, steps + 1, localseen);
				if(current > max)
					max = current;
			}
			return steps + max;
		}
	}
	
	public List<JProperty> getInverseProperties() {
		return this.inverseProperties;
	}
	
	public List<JProperty> getMissingProperties() {
		return this.missingProperties;
	}
	
	/** generate a verbose report of this JClass */
	public String toReport() {
		StringBuffer buf = new StringBuffer();
		buf.append("\n" + getName());
		if(this.superclasses.size() > 0)
			buf.append("  extends: ");
		for(JClass jc : this.superclasses) {
			buf.append(jc.getN3Name()).append(", ");
		}
		buf.append("| URI: " + getMappedTo().toSPARQL() + "\n");
		
		if(getComment() != null)
			buf.append("''").append(getComment()).append("''\n");
		
		buf.append("properties:\n");
		for(JProperty jp : this.properties) {
			buf.append(jp.toReport());
		}
		buf.append("inverse properties:\n");
		for(JProperty jp : this.inverseProperties) {
			buf.append(jp.toReport());
		}
		return buf.toString();
	}
	
	/**
	 * two JClass instances are equal if they have the same name and belong to
	 * the same JPackage
	 */
	@Override
	public boolean equals(Object other) {
		if(other instanceof JClass) {
			JClass otherclass = (JClass)other;
			return (this.getName().equals(otherclass.getName()) && this.getPackage().equals(
			        otherclass.getPackage()));
		}
		return false;
	}
	
	public boolean getCardinalityexception() {
		return this.cardinalityexception;
	}
	
	public int compareTo(JClass other) {
		return this.getName().compareTo(other.getName());
	}
	
	/**
	 * requires the hierarchy to be flat already
	 * 
	 * @param missingProp
	 * @return true if this class or any of its super-classes (recursively) have
	 *         this property
	 */
	public boolean hasJavaProperty(JProperty prop) {
		if(this.getProperties().contains(prop)) {
			return true;
		} else if(this.getSuperclass() == null) {
			return false;
		} else {
			return this.getSuperclass().hasJavaProperty(prop);
		}
	}
	
}
