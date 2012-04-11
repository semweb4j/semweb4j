package org.ontoware.rdf2go.impl.jena27;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.AbstractBlankNodeImpl;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.LanguageTagLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.shared.impl.JenaParameters;


/**
 * converter between java objects and jena nodes (both ways)
 * 
 * @author xam
 * 
 */
public class TypeConversion {
	
	/**
	 * the logger
	 */
	static Logger log = LoggerFactory.getLogger(TypeConversion.class);
	
	/**
	 * The new Implementation: model is needed because Jena can only create data
	 * typed literals with unknown data types if it creates them directly in the
	 * context of a model instance.
	 */
	public static Node toJenaNode(Object o, com.hp.hpl.jena.rdf.model.Model model)
	        throws RuntimeException {
		assert o != null;
		if(o instanceof URI) {
			log.debug("instanceof URI");
			return Node.createURI(((URI)o).toString());
		}
		
		if(o instanceof String) {
			// plain literal
			log.debug("instanceof String");
			return Node.createLiteral((String)o, null, false);
		}
		
		if(o instanceof PlainLiteral) {
			// plain literal
			log.debug("instanceof String");
			return Node.createLiteral(((PlainLiteral)o).getValue(), null, false);
		}
		
		if(o instanceof DatatypeLiteral) {
			// datatyped
			log.debug("instanceof DatatypeLiteral");
			
			boolean originalFlag = JenaParameters.enableSilentAcceptanceOfUnknownDatatypes;
			JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = true;
			
			Literal l = model.createTypedLiteral(((DatatypeLiteral)o).getValue(),
			        ((DatatypeLiteral)o).getDatatype().toString());
			
			JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = originalFlag;
			
			return l.asNode();
			
		}
		
		if(o instanceof LanguageTagLiteral) {
			// langtag
			log.debug("instanceof LanguageTagLiteral");
			return Node.createLiteral(((LanguageTagLiteral)o).getValue(),
			        ((LanguageTagLiteral)o).getLanguageTag(), null);
		}
		
		if(o instanceof BlankNode) {
			// blank node
			log.debug("instanceof BlankNode");
			assert o instanceof AbstractBlankNodeImpl : "expected a BlankNodeImpl and found a "
			        + o.getClass();
			AnonId anonId = new AnonId(((AbstractBlankNodeImpl)o).getUnderlyingBlankNode()
			        .toString());
			Node jenaNode = Node.createAnon(anonId);
			return jenaNode;
		}
		
		if(o instanceof Variable) {
			// variable
			log.debug("instanceof Variable");
			return Node.ANY;
		}
		
		throw new RuntimeException("no transformation from " + o.getClass()
		        + " to jena has been implemented");
	}
	
	/**
	 * transforms the given object into a Jena-node
	 * 
	 * @param o The object to transform
	 * @return The jena node
	 * @throws RuntimeException if the object can't be transformed in a jena
	 *             node
	 */
	public static Node toJenaNode(Object o) throws RuntimeException {
		assert o != null;
		if(o instanceof URI) {
			log.debug("instanceof URI");
			return Node.createURI(((URI)o).toString());
		}
		
		if(o instanceof String) {
			// plain literal
			log.debug("instanceof String");
			return Node.createLiteral((String)o, null, false);
		}
		
		if(o instanceof PlainLiteral) {
			// plain literal
			log.debug("instanceof String");
			return Node.createLiteral(((PlainLiteral)o).getValue(), null, false);
		}
		
		if(o instanceof DatatypeLiteral) {
			// datatyped
			log.debug("instanceof DatatypeLiteral");
			// there is an issue with jena datatypes
			// the language (middle part parameter) has to be null
			// the RDF specification says it must not be set!
			// (this is left in JENA because of backward compatibility)
			return Node.createLiteral(((DatatypeLiteral)o).getValue(), null, new GeneralDataType(
			        ((DatatypeLiteral)o).getDatatype() + ""));
		}
		
		if(o instanceof LanguageTagLiteral) {
			// langtag
			log.debug("instanceof LanguageTagLiteral");
			return Node.createLiteral(((LanguageTagLiteral)o).getValue(),
			        ((LanguageTagLiteral)o).getLanguageTag(), null);
		}
		
		if(o instanceof BlankNode) {
			// blank node
			log.debug("instanceof BlankNode");
			assert o instanceof AbstractBlankNodeImpl : "expected a BlankNodeImpl and found a "
			        + o.getClass();
			AnonId anonId = new AnonId(((AbstractBlankNodeImpl)o).getUnderlyingBlankNode()
			        .toString());
			Node jenaNode = Node.createAnon(anonId);
			return jenaNode;
		}
		
		if(o instanceof Variable) {
			// variable
			log.debug("instanceof Variable");
			return Node.ANY;
		}
		
		throw new RuntimeException("no transformation from " + o.getClass()
		        + " to jena has been implemented");
	}
	
	/**
	 * Transforms a Jena node into a java object. Possible node types: uri,
	 * variable, literal (datatype, languageTag), blank node
	 * 
	 * @param n The node to transform
	 * @return A specific java object
	 * @throws ModelRuntimeException from the underlying model
	 */
	public static org.ontoware.rdf2go.model.node.Node toRDF2Go(Node n) throws ModelRuntimeException {
		// A return of null indicates that the variable is not present in this
		// solution.
		if(n == null)
			return null;
		
		if(n.isURI())
			return new URIImpl(n.getURI());
		
		if(n.isVariable())
			throw new RuntimeException("Cannot convert a Jena variable to an RDF2Go node");
		
		if(n.isLiteral()) {
			LiteralLabel lit = n.getLiteral();
			// datatype
			if(lit.getDatatypeURI() != null) {
				return new DatatypeLiteralImpl(lit.getLexicalForm(), new URIImpl(
				        lit.getDatatypeURI()));
			}
			
			// language tagged
			if(lit.language() != null && !lit.language().equals(""))
				return new LanguageTagLiteralImpl(lit.getLexicalForm(), lit.language());
			
			// plain
			return new PlainLiteralImpl(lit.getLexicalForm());
		}
		
		if(n.isBlank())
			return new JenaBlankNode(n);
		
		// none of the above - don't know how to transform that
		throw new RuntimeException("no transformation defined from " + n + " to java");
	}
	
}
