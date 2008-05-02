package org.ontoware.rdfreactor.generator;

import java.util.Calendar;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.generator.java.JClass;
import org.ontoware.rdfreactor.generator.java.JModel;
import org.ontoware.rdfreactor.generator.java.JPackage;
import org.ontoware.rdfreactor.generator.java.JProperty;

public class Semantics {

	public static final String NS_XSD = "http://www.w3.org/2001/XMLSchema#";

	/**
	 * Add the build-in mapping of URIs to classes.
	 * 
	 * @param jm
	 */
	public static void addBuiltInsCommon(JModel jm) {

		// TODO: shouldn't JPackage.RDFSCHEMA be added to 'jm' ?

		jm.addMapping(RDF.Alt, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".Alt", RDF.Alt));
		jm.addMapping(RDF.Bag, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".Bag", RDF.Bag));

		JClass _class = new JClass(JPackage.RDFSCHEMA, JPackage.RDFSCHEMA
				.getName()
				+ ".Class", RDFS.Class);
		jm.addMapping(RDFS.Class, _class);

		jm.addMapping(RDFS.Container, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".Container", RDFS.Container));
		jm.addMapping(RDFS.ContainerMembershipProperty, new JClass(
				JPackage.RDFSCHEMA, JPackage.RDFSCHEMA.getName()
						+ ".ContainerMembershipProperty",
				RDFS.ContainerMembershipProperty));
		jm.addMapping(RDFS.Datatype, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".Datatype", RDFS.Datatype));
		// literal mapping
		jm.addMapping(RDFS.Literal, JClass.STRING);
		jm.addMapping(RDF.List, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".List", RDF.List));
		jm.addMapping(RDF.nil, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".Nil", RDF.nil));
		// rdf property is special
		jm.addMapping(RDF.Seq, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".Seq", RDF.Seq));
		jm.addMapping(RDF.Statement, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".Statement", RDF.Statement));
		jm.addMapping(RDF.XMLLiteral, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".XMLLiteral", RDF.XMLLiteral));

		jm.addMapping(RDF.Property, new JClass(JPackage.RDFSCHEMA,
				JPackage.RDFSCHEMA.getName() + ".Property", RDF.Property));

		JClass _resource = new JClass(JPackage.RDFSCHEMA, JPackage.RDFSCHEMA
				.getName()
				+ ".Resource", RDFS.Resource);
		jm.addMapping(RDFS.Resource, _resource);
		_resource.getProperties().add(
				new JProperty(_resource, "label", RDFS.label));
		_resource.getProperties().add(
				new JProperty(_resource, "comment", RDFS.comment));
		_resource.getProperties().add(
				new JProperty(_resource, "type", RDF.type));
		_resource.getProperties().add(
				new JProperty(_resource, "member", RDFS.member));

		// owl mapping
		String owl = "org.ontoware.rdfreactor.schema.owl";

		// TODO: shouldn't JPackage.OWL be added to jm ?

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

		//////////////////// java.net
		// IDs
		jm.addMapping(XSD._anyURI, new JClass(new JPackage("java.net"),
				URI.class.getName() + "", XSD._anyURI));

		/////////////////// java.lang
		// primitive types
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

		// strings
		jm.addMapping(XSD._normalizedString, new JClass(JPackage.JAVA_LANG,
				String.class.getName() + "", XSD._normalizedString));
		jm.addMapping(XSD._string, new JClass(JPackage.JAVA_LANG, String.class
				.getName()
				+ "", XSD._string));

		/////////////////////////// java.util
		
		// date and time -> Calendar.class
		jm.addMapping(XSD._dateTime, new JClass(JPackage.JAVA_UTIL,
				Calendar.class.getName() + "", XSD._dateTime));
		jm.addMapping(XSD._date, new JClass(JPackage.JAVA_UTIL, Calendar.class
				.getName()
				+ "", XSD._date));
		jm.addMapping(XSD._time, new JClass(JPackage.JAVA_UTIL, Calendar.class
				.getName()
				+ "", XSD._time));

		// get all properties
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
		jm.knownProperties.add(RDFS.member);
		jm.knownProperties.add(RDFS.domain);
		jm.knownProperties.add(RDFS.range);
		jm.knownProperties.add(RDFS.subClassOf);
		jm.knownProperties.add(RDFS.subPropertyOf);

	}

	/**
	 * @return a JModel with the common built-ins
	 */
	public static JModel getbuiltIns_RDFS() {
		JModel jm = new JModel(JClass.RDFS_CLASS);
		addBuiltInsCommon(jm);
		return jm;
	}

	/**
	 * @return a JModel with the common built-ins
	 */
	public static JModel getbuiltIns_OWL() {
		JModel jm = new JModel(JClass.OWL_CLASS);
		addBuiltInsCommon(jm);
		return jm;
	}

}
