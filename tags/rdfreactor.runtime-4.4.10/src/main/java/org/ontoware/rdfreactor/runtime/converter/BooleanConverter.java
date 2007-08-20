package org.ontoware.rdfreactor.runtime.converter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.runtime.INodeConverter;
import org.ontoware.rdfreactor.runtime.RDFDataException;

public class BooleanConverter implements INodeConverter<Boolean>{

	public Boolean toJava(Node node) {
		return node2Boolean(node);
	}

	/**
	 * Static versions for convenience
	 * @param node
	 * @return the node converted to a Boolean, false if null.
	 * @throws RDFDataException if node is not null, but could not be converted
	 */
	public static Boolean node2Boolean(Node node) {
		
		if (node == null)
			return false;

		if (node instanceof DatatypeLiteral) {
			DatatypeLiteral dnode = (DatatypeLiteral) node;
			if (dnode.getDatatype().equals(XSD._boolean)) {
				return new Boolean(Boolean.parseBoolean(dnode.getValue()));
			} else
				throw new RDFDataException("Datatype is " + dnode.getDatatype()
						+ " which is not known");
		}

		if (node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
					"Cannot convert languageTaggedLiterals to booleans - because that is more often an error than a feature");
		}

		if (node instanceof PlainLiteral)
			return new Boolean(Boolean.parseBoolean(((PlainLiteral) node)
					.getValue()));

		throw new RDFDataException("Cannot convert a " + node.getClass()
				+ " to Boolean");
	}

	public Node toNode(Model model, Object javaValue) {
		// TODO Auto-generated method stub
		return null;
	}	
}
