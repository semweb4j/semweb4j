package org.ontoware.rdf2go.examples;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDFS;

/**
 * how to write basic model usage operations
 */
public class ModelUsageExample {

	public static void main(String[] args) throws Exception {

		// get a model
		Model model = RDF2Go.getModelFactory().createModel();

		// add statements

		// the cleanest way
		Resource s = model.createURI("urn:test:a");
		URI p = model.createURI("http://www.w3.org/2000/01/rdf-schema#label");
		Node o = model.createPlainLiteral("Hello World A");
		model.addStatement(s, p, o);

		// a shortcut: built-in URIs for RDF and RDFS
		model.addStatement("urn:test:b", RDFS.label, "Hello World B");
		
		// a shortcut: resource URIs and plain literal objects as strings
		model.addStatement("urn:test:c", RDFS.label, "Hello World C");

		// list statements
		for (Statement stmt : model)
			System.out.println(stmt);
		
		// query for triple pattern
		ClosableIterator<? extends Statement> it = model.findStatements( s, Variable.ANY, Variable.ANY );
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		it.close();

	}

}
