package org.ontoware.rdfreactor.runtime.converter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.runtime.INodeConverter;
import org.ontoware.rdfreactor.runtime.RDFDataException;


/**
 * Converts an RDF2Go {@link Node} to an RDF2Go {@link URI}.
 * 
 * {@link Literal} is just turned into {@link URI}. {@link URI} remains
 * {@link URI}, {@link LanguageTagLiteral} fails. {@link DatatypeLiteral} works
 * only if type is xsd:anyURI. {@link BlankNode} fails. Null returns null.
 * 
 * @author voelkel
 * 
 */
public class UriConverter implements INodeConverter<URI> {
	
	public URI toJava(Node node) {
		
		if(node == null)
			return null;
		
		if(node instanceof URI) {
			return node.asURI();
		}
		
		if(node instanceof PlainLiteral) {
			return new URIImpl(node.asLiteral().getValue());
		}
		
		if(node instanceof LanguageTagLiteral) {
			throw new RDFDataException(
			        "Cannot convert a language tagged literal to a URI - it makes no sense");
		}
		
		if(node instanceof DatatypeLiteral) {
			URI datatype = node.asDatatypeLiteral().getDatatype();
			if(datatype.equals(XSD._anyURI)) {
				return new URIImpl(node.asDatatypeLiteral().getValue());
			} else {
				throw new RDFDataException("Cannot convert from datatype " + datatype + " to URI");
			}
		}
		
		throw new RDFDataException("Cannot convert from " + node.getClass() + " to URI");
		
	}
	
	public Node toNode(Model model, Object javaValue) {
		return (URI)javaValue;
	}
	
}
