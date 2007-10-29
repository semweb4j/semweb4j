package org.ontoware.rdfreactor.generator.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * A <b>JModel</b> is the result of the translation of a given RDFS/OWL
 * ontology, and is used as an internal representation of the ontology. This
 * model of the ontology can then be used to generate the source code through a
 * Apache Velocity template.
 * 
 * Every <b>JModel</b> consists of one or more <b>JPackage</b> instances, a
 * root JClass and a Mapping of Java Objects to JClass Objects representing them
 * in the JModel.
 * 
 * A JModel, which is represented through the classes JModel, JPackage, JClass
 * and JProperty is much more expressive then the object oriented Model
 * internaly used by Java. A JModel allows class cycles, multiple inheritance,
 * properties without a type. Inverse Properties can be explicitly stated for a
 * property.
 * 
 * This expressiveness is the result of the need to transform the RDF Model into
 * the the Object Oriented Model of Java. Therefore a JModel must be able to
 * express both the Java and the RDF Model.
 * 
 * The Transformation from an ontology into a JModel is initiated in
 * CodeGenerator.generate(...), the Apache Velocity template engine is invoked
 * in SourceCodeWriter.write(...)
 * 
 * In the future EMF/Ecore of the Eclipse Project might be used instead of
 * JModel, because Ecore is an UML dialect and has a far superior tool support
 * over JModel.
 * 
 * @author $Author: xamde $
 * @version $Id: JModel.java,v 1.9 2006/09/11 10:07:57 xamde Exp $
 * 
 * TODO enable anonymous objects as class and property identifiers for OWL support
 * 
 */

// TODO consider switching to EMF/Ecore from Eclipse
public class JModel {

	private static Log log = LogFactory.getLog(JModel.class);

	/** the List of JPackages in this JModel */
	private List<JPackage> packages;

	/**
	 * a Map of Java Objects to the JClass representing them in the JModel TODO:
	 * make private
	 */
	// FIXME -> private
	public Map<Resource, JClass> classMap;

	public Set<URI> knownProperties;
	
	/** the root JClass of this JModel */
	private JClass root;

	private boolean writetostore;

	/**
	 * the only constructor:
	 * 
	 * @param root
	 *            is the root JClass of the new JModel
	 * @param writetostore -
	 *            if genertaed classes should write to the rdf model
	 */
	public JModel(JClass root, boolean writetostore) {
		this.packages = new ArrayList<JPackage>();
		this.classMap = new HashMap<Resource, JClass>();
		this.knownProperties = new HashSet<URI>();
		this.root = root;
		this.writetostore = writetostore;
	}

	/**
	 * apply consistency checks to this JModel and all instances of JPackage,
	 * JClass and JProperty in it.
	 * 
	 * @return true if the JModel and all its parts are consistent
	 */
	public boolean isConsistent() {
		boolean result = true;
		for (JPackage jp : packages) {
			if (!jp.isConsistent())
				log.warn(jp + " is not consistent");
			result &= jp.isConsistent();
		}
		return result;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nJModel\n");
		for (JPackage jp : packages) {
			buf.append(jp.toString());
		}
		return buf.toString();
	}

	/** generate a verbose report of this JModel and its parts */
	public String toReport() {
		StringBuffer buf = new StringBuffer();
		for (JPackage jp : packages) {
			buf.append(jp.toReport());
		}
		return buf.toString();
	}

	/**
	 * remove self-references and circles. Reducing to a maximally nested
	 * inheritance tree.
	 * 
	 * circles always are of length two, due to RDFS and OWL sematics
	 */
	public void flattenInheritanceHierarchy(JPackage jp) {
		for (JClass jc : jp.getClasses()) {
			// FIXME for (JClass jc : mapping.values()) {
			log
					.debug("pruning on " + jc + " with " + jc.getSuperclasses().size()
							+ " superclasses");
			switch (jc.getSuperclasses().size()) {
			case 0:
				log.debug("no superclass? set inherited superclass to ontology root");
				jc.setJavaSuperclass(root);
				break;
			case 1: {
				log.debug("have 1 superclass");
				JClass superclass = jc.getSuperclasses().get(0);
				assert superclass != null;
				if (superclass.equals(jc)) {
					log.debug("avoid having yourself as your own superclass");
					jc.setJavaSuperclass(root);
				} else {
					// use the one we have
					jc.setJavaSuperclass(superclass);
				}
			}
				break;
			default: {
				log.debug("found " + jc.getSuperclasses().size() + " superclasses, pruning...");
				// prune hierarchy.
				
				// Alg:
				// try to flatten superclasses to a strict hierarchy
				// find most specific class among superclasses
				// and assert that one is a subclass of all other classes
				// so there must be one superclass, that has most other
				// superclasses

				// FIXME detec circles

				// find the superclass that has itself the largest number of
				// superclasses (not depth)
				// Note: this can handle cycles (uses no recursion)
				int maxSuperclassCount = 0;
				JClass mostSpecificSuperClass = root;
				for (JClass superclass : jc.getSuperclasses()) {
					log.debug("trying " + superclass + " (" + superclass.getSuperclasses().size()
							+ " superclasses)");
					if ((!superclass.equals(jc))
							&& superclass.getSuperclasses().size() > maxSuperclassCount
							&& !superclass.getSuperclasses().contains(jc)) {
						maxSuperclassCount = superclass.getSuperclasses().size();
						mostSpecificSuperClass = superclass;
						log.debug(superclass + " has " + maxSuperclassCount + " superclasses");
					}
				}
				// have we found a valid superclass?
				if (!mostSpecificSuperClass.equals(jc)) {
					log.debug("most specific superclass of " + jc.getName() + " is "
							+ mostSpecificSuperClass);
					jc.setJavaSuperclass(mostSpecificSuperClass);
				} else {
					log.debug("most specific superclass of " + jc.getName() + " is root");
					jc.setJavaSuperclass(root);
				}
			}
				break;
			}
			assert jc.getSuperclass() != null;
		}

	}

	public void addPackage(JPackage jp) {
		this.packages.add(jp);
	}

	public List<JPackage> getPackages() {
		return this.packages;
	}

	/**
	 * generates artificial inverse properties, named propertyName + "_Inverse"
	 * TODO read inverse props from OWL, this generates ALL inverse
	 */
	public void addInverseProperties() {
		// add inverse properties
		for (JPackage jp : getPackages()) {
			for (JClass jc : jp.getClasses()) {
				for (JProperty jprop : jc.getProperties()) {
					JProperty inverse;
					if (!jprop.hasInverse()) {
						// TODO: the _inverse seems to be missing, right ?
						inverse = new JProperty(jc, jprop.getName(), jprop.getMappedTo());
						jprop.setInverse(inverse);
						inverse.setInverse(jprop);
					}
					inverse = jprop.getInverse();

					for (JClass type : jprop.getTypes()) {
						// add only if not already present
						if (!type.getInverseProperties().contains(inverse))
							type.getInverseProperties().add(inverse);
						// always:
						inverse.addType(jc);
					}
				}
			}
		}
	}

	/**
	 * @return the root JClass of the JModel
	 */
	public JClass getRoot() {
		return this.root;
	}

	/**
	 * Add a mapping to the JModel
	 * 
	 * @param id
	 *            is a Java Object
	 * @param jc
	 *            is the JClass in this JModel to which the Java Object should
	 *            be mapped
	 */
	public void addMapping(Resource id, JClass jc) {
		this.classMap.put(id, jc);
	}

	/**
	 * @param id
	 *            is a Java Object
	 * @return the JClass to which the Java Object is mapped in this JModel
	 */
	public JClass getMapping(Resource id) {
		JClass result = this.classMap.get(id);	
		
		assert result != null : "noclass mapped to id " + id+" idclass = "+id.getClass();
		return result;
	}

	/**
	 * @param id
	 *            is a Java Object
	 * @return true if the given Java Object has a mapping to a JClass in this
	 *         JModel
	 */
	public boolean hasMapping(Object id) {
		return classMap.containsKey(id);
	}

	/**
	 * Set the root of this JModel
	 * 
	 * @param root
	 *            is the new root JClass
	 */
	public void setRoot(JClass root) {
		this.root = root;
	}

	public boolean getAlwaysWriteToModel() {
		return this.writetostore;
	}

	public void setAlwaysWriteToModel(boolean alwaysWriteToModel) {
		this.writetostore= alwaysWriteToModel;
	}

	// TODO: implement equals() ?
}