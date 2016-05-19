/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 * 
 * Licensed under the Aduna BSD-style license.
 */
package org.eclipse.rdf4j.rdf2go;

import java.util.Optional;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.AbstractBlankNodeImpl;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.LanguageTagLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.QueryLanguage;


/**
 * Converts RDF2Go types to OpenRDF types and vice versa. Null converts to null,
 * in any direction.
 */
public class ConversionUtil {
	
	/**
	 * Converts an RDF2Go data type into its corresponding OpenRDF
	 * representation.
	 * 
	 * @param object The RDF2Go object to transform.
	 * @param factory The OpenRDF ValueFactory to use for creating the OpenRDF
	 *            representation.
	 * @return An instance of an OpenRDF data type representing the specified
	 *         Object. Returns 'null' when the specified object is null.
	 * @throws IllegalArgumentException when the specified Object is of an
	 *             unrecognized type.
	 */
	public static Value toRDF4J(Object object, ValueFactory factory) {
		if(object == null) {
			return null;
		} else if(object instanceof URI) {
			return toRDF4J((URI)object, factory);
		} else if(object instanceof String) {
			return toRDF4J((String)object, factory);
		} else if(object instanceof PlainLiteral) {
			return toRDF4J((PlainLiteral)object, factory);
		} else if(object instanceof LanguageTagLiteral) {
			return toRDF4J((LanguageTagLiteral)object, factory);
		} else if(object instanceof DatatypeLiteral) {
			return toRDF4J((DatatypeLiteral)object, factory);
		} else if(object instanceof BlankNode) {
			return toRDF4J((BlankNode)object, factory);
		} else if(object instanceof Variable) {
			return toRDF4J((Variable)object, factory);
		} else
		// fix for RDFReactor compatibility
		if(object instanceof org.ontoware.rdf2go.model.node.Resource) {
			try {
				URI uri = ((org.ontoware.rdf2go.model.node.Resource)object).asURI();
				return toRDF4J(uri, factory);
			} catch(ClassCastException e) {
				// if a resource is not a URI it must be a blank node
				BlankNode blankNode = ((org.ontoware.rdf2go.model.node.Resource)object)
				        .asBlankNode();
				return toRDF4J(blankNode, factory);
			}
		} else {
			throw new IllegalArgumentException("Unexpected object type: "
			        + object.getClass().getName());
		}
	}
	
	public static org.eclipse.rdf4j.model.IRI toRDF4J(URI uri, ValueFactory factory) {
		return uri == null ? null : factory.createIRI(uri.toString());
	}
	
	public static org.eclipse.rdf4j.model.Literal toRDF4J(String string, ValueFactory factory) {
		return string == null ? null : factory.createLiteral(string);
	}
	
	public static org.eclipse.rdf4j.model.Literal toRDF4J(PlainLiteral literal, ValueFactory factory) {
		return literal == null ? null : factory.createLiteral(literal.getValue());
	}
	
	public static org.eclipse.rdf4j.model.Literal toRDF4J(LanguageTagLiteral literal,
														  ValueFactory factory) {
		return literal == null ? null : factory.createLiteral(literal.getValue(),
		        literal.getLanguageTag());
	}
	
	public static org.eclipse.rdf4j.model.Literal toRDF4J(DatatypeLiteral literal, ValueFactory factory) {
		return literal == null ? null : factory.createLiteral(literal.getValue(),
		        toRDF4J(literal.getDatatype(), factory));
	}
	
	/**
	 * Implementation note: This method does not use the {@link ValueFactory}
	 * but directly fetches the Sesame object reference from RDF2Gos wrapper
	 * object.
	 * 
	 * @param node RDF2Go blank node
	 * @param factory OpenRDF value factory
	 * @return the OpenRDF instance wrapped in the given RDF2Go blank node or a
	 *         new OpenRDF blank node with the same internal ID.
	 */
	public static BNode toRDF4J(BlankNode node, ValueFactory factory) {
		BNode result = null;
		
		if(node != null) {
			Object underlyingBlankNode = ((AbstractBlankNodeImpl)node).getUnderlyingBlankNode();
			
			if(underlyingBlankNode instanceof BNode) {
				result = (BNode)underlyingBlankNode;
			} else {
				result = factory.createBNode(String.valueOf(underlyingBlankNode));
			}
		}
		
		return result;
	}
	
	/**
	 * Variables in Sesame are represented by <code>null</code>.
	 * 
	 * @param variable - not used
	 * @param factory - not used
	 * @return always null
	 */
	public static Value toRDF4J(Variable variable, ValueFactory factory) {
		return null;
	}
	
	public static org.eclipse.rdf4j.model.Statement toRDF4J(Statement statement, ValueFactory factory) {
		Resource subject = (Resource) toRDF4J(statement.getSubject(), factory);
		org.eclipse.rdf4j.model.IRI predicate = toRDF4J(statement.getPredicate(), factory);
		Value object = toRDF4J(statement.getObject(), factory);
		org.eclipse.rdf4j.model.IRI context = toRDF4J(statement.getContext(), factory);
		
		return factory.createStatement(subject, predicate, object, context);
	}
	
	public static QueryLanguage toOpenRDFQueryLanguage(String queryLanguage) {
		String queryLanguageLowerCase = queryLanguage.toLowerCase();
		
		if(queryLanguageLowerCase.equals("sparql")) {
			return QueryLanguage.SPARQL;
		} else if(queryLanguageLowerCase.equals("serql")) {
			return QueryLanguage.SERQL;
		} else if(queryLanguageLowerCase.equals("serqo")) {
			return QueryLanguage.SERQO;
		} else {
			throw new QueryLanguageNotSupportedException("Query language '"
			        + queryLanguageLowerCase
			        + "' not supported. Valid values are \"sparql\", \"serql\" and \"serqo\".");
		}
	}
	
	/**
	 * Converts an OpenRDF Value instance into its corresponding RDF2Go
	 * representation.
	 * 
	 * @param value The Value to transform.
	 * @return An instance of an RDF2Go data type representing the specified
	 *         Value. Returns 'null' when the Value is null.
	 * @throws IllegalArgumentException when the specified Value is of an
	 *             unrecognized type.
	 */
	public static Node toRdf2go(Value value) {
		if(value == null) {
			return null;
		} else if(value instanceof org.eclipse.rdf4j.model.IRI) {
			return toRdf2go((org.eclipse.rdf4j.model.IRI)value);
		} else if(value instanceof org.eclipse.rdf4j.model.Literal) {
			return toRdf2go((org.eclipse.rdf4j.model.Literal)value);
		} else if(value instanceof BNode) {
			return toRdf2go((BNode)value);
		} else {
			throw new IllegalArgumentException("Unexpected Value type: "
			        + value.getClass().getName());
		}
	}
	
	public static URI toRdf2go(org.eclipse.rdf4j.model.IRI iri) {
		return iri == null ? null : new URIImpl(iri.toString(), false);
	}
	
	public static Literal toRdf2go(org.eclipse.rdf4j.model.Literal literal) {
		if(literal == null) {
			return null;
		}
		
		String label = literal.getLabel();
		Optional<String> language = literal.getLanguage();
		Object dataType = literal.getDatatype();
		
		if(language.isPresent()) {
			return new LanguageTagLiteralImpl(label, language.get());
		} else if(dataType != null) {
			if ("http://www.w3.org/2001/XMLSchema#string".equals(dataType.toString())) {
				// RDF2go treats String-Typed literals as plain.
				return new PlainLiteralImpl(label);
			} else {
				return new DatatypeLiteralImpl(label, new URIImpl(dataType.toString(), false));
			}
		} else {
			return new PlainLiteralImpl(label);
		}
	}
	
	public static BlankNode toRdf2go(BNode node) {
		return node == null ? null : new OpenrdfBlankNode(node);
	}
}
