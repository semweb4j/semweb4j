package org.ontoware.rdfreactor.generator.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A <b>JPackage</b> represents a package in Java. Explicit modelling of
 * packages allows the mapping of different namespaces in RDF to different Java
 * packages.
 * 
 * A JPackage has a name (typically with dots) and a List of JClass instances,
 * representing the classes in the package.
 * 
 * @author $Author: xamde $
 * @version $Id: JPackage.java,v 1.8 2006/06/21 13:24:40 xamde Exp $
 * 
 */
public class JPackage {
	
	public static final JPackage JAVA_LANG = new JPackage("java.lang");
	
	public static final JPackage JAVA_UTIL = new JPackage("java.util");
	
	public static final JPackage RDFSCHEMA = new JPackage("org.ontoware.rdfreactor.schema.rdfs");
	
	public static final JPackage OWL = new JPackage("org.ontoware.rdfreactor.schema.owl");
	
	private static Logger log = LoggerFactory.getLogger(JPackage.class);
	
	/**
	 * the name of the JPackage, usually it conforms to java package naming
	 * conventions
	 */
	private String name;
	
	/** the List of JClasses belonging to this JPackage */
	private List<JClass> classes = new ArrayList<JClass>();
	
	public JPackage(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void addClass(JClass someClass) {
		this.classes.add(someClass);
	}
	
	public List<JClass> getClasses() {
		return this.classes;
	}
	
	/**
	 * apply consistency checks to this JPackage and all instances of JClass and
	 * JProperty in it.
	 * 
	 * @return true if the JPackage and all its parts are consistent
	 */
	public boolean isConsistent() {
		boolean result = true;
		for(JClass jc : this.classes) {
			if(!jc.isConsistent())
				log.warn(jc.getName() + " is not consistent");
			result &= jc.isConsistent();
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("  JPackage " + this.name + "\n");
		for(JClass jc : this.classes) {
			buf.append(jc.toDebug());
		}
		return buf.toString();
	}
	
	/** generate a verbose report of this JPackage */
	public String toReport() {
		StringBuffer buf = new StringBuffer();
		buf.append("Package -------------------\n");
		for(JClass jc : this.classes) {
			buf.append(jc.toReport());
		}
		return buf.toString();
	}
	
	/**
	 * two packages are equal, iff they have the same name. Note: they may have
	 * different classes.
	 */
	@Override
	public boolean equals(Object other) {
		return (other instanceof JPackage && ((JPackage)other).getName().equals(this.getName()));
	}
	
	public void sortClasses() {
		Collections.sort(this.classes);
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
