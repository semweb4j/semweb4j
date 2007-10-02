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

public class ShortConverter implements INodeConverter<Short> {

	public Short toJava(Node node) {
		return node2Short(node);
	}

	public static Short node2Short(Node node) {
		if (node == null)
			return null;

		if (node instanceof PlainLiteral) {
			return toShort(node.asLiteral());
		}

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert a language tagged literal to an Short - it makes no sense");
		}

		if (node instanceof DatatypeLiteral) {
			URI datatype = node.asDatatypeLiteral().getDatatype();
			if (datatype.equals(XSD._short)) {
				return toShort(node.asDatatypeLiteral());
			} else {
				throw new RDFDataException("Cannot convert from datatype "
						+ datatype + " to URI");
			}
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to Short");
	}

	public static Short toShort(Literal literal) {
		return new Short(Short.parseShort(literal.getValue()));
	}
	
	public Node toNode(Model model, Object javaValue) {
		return model.createDatatypeLiteral( ""+(Short) javaValue, XSD._short);
	}


}
