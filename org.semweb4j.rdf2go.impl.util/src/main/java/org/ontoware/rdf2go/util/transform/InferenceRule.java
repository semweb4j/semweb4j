package org.ontoware.rdf2go.util.transform;

import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.node.URI;

public class InferenceRule implements TransformerRule {

	String search, add;

	public InferenceRule(String search, String add) {
		super();
		this.search = search;
		this.add = add;
	}

	public String getSearch() {
		return search;
	}

	public String getAdd() {
		return add;
	}

	public void applyRule(Model model, Map<String, URI> namespaceMap) {

		Model removeModel = RDF2Go.getModelFactory().createModel();
		removeModel.open();

		Model addModel = RDF2Go.getModelFactory().createModel();
		addModel.open();

		String query = Transformer.toSparqlConstruct(namespaceMap, add, search);

		ClosableIterator<Statement> it = model.sparqlConstruct(query)
				.iterator();
		while (it.hasNext()) {
			Statement stmt = it.next();
			addModel.addStatement(stmt);
		}
		it.close();

		Diff diff = new DiffImpl(addModel.iterator(), removeModel.iterator());
		addModel.close();
		removeModel.close();
		model.update(diff);
	}

}
