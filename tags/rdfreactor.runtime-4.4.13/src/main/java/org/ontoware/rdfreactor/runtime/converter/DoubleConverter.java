package org.ontoware.rdfreactor.runtime.converter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.runtime.INodeConverter;
import org.ontoware.rdfreactor.runtime.RDFDataException;

public class DoubleConverter implements INodeConverter<Double> {

	public Double toJava(Node node) {
		return node2Double(node);
	}

	public static Double node2Double(Node node) {
		if (node == null)
			return null;

		if (node instanceof PlainLiteral) {
			return toDouble(node.asLiteral());
		}

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert a language tagged literal to an Double - it makes no sense");
		}

		if (node instanceof DatatypeLiteral) {
			URI datatype = node.asDatatypeLiteral().getDatatype();

			if (datatype.equals(XSD._double)) {
				return toDouble(node.asDatatypeLiteral());
			} else {
				throw new RDFDataException("Cannot convert from datatype "
						+ datatype + " to URI");
			}
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to Double");
	}

	public static Double toDouble(Literal literal) {
		return new Double(Double.parseDouble(literal.getValue()));
	}

	public Node toNode(Model model, Object javaValue) {
		return model.createDatatypeLiteral( ""+(Double) javaValue, XSD._double);
	}

}
