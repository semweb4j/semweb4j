/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.examples;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.TestCase;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;

/**
 * showcase to answer Gunnars questions
 */
public class ApiUsabilityTest2 extends TestCase {

	/**
	 * Gunnar: need a function that queries and removes triples from all
	 * contexts... i.e. remove all (null, rdf:type, null) from ALL contexts...
	 * 
	 * Max: So basically this would mean handling of Triple-patterns not just
	 * triples, in the add/remove methods. Ok.
	 * 
	 * 
	 * @throws Exception
	 * @throws ModelRuntimeException
	 */
	public void testDeleteTriplePatternInAllGraphs() throws ModelRuntimeException, Exception {
		ModelSet set = RDF2Go.getModelFactory().createModelSet();

		Iterator<? extends Model> it = set.getModels();
		while (it.hasNext()) {
			Model model = it.next();
			// This line breaks really badly on sesame2...
			// and on many other locking stores as well ..
			// model.removeAll(model.findStatements(Variable.ANY, RDF.type,
			// Variable.ANY).iterator());

			// just added this method
			model.removeStatements(Variable.ANY, RDF.type, Variable.ANY);
		}
	}

	public void testGunnar1() throws ModelRuntimeException, Exception {

		// null? Can't we also have a method that takes no parameters?
		Properties p = new Properties();
		p.setProperty("modelbuilder", "alsid");

		// Max: um, no. Then we have no way to figure out which impl we should
		// use.

		// this throws ModelException AND Exception, which is kinda redundant.
		// I woudl prefer it caught any exception it got and enveloped it it in
		// modelException.
		// "throws Exception" is kinda ugly.
		Model m = RDF2Go.getModelFactory().createModel(p);

		// Max: Agree, fixed.

		// This is problmatic, Jena has shown many times that reading RDF over
		// readers
		// has a high risk of creating encoding problems. I would use an
		// InputStream as well.
		// Max: That's not my problem. The adapter can be written so that it
		// uses InputStreams internally.

		// And we should maybe expose a utility method that takes a URL as a
		// string?
		// Max: no, this would force all implementators to have http inside
		// their impl.
		// Instead, a util class should provide this

		// Also, what about the format of the file? Is this autodetected?
		// default to rdf/xml?
		// I think we should at least support rdf/xml, n3, ntriples and trix
		m.readFrom(new FileReader("/home/grimnes/tmp/myrdffile.rdf"));

		// Max: If you say so...
		// each feature puts the burden higher to implement such an API!
		// many functionalites can be delegated to external Utils
		// do we really need these things in the main api?
		// ok, added them.

		m.readFrom(new FileReader("/home/grimnes/tmp/myrdffile.rdf"), Syntax.Ntriples);

		// Don't print to keep the test logs clean
//		for (Statement s : m) {
//			 System.out.println(s);
//		}

		// this is fatal - we cannot make any assumptions about the protocol of
		// the URL,
		// this validates using java.net.URL, which for instance doesn't allow
		// urn:blah, isbn:blah, gnowsis:blah, etc.
		// Regardless of what the rdf/uri specs say there is lots and lots of
		// data out there
		// that violates this - and we can't have our rdf api break on it.
		// Another issue is performance - the sesame guys said validating URIs
		// made everything much slower.

		// URI person = m.createURI("urn:gunnar"); <- breaks with
		// MalformedURLException: unknown protocol: urn
		URI person = m.createURI("http://example.com/gunnar");

		// MAX: ok, changed into
		// if (log.isDebugEnabled()) { .. check here }

		// We should write a schema-generator for rdf2go - unless this already
		// exists?
		URI Person = m.createURI("http://xmlns.com/foaf/0.1/Person");
		URI knows = m.createURI("http://xmlns.com/foaf/0.1/knows");
		URI name = m.createURI("http://xmlns.com/foaf/0.1/name");
		URI age = m.createURI("http://xmlns.com/foaf/0.1/age");

		// Max: use RDFReactor and then say "Person.KNOWS" to use the property
		// with domain Person and name "knows"

		// but nice RDF vocab.
		m.addStatement(person, RDF.type, Person);

		// "auto-boxing" of literals is also nice :)
		m.addStatement(person, name, "Gunnar");

		// This isn't though :)
		// m.addStatement(person,age,12);
		// m.addStatement(person,age,23.2);
		// m.addStatement(person,age,true);
		// etc.

		// Max: what do you want to have here? Support for all primitive java
		// types? Could be added.

		// Two points - why can't addStatement simply be add? This is 2006, we
		// have method overloading...

		// Max: you might also add rules, namespace prefix definitions, runtime
		// properties,
		// So, no, it should remain "addStatement".

		// and can we also have an XMLSchema vocab file?
		m.addStatement(person, age, m.createDatatypeLiteral("8", m
				.createURI("http://www.w3.org/2001/XMLSchema#integer")));

		// Max: it's called "XSD" for "XML Schmema Datatypes"
		m.addStatement(person, age, m.createDatatypeLiteral("8", XSD._integer));

		URI person2 = m.createURI("http://example.com/max");

		// And why does Diff not have an empty constructor?
		Diff d = new DiffImpl(null, null);
		d.addStatement(person2, RDF.type, Person);
		d.addStatement(person2, knows, person);
		m.update(d);

		// Max: it has

		// Again, where is the rdf format?
		// i would also like a "String m.serialize()" method -
		// that doesn't require me to use a StringWriter...
		m.writeTo(new FileWriter("/tmp/myout.rdf"), Syntax.Trix);

		// Max: agree, fixed.

	}

	public static void main(String[] args) throws Exception {

		new ApiUsabilityTest2().testGunnar1();
	}

}
