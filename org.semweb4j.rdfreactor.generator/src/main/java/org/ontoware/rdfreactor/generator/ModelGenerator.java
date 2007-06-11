package org.ontoware.rdfreactor.generator;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.impl.jena24.ModelImplJena24;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.generator.java.JClass;
import org.ontoware.rdfreactor.generator.java.JModel;
import org.ontoware.rdfreactor.generator.java.JPackage;
import org.ontoware.rdfreactor.generator.java.JProperty;
import org.ontoware.rdfreactor.model.TypeUtils;
import org.ontoware.rdfreactor.schema.owl.DeprecatedProperty;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfschema.Class;
import org.ontoware.rdfreactor.schema.rdfschema.Property;
import org.ontoware.rdfreactor.schema.rdfschema.Resource;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

public class ModelGenerator {

	private static Log log = LogFactory.getLog(ModelGenerator.class);

	public static final String NS_XSD = "http://www.w3.org/2001/XMLSchema#";

	/**
	 * Add the buil-in mapping of URIs to classes.
	 * 
	 * @param jm
	 */
	private static void addBuiltInsCommon(JModel jm) {

		String rdfschema = "org.ontoware.rdfreactor.schema.rdfschema";

		// TODO: shouldnt JPackage.RDFSCHEMA be added to jm ?

		jm.addMapping(RDF.Alt, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".Alt", RDF.Alt));
		jm.addMapping(RDF.Bag, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".Bag", RDF.Bag));

		JClass _class = new JClass(JPackage.RDFSCHEMA, rdfschema + ".Class",
				RDFS.Class);
		jm.addMapping(RDFS.Class, _class);

		jm.addMapping(RDFS.Container, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".Container", RDFS.Container));
		jm.addMapping(RDFS.ContainerMembershipProperty, new JClass(
				JPackage.RDFSCHEMA, rdfschema + ".ContainerMembershipProperty",
				RDFS.ContainerMembershipProperty));
		jm.addMapping(RDFS.Datatype, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".Datatype", RDFS.Datatype));
		// literal mapping
		jm.addMapping(RDFS.Literal, JClass.STRING);
		jm.addMapping(RDF.List, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".List", RDF.List));
		jm.addMapping(RDF.nil, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".Nil", RDF.nil));
		// rdf property is special
		jm.addMapping(RDF.Seq, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".Seq", RDF.Seq));
		jm.addMapping(RDF.Statement, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".Statement", RDF.Statement));
		jm.addMapping(RDF.XMLLiteral, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".XMLLiteral", RDF.XMLLiteral));

		jm.addMapping(RDF.Property, new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".Property", RDF.Property));
		JClass _resource = new JClass(JPackage.RDFSCHEMA, rdfschema
				+ ".Resource", RDFS.Resource);

		jm.addMapping(RDFS.Resource, _resource);

		_resource.getProperties().add(
				new JProperty(_class, "label", RDFS.label));
		_resource.getProperties().add(
				new JProperty(_class, "comment", RDFS.comment));
		_resource.getProperties().add(new JProperty(_class, "type", RDF.type));

		// owl mapping
		String owl = "org.ontoware.rdfreactor.schema.owl";

		// TODO: shouldnt JPackage.OWL be added to jm ?

		jm.addMapping(OWL.Class, new JClass(JPackage.OWL, owl + ".Class",
				OWL.Class));
		jm.addMapping(OWL.AllDifferent, new JClass(JPackage.OWL, owl
				+ ".AllDifferent", OWL.AllDifferent));
		jm.addMapping(OWL.AnnotationProperty, new JClass(JPackage.OWL, owl
				+ ".AnnotationProperty", OWL.AnnotationProperty));
		jm.addMapping(OWL.DataRange, new JClass(JPackage.OWL, owl
				+ ".DataRange", OWL.DataRange));
		jm.addMapping(OWL.DatatypeProperty, new JClass(JPackage.OWL, owl
				+ ".DatatypeProperty", OWL.DatatypeProperty));
		jm.addMapping(OWL.DeprecatedClass, new JClass(JPackage.OWL, owl
				+ ".DeprecatedClass", OWL.DeprecatedClass));
		jm.addMapping(OWL.DeprecatedProperty, new JClass(JPackage.OWL, owl
				+ ".DeprecatedProperty", OWL.DeprecatedProperty));
		jm.addMapping(OWL.FunctionalProperty, new JClass(JPackage.OWL, owl
				+ ".FunctionalProperty", OWL.FunctionalProperty));
		jm.addMapping(OWL.InverseFunctionalProperty, new JClass(JPackage.OWL,
				owl + ".InverseFunctionalProperty",
				OWL.InverseFunctionalProperty));
		jm.addMapping(OWL.Nothing, new JClass(JPackage.OWL, owl + ".Nothing",
				OWL.Nothing));
		jm.addMapping(OWL.ObjectProperty, new JClass(JPackage.OWL, owl
				+ ".ObjectProperty", OWL.ObjectProperty));
		jm.addMapping(OWL.Ontology, new JClass(JPackage.OWL, owl + ".Ontology",
				OWL.Ontology));
		jm.addMapping(OWL.OntologyProperty, new JClass(JPackage.OWL, owl
				+ ".OntologyProperty", OWL.OntologyProperty));
		jm.addMapping(OWL.Restriction, new JClass(JPackage.OWL, owl
				+ ".Restriction", OWL.Restriction));
		jm.addMapping(OWL.SymmetricProperty, new JClass(JPackage.OWL, owl
				+ ".SymmetricProperty", OWL.SymmetricProperty));
		jm.addMapping(OWL.Thing, new JClass(JPackage.OWL, owl + ".Thing",
				OWL.Thing));
		jm.addMapping(OWL.TransitiveProperty, new JClass(JPackage.OWL, owl
				+ ".TransitiveProperty", OWL.TransitiveProperty));
		// XML Schema

		// IDs
		jm.addMapping(XSD._anyURI, new JClass(new JPackage("java.net"),
				URI.class.getName() + "", XSD._anyURI));

		// primitve types
		jm.addMapping(XSD._byte, new JClass(JPackage.JAVA_LANG, Byte.class
				.getName()
				+ "", XSD._byte));
		jm.addMapping(XSD._boolean, new JClass(JPackage.JAVA_LANG,
				Boolean.class.getName() + "", XSD._boolean));

		// numbers
		// TODO: better xsd handling for non negative integer
		jm.addMapping(XSD._nonNegativeInteger, new JClass(JPackage.JAVA_LANG,
				Integer.class.getName() + "", XSD._nonNegativeInteger));

		jm.addMapping(XSD._integer, new JClass(JPackage.JAVA_LANG,
				Integer.class.getName() + "", XSD._integer));
		jm.addMapping(XSD._int, new JClass(JPackage.JAVA_LANG, Integer.class
				.getName()
				+ "", XSD._int));
		jm.addMapping(XSD._long, new JClass(JPackage.JAVA_LANG, Long.class
				.getName()
				+ "", XSD._long));
		jm.addMapping(XSD._double, new JClass(JPackage.JAVA_LANG, Double.class
				.getName()
				+ "", XSD._double));
		jm.addMapping(XSD._float, new JClass(JPackage.JAVA_LANG, Float.class
				.getName()
				+ "", XSD._float));

		// date and time -> Date.class
		jm.addMapping(XSD._dateTime, new JClass(JPackage.JAVA_UTIL, Date.class
				.getName()
				+ "", XSD._dateTime));
		jm.addMapping(XSD._date, new JClass(JPackage.JAVA_UTIL, Date.class
				.getName()
				+ "", XSD._date));
		jm.addMapping(XSD._time, new JClass(JPackage.JAVA_UTIL, Date.class
				.getName()
				+ "", XSD._time));

		// strings
		jm.addMapping(XSD._normalizedString, new JClass(JPackage.JAVA_LANG,
				String.class.getName() + "", XSD._normalizedString));
		jm.addMapping(XSD._string, new JClass(JPackage.JAVA_LANG, String.class
				.getName()
				+ "", XSD._string));

		// get all properties
		jm.knownProperties.add(RDF.nil);
		jm.knownProperties.add(RDF.first);
		jm.knownProperties.add(RDF.rest);
		jm.knownProperties.add(RDF.subject);
		jm.knownProperties.add(RDF.predicate);
		jm.knownProperties.add(RDF.object);
		jm.knownProperties.add(RDF.type);
		jm.knownProperties.add(RDF.value);
		jm.knownProperties.add(RDFS.label);
		jm.knownProperties.add(RDFS.comment);
		jm.knownProperties.add(RDFS.seeAlso);
		jm.knownProperties.add(RDFS.isDefinedBy);
		jm.knownProperties.add(RDFS.domain);
		jm.knownProperties.add(RDFS.range);
		jm.knownProperties.add(RDFS.subClassOf);
		jm.knownProperties.add(RDFS.subPropertyOf);

	}

	/**
	 * @return a JModel with the common built-ins
	 */
	private static JModel getbuiltIns_RDFS() {
		JModel jm = new JModel(JClass.RDFS_CLASS, true);
		addBuiltInsCommon(jm);
		return jm;
	}

	/**
	 * @return a JModel with the common built-ins
	 */
	private static JModel getbuiltIns_RDFS_AND_OWL() {
		JModel jm = new JModel(JClass.RDFS_CLASS, true);
		addBuiltInsCommon(jm);
		return jm;
	}

	/**
	 * @return a JModel with the common built-ins
	 */
	static JModel getbuiltIns_OWL() {
		JModel jm = new JModel(JClass.OWL_CLASS, true);
		addBuiltInsCommon(jm);
		return jm;
	}

	public static JModel createFromRDFS(
			com.hp.hpl.jena.rdf.model.Model schemaDataModel,
			String packagename, boolean skipbuiltins) throws Exception {
		log.info("Schema has " + schemaDataModel.size() + " triples");

		// enable RDFS inferencing
		com.hp.hpl.jena.rdf.model.Model jenaModel = ModelFactory
				.createRDFSModel(schemaDataModel);
		Model m = new ModelImplJena24(null, jenaModel);
		m.open();
		return createFromRDFS(m, packagename, skipbuiltins);

	}

	private static String toJavaComment(String[] rdfComments) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < rdfComments.length; i++) {
			buf.append(rdfComments[i]);
			if (i + 1 < rdfComments.length) {
				buf.append("\n");
			}
		}
		return buf.toString();
	}

	/** create from RDF2Go model */
	public static JModel createFromRDFS(Model m, String packagename,
			boolean skipbuiltins) throws Exception {

		// prepare JModel
		log.debug("add build-ins");
		JModel jm = getbuiltIns_RDFS();
		JPackage jp = new JPackage(packagename);
		jm.getPackages().add(jp);
		/**
		 * local ontology root
		 */
		JClass localRoot = new JClass(jp, "Thing", RDFS.Class);
		localRoot
				.setComment("This class acts as a catch-all for all properties, for which no domain has specified.");
		localRoot.addSuperclass(jm.getRoot());
		jm.setRoot(localRoot);

		// process
		log.debug("de-anonymizing (replacing bnodes with random uris");
		deanonymize(m);

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
				jc.setComment(toJavaComment(rc.getAllComment())); // might be
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
			log.debug("PROPERTY " + rp.getResource());

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
				// no domain = no generated property
				// TODO: ignore if already in higher level
				if (domains == null || domains.length == 0) {
					log.warn("PROPERTY " + rp.getResource()
							+ " has no domain, using root");
					handleProperty(m, jm, jm.getRoot(), rp);
				} else {
					for (Resource domain : domains) {
						log.debug("PROPERTY " + rp.getResource()
								+ " has domain " + domain);
						JClass domainClass = jm
								.getMapping(domain.getResource());
						assert domainClass != null : "found no JClass for "
								+ rp.getDomain().getResource();

						// domainclass might be a built-in, redirect to root
						if (getbuiltIns_RDFS().classMap
								.containsValue(domainClass)) {
							log
									.debug("domain "
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
			com.hp.hpl.jena.rdf.model.Model schemaDataModel,
			String packagename, boolean skipbuiltins) throws Exception {
		JModel jm = getbuiltIns_RDFS_AND_OWL();

		com.hp.hpl.jena.rdf.model.Model jenaModel = ModelFactory
				.createRDFSModel(schemaDataModel);
		Model m = new ModelImplJena24(null, jenaModel);
		m.open();

		deanonymize(m);

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
			com.hp.hpl.jena.rdf.model.Model schemaDataModel,
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

		Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();// miniReasoner();
		reasoner = reasoner.bindSchema(schemaDataModel);
		InfModel jenaModel = ModelFactory.createInfModel(reasoner,
				schemaDataModel);
		Model m = new ModelImplJena24(null, jenaModel);

		log.debug("de-anonymizing");
		deanonymize(m);

		log.debug("after inferencing, found " + m.size() + " statements");

		// // DEBUG
		// File debugOut = new File(outDir, packagename + "/schema.nt");
		// debugOut.mkdir();
		// jenaModel.write(new FileWriter(debugOut), "N-TRIPLES");

		JPackage jp = new JPackage(packagename);

		// bootstrapping?

		JModel jm = ModelGenerator.getbuiltIns_OWL();
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
			} else if (classURI.toString().startsWith(NS_XSD)) {
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
		// name it
		String propertyName = JavaNamingUtils.toBeanName(rp,
				domainClass.usedPropertynames);
		domainClass.usedPropertynames.add(propertyName);
		assert propertyName != null;
		JProperty jprop = new JProperty(domainClass, propertyName, (URI) rp
				.getResource());
		jprop.setComment(toJavaComment(rp.getAllComment())); // might be
																// null,
		// wire
		log.debug("Adding property '" + jprop.getName() + "' to '"
				+ domainClass.getName() + "'");
		jprop.getJClass().getProperties().add(jprop);
		// ok.
		log.debug("PROPERTY by domain added '" + jprop.getName() + "' to '"
				+ domainClass.getName() + "'");

		log.debug("PROPERTY checking ranges...");
		for (Class range : rp.getAllRange()) {
			log.debug("range is " + range);
			jprop.addType(jm.getMapping(range.getResource()));
		}

		if (rp.getAllRange().length == 0) {
			// set to Class
			jprop.addType(jm.getRoot());
		}

		// figure out cardinality
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

		// if (jprop.getTypes().size() == 0)
		// log.debug(jprop+ " no type defined");
		// jprop.fixRanges(jm);
		// if (jprop.getTypes().size() == 0)
		// log.warn(jprop+ " has no types after fixing");

	}

	/**
	 * Remove all Statements with BlankNodes as Subjects or Objects from the
	 * given RDF2Go model.
	 * 
	 * @param m -
	 *            deanonymize this model
	 * @throws Exception
	 */
	public static void deanonymize(Model m) throws Exception {
		Iterator<Statement> it = m.iterator();
		Map<BlankNode, URI> replacement = new HashMap<BlankNode, URI>();
		Set<Statement> badStatements = new HashSet<Statement>();
		Set<Statement> goodStatements = new HashSet<Statement>();
		while (it.hasNext()) {
			Statement s = it.next();
			org.ontoware.rdf2go.model.node.Resource subject = s.getSubject();
			if (s.getSubject() instanceof BlankNode) {
				badStatements.add(s);
				subject = toURI((BlankNode) subject, replacement);
			}
			Node object = s.getObject();
			if (object instanceof BlankNode) {
				badStatements.add(s);
				object = toURI((BlankNode) object, replacement);
			}
			goodStatements.add(new StatementImpl(m.getContextURI(), subject, s
					.getPredicate(), object));
		}
		for (Statement s : badStatements)
			m.removeStatement(s);
		for (Statement s : goodStatements)
			m.addStatement(s);

	}

	/** counter used for toURI() */
	private static long counter = 0;

	/**
	 * Generate a unique URI for a BlankNode and put the BlankNode/URI pair into
	 * the given map.
	 * 
	 * @param blankNode -
	 *            generate a URI for this BlankNode
	 * @param replacement -
	 *            Map of BlankNode/URI pairs
	 * @return URI generated for the BlankNode
	 */
	private static URI toURI(BlankNode blankNode,
			Map<BlankNode, URI> replacement) {
		URI result = replacement.get(blankNode);
		if (result == null)
			result = new URIImpl("blank://" + counter++);
		// TODO BlankNode identity might be too weak
		replacement.put(blankNode, result);
		return result;
	}
}
