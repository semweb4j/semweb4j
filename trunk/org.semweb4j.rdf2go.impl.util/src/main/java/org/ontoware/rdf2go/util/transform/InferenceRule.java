package org.ontoware.rdf2go.util.transform;

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
	
	
}
