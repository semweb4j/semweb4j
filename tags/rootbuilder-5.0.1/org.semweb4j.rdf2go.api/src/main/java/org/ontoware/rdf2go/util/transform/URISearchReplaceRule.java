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

package org.ontoware.rdf2go.util.transform;

import java.util.Map;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;

public class URISearchReplaceRule implements TransformerRule {

	private URI search, replace;

	public URISearchReplaceRule(URI search, URI replace) {
		super();
		this.search = search;
		this.replace = replace;
	}

	@Override
    public void applyRule(Model model, Map<String, URI> namespaceMap) {
		searchAndReplace(model, namespaceMap, this.search, this.replace);
	}

	public static void searchAndReplace(Model model,
			Map<String, URI> namespaceMap, URI search, URI replace) {
		SearchRemoveAddRule.searchAndReplace(model, namespaceMap,

		search.toSPARQL() + " ?p ?o",

		search.toSPARQL() + " ?p ?o",

		replace.toSPARQL() + " ?p ?o");

		SearchRemoveAddRule.searchAndReplace(model, namespaceMap,

		"?s " + search.toSPARQL() + " ?o",

		"?s " + search.toSPARQL() + " ?o",

		"?s " + replace.toSPARQL() + " ?o");
		SearchRemoveAddRule.searchAndReplace(model, namespaceMap,

		"?s ?p " + search.toSPARQL(),

		"?s ?p " + search.toSPARQL(),

		"?s ?p " + replace.toSPARQL());
	}

}
