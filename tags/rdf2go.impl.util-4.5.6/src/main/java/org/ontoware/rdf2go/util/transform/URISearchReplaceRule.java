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

	public void applyRule(Model model, Map<String, URI> namespaceMap) {
		searchAndReplace(model, namespaceMap, search, replace);
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
