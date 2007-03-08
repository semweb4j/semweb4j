/*
 * LICENSE INFORMATION
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

/**
 * reads an RDF/S file and creates an RDF2Go Vocabulary file from it.
 * 
 * usage:
 * 
 * <pre>
 *  VocabularyWriter -i &lt;inputrdfsfile&gt; -o &lt;outputdir&gt; 
 *  --package &lt;packagename&gt; -a &lt;namespace&gt; -n &lt;nameofjavafile&gt;
 *  -namespacestrict &lt;false|true&gt;
 * 
 *  -namespacestrict If true, only elements from within the namespace (-a)
 *  are generated. Default false.
 * 
 *  Example values:
 *  --package  org.semanticdesktop.aperture.outlook.vocabulary 
 *  -o src/outlook/org/semanticdesktop/aperture/outlook/vocabulary/
 *  -i doc/ontology/data.rdfs
 *  -a http://aperture.semanticdesktop.org/ontology/data#
 *  -n DATA
 * 
 * 
 *  @author sauermann
 *  $Id: VocabularyWriter.java,v 1.14 2007/02/20 09:13:33 leo_sauermann Exp $
 * 
 */
public class VocabularyWriter {

	String inputRdf = null;

	String outputDir = null;

	String outputFileN = null;

	String ns = null;

	String packagen = null;

	Model myModel = null;

	// output stream
	PrintStream outP;

	// transform variables
	File inputRdfF;

	File outputDirF;

	File outputF;

	Boolean namespacestrict = false;

	// avoid duplicates
	HashMap<String, String> uriToLocalName = new HashMap<String, String>();

	public VocabularyWriter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void go(String[] args) throws Exception {
		getOpt(args);
		loadOnt();
		writeVocab();
	}

	private void loadOnt() throws Exception {
		// read
		
		myModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		System.out.println("reading from " + inputRdfF.getAbsolutePath()
				+ " in format " + Syntax.RdfXml);
		Reader reader = new BufferedReader(new FileReader(inputRdfF));
		myModel.readFrom(reader, Syntax.RdfXml);
		reader.close();
	}

	private void writeVocab() throws Exception {

		// prepare output
		outP = new PrintStream(outputF);
		try {
			// preamble
			outP.println("package " + packagen + ";\n");
			outP.println("import org.ontoware.rdf2go.model.node.URI;");
			outP
					.println("import org.ontoware.rdf2go.model.node.impl.URIImpl;\n");
			outP.println("/**");
			outP.println(" * Vocabulary File. Created by "
					+ VocabularyWriter.class.getName() + " on " + new Date());
			outP.println(" * input file: " + inputRdf);
			outP.println(" * namespace: " + ns);
			outP.println(" */");
			outP.println("public interface " + outputFileN + " {");
			outP.println("	public static final URI NS_" + outputFileN
					+ " = URIImpl.createURIWithoutChecking(\"" + ns + "\");\n");

			// iterate through classes
			generateElement(RDFS.Class, false);
			generateElement(OWL.Class, false);

			// iterate through properties
			generateElement(RDF.Property, true);
			generateElement(OWL.DatatypeProperty, true);
			generateElement(OWL.ObjectProperty, true);

			// end
			outP.println("}");
		} finally {
			outP.close();
		}
		System.out.println("successfully wrote file to " + outputF);
	}

	public void generateElement(URI type, boolean isProperty) throws Exception {
		ClosableIterator<? extends Statement> queryC = myModel
		.findStatements(Variable.ANY, RDF.type, type);
		try {
			while (queryC.hasNext()) {
				Statement answer = queryC.next();
				Resource rx = answer.getSubject();
				// we do not create constants for blank nodes
				if (!(rx instanceof URI))
					continue;
				URI vx = (URI) rx;
				String uri = vx.toString();
				String localName = getLocalName(vx);
				String javalocalName = asLegalJavaID(localName, !isProperty);
				if (uriToLocalName.containsKey(uri))
					continue;
				uriToLocalName.put(uri, javalocalName);
				// check namespace strict?
				if (namespacestrict && !uri.startsWith(ns))
					continue;
				outP.println("    /**");
				printCommentAndLabel(vx);
				outP.println("     */");
				outP.println("    public static final URI " + javalocalName
						+ " = URIImpl.createURIWithoutChecking(\"" + uri
						+ "\");\n");
			}
		} finally {
			queryC.close();
		}
	}

	/**
	 * The RDF2Go interface doesn't support getting a local name from the URI. I
	 * 'borrowed' this snippet from the Sesame LiteralImpl.
	 */
	private String getLocalName(URI vx) {
		String fullUri = vx.toString();
		int splitIdx = fullUri.indexOf('#');

		if (splitIdx < 0) {
			splitIdx = fullUri.lastIndexOf('/');
		}

		if (splitIdx < 0) {
			splitIdx = fullUri.lastIndexOf(':');
		}

		if (splitIdx < 0) {
			throw new RuntimeException("Not a legal (absolute) URI: " + fullUri);
		}
		return fullUri.substring(splitIdx + 1);
	}

	/**
	 * print comment and label of the uri to the passed stream
	 * 
	 * @param uri
	 */
	public void printCommentAndLabel(URI uri) throws Exception {

		ClosableIterator<? extends Statement> queryC = myModel
		.findStatements(uri, RDFS.label, Variable.ANY);
		try {
			String l = "";
			while (queryC.hasNext()) {
				Statement answer = queryC.next();
				;
				Node vl = answer.getObject();
				l += vl.toString() + " ";
			}
			if (l.length() > 0)
				outP.println("     * Label: " + l);
		} finally {
			queryC.close();
		}

		queryC = myModel.findStatements(uri, RDFS.comment, Variable.ANY);
		try {
			String l = "";
			while (queryC.hasNext()) {
				Statement answer = queryC.next();
				;
				Node vl = answer.getObject();
				l += vl.toString() + " ";
			}
			if (l.length() > 0)
				outP.println("     * Comment: " + l);
		} finally {
			queryC.close();
		}

		queryC = myModel.findStatements(uri, RDFS.domain, Variable.ANY);
		try {
			String l = "";
			while (queryC.hasNext()) {
				Statement answer = queryC.next();
				;
				Node vl = answer.getObject();
				l += vl.toString() + " ";
			}
			if (l.length() > 0)
				outP.println("     * Comment: " + l);
		} finally {
			queryC.close();
		}

		queryC = myModel.findStatements(uri, RDFS.range, Variable.ANY);
		try {
			String l = "";
			while (queryC.hasNext()) {
				Statement answer = queryC.next();
				;
				Node vl = answer.getObject();
				l += vl.toString() + " ";
			}
			if (l.length() > 0)
				outP.println("     * Range: " + l);
		} finally {
			queryC.close();
		}
	}

	public void getOpt(String[] args) throws Exception {
		int i = 0;
		if (args.length == 0) {
			help();
			throw new Exception("no arguments given");
		}
		// args
		while ((i < args.length) && args[i].startsWith("-")) {
			if (args[i].equals("-i")) {
				i++;
				inputRdf = args[i];
			} else if (args[i].equals("-o")) {
				i++;
				outputDir = args[i];
			} else if (args[i].equals("-a")) {
				i++;
				ns = args[i];
			} else if (args[i].equals("-n")) {
				i++;
				outputFileN = args[i];
			} else if (args[i].equals("--package")) {
				i++;
				packagen = args[i];
			} else if (args[i].equals("-namespacestrict")) {
				i++;
				String s = args[i];
				if ("false".equals(s))
					namespacestrict = false;
				else if ("true".equals(s))
					namespacestrict = true;
				else
					throw new Exception(
							"namespacestrict only allows 'true' or 'false', not '"
									+ s + "'");

			} else
				throw new Exception("unknow argument " + args[i]);
			i++;
		}

		if (inputRdf == null)
			usage("no input file given");
		if (outputDir == null)
			usage("no output dir given");
		if (ns == null)
			usage("no namespace given");
		if (outputFileN == null)
			usage("no output classname given");
		if (packagen == null)
			usage("no package name given");

		// transform variables
		inputRdfF = new File(inputRdf);
		outputDirF = new File(outputDir);
		outputF = new File(outputDir, outputFileN + ".java");
	}

	private void help() {
		System.err
				.println("Syntax: java VocabularyWriter -i inputfile -o outputdir -a namespace -n classname --package package ");
	}

	/**
	 * documentation see class, above.
	 */
	public static void main(String[] args) throws Exception {
		new VocabularyWriter().go(args);

	}

	/**
	 * Convert s to a legal Java identifier; capitalise first char if cap is
	 * true this method is copied from jena code.
	 */
	protected String asLegalJavaID(String s, boolean cap) {
		StringBuilder buf = new StringBuilder();
		int i = 0;

		// treat the first character specially - must be able to start a Java
		// ID, may have to upcase
		try {
			for (; !Character.isJavaIdentifierStart(s.charAt(i)); i++) {
			}
		} catch (StringIndexOutOfBoundsException e) {
			System.err
					.println("Could not identify legal Java identifier start character in '"
							+ s + "', replacing with __");
			return "__";
		}
		buf.append(cap ? Character.toUpperCase(s.charAt(i)) : s.charAt(i));

		// copy the remaining characters - replace non-legal chars with '_'
		for (++i; i < s.length(); i++) {
			char c = s.charAt(i);
			buf.append(Character.isJavaIdentifierPart(c) ? c : '_');
		}

		// check standard name
		String result = buf.toString();
		if (result.equals("class") || result.equals("abstract"))
			result = result + "_";
		return result;
	}

	private static void usage(String string) throws Exception {
		throw new Exception(string);
	}

}