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

public class IntegerConverter implements INodeConverter<Integer> {

	public Integer toJava(Node node) {
		return node2integer(node);
	}

	public static Integer node2integer(Node node) {
		if (node == null)
			return null;

		if (node instanceof PlainLiteral) {
			return toInteger(node.asLiteral());
		}

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert a language tagged literal to an Integer - it makes no sense");
		}

		if (node instanceof DatatypeLiteral) {
			URI datatype = node.asDatatypeLiteral().getDatatype();

			// TODO add handling for XSD._nonPositiveInteger and other weird
			// types

			// Note: in general, xsd:integer cannot be handled by Java
			
			if (datatype.equals(XSD._int)) {
				return toInteger(node.asDatatypeLiteral());
			} else {
				throw new RDFDataException("Cannot convert from datatype "
						+ datatype + " to URI");
			}
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to Integer");
	}

	public static Integer toInteger(Literal literal) {
		return new Integer(Integer.parseInt(literal.getValue()));
	}

	public Node toNode(Model model, Object javaValue) {
		return model.createDatatypeLiteral( ""+javaValue, XSD._int);
	}

}
