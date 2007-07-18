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
package org.ontoware.rdf2go;

/**
 * Helper for SPARQL encode/decode of strings
 * @author voelkel
 *
 */
public class SparqlUtil {
	
	/** @return "\"" + sparqlEncode(raw) +"\"" */
	public static String toSparqlLiteral( String raw ) {
		return "\"" + sparqlEncode(raw) + "\"";
	}

	/**
	 * @param raw
	 * @return prefix all ' and " with a \
	 */
	public static String sparqlEncode( String raw )  {
		String result = raw;
		
		result = result.replaceAll("\\\\","\\\\\\\\");
		result = result.replaceAll("'","\\\\'");
		result = result.replaceAll("\"","\\\"");
		
		return result;
	}
	
}
