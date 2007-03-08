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
package org.ontoware.rdf2go.model.impl;

import java.rmi.server.UID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Uses UUIDs. If debug is enabled, very short numbered URIs are generated.
 * 
 * @author voelkel
 * 
 */
public class URIGenerator {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(URIGenerator.class);

	public static URI createNewRandomUniqueURI() {
		// TODO: add server name to generated uri
		return new URIImpl("urn:rnd:" + new UID().toString());
	}

}
