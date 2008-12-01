/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go.model.impl;

import java.io.IOException;
import java.io.Writer;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.ModelWriter;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * Writes a simple Model in TRiX syntax, using a given context URI.
 * @author voelkel
 */
public class StatementWriter extends AbstractModelWriter implements ModelWriter {

	private Writer writer;

	/**
	 * @param w
	 * @throws IOException
	 */
	public StatementWriter(Writer w, URI graphName) throws IOException {
		this.writer = w;

		w.write("<TriX xmlns=\"http://www.w3.org/2004/03/trix/trix-1/\">\n" + // .
				"  <graph>\n" + // .
				"    <uri>"+graphName+"</uri>\n");
	}

	public void close() throws IOException {
		this.writer.write("  </graph>\n" + "</TriX>");
	}

	@Override
	public void addStatement(Resource subject, URI predicate, Node object) throws ModelRuntimeException {

		try {
			this.writer.write("    <triple>\n");
			writeNode(subject);
			writeNode(predicate);
			writeNode(object);
			this.writer.write("    </triple>\n");
		} catch (IOException e) {
			throw new ModelRuntimeException(e);
		}

	}

	private void writeNode(Object node) throws IOException {
		if (node instanceof URI) {
			this.writer.write("      <uri>" + ((URI) node).toString() + "</uri>\n");
		} else if (node instanceof BlankNode) {
			this.writer.write("      <id>" + ((BlankNode) node).toString() + "</id>\n");
		} else if (node instanceof DatatypeLiteral) {
			this.writer.write("      <typedLiteral datatype=\"" + ((DatatypeLiteral) node).getDatatype()
					+ "\">" + ((DatatypeLiteral) node).getValue() + "</typedLiteral>\n");
		} else if (node instanceof LanguageTagLiteral) {
			this.writer.write("      <plainLiteral xml:lang=\""
					+ ((LanguageTagLiteral) node).getLanguageTag() + "\">"
					+ ((LanguageTagLiteral) node).toString() + "</plainLiteral>\n");
		} else if (node instanceof PlainLiteral) {
			this.writer.write("      <plainLiteral>" + ((PlainLiteral) node).getValue() + "</plainLiteral>\n");
		} else
			throw new RuntimeException("Cannot write to RDF: " + node.getClass());
	}
}
