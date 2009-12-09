package org.ontoware.rdf2go;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.runtime.Base;
import org.ontoware.rdfreactor.schema.rdfs.Seq;

/**
 * @author voelkel
 * 
 */
public class TestSequence {

	// <?xml version="1.0"?>
	// <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
	// <rdf:Seq rdf:about="http://example.org/favourite-fruit">
	// <rdf:li rdf:resource="http://example.org/banana"/>
	// <rdf:li rdf:resource="http://example.org/apple"/>
	// <rdf:li rdf:resource="http://example.org/pear"/>
	// </rdf:Seq>
	// </rdf:RDF>

	public static void main(String[] args) throws ModelRuntimeException,
			IOException {
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		PlainLiteralImpl pli = new PlainLiteralImpl("Hello");
		PlainLiteralImpl pli2 = new PlainLiteralImpl("World");
		Seq s = new Seq(model, "urn:test:newSeq", true);
		Base.add(model, s, RDF.li(1), pli);
		Base.add(model, s, RDF.li(2), pli2);
		model.writeTo(System.out, Syntax.RdfXml);
	}

}
