package org.ontoware.rdf2go.util.transform;

import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Replaces one prefix of all URIs with another prefix.
 * 
 * @author voelkel
 */
public class NamespaceSearchReplaceRule implements TransformerRule {

	private String searchURIPrefix, replaceURIPrefix;

	public NamespaceSearchReplaceRule(String searchURIPrefix,
			String replaceURIPrefix) {
		super();
		this.searchURIPrefix = searchURIPrefix;
		this.replaceURIPrefix = replaceURIPrefix;
	}

	/*
	 * Namespace-map is ignored for this rule (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.util.transform.TransformerRule#applyRule(org.ontoware.rdf2go.model.Model,
	 *      java.util.Map)
	 */
	public void applyRule(Model model, @SuppressWarnings("unused")
	Map<String, URI> namespaceMap) {
		searchAndReplace(model, this.searchURIPrefix, this.replaceURIPrefix);
	}

	public static void searchAndReplace(Model model, String searchURIPrefix,
			String replaceURIPrefix) {
		Model add = RDF2Go.getModelFactory().createModel();
		add.open();

		Model remove = RDF2Go.getModelFactory().createModel();
		remove.open();

		ClosableIterator<Statement> it = model.iterator();
		while (it.hasNext()) {
			Statement stmt = it.next();
			Resource s = stmt.getSubject();
			URI p = stmt.getPredicate();
			Node o = stmt.getObject();

			boolean match = false;
			if (s instanceof URI
					&& s.asURI().toString().startsWith(searchURIPrefix)) {
				match = true;
				String sURI = s.asURI().toString().replace(searchURIPrefix,
						replaceURIPrefix);
				s = new URIImpl(sURI);
			}
			if (p.toString().startsWith(searchURIPrefix)) {
				match = true;
				String pURI = p.toString().replace(searchURIPrefix,
						replaceURIPrefix);
				p = new URIImpl(pURI);

			}
			if (o instanceof URI
					&& o.asURI().toString().startsWith(searchURIPrefix)) {
				match = true;
				String oURI = o.asURI().toString().replace(searchURIPrefix,
						replaceURIPrefix);
				o = new URIImpl(oURI);
			}

			if (match) {
				remove.addStatement(stmt);
				add.addStatement(s, p, o);
			}
		}
		it.close();
		ClosableIterator<Statement> addIt = add.iterator();
		ClosableIterator<Statement> removeIt = remove.iterator();
		model.update( new DiffImpl(addIt, removeIt));
		addIt.close();
		removeIt.close();
		add.close();
		remove.close();
	}

}
