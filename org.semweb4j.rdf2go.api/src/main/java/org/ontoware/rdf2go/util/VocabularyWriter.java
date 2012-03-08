/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2010
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go
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
 * $Id: VocabularyWriter.java,v 1.14 2007/02/20 09:13:33 leo_sauermann Exp $
 * 
 */
public class VocabularyWriter {
	
	String inputRdf = null;
	
	String outputDirName = null;
	
	String outputFileName = null;
	
	String ns = null;
	
	String packageName = null;
	
	Model model = null;
	
	// output stream
	PrintStream outP;
	
	// transform variables
	File inputRdfFile;
	
	File outputDirFile;
	
	File outputFile;
	
	boolean namespacestrict = false;
	
	// avoid duplicates
	HashMap<String,String> uriToLocalName = new HashMap<String,String>();
	
	public VocabularyWriter() {
		super();
	}
	
	public void go(String[] args) throws Exception {
		getOpt(args);
		loadOnt();
		writeVocab();
	}
	
	private void loadOnt() throws Exception {
		// read
		Syntax syntax = RDFTool.guessSyntax(this.inputRdfFile.toString());
		this.model = RDF2Go.getModelFactory().createModel(Reasoning.none);
		this.model.open();
		System.out.println("reading from " + this.inputRdfFile.getAbsolutePath() + " in format "
		        + syntax);
		Reader reader = new BufferedReader(new FileReader(this.inputRdfFile));
		this.model.readFrom(reader, syntax);
		reader.close();
	}
	
	private void writeVocab() throws Exception {
		
		// prepare output
		this.outP = new PrintStream(this.outputFile);
		try {
			// preamble
			this.outP.println("package " + this.packageName + ";\n");
			this.outP.println("import org.ontoware.rdf2go.model.node.URI;");
			this.outP.println("import org.ontoware.rdf2go.model.node.impl.URIImpl;\n");
			this.outP.println("/**");
			this.outP.println(" * Vocabulary File. Created by " + VocabularyWriter.class.getName()
			        + " on " + new Date());
			this.outP.println(" * input file: " + this.inputRdf);
			this.outP.println(" * namespace: " + this.ns);
			this.outP.println(" */");
			this.outP.println("public interface " + this.outputFileName + " {");
			this.outP.println("	public static final URI NS_" + this.outputFileName
			        + " = new URIImpl(\"" + this.ns + "\",false);\n");
			
			// iterate through classes
			generateElement(RDFS.Class, false);
			generateElement(OWL.Class, false);
			
			// iterate through properties
			generateElement(RDF.Property, true);
			generateElement(OWL.DatatypeProperty, true);
			generateElement(OWL.ObjectProperty, true);
			
			// end
			this.outP.println("}");
		} finally {
			this.outP.close();
		}
		System.out.println("successfully wrote file to " + this.outputFile);
	}
	
	public void generateElement(URI type, boolean isProperty) throws Exception {
		ClosableIterator<? extends Statement> queryC = this.model.findStatements(Variable.ANY,
		        RDF.type, type);
		try {
			while(queryC.hasNext()) {
				Statement answer = queryC.next();
				Resource rx = answer.getSubject();
				// we do not create constants for blank nodes
				if(!(rx instanceof URI))
					continue;
				URI vx = (URI)rx;
				String uri = vx.toString();
				// check URI once and for all
				boolean valid = this.model.isValidURI(uri);
				if(!valid) {
					this.outP.println("    /* cannot export " + uri + ", not a valid URI */");
				} else {
					String localName = getLocalName(vx);
					String javalocalName = asLegalJavaID(localName, !isProperty);
					if(this.uriToLocalName.containsKey(uri))
						continue;
					this.uriToLocalName.put(uri, javalocalName);
					// check namespace strict?
					if(this.namespacestrict && !uri.startsWith(this.ns))
						continue;
					this.outP.println("    /**");
					printCommentAndLabel(vx);
					this.outP.println("     */");
					this.outP.println("    public static final URI " + javalocalName
					        + " = new URIImpl(\"" + uri + "\", false);\n");
				}
			}
		} finally {
			queryC.close();
		}
	}
	
	/**
	 * The RDF2Go interface doesn't support getting a local name from the URI. I
	 * 'borrowed' this snippet from the Sesame LiteralImpl.
	 */
	private static String getLocalName(URI vx) {
		String fullUri = vx.toString();
		int splitIdx = fullUri.indexOf('#');
		
		if(splitIdx < 0) {
			splitIdx = fullUri.lastIndexOf('/');
		}
		
		if(splitIdx < 0) {
			splitIdx = fullUri.lastIndexOf(':');
		}
		
		if(splitIdx < 0) {
			throw new RuntimeException("Not a legal (absolute) URI: " + fullUri);
		}
		return fullUri.substring(splitIdx + 1);
	}
	
	/**
	 * print comment and label of the uri to the passed stream
	 * 
	 * @param uri of a resource
	 */
	public void printCommentAndLabel(URI uri) throws Exception {
		
		ClosableIterator<? extends Statement> queryC = this.model.findStatements(uri, RDFS.label,
		        Variable.ANY);
		try {
			StringBuffer lBuf = new StringBuffer();
			while(queryC.hasNext()) {
				Statement answer = queryC.next();
				Node vl = answer.getObject();
				lBuf.append(vl.toString().concat(" "));
			}
			String l = lBuf.toString();
			if(l.length() > 0)
				this.outP.println("     * Label: " + l);
		} finally {
			queryC.close();
		}
		
		queryC = this.model.findStatements(uri, RDFS.comment, Variable.ANY);
		try {
			String l = "";
			while(queryC.hasNext()) {
				Statement answer = queryC.next();
				Node vl = answer.getObject();
				l += vl.toString() + " ";
			}
			if(l.length() > 0)
				this.outP.println("     * Comment: " + l);
		} finally {
			queryC.close();
		}
		
		queryC = this.model.findStatements(uri, RDFS.domain, Variable.ANY);
		try {
			String l = "";
			while(queryC.hasNext()) {
				Statement answer = queryC.next();
				Node vl = answer.getObject();
				l += vl.toString() + " ";
			}
			if(l.length() > 0)
				this.outP.println("     * Comment: " + l);
		} finally {
			queryC.close();
		}
		
		queryC = this.model.findStatements(uri, RDFS.range, Variable.ANY);
		try {
			String l = "";
			while(queryC.hasNext()) {
				Statement answer = queryC.next();
				Node vl = answer.getObject();
				l += vl.toString() + " ";
			}
			if(l.length() > 0)
				this.outP.println("     * Range: " + l);
		} finally {
			queryC.close();
		}
	}
	
	public void getOpt(String[] args) throws Exception {
		int i = 0;
		if(args.length == 0) {
			help();
			throw new Exception("no arguments given");
		}
		// args
		while((i < args.length) && args[i].startsWith("-")) {
			if(args[i].equals("-i")) {
				i++;
				this.inputRdf = args[i];
			} else if(args[i].equals("-o")) {
				i++;
				this.outputDirName = args[i];
			} else if(args[i].equals("-a")) {
				i++;
				this.ns = args[i];
			} else if(args[i].equals("-n")) {
				i++;
				this.outputFileName = args[i];
			} else if(args[i].equals("--package")) {
				i++;
				this.packageName = args[i];
			} else if(args[i].equals("-namespacestrict")) {
				i++;
				String s = args[i];
				if("false".equals(s))
					this.namespacestrict = false;
				else if("true".equals(s))
					this.namespacestrict = true;
				else
					throw new Exception("namespacestrict only allows 'true' or 'false', not '" + s
					        + "'");
				
			} else
				throw new Exception("unknow argument " + args[i]);
			i++;
		}
		
		if(this.inputRdf == null)
			usage("no input file given");
		if(this.outputDirName == null)
			usage("no output dir given");
		if(this.ns == null)
			usage("no namespace given");
		if(this.outputFileName == null)
			usage("no output classname given");
		if(this.packageName == null)
			usage("no package name given");
		
		// transform variables
		this.inputRdfFile = new File(this.inputRdf);
		this.outputDirFile = new File(this.outputDirName);
		this.outputFile = new File(this.outputDirFile, this.outputFileName + ".java");
	}
	
	private static void help() {
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
			for(; !Character.isJavaIdentifierStart(s.charAt(i)); i++) {
				// skip all characters which are illegal at the start
			}
		} catch(StringIndexOutOfBoundsException e) {
			System.err.println("Could not identify legal Java identifier start character in '" + s
			        + "', replacing with __");
			return "__";
		}
		buf.append(cap ? Character.toUpperCase(s.charAt(i)) : s.charAt(i));
		
		// copy the remaining characters - replace non-legal chars with '_'
		for(++i; i < s.length(); i++) {
			char c = s.charAt(i);
			buf.append(Character.isJavaIdentifierPart(c) ? c : '_');
		}
		
		// check standard name
		String result = buf.toString();
		if(result.equals("class") || result.equals("abstract"))
			result = result + "_";
		return result;
	}
	
	private static void usage(String string) throws Exception {
		throw new Exception(string);
	}
	
}
