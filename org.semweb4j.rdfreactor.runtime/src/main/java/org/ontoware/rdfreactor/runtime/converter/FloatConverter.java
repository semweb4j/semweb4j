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

public class FloatConverter implements INodeConverter<Float> {

	public Float toJava(Node node) {
		return node2Float(node);
	}

	public static Float node2Float(Node node) {
		if (node == null)
			return null;

		if (node instanceof PlainLiteral) {
			return toFloat(node.asLiteral());
		}

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert a language tagged literal to a Float - it makes no sense");
		}

		if (node instanceof DatatypeLiteral) {
			URI datatype = node.asDatatypeLiteral().getDatatype();

			if (datatype.equals(XSD._float)) {
				return toFloat(node.asDatatypeLiteral());
			} else {
				throw new RDFDataException("Cannot convert from datatype "
						+ datatype + " to URI");
			}
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to Float");
	}

	public static Float toFloat(Literal literal) {
		return new Float(Float.parseFloat(literal.getValue()));
	}

	public Node toNode(Model model, Object javaValue) {
		return model.createDatatypeLiteral( ""+javaValue, XSD._float);
	}

}
