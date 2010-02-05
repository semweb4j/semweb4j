/**
 * LICENSE INFORMATION
 *
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2010
 *
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for RDF syntaxes, and registry for them. A framework can register new
 * syntaxes by creating them with the constructor that automatically registers,
 * or by calling the {@link #register(Syntax)} method.
 * 
 * You can chose to use a Syntax in your application without registering it.
 * 
 * @author voelkel, sauermann
 */
public class Syntax {

	/**
	 * List of known RDF file formats. Note: This has to be defined at the top
	 * of the class, otherwise the constructor of the final Syntaxes cannot use
	 * it.
	 */
	private static List<Syntax> SYNTAXES = new ArrayList<Syntax>();

	/**
	 * RDF syntax RDF XML
	 */
	public static final Syntax RdfXml = new Syntax("rdfxml",
			"application/rdf+xml", ".rdf", true);

	/**
	 * RDF syntax Turtle
	 */
	public static final Syntax Turtle = new Syntax("turtle",
			"application/x-turtle", ".ttl", true);

	/**
	 * RDF syntax NTriples
	 */
	public static final Syntax Ntriples = new Syntax("ntriples", "text/plain",
			".nt", true);

	/**
	 * RDF syntax Trix For mroe info see: http://swdev.nokia.com/trix/TriX.html
	 */
	public static final Syntax Trix = new Syntax("trix", "application/trix",
			".trix", true);

	/**
	 * RDF Syntax Trig For more info see
	 * http://sites.wiwiss.fu-berlin.de/suhl/bizer/TriG/
	 */
	public static final Syntax Trig = new Syntax("trig", "application/x-trig",
			".trig", true);

	/**
	 * register a new RDF Syntax you want to have available throughout your
	 * application.
	 * 
	 * @param syntax
	 *            the new syntax to register.
	 */
	public static void register(Syntax syntax) {
		SYNTAXES.add(syntax);
	}

	/**
	 * return the RDF syntax with the given name.
	 * 
	 * @param name
	 *            the name of the syntax to search
	 * @return the syntax or <code>null</code>, if none registered
	 */
	public static Syntax forName(String name) {
		for (Syntax x : SYNTAXES) {
			if (x.getName().equals(name))
				return x;
		}
		return null;
	}

	/**
	 * return the RDF syntax with the given MIME-type.
	 * 
	 * @param mimeType
	 *            the MIME-type of the syntax to find
	 * @return the syntax or <code>null</code>, if none registered
	 */
	public static Syntax forMimeType(String mimeType) {
		for (Syntax x : SYNTAXES) {
			if (x.getMimeType().equals(mimeType))
				return x;
		}
		return null;
	}

	/**
	 * unregister an RDF Syntax from which you know that your application will
	 * never ever support it. This may help you to build user interfaces where
	 * users can select RDF syntaxes. If the syntax was unknown, returns false
	 * 
	 * @param syntax
	 *            the syntax to unregister
	 * @return true, if the syntax was found and removed
	 */
	public static boolean unregister(Syntax syntax) {
		return SYNTAXES.remove(syntax);
	}

	/**
	 * list all available syntaxes. List is not modifyable.
	 * 
	 * @return a list of available syntaxes
	 */
	public static List<Syntax> list() {
		return Collections.unmodifiableList(SYNTAXES);
	}

	private final String name;

	private final String mimeType;

	private final String filenameExtension;

	/**
	 * return the MIME-type of this format.
	 * 
	 * @return the MIME type
	 */
	public String getMimeType() {
		return this.mimeType;
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
		return this.filenameExtension;
	}

	/**
	 * Generate a new Syntax
	 * 
	 * @param name
	 *            the name of the RDF syntax
	 * @param mimeType
	 *            the MIMEtype of the RDF syntax
	 */
	public Syntax(final String name, final String mimeType,
			final String filenameExtension) {
		super();
		this.name = name;
		this.mimeType = mimeType;
		this.filenameExtension = filenameExtension;
	}

	/**
	 * Generate a new Syntax and register it
	 * 
	 * @param name
	 *            the name of the RDF syntax
	 * @param mimeType
	 *            the MIMEtype of the RDF syntax
	 * @param registerItNow
	 *            register the new Syntax now.
	 */
	public Syntax(final String name, final String mimeType,
			final String filenameExtension, boolean registerItNow) {
		this(name, mimeType, filenameExtension);
		if (registerItNow)
			register(this);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Syntax) {
			return this.mimeType.equals(((Syntax) obj).mimeType);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.mimeType.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RDF Syntax '" + this.name + "' MIME-type=" + this.mimeType;
	}

}
