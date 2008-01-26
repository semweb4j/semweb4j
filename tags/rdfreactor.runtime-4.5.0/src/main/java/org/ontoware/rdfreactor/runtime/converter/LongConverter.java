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

public class LongConverter implements INodeConverter<Long> {

	public Long toJava(Node node) {
		return node2Long(node);
	}

	public static Long node2Long(Node node) {
		if (node == null)
			return null;

		if (node instanceof PlainLiteral) {
			return toLong(node.asLiteral());
		}

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert a language tagged literal to an Long - it makes no sense");
		}

		if (node instanceof DatatypeLiteral) {
			URI datatype = node.asDatatypeLiteral().getDatatype();
			if (datatype.equals(XSD._long)) {
				return toLong(node.asDatatypeLiteral());
			} else {
				throw new RDFDataException("Cannot convert from datatype "
						+ datatype + " to Long");
			}
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to Long");
	}

	public static Long toLong(Literal literal) {
		return new Long(Long.parseLong(literal.getValue()));
	}

	public Node toNode(Model model, Object javaValue) {
		return model.createDatatypeLiteral( ""+(Long) javaValue, XSD._long);
	}

}
