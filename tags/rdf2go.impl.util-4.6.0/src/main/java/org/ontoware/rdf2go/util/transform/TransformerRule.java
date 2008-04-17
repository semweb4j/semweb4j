package org.ontoware.rdf2go.util.transform;

import java.util.Map;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;

public interface TransformerRule {
	
	void applyRule( Model model, Map<String,URI> namespaceMap );
	
}
