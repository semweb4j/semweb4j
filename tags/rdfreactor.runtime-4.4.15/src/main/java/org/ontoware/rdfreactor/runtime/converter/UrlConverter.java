package org.ontoware.rdfreactor.runtime.converter;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.runtime.INodeConverter;
import org.ontoware.rdfreactor.runtime.RDFDataException;

public class UrlConverter implements INodeConverter<java.net.URL> {

	public java.net.URL convert(Node node) {
		if (node == null)
			return null;

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert a language tagged literal to a URI - it makes no sense");
		}

		try {
			if (node instanceof URI) {
				return new URL(node.asURI().toString());
			}

			if (node instanceof PlainLiteral) {
				return new java.net.URL(node.asLiteral().getValue());
			}

			if (node instanceof DatatypeLiteral) {
				URI datatype = node.asDatatypeLiteral().getDatatype();
				if (datatype.equals(XSD._anyURI)) {
					return new java.net.URL(node.asDatatypeLiteral().getValue());
				} else {
					throw new RDFDataException("Cannot convert from datatype "
							+ datatype + " to URI");
				}
			}
		} catch (MalformedURLException e) {
			throw new RDFDataException("Could not convert <"
					+ node.asLiteral().getValue() + "> to a java.net.URL", e);
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to URL");
	}

	public URL toJava(Node node) {
		return convert(node);
	}

	public Node toNode(Model model, Object javaValue) {
		try {
			return model.createURI(((URL) javaValue).toURI() + "");
		} catch (URISyntaxException e) {
			throw new RDFDataException("error in URL syntax?",e);
		}
	}

}
