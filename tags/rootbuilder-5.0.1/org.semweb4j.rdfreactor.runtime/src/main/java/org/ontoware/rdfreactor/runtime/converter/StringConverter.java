package org.ontoware.rdfreactor.runtime.converter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.runtime.INodeConverter;
import org.ontoware.rdfreactor.runtime.RDFDataException;

public class StringConverter implements INodeConverter<String> {

	public String toJava(Node node) {
		return node2string(node);
	}

	public static String node2string(Node node) {
		if (node == null)
			return null;

		if (node instanceof PlainLiteral) {
			return node.asLiteral().getValue();
		}

		if (node instanceof LanguageTagLiteral) {
			// log.debug("Ignoring language");
			return node.asLiteral().getValue();
		}

		if (node instanceof DatatypeLiteral) {
			URI datatype = node.asDatatypeLiteral().getDatatype();

			if (datatype.equals(XSD._string)
					|| datatype.equals(XSD._normalizedString)) {
				return node.asDatatypeLiteral().getValue();
			} else {
				throw new RDFDataException("Cannot convert from datatype "
						+ datatype + " to URI");
			}
		}

		throw new RDFDataException("Cannot convert from " + node.getClass()
				+ " to Float");
	}

	public Node toNode(Model model, Object javaValue) {
		// representing all strings as XSD._string would also be possible - but
		// why make things more complicated?
		return model.createPlainLiteral((String) javaValue);
	}

}
