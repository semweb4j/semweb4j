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

public class ByteConverter implements INodeConverter<Byte> {

	public Byte toJava(Node node) {
		return node2Byte(node);
	}

	public static Byte node2Byte(Node node) {
		if (node == null)
			return null;

		if (node instanceof PlainLiteral) {
			return toByte(node.asLiteral());
		}

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert a language tagged literal to an Byte - it makes no sense");
		}

		if (node instanceof DatatypeLiteral) {
			URI datatype = node.asDatatypeLiteral().getDatatype();
			if (datatype.equals(XSD._byte)) {
				return toByte(node.asDatatypeLiteral());
			} else {
				throw new RDFDataException("Cannot convert from datatype "
						+ datatype + " to URI");
			}
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to Byte");
	}

	public static Byte toByte(Literal literal) {
		return new Byte(Byte.parseByte(literal.getValue()));
	}

	public Node toNode(Model model, Object javaValue) {
		return model.createDatatypeLiteral( ""+(Byte) javaValue, XSD._byte);
	}

}
