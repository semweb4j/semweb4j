package org.ontoware.rdf2go.util.transform;

import java.util.List;
import java.util.Map;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms RDF syntactically according to configuration, e.g. renaming
 * properties
 * 
 * @author voelkel
 * 
 */
public class Transformer {

	private static Logger log = LoggerFactory.getLogger(Transformer.class);
	
	/**
	 * @param model
	 * @param rules -
	 *            will be processed in order
	 */
	public static void transform(Model model, Map<String, URI> namespaceMap,
			List<TransformerRule> rules) {
		
		for (int i = 0; i < rules.size(); i++) {
			log.debug("Executing rule "+i+" -------- "+rules.get(i).getClass());
			rules.get(i).applyRule(model, namespaceMap);
		}
		
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
		log.debug("query \n"+query);
		return query.toString();
	}
}
