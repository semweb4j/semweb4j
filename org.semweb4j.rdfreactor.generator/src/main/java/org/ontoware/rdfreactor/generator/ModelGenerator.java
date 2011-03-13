package org.ontoware.rdfreactor.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.generator.java.JClass;
import org.ontoware.rdfreactor.generator.java.JModel;
import org.ontoware.rdfreactor.generator.java.JPackage;
import org.ontoware.rdfreactor.generator.java.JProperty;
import org.ontoware.rdfreactor.schema.bootstrap.Class;
import org.ontoware.rdfreactor.schema.bootstrap.DeprecatedProperty;
import org.ontoware.rdfreactor.schema.bootstrap.OWL_Protege_NRL_Restriction;
import org.ontoware.rdfreactor.schema.bootstrap.Property;
import org.ontoware.rdfreactor.schema.bootstrap.Resource;
import org.ontoware.rdfreactor.schema.bootstrap.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Creates an internal JModel from an ontology model.
 * 
 * @author voelkel
 */
public class ModelGenerator {
	
	private static Logger log = LoggerFactory.getLogger(ModelGenerator.class);
	
	public static JModel createFromRDFS_Schema(Model modelWithSchemaData, String packagename,
	        boolean skipbuiltins) {
		
		log.info("Input model has " + modelWithSchemaData.size() + " triples");
		
		// enable RDFS inferencing
		Model m = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		m.open();
		m.addAll(modelWithSchemaData.iterator());
		
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
		List<? extends Class> rdfclasses = Class.getAllInstance_as(m).asList();
		log.info("Got " + rdfclasses.size() + " rdfs:Classes");
		for(Class c : rdfclasses) {
			log.debug("Found class: " + c.getResource());
		}
		Property[] rdfproperties = Property.getAllInstance_as(m).asArray();
		for(Property p : rdfproperties) {
			log.debug("Found property: " + p.getResource());
		}
		log.info("Got " + rdfproperties.length + " rdfs:Properties");
		log.debug("Found " + m.size() + " statements in schema after inferencing.");
		
		// get all classes and assign to package
		Set<String> usedClassnames = new HashSet<String>();
		usedClassnames.add(jm.getRoot().getName());
		Set<Class> rdfsClasses = new HashSet<Class>();
		
		for(Class rc : Class.getAllInstance_as(m).asList()) {
			
			if(skipbuiltins && jm.hasMapping(rc.getResource())) {
				log.debug("CLASS " + rc + " is known -> skipping generation");
			} else if(!(rc.getResource() instanceof URI)) {
				log.warn("A Class with a blank node ID makes not much sense -> ignored");
			} else {
				rdfsClasses.add(rc);
				// TODO better classname guessing
				String classname = JavaNamingUtils.toBeanName(rc, usedClassnames);
				assert classname != null;
				usedClassnames.add(classname);
				
				log.debug("CLASS " + classname + " generated for " + rc.getResource() + " ...");
				assert rc.getResource() instanceof URI : "A Class with a blank node ID makes not much sense";
				JClass jc = new JClass(jp, classname, (URI)rc.getResource());
				jc.setComment(Utils.toJavaComment(rc.getAllComment_asList())); // might
				// be
				// null, ok.
				jm.addMapping(rc.getResource(), jc);
			}
		}
		
		log.debug(">>>> Inheritance");
		// get all classes and link superclasses
		for(org.ontoware.rdfreactor.schema.bootstrap.Class rc : rdfsClasses) {
			log.debug("rdfs:Class " + rc.getResource());
			JClass jc = jm.getMapping(rc.getResource());
			for(org.ontoware.rdfreactor.schema.bootstrap.Class superclass : rc
			        .getAllSubClassOf_asList())
				jc.addSuperclass(jm.getMapping(superclass.getResource()));
		}
		
		log.info("-------------- PROPERTIES ...");
		
		for(Property rp : Property.getAllInstance_as(m).asList()) {
			log.info("PROPERTY " + rp.getResource());
			
			if(skipbuiltins && jm.knownProperties.contains(rp.getResource().asURI())) {
				// do nothing
				log.debug("Skipping built-in property " + rp.getResource().asURI().toSPARQL());
			} else if(DeprecatedProperty.hasInstance(rp.getModel(), rp.getResource().asURI())) {
				log.info("Skipping deprecated property " + rp
				        + "(as indicated by owl:DeprecatedProperty)");
			} else {
				// inspect domains
				List<Class> domains = rp.getAllDomain_asList();
				// TODO: ignore if already in higher level
				if(domains == null || domains.size() == 0) {
					log.warn("PROPERTY " + rp.getResource() + " has no domain, using root");
					handleProperty(m, jm, jm.getRoot(), rp);
				} else {
					for(Resource domain : domains) {
						log.info("PROPERTY " + rp.getResource() + " has domain " + domain);
						JClass domainClass = jm.getMapping(domain.getResource());
						assert domainClass != null : "found no JClass for "
						        + rp.getAllDomain_asList().get(0).getResource();
						
						// domainclass might be a built-in, redirect to root
						if(Semantics.getbuiltIns_RDFS().containsJClass(domainClass)) {
							log.info("domain " + domainClass
							        + " is a built-in, hence we attach the property to the root ("
							        + jm.getRoot() + ")");
							domainClass = jm.getRoot();
						}
						
						handleProperty(m, jm, domainClass, rp);
					}
				}
			}
			
			jm.flattenInheritanceHierarchy(jp);
			
			jm.materialiseMissingProperties(jp);
			
		}
		m.close();
		m = null;
		return jm;
	}
	
	/**
	 * TODO: this is experimental
	 * 
	 * @param schemaDataModel com.hp.hpl.jena.rdf.model.Model
	 * @param packagename
	 * @return
	 * @throws Exception
	 */
	public static JModel createFromRDFS_AND_OWL(Model schemaDataModel, String packagename,
	        boolean skipbuiltins) {
		log.info("Initialising JModel");
		JModel jm = Semantics.getbuiltIns_RDFS();
		
		log.info("Loading schema triples");
		Model m = RDF2Go.getModelFactory().createModel(Reasoning.rdfsAndOwl);
		m.open();
		m.addAll(schemaDataModel.iterator());
		
		// com.hp.hpl.jena.rdf.model.Model jenaModel = ModelFactory
		// .createRDFSModel(schemaDataModel);
		// Model m = new ModelImplJena24(null, jenaModel);
		// m.open();
		
		log.info("Skolemisation (replacing all blank nodes with random URIs)");
		Utils.deanonymize(m);
		
		log.info("Add mapping from OWL to RDF");
		// add mapping from OWL to RDF
		m.addStatement(OWL.Class, RDFS.subClassOf, RDFS.Class);
		m.addStatement(OWL.AnnotationProperty, RDFS.subClassOf, RDF.Property);
		m.addStatement(OWL.DatatypeProperty, RDFS.subClassOf, RDF.Property);
		m.addStatement(OWL.FunctionalProperty, RDFS.subClassOf, RDF.Property);
		m.addStatement(OWL.InverseFunctionalProperty, RDFS.subClassOf, RDF.Property);
		m.addStatement(OWL.ObjectProperty, RDFS.subClassOf, RDF.Property);
		m.addStatement(OWL.OntologyProperty, RDFS.subClassOf, RDF.Property);
		
		log.debug("MODEL after inferencing, found " + m.size() + " statements");
		JPackage jp = new JPackage(packagename);
		jm.getPackages().add(jp);
		
		log.info("Creating a class called 'Thing' for all properties with no given domain");
		JClass localClass = new JClass(jp, "Thing", RDFS.Class);
		localClass.addSuperclass(jm.getRoot());
		jm.setRoot(localClass);
		
		// get all classes and assign to package
		Set<String> usedClassnames = new HashSet<String>();
		usedClassnames.add(jm.getRoot().getName());
		Set<Class> rdfsClasses = new HashSet<Class>();
		
		for(Class rc : Class.getAllInstance_as(m).asList()) {
			
			if(skipbuiltins && jm.hasMapping(rc.getResource())) {
				log.debug("CLASS " + rc + " is known -> skipping generation");
			} else {
				rdfsClasses.add(rc);
				// TODO better class-name guessing
				String classname = JavaNamingUtils.toBeanName(rc, usedClassnames);
				assert classname != null;
				usedClassnames.add(classname);
				
				log.debug("CLASS " + classname + " generated for " + rc.getResource().toSPARQL()
				        + " ...");
				JClass jc = new JClass(jp, classname, (URI)rc.getResource());
				jc.setComment(rc.getAllComment_asList().get(0)); // might be
				// null, ok.
				jm.addMapping(rc.getResource(), jc);
			}
		}
		
		log.info(">>>> Inheritance");
		// get all classes and link super-classes
		for(org.ontoware.rdfreactor.schema.bootstrap.Class rc : rdfsClasses) {
			log.debug("rdfs:Class " + rc.getResource());
			JClass jc = jm.getMapping(rc.getResource());
			for(org.ontoware.rdfreactor.schema.bootstrap.Class superclass : rc
			        .getAllSubClassOf_asList())
				jc.addSuperclass(jm.getMapping(superclass.getResource()));
		}
		
		log.info(">>>> Flatten inheritance hierarchy");
		jm.flattenInheritanceHierarchy(jp);
		
		// get all properties
		log.info("-------------- PROPERTIES ...");
		for(Property rp : Property.getAllInstance_as(m).asList()) {
			log.debug("PROPERTY " + rp.getResource());
			List<Class> domains = rp.getAllDomain_asList();
			// no domain = no generated property
			if(domains == null || domains.size() == 0) {
				// log.warn("PROPERTY " + rp.getID() + " has no domain, so we
				// ignore it");
				log.debug("PROPERTY " + rp.getResource() + " has no domain, using root");
				handleProperty(m, jm, jm.getRoot(), rp);
			} else {
				for(Resource domain : domains) {
					JClass domainClass = jm.getMapping(domain.getResource());
					assert domainClass != null : "found no JClass for "
					        + rp.getAllDomain_asList().get(0).getResource();
					handleProperty(m, jm, domainClass, rp);
				}
			}
		}
		m.close();
		return jm;
	}
	
	public static JModel createFromOWL(Model schemaDataModel, String packagename,
	        boolean skipbuiltins) {
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
		
		// Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();//
		// miniReasoner();
		// reasoner = reasoner.bindSchema(schemaDataModel);
		// InfModel jenaModel = ModelFactory.createInfModel(reasoner,
		// schemaDataModel);
		// Model m = new ModelImplJena24(null, jenaModel);
		
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
		Set<org.ontoware.rdfreactor.schema.bootstrap.OwlClass> owlClasses = new HashSet<org.ontoware.rdfreactor.schema.bootstrap.OwlClass>();
		Set<String> usedClassnames = new HashSet<String>();
		
		for(org.ontoware.rdfreactor.schema.bootstrap.OwlClass oc : org.ontoware.rdfreactor.schema.bootstrap.OwlClass
		        .getAllInstance_as(m).asList()) {
			log.debug("Found owl:Class " + oc.getResource() + " (have " + owlClasses.size()
			        + " already)");
			
			org.ontoware.rdf2go.model.node.Resource classURI = oc.getResource();
			
			// check if restriction or real class
			if(m.contains(classURI, RDF.type, OWL.Restriction)) {
				log.debug("skipping restriction " + classURI);
			} else if(skipbuiltins && jm.hasMapping(classURI)) {
				log.debug("skipping known class " + classURI);
				// TODO add all XSD classes to default JModels and remove this
				// check
			} else if(classURI.toString().startsWith(Semantics.NS_XSD)) {
				log.debug("skipping XML Schema class " + classURI);
				// TODO: what is the purpose of this?
			} else if(oc.getResource() instanceof BlankNode) {
				log.debug("skipping blank class " + classURI);
			} else {
				log.debug("owl:Class : " + classURI);
				owlClasses.add(oc);
				// TODO better classname guessing
				String classname = JavaNamingUtils.toBeanName(oc, usedClassnames);
				assert classname != null;
				usedClassnames.add(classname);
				log.debug("generating class " + classname + " for " + classURI + " ...");
				JClass jc = new JClass(jp, classname, (URI)oc.getResource());
				jc.setComment(oc.getAllComment_asList().get(0));
				jm.addMapping(oc.getResource(), jc);
			}
		}
		log.debug("dealing with " + owlClasses.size() + " 'real' classes");
		
		log.debug(">>>> Inheritance");
		// get all classes and link superclasses
		for(org.ontoware.rdfreactor.schema.bootstrap.OwlClass oc : owlClasses) {
			log.debug("owl:Class " + oc.getResource());
			JClass jc = jm.getMapping(oc.getResource());
			for(org.ontoware.rdfreactor.schema.bootstrap.OwlClass superclass : TypeUtils
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
		
		for(Property rp : Property.getAllInstance_as(m).asList()) {
			log.debug("> Processing property " + rp.getResource());
			// name it
			String propertyName = JavaNamingUtils.toBeanName(rp, usedPropertynames);
			usedPropertynames.add(propertyName);
			assert propertyName != null;
			
			List<Class> domains = rp.getAllDomain_asList();
			// no domain = no generated property
			if(domains == null || domains.size() == 0) {
				log.warn("Property " + rp.getResource() + " has no domain, so we ignore it");
			} else {
				for(Class domain : domains) {
					if(!owlClasses.contains(domain)) {
						// log.debug("ignored");
					} else {
						JClass domainClass = jm.getMapping(domain.getResource());
						assert domainClass != null : "found no JClass for "
						        + rp.getAllDomain_asList().get(0).getResource();
						
						JProperty jprop = new JProperty(domainClass, propertyName, (URI)rp
						        .getResource());
						// wire
						log.debug("Adding property '" + jprop.getName() + "' to '"
						        + domainClass.getName() + "'");
						jprop.getJClass().getProperties().add(jprop);
						jprop.setComment(rp.getAllComment_asList().get(0));
						
						for(Class range : rp.getAllRange_asList()) {
							if(owlClasses.contains(range
							        .castTo(org.ontoware.rdfreactor.schema.owl.OwlClass.class)))
								jprop.addType(jm.getMapping(range.getResource()));
						}
						jprop.fixRanges(jm);
						
						// figure out cardinality
						
						ClosableIterator<Statement> it = m.findStatements(Variable.ANY,
						        OWL.onProperty, rp.getResource());
						while(it.hasNext()) {
							Statement stmt = it.next();
							org.ontoware.rdf2go.model.node.Resource restrictionResource = stmt
							        .getSubject();
							OWL_Protege_NRL_Restriction restriction = OWL_Protege_NRL_Restriction
							        .getInstance(m, restrictionResource);
							
							int min = restriction.getAllMinCardinality_asList().get(0);
							log.debug("Found minrestriction on " + rp + " minCard = " + min);
							if(min != -1)
								jprop.setMinCardinality(min);
							int max = restriction.getAllMaxCardinality_asList().get(0);
							log.debug("Found maxrestriction on " + rp + " maxCard = " + max);
							if(max != -1)
								jprop.setMaxCardinality(max);
						}
						it.close();
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
		m.close();
		return jm;
	}
	
	/**
	 * Handle all aspects of integrating a property into the JModel: construct
	 * the JProperty, add all its ranges to the JProperty and set min and max
	 * cardinality.
	 * 
	 * @param m - the underlying RDF2Go model
	 * @param jm - the target JModel
	 * @param domainClass - the JClass domain of the property
	 * @param property - the Property instance representing the Property in the
	 *            RDF2Go model
	 */
	private static void handleProperty(Model m, JModel jm, JClass domainClass, Property property) {
		
		// obtain a nice Java-conform name which has not yet been used
		String propertyName = JavaNamingUtils.toBeanName(property, domainClass
		        .getUsedPropertyNames());
		assert propertyName != null;
		JProperty jprop = new JProperty(domainClass, propertyName, (URI)property.getResource());
		// carry over the comment from RDF to Java, might be null
		jprop.setComment(Utils.toJavaComment(property.getAllComment_asList()));
		log.debug("PROPERTY Adding '" + jprop.getName() + "' to '" + domainClass.getName() + "'");
		jprop.getJClass().getProperties().add(jprop);
		
		// process range information
		log.debug("PROPERTY checking ranges...");
		for(Class range : property.getAllRange_asList()) {
			log.debug("range is " + range);
			jprop.addType(jm.getMapping(range.getResource()));
		}
		if(property.getAllRange_asList().size() == 0) {
			// if no range is given, set to ontology root class (rdfs:Class or
			// owl:Class)
			jprop.addType(jm.getRoot());
		}
		
		// process cardinality constraints (convert this property to an OWL
		// restriction)
		OWL_Protege_NRL_Restriction restriction = (OWL_Protege_NRL_Restriction)property
		        .castTo(OWL_Protege_NRL_Restriction.class);
		assert restriction != null;
		
		Integer card = restriction.getCardinality();
		Integer minCard = restriction.getMinCardinality();
		
		Integer maxCard = restriction.getMaxCardinality();
		int min = -1;
		int max = -1;
		
		if(minCard != null) {
			min = minCard;
			log.debug("Found minrestriction on " + property + " minCard = " + min);
		} else if(card != null) {
			log.debug("Found card.restriction on " + property + " card = " + min);
			min = card;
		}
		jprop.setMinCardinality(min);
		
		if(maxCard != null) {
			max = maxCard;
			log.debug("Found maxrestriction on " + property + " maxCard = " + max);
		} else if(card != null) {
			log.debug("Found card.restriction on " + property + " card = " + min);
			max = card;
		}
		jprop.setMaxCardinality(max);
		
		if(min != -1 || max != -1) {
			domainClass.cardinalityexception = true;
			log.debug("added card.exception in class " + domainClass.getName());
		}
		
	}
}
