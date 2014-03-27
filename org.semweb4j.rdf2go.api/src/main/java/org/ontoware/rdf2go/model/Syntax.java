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

package org.ontoware.rdf2go.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Class for RDF syntaxes, and registry for them. A framework can register new
 * syntaxes at runtime by creating them with the constructor that automatically
 * registers, or by calling the {@link #register(Syntax)} method.
 * 
 * You can chose to use a Syntax in your application without registering it.
 * 
 * @author Max Völkel
 * @author Leo Sauermann
 * @author Roland Stühmer
 */
public class Syntax {
	
	/**
	 * List of known RDF file formats. Note: This has to be defined at the top
	 * of the class, otherwise the constructor of the final Syntaxes cannot use
	 * it.
	 */
	private static Set<Syntax> SYNTAXES = new HashSet<Syntax>();
	
	public static void resetFactoryDefaults() {
		SYNTAXES.clear();
		SYNTAXES.add(Ntriples);
		SYNTAXES.add(Nquads);
		SYNTAXES.add(RdfXml);
		SYNTAXES.add(Trig);
		SYNTAXES.add(Trix);
		SYNTAXES.add(Turtle);
		SYNTAXES.add(RdfJson);
		SYNTAXES.add(JsonLd);
	}
	
	/**
	 * RDF syntax RDF/XML.
	 * 
	 * @see <a href="http://www.w3.org/TR/rdf-syntax-grammar/">RDF/XML
	 *      syntax</a>
	 */
	public static final Syntax RdfXml = new Syntax(
			"rdfxml",
			Arrays.asList(new String[] {"application/rdf+xml", "application/xml"}),
			Arrays.asList(new String[] { ".rdf", ".rdfs", ".owl", ".xml" }),
			true);
	
	/**
	 * RDF syntax Turtle.
	 * 
	 * @see <a href="http://www.w3.org/TR/turtle/">Turtle syntax</a>
	 */
	public static final Syntax Turtle = new Syntax(
			"turtle",
			Arrays.asList(new String[] {"text/turtle", "application/x-turtle"}),
			Arrays.asList(new String[] {".ttl"}),
			true);
	
	/**
	 * RDF syntax N-Triples.
	 * 
	 * @see <a href="http://www.w3.org/TR/rdf-testcases/#ntriples">N-Triples
	 *      syntax</a>
	 */
	public static final Syntax Ntriples = new Syntax(
			"ntriples",
			"text/plain",
			".nt",
			true);
	
	/**
	 * RDF syntax N-Quads.
	 * 
	 * @see <a href="http://www.w3.org/TR/n-quads/">N-Quads syntax</a>
	 */
	public static final Syntax Nquads = new Syntax(
			"nquads",
			Arrays.asList(new String[] {"application/n-quads", "text/x-nquads"}),
			Arrays.asList(new String[] {".nq"}),
			true);
	
	/**
	 * RDF syntax TriX.
	 * 
	 * @see <a
	 *      href="http://www.hpl.hp.com/techreports/2004/HPL-2004-56.html">TriX
	 *      syntax</a>
	 */
	public static final Syntax Trix = new Syntax(
			"trix",
			"application/trix",
			".trix",
			true);
	
	/**
	 * RDF syntax TriG.
	 * 
	 * @see <a href="http://www.w3.org/TR/trig/">TriG
	 *      syntax</a>
	 */
	public static final Syntax Trig = new Syntax(
			"trig",
			Arrays.asList(new String[] {"application/trig", "application/x-trig"}),
			Arrays.asList(new String[] {".trig"}),
			true);

	/**
	 * RDF syntax RDF/JSON.
	 * 
	 * @see <a href="http://www.w3.org/TR/rdf-json/">RDF/JSON syntax</a>
	 */
	public static final Syntax RdfJson = new Syntax(
			"rdfjson",
			"application/rdf+json",
			".rj",
			true);

	/**
	 * RDF syntax JSON-LD.
	 * 
	 * @see <a href="http://www.w3.org/TR/json-ld/">RDF/JSON syntax</a>
	 */
	public static final Syntax JsonLd = new Syntax(
			"jsonld",
			"application/ld+json",
			".jsonld",
			true);

	/**
	 * register a new RDF Syntax you want to have available throughout your
	 * application.
	 * 
	 * @param syntax the new syntax to register.
	 */
	public static void register(Syntax syntax) {
		SYNTAXES.add(syntax);
	}
	
	/**
	 * return the RDF syntax with the given name.
	 * 
	 * @param name the name of the syntax to search
	 * @return the syntax or <code>null</code>, if none registered
	 */
	public static Syntax forName(String name) {
		for(Syntax x : SYNTAXES) {
			if(x.getName().equals(name))
				return x;
		}
		return null;
	}
	
	/**
	 * return the RDF syntax with the given MIME-type.
	 * 
	 * @param mimeTypes the MIME-type of the syntax to find
	 * @return the syntax or <code>null</code>, if none registered
	 */
	public static Syntax forMimeType(String mimeType) {
		for(Syntax x : SYNTAXES) {
			if(x.getMimeTypes().contains(mimeType))
				return x;
		}
		return null;
	}
	
	/**
	 * unregister an RDF Syntax from which you know that your application will
	 * never ever support it. This may help you to build user interfaces where
	 * users can select RDF syntaxes. If the syntax was unknown, returns false
	 * 
	 * @param syntax the syntax to unregister
	 * @return true, if the syntax was found and removed
	 */
	public static boolean unregister(Syntax syntax) {
		return SYNTAXES.remove(syntax);
	}
	
	/**
	 * list all available syntaxes. Collection is not modifyable.
	 * 
	 * @return a Collection of available syntaxes
	 */
	public static Collection<Syntax> collection() {
		return Collections.unmodifiableCollection(SYNTAXES);
	}
	
	/**
	 * list all available syntaxes. List is not modifyable.
	 * 
	 * @return a Collection of available syntaxes
	 * @deprecated Use #collection() instead
	 */
	@Deprecated
	public static List<Syntax> list() {
		return new ArrayList<Syntax>(collection());
	}
	
	private final String name;
	
	private final List<String> mimeTypes;
	
	private final List<String> filenameExtensions;
	
	/**
	 * return the default MIME-type of this format.
	 * 
	 * @return the MIME type
	 */
	public String getMimeType() {
		return this.mimeTypes.get(0);
	}
	
	/**
	 * return all MIME-typse of this format.
	 * 
	 * @return the MIME types
	 */
	public List<String> getMimeTypes() {
		return this.mimeTypes;
	}
	
	/**
	 * @return the common name of this format
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the suggested filename-extension, including the leading '.'
	 */
	public String getFilenameExtension() {
		return this.filenameExtensions.get(0);
	}
	
	/**
	 * Generate a new Syntax
	 * 
	 * @param name
	 *            the name of the RDF syntax
	 * @param mimeTypes
	 *            the MIMEtype of the RDF syntax
	 * @param filenameExtensions
	 *            including the dot
	 */
	public Syntax(final String name, final String mimeType, final String filenameExtension) {
		this(name, Arrays.asList(new String[] { mimeType }), Arrays
				.asList(new String[] { filenameExtension }), false);
	}
	
	/**
	 * Generate a new Syntax and register it
	 * 
	 * @param name
	 *            the name of the RDF syntax
	 * @param mimeTypes
	 *            the MIMEtypes of the RDF syntax
	 * @param filenameExtensions
	 *            including the dot
	 * @param registerItNow
	 *            register the new Syntax now.
	 */
	public Syntax(final String name, final String mimeType, final String filenameExtension,
	        boolean registerItNow) {
		this(name, Arrays.asList(new String[] { mimeType }), Arrays
				.asList(new String[] { filenameExtension }), registerItNow);
	}
	
	/**
	 * Generate a new Syntax
	 * 
	 * @param name
	 *            the name of the RDF syntax
	 * @param mimeTypes
	 *            the MIMEtypes of the RDF syntax, the first type in the ordered
	 *            list will be used as the default
	 * @param filenameExtensions
	 *            including the dot, the first type in the ordered list will be
	 *            used as the default
	 */
	public Syntax(final String name, final List<String> mimeTypes, final List<String> filenameExtensions) {
		this(name, mimeTypes, filenameExtensions, false);
	}
	
	/**
	 * Generate a new Syntax and register it
	 * 
	 * @param name
	 *            the name of the RDF syntax
	 * @param mimeTypes
	 *            the MIMEtypes of the RDF syntax, the first type in the ordered
	 *            list will be used as the default
	 * @param filenameExtensions
	 *            including the dot, the first type in the ordered list will be
	 *            used as the default
	 * @param registerItNow
	 *            register the new Syntax now.
	 */
	public Syntax(final String name, final List<String> mimeTypes, final List<String> filenameExtensions, final boolean registerItNow) {
		
		if (mimeTypes.size() < 1) {
			throw new IllegalArgumentException("At least one mimeTypes must be specified.");
		}
		if (filenameExtensions.size() < 1) {
			throw new IllegalArgumentException("At least one filenameExtensions must be specified.");
		}
		
		this.name = name;
		this.mimeTypes = mimeTypes;
		this.filenameExtensions = filenameExtensions;
		
		if(registerItNow) {
			register(this);
		}
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Syntax) {
			return this.mimeTypes.equals(((Syntax)obj).mimeTypes);
		}
		return false;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.mimeTypes.hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RDF Syntax '" + this.name + "' MIME-type=" + this.getMimeType();
	}
	
}
