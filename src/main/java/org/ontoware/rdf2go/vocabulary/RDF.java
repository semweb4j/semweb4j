/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 * Created on Nov 29, 2004
 *
 */
package org.ontoware.rdf2go.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * The RDF vocabulary as URIs
 * @author mvo
 * 
 */
public class RDF {

	/**
	 * The RDF Namespace
	 */
	public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	protected static final URI toURI(String local) {
		return new URIImpl(RDF_NS + local,false);
	}

	public static URI li(int i) {
		return toURI(RDF_NS + "_" + i);
	}

	public static final URI Alt = toURI("Alt");

	/** http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag */
	public static final URI Bag = toURI("Bag");

	public static final URI Property = toURI("Property");

	public static final URI Seq = toURI("Seq");

	public static final URI Statement = toURI("Statement");

	public static final URI List = toURI("List");

	public static final URI nil = toURI("nil");

	public static final URI first = toURI("first");

	public static final URI rest = toURI("rest");

	public static final URI subject = toURI("subject");

	public static final URI predicate = toURI("predicate");

	public static final URI object = toURI("object");

	public static final URI type = toURI("type");

	public static final URI value = toURI("value");

	// literals
	public static final URI XMLLiteral = toURI("XMLLiteral");

}
