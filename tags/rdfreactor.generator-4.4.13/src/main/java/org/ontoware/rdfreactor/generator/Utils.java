package org.ontoware.rdfreactor.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public class Utils {

	/**
	 * Generate a unique URI for a BlankNode and put the BlankNode/URI pair into
	 * the given map.
	 * 
	 * @param blankNode -
	 *            generate a URI for this BlankNode
	 * @param replacement -
	 *            Map of BlankNode/URI pairs
	 * @return URI generated for the BlankNode
	 */
	public static URI toURI(BlankNode blankNode,
			Map<BlankNode, URI> replacement, long counter) {
		URI result = replacement.get(blankNode);
		if (result == null)
			result = new URIImpl("blank://" + counter++);
		// TODO BlankNode identity might be too weak
		replacement.put(blankNode, result);
		return result;
	}

	/**
	 * Remove all Statements with BlankNodes as Subjects or Objects from the
	 * given RDF2Go model.
	 * 
	 * @param m -
	 *            de-anonymise this model
	 * @throws Exception
	 */
	public static void deanonymize(Model m) throws Exception {
		Iterator<Statement> it = m.iterator();
		long counter = 0;
		Map<BlankNode, URI> replacement = new HashMap<BlankNode, URI>();
		Set<Statement> badStatements = new HashSet<Statement>();
		Set<Statement> goodStatements = new HashSet<Statement>();
		while (it.hasNext()) {
			Statement s = it.next();
			org.ontoware.rdf2go.model.node.Resource subject = s.getSubject();
			if (s.getSubject() instanceof BlankNode) {
				badStatements.add(s);
				subject = toURI((BlankNode) subject, replacement, counter);
			}
			Node object = s.getObject();
			if (object instanceof BlankNode) {
				badStatements.add(s);
				object = toURI((BlankNode) object, replacement, counter);
			}
			goodStatements.add(new StatementImpl(m.getContextURI(), subject, s
					.getPredicate(), object));
		}
		for (Statement s : badStatements)
			m.removeStatement(s);
		for (Statement s : goodStatements)
			m.addStatement(s);

	}

	public static String toJavaComment(String[] rdfComments) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < rdfComments.length; i++) {
			buf.append(rdfComments[i]);
			if (i + 1 < rdfComments.length) {
				buf.append("\n");
			}
		}
		return buf.toString();
	}

}
