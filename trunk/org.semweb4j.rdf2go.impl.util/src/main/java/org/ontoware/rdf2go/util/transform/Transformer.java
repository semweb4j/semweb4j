package org.ontoware.rdf2go.util.transform;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.node.URI;

/**
 * Transforms RDF syntactically according to configuration, e.g. renaming
 * properties
 * 
 * @author voelkel
 * 
 */
public class Transformer {

	/**
	 * @param model
	 * @param rules -
	 *            will be processed in order
	 */
	public static void transform(Model model, Map<String, URI> namespaceMap,
			List<TransformerRule> rules) {
		for (TransformerRule rule : rules) {
			if (rule instanceof SearchReplaceRule) {
				SearchReplaceRule srRule = (SearchReplaceRule) rule;
				applySearchReplaceRule(model, namespaceMap, srRule.getSearch(),
						srRule.getRemove(), srRule.getAdd());
			} else if (rule instanceof InferenceRule) {
				InferenceRule infRule = (InferenceRule) rule;
				applyInferenceRule(model, namespaceMap, infRule.getSearch(),
						infRule.getAdd());
			} else
				throw new IllegalArgumentException(
						"Cannot handle rules of type " + rule.getClass());
		}
	}

	public static void applySearchReplaceRule(Model model,
			Map<String, URI> namespaceMap, String where,
			String constructRemove, String constructAdd) {
		Model remove = RDF2Go.getModelFactory().createModel();
		remove.open();
		ClosableIterator<Statement> it = model.sparqlConstruct(
				toSparqlConstruct(namespaceMap, constructRemove, where))
				.iterator();
		remove.addAll((Iterator<? extends Statement>) it);
		it.close();

		Model add = RDF2Go.getModelFactory().createModel();
		add.open();
		it = model.sparqlConstruct(
				toSparqlConstruct(namespaceMap, constructAdd, where))
				.iterator();
		add.addAll((Iterator<? extends Statement>) it);
		it.close();

		Diff diff = new DiffImpl(add.iterator(), remove.iterator());
		model.update(diff);
	}

	public static void applyInferenceRule(Model model,
			Map<String, URI> namespaceMap, String where, String constructAdd) {
		Model remove = RDF2Go.getModelFactory().createModel();
		remove.open();

		Model add = RDF2Go.getModelFactory().createModel();
		add.open();
		ClosableIterator<Statement> it = model.sparqlConstruct(
				toSparqlConstruct(namespaceMap, constructAdd, where))
				.iterator();
		add.addAll((Iterator<? extends Statement>) it);
		it.close();

		Diff diff = new DiffImpl(add.iterator(), remove.iterator());
		model.update(diff);
	}

	public static String toSparqlConstruct(Map<String, URI> namespaceMap,
			String construct, String where) {
		StringBuffer query = new StringBuffer();
		for (String ns : namespaceMap.keySet()) {
			query.append("PREFIX ").append(ns).append(": ").append(
					namespaceMap.get(ns).toSPARQL()).append("\n");
		}
		query.append("CONSTRUCT {\n");
		query.append(construct).append("\n");
		query.append("} WHERE {\n");
		query.append(where).append("\n");
		query.append("}\n");
		return query.toString();
	}
}
