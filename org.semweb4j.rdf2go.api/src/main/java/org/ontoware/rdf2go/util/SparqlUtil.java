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

import org.ontoware.rdf2go.model.node.Node;


/**
 * Helper for SPARQL encode/decode of strings
 * 
 * @author voelkel
 * 
 */
public class SparqlUtil {
	
	/** @return "\"" + sparqlEncode(raw) +"\"" */
	public static String toSparqlLiteral(String raw) {
		return "\"" + sparqlEncode(raw) + "\"";
	}
	
	/**
	 * @param raw the raw string to be encoded
	 * @return prefix all ' and " with a \
	 */
	public static String sparqlEncode(String raw) {
		String result = raw;
		
		result = result.replace("\\", "\\\\");
		result = result.replace("'", "\\'");
		result = result.replace("\"", "\\\"");
		
		return result;
	}
	
	/**
	 * @param raw string to be encoded
	 * @return the string ready to be INCLUDED in a SPARQL REGEX, which is
	 *         defined in http://www.w3.org/TR/xpath-functions/#regex-syntax
	 */
	public static String sparqlRegExpEncode(String raw) {
		String result = raw;
		
		// // SPARQL ENCODE
		// result = result.replace("\\","\\\\");
		// result = result.replace("'","\\'");
		// result = result.replace("\"","\\\"");
		
		// REGEX ENCODE
		result = result.replace("\\", "\\\\");
		result = result.replace("|", "\\|");
		result = result.replace(".", "\\.");
		result = result.replace("-", "\\-");
		result = result.replace("^", "\\^");
		result = result.replace("?", "\\?");
		result = result.replace("*", "\\*");
		result = result.replace("+", "\\+");
		
		result = result.replace("{", "\\{");
		result = result.replace("}", "\\}");
		result = result.replace("(", "\\(");
		result = result.replace(")", "\\)");
		result = result.replace("[", "\\[");
		result = result.replace("]", "\\]");
		return result;
	}
	
	/**
	 * Helps to use SPARQL query templates.
	 * 
	 * @param queryTemplate with placeholders
	 * @param args to be inserted instead of placeholders
	 * @return the queryTemplate string where each template placeholder is
	 *         replaced be the nodes.toSPARQL() method.
	 */
	public static String formatQuery(String queryTemplate, Object ... args) {
		Object[] sparqlArgs = new Object[args.length];
		for(int i = 0; i < args.length; i++) {
			if(args[i] instanceof Node) {
				sparqlArgs[i] = ((Node)args[i]).toSPARQL();
			} else {
				sparqlArgs[i] = args[i];
			}
		}
		return String.format(queryTemplate, sparqlArgs);
	}
	
}
