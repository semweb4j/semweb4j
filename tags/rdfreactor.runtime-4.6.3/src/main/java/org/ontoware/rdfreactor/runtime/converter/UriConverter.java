package org.ontoware.rdfreactor.runtime.converter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.runtime.INodeConverter;
import org.ontoware.rdfreactor.runtime.RDFDataException;

public class UriConverter implements INodeConverter<URI> {

	public URI toJava(Node node) {

		if (node == null)
			return null;

		if (node instanceof URI) {
			return node.asURI();
		}

		if (node instanceof PlainLiteral) {
			return new URIImpl(node.asLiteral().getValue());
		}

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert a language tagged literal to a URI - it makes no sense");
		}

		if (node instanceof DatatypeLiteral) {
			URI datatype = node.asDatatypeLiteral().getDatatype();
			if (datatype.equals(XSD._anyURI)) {
				return new URIImpl(node.asDatatypeLiteral().getValue());
			} else {
				throw new RDFDataException("Cannot convert from datatype "
						+ datatype + " to URI");
			}
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to URI");

	}

	public Node toNode(@SuppressWarnings("unused")
	Model model, Object javaValue) {
		return (URI) javaValue;
	}

}
