/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go.model.impl;

import java.rmi.server.UID;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses UUIDs.
 * 
 * @author voelkel
 * 
 */
public class URIGenerator {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(URIGenerator.class);

	public static URI createNewRandomUniqueURI() {
		return new URIImpl("urn:rnd:" + new UID().toString());
	}

	
	/**
	 * @param uriPrefix - must include schema information
	 * @return a new, random unique URI starting with uriPrefix
	 */
	public static URI createNewRandomUniqueURI(String uriPrefix) {
		return new URIImpl(uriPrefix + new UID().toString());
	}
}
