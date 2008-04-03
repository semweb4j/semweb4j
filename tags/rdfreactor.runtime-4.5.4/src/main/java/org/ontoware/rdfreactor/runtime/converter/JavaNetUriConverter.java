package org.ontoware.rdfreactor.runtime.converter;

import java.net.URISyntaxException;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.runtime.INodeConverter;
import org.ontoware.rdfreactor.runtime.RDFDataException;

public class JavaNetUriConverter implements INodeConverter<java.net.URI> {

	public java.net.URI toJava(Node node) {
		if (node == null)
			return null;

		if (node instanceof URI) {
			return node.asURI().asJavaURI();
		}

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert a language tagged literal to a URI - it makes no sense");
		}

		try {
			if (node instanceof PlainLiteral) {
				return new java.net.URI(node.asLiteral().getValue());
			}
			if (node instanceof DatatypeLiteral) {
				URI datatype = node.asDatatypeLiteral().getDatatype();
				if (datatype.equals(XSD._anyURI)) {
					return new java.net.URI(node.asDatatypeLiteral().getValue());
				} else {
					throw new RDFDataException("Cannot convert from datatype "
							+ datatype + " to URI");
				}
			}
		} catch (URISyntaxException e) {
			throw new RDFDataException("Could not convert <"
					+ node.asLiteral().getValue() + "> to a java.net.URI", e);
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to URI");
	}

	public Node toNode(Model model, Object javaValue) {
		return model.createURI(javaValue.toString());
	}

}
