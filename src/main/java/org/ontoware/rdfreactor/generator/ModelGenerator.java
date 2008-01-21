package org.ontoware.rdfreactor.generator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.generator.java.JClass;
import org.ontoware.rdfreactor.generator.java.JModel;
import org.ontoware.rdfreactor.generator.java.JPackage;
import org.ontoware.rdfreactor.generator.java.JProperty;
import org.ontoware.rdfreactor.runtime.TypeUtils;
import org.ontoware.rdfreactor.schema.owl.DeprecatedProperty;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfschema.Class;
import org.ontoware.rdfreactor.schema.rdfschema.Property;
import org.ontoware.rdfreactor.schema.rdfschema.Resource;

/**
 * Creates an internal JModel from an ontology model
 * @author voelkel
 *
 */
public class ModelGenerator {

	private static Log log = LogFactory.getLog(ModelGenerator.class);

	public static JModel createFromRDFS_Schema(
			Model schemaDataModel,
			String packagename, boolean skipbuiltins) throws Exception {

		log.info("Input model has " + schemaDataModel.size() + " triples");

		// enable RDFS inferencing
		Model m = RDF2Go.getModelFactory().createModel( Reasoning.rdfs );
		m.open();
		m.addAll(schemaDataModel.iterator());
		
		// prepare JModel
		log.debug("add build-ins");
		JModel jm = Semantics.getbuiltIns_RDFS();
		JPackage jp = new JPackage(packagename);
		jm.getPackages().add(jp);

		// set local ontology root
		JClass localRoot = new JClass(jp, "Thing", RDFS.Class);
		localRoot
				.setComment("This class acts as a catch-all for all properties, for which no domain has specified.");
		localRoot.addSuperclass(jm.getRoot());
		jm.setRoot(localRoot);

		
		// process
		log.debug("de-anonymizing (replacing bnodes with random uris");
		Utils.deanonymize(m);

		// analysis (triggers also inferencing)
		Class[] rdfclasses = Class.getAllInstances(m);
		log.info("Got " + rdfclasses.length + " rdfs:Classes");
		for (Class c : rdfclasses) {
			log.debug("Found class: " + c.getResource());
		}
		Property[] rdfproperties = Property.getAllInstances(m);
		for (Property p : rdfproperties) {
			log.debug("Found property: " + p.getResource());
		}
		log.info("Got " + rdfproperties.length + " rdfs:Properties");
		log.debug("Found " + m.size()
				+ " statements in schema after inferencing.");

		// get all classes and assign to package
		Set<String> usedClassnames = new HashSet<String>();
		Set<Class> rdfsClasses = new HashSet<Class>();

		for (Class rc : Class.getAllInstances(m)) {

			if (skipbuiltins && jm.hasMapping(rc.getResource())) {
				log.debug("CLASS " + rc + " is known -> skipping generation");
			} else if (!(rc.getResource() instanceof URI)) {
				log
						.warn("A Class with a blank node ID makes not much sense -> ignored");
			} else {
				rdfsClasses.add(rc);
				// TODO better classname guessing
				String classname = JavaNamingUtils.toBeanName(rc,
						usedClassnames);
				assert classname != null;
				usedClassnames.add(classname);

				log.debug("CLASS " + classname + " generated for "
						+ rc.getResource() + " ...");
				assert rc.getResource() instanceof URI : "A Class with a blank node ID makes not much sense";
				JClass jc = new JClass(jp, classname, (URI) rc.getResource());
				jc.setComment(Utils.toJavaComment(rc.getAllComment())); // might be
				// null, ok.
				jm.addMapping(rc.getResource(), jc);
			}
		}

		log.debug(">>>> Inheritance");
		// get all classes and link superclasses
		for (org.ontoware.rdfreactor.schema.rdfschema.Class rc : rdfsClasses) {
			log.debug("rdfs:Class " + rc.getResource());
			JClass jc = jm.getMapping(rc.getResource());
			for (org.ontoware.rdfreactor.schema.rdfschema.Class superclass : rc
					.getAllSubClassOf())
				jc.addSuperclass(jm.getMapping(superclass.getResource()));
		}

		jm.flattenInheritanceHierarchy(jp);

		log.info("-------------- PROPERTIES ...");

		for (Property rp : Property.getAllInstances(m)) {
			log.info("PROPERTY " + rp.getResource());

			if (skipbuiltins
					&& jm.knownProperties.contains(rp.getResource().asURI())) {
				// do nothing
				log.debug("Skipping built-in property "
						+ rp.getResource().asURI().toSPARQL());
			} else if (DeprecatedProperty.hasInstance(rp.getModel(), rp
					.getResource().asURI())) {
				log.info("Skipping deprecated property " + rp
						+ "(as indicated by owl:DeprecatedProperty)");
			} else {
				// inspect domains
				Resource[] domains = rp.getAllDomain();
				// TODO: ignore if already in higher level
				if (domains == null || domains.length == 0) {
					log.warn("PROPERTY " + rp.getResource()
							+ " has no domain, using root");
					handleProperty(m, jm, jm.getRoot(), rp);
				} else {
					for (Resource domain : domains) {
						log.info("PROPERTY " + rp.getResource()
								+ " has domain " + domain);
						JClass domainClass = jm
								.getMapping(domain.getResource());
						assert domainClass != null : "found no JClass for "
								+ rp.getDomain().getResource();

						// domainclass might be a built-in, redirect to root
						if (Semantics.getbuiltIns_RDFS().classMap
								.containsValue(domainClass)) {
							log
									.info("domain "
											+ domainClass
											+ " is a built-in, hence we attach the property to the root ("
											+ jm.getRoot() + ")");
							domainClass = jm.getRoot();
						}

						handleProperty(m, jm, domainClass, rp);
					}
				}
			}

		}
		return jm;
	}

	/**
	 * TODO: this is experimental
	 * 
	 * @param schemaDataModel
	 *            com.hp.hpl.jena.rdf.model.Model
	 * @param packagename
	 * @return
	 * @throws Exception
	 */
	public static JModel createFromRDFS_AND_OWL(
			Model schemaDataModel,
			String packagename, boolean skipbuiltins) throws Exception {
		JModel jm = Semantics.getbuiltIns_RDFS();

		Model m = RDF2Go.getModelFactory().createModel( Reasoning.rdfsAndOwl );
		m.open();
		m.addAll(schemaDataModel.iterator());
		
//		com.hp.hpl.jena.rdf.model.Model jenaModel = ModelFactory
//				.createRDFSModel(schemaDataModel);
//		Model m = new ModelImplJena24(null, jenaModel);
//		m.open();

		Utils.deanonymize(m);

		// add mapping from OWL to RDF
		m.addStatement(OWL.Class, RDFS.subClassOf, RDFS.Class);
		m.addStatement(OWL.AnnotationProperty, RDFS.subClassOf, RDF.Property);
		m.addStatement(OWL.DatatypeProperty, RDFS.subClassOf, RDF.Property);
		m.addStatement(OWL.FunctionalProperty, RDFS.subClassOf, RDF.Property);
		m.addStatement(OWL.InverseFunctionalProperty, RDFS.subClassOf,
				RDF.Property);
		m.addStatement(OWL.ObjectProperty, RDFS.subClassOf, RDF.Property);
		m.addStatement(OWL.OntologyProperty, RDFS.subClassOf, RDF.Property);

		log.debug("MODEL after inferencing, found " + m.size() + " statements");
		JPackage jp = new JPackage(packagename);
		jm.getPackages().add(jp);

		// FIXME experimental to handle properties with no given domain
		JClass localClass = new JClass(jp, "Class", RDFS.Class);
		localClass.addSuperclass(jm.getRoot());
		jm.setRoot(localClass);

		// get all classes and assign to package
		Set<String> usedClassnames = new HashSet<String>();
		Set<Class> rdfsClasses = new HashSet<Class>();

		for (Class rc : Class.getAllInstances(m)) {

			if (skipbuiltins && jm.hasMapping(rc.getResource())) {
				log.debug("CLASS " + rc + " is known -> skipping generation");
			} else {
				rdfsClasses.add(rc);
				// TODO better classname guessing
				String classname = JavaNamingUtils.toBeanName(rc,
						usedClassnames);
				assert classname != null;
				usedClassnames.add(classname);

				log.debug("CLASS " + classname + " generated for "
						+ rc.getResource() + " ...");
				JClass jc = new JClass(jp, classname, (URI) rc.getResource());
				jc.setComment(rc.getComment()); // might be null, ok.
				jm.addMapping(rc.getResource(), jc);
			}
		}

		log.debug(">>>> Inheritance");
		// get all classes and link superclasses
		for (org.ontoware.rdfreactor.schema.rdfschema.Class rc : rdfsClasses) {
			log.debug("rdfs:Class " + rc.getResource());
			JClass jc = jm.getMapping(rc.getResource());
			for (org.ontoware.rdfreactor.schema.rdfschema.Class superclass : rc
					.getAllSubClassOf())
				jc.addSuperclass(jm.getMapping(superclass.getResource()));
		}

		jm.flattenInheritanceHierarchy(jp);

		// get all properties
		log.info("-------------- PROPERTIES ...");
		for (Property rp : Property.getAllInstances(m)) {
			log.debug("PROPERTY " + rp.getResource());
			Resource[] domains = rp.getAllDomain();
			// no domain = no generated property
			if (domains == null || domains.length == 0) {
				// log.warn("PROPERTY " + rp.getID() + " has no domain, so we
				// ignore it");
				log.debug("PROPERTY " + rp.getResource()
						+ " has no domain, using root");
				handleProperty(m, jm, jm.getRoot(), rp);
			} else {
				for (Resource domain : domains) {
					JClass domainClass = jm.getMapping(domain.getResource());
					assert domainClass != null : "found no JClass for "
							+ rp.getDomain().getResource();
					handleProperty(m, jm, domainClass, rp);
				}
			}
		}
		return jm;
	}

	public static JModel createFromOWL(
			Model schemaDataModel,
			String packagename, boolean skipbuiltins) throws Exception {
		// DIGReasonerFactory drf = (DIGReasonerFactory) ReasonerRegistry
		// .theRegistry().getFactory(DIGReasonerFactory.URI);
		// DIGReasoner r = (DIGReasoner) drf.createWithOWLAxioms(null);
		// OntModel base = ModelFactory.createOntologyModel(
		// OntModelSpec.OWL_DL_MEM, null);
		// // ... build or load the model contents ...
		// base.add(schemaDataModel);
		//
		// OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
		// spec.setReasoner(r);
		//
		// OntModel combined = ModelFactory.createOntologyModel(spec, base);
		//
		// Model m = new ModelImplJena22(combined);

		Model m = RDF2Go.getModelFactory().createModel(Reasoning.owl);
		m.open();
		m.addAll(schemaDataModel.iterator());
		
//		Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();// miniReasoner();
//		reasoner = reasoner.bindSchema(schemaDataModel);
//		InfModel jenaModel = ModelFactory.createInfModel(reasoner,
//				schemaDataModel);
//		Model m = new ModelImplJena24(null, jenaModel);

		log.debug("de-anonymizing");
		Utils.deanonymize(m);

		log.debug("after inferencing, found " + m.size() + " statements");

		// // DEBUG
		// File debugOut = new File(outDir, packagename + "/schema.nt");
		// debugOut.mkdir();
		// jenaModel.write(new FileWriter(debugOut), "N-TRIPLES");

		JPackage jp = new JPackage(packagename);

		// bootstrapping?

		JModel jm = Semantics.getbuiltIns_OWL();
		jm.addPackage(jp);

		/**
		 * local ontology root
		 */
		JClass localRoot = new JClass(jp, "Thing", OWL.Class);
		localRoot
				.setComment("This class acts as a catch-all for all properties, for which no domain has specified.");
		localRoot.addSuperclass(jm.getRoot());
		jm.setRoot(localRoot);

		// get all classes and assign to package
		Set<org.ontoware.rdfreactor.schema.owl.Class> owlClasses = new HashSet<org.ontoware.rdfreactor.schema.owl.Class>();
		Set<String> usedClassnames = new HashSet<String>();

		for (org.ontoware.rdfreactor.schema.owl.Class oc : org.ontoware.rdfreactor.schema.owl.Class
				.getAllInstances(m)) {
			log.debug("Found owl:Class " + oc.getResource() + " (have "
					+ owlClasses.size() + " already)");

			org.ontoware.rdf2go.model.node.Resource classURI = oc.getResource();

			// check if restriction or real class
			if (m.contains(classURI, RDF.type, OWL.Restriction)) {
				log.debug("skipping restriction " + classURI);
			} else if (skipbuiltins && jm.hasMapping(classURI)) {
				log.debug("skipping known class " + classURI);
				// TODO add all XSD classes to default JModels and remove this
				// check
			} else if (classURI.toString().startsWith(Semantics.NS_XSD)) {
				log.debug("skipping XML Schema class " + classURI);
				// TODO: what is the purpose of this?
			} else if (oc.getResource() instanceof BlankNode) {
				log.debug("skipping blank class " + classURI);
			} else {
				log.debug("owl:Class : " + classURI);
				owlClasses.add(oc);
				// TODO better classname guessing
				String classname = JavaNamingUtils.toBeanName(oc,
						usedClassnames);
				assert classname != null;
				usedClassnames.add(classname);
				log.debug("generating class " + classname + " for " + classURI
						+ " ...");
				JClass jc = new JClass(jp, classname, (URI) oc.getResource());
				jc.setComment(oc.getComment());
				jm.addMapping((URI) oc.getResource(), jc);
			}
		}
		log.debug("dealing with " + owlClasses.size() + " 'real' classes");

		log.debug(">>>> Inheritance");
		// get all classes and link superclasses
		for (org.ontoware.rdfreactor.schema.owl.Class oc : owlClasses) {
			log.debug("owl:Class " + oc.getResource());
			JClass jc = jm.getMapping(oc.getResource());
			for (org.ontoware.rdfreactor.schema.owl.Class superclass : TypeUtils
					.getAllRealSuperclasses(oc, owlClasses))
				jc.addSuperclass(jm.getMapping(superclass.getResource()));
		}
		jm.flattenInheritanceHierarchy(jp);

		// get all properties
		log.info(">>> Processing properties ...");

		// this uniqueness constraint can be weakened,
		// property names need only to be unique within a class,
		// but this might be more consistent anyways
		Set<String> usedPropertynames = new HashSet<String>();

		for (Property rp : Property.getAllInstances(m)) {
			log.debug("> Processing property " + rp.getResource());
			// name it
			String propertyName = JavaNamingUtils.toBeanName(rp,
					usedPropertynames);
			usedPropertynames.add(propertyName);
			assert propertyName != null;

			Class[] domains = rp.getAllDomain();
			// no domain = no generated property
			if (domains == null || domains.length == 0) {
				log.warn("Property " + rp.getResource()
						+ " has no domain, so we ignore it");
			} else {
				for (Class domain : domains) {
					if (!owlClasses.contains(domain.castTo(Class.class))) {
						// log.debug("ignored");
					} else {
						JClass domainClass = jm
								.getMapping(domain.getResource());
						assert domainClass != null : "found no JClass for "
								+ rp.getDomain().getResource();

						JProperty jprop = new JProperty(domainClass,
								propertyName, (URI) rp.getResource());
						// wire
						log.debug("Adding property '" + jprop.getName()
								+ "' to '" + domainClass.getName() + "'");
						jprop.getJClass().getProperties().add(jprop);
						jprop.setComment(rp.getComment());

						for (Class range : rp.getAllRange()) {
							if (owlClasses
									.contains(range
											.castTo(org.ontoware.rdfreactor.schema.owl.Class.class)))
								jprop.addType(jm
										.getMapping(range.getResource()));
						}
						jprop.fixRanges(jm);

						// figure out cardinaliy
						for (Restriction restriction : TypeUtils
								.getAllRestriction(rp)) {
							int min = restriction.getMinCardinality();
							log.debug("Found minrestriction on " + rp
									+ " minCard = " + min);
							if (min != -1)
								jprop.setMinCardinality(min);
							int max = restriction.getMaxCardinality();
							log.debug("Found maxrestriction on " + rp
									+ " maxCard = " + max);
							if (max != -1)
								jprop.setMaxCardinality(max);
						}

					}
				}

			}
		}

		// // prune
		// log.debug(">>>>>> Pruning");
		// for (JClass jc : jp.getClasses()) {
		// // FIXME: this is too simple: if no properties: remove
		// if (jc.getProperties().size() == 0) {
		// log.debug(jc.getName() + " has no properties, removing");
		// jp.getClasses().remove(jc);
		// }
		// }
		return jm;
	}

	/**
	 * Handle all aspects of integrating a property into the JModel: construct
	 * the JProperty, add all its ranges to the JProperty and set min and max
	 * cardinality.
	 * 
	 * @param m -
	 *            the underlying RDF2Go model
	 * @param jm -
	 *            the target JModel
	 * @param domainClass -
	 *            the JClass domain of the property
	 * @param rp -
	 *            the Property instance representing the Property in the RDF2Go
	 *            model
	 */
	private static void handleProperty(Model m, JModel jm, JClass domainClass,
			Property rp) {

		// obtain a nice Java-conform name which has not yet been used
		String propertyName = JavaNamingUtils.toBeanName(rp,
				domainClass.getUsedPropertyNames());
		assert propertyName != null;
		JProperty jprop = new JProperty(domainClass, propertyName, (URI) rp
				.getResource());
		// carry over the comment from RDF to Java, might be null
		jprop.setComment(Utils.toJavaComment(rp.getAllComment())); 
		log.debug("PROPERTY Adding '" + jprop.getName() + "' to '"
				+ domainClass.getName() + "'");
		jprop.getJClass().getProperties().add(jprop);

		// process range information
		log.debug("PROPERTY checking ranges...");
		for (Class range : rp.getAllRange()) {
			log.debug("range is " + range);
			jprop.addType(jm.getMapping(range.getResource()));
		}
		if (rp.getAllRange().length == 0) {
			// if no range is given, set to ontology root class (rdfs:Class or owl:Class)
			jprop.addType(jm.getRoot());
		}

		// process cardinality constraints
		Restriction restriction = (Restriction) rp.castTo(Restriction.class);
		int cardinality = restriction.getCardinality();
		int min = restriction.getMinCardinality();
		// might still be -1
		if (min == -1)
			min = cardinality;
		if (min != -1) {
			log.debug("Found minrestriction on " + rp + " minCard = " + min);
			jprop.setMinCardinality(min);
		}
		int max = restriction.getMaxCardinality();
		// might still be -1
		if (max == -1)
			max = cardinality;
		if (max != -1) {
			log.debug("Found maxrestriction on " + rp + " maxCard = " + max);
			jprop.setMaxCardinality(max);
		}

	}
}
