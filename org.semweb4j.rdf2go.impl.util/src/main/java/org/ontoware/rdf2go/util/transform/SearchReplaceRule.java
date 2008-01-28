package org.ontoware.rdf2go.util.transform;


public class SearchReplaceRule implements TransformerRule {

	private String search, remove, add;
	
	public String getSearch() {
		return search;
	}

	public String getRemove() {
		return remove;
	}

	public String getAdd() {
		return add;
	}

	public SearchReplaceRule(String search, String remove, String add) {
		super();
		this.search = search;
		this.remove = remove;
		this.add=add;
	}

	public SearchReplaceRule(String search, String replace) {
		super();
		this.search = search;
		this.remove = search;
		this.add=replace;
	}
	
	
}
