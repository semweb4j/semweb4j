/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.BlankNodeImpl;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.LanguageTagLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.querymodel.QueryLanguage;

/**
 * Converts RDF2Go types to OpenRDF types and vice versa. Null converts to null,
 * in any direction.
 */
public class ConversionUtil {

	/**
	 * Converts an RDF2Go data type into its corresponding OpenRDF
	 * representation.
	 * 
	 * @param object
	 *        The RDF2Go object to transform.
	 * @param factory
	 *        The OpenRDF ValueFactory to use for creating the OpenRDF
	 *        representation.
	 * @return An instance of an OpenRDF data type representing the specified
	 *         Object. Returns 'null' when the specified object is null.
	 * @throws IllegalArgumentException
	 *         when the specified Object is of an unrecognized type.
	 */
	public static Value toOpenRDF(Object object, ValueFactory factory) {
		if (object == null) {
			return null;
		}
		else if (object instanceof URI) {
			return toOpenRDF((URI)object, factory);
		}
		else if (object instanceof String) {
			return toOpenRDF((String)object, factory);
		}
		else if (object instanceof PlainLiteral) {
			return toOpenRDF((PlainLiteral)object, factory);
		}
		else if (object instanceof LanguageTagLiteral) {
			return toOpenRDF((LanguageTagLiteral)object, factory);
		}
		else if (object instanceof DatatypeLiteral) {
			return toOpenRDF((DatatypeLiteral)object, factory);
		}
		else if (object instanceof BlankNode) {
			return toOpenRDF((BlankNode)object, factory);
		}
		else if (object instanceof Variable) {
			return toOpenRDF((Variable)object, factory);
		}
		else {
			throw new IllegalArgumentException("Unexpected object type: " + object.getClass().getName());
		}
	}
	
	public static org.openrdf.model.URI toOpenRDF(URI uri, ValueFactory factory) {
		return uri == null ? null : factory.createURI(uri.toString());
	}
	
	public static org.openrdf.model.Statement toOpenRDF(org.ontoware.rdf2go.model.Statement statement, ValueFactory factory) {
		org.openrdf.model.URI context = toOpenRDF(statement.getContext(), factory);
		Resource s = (Resource) toOpenRDF(statement.getSubject(), factory);
		org.openrdf.model.URI p = toOpenRDF(statement.getPredicate(), factory);
		Value o = toOpenRDF(statement.getObject(), factory);
		org.openrdf.model.Statement openRdfStatement = factory.createStatement(s,p,o,context);
		return openRdfStatement;
	}

	public static org.openrdf.model.Literal toOpenRDF(String string, ValueFactory factory) {
		return string == null ? null : factory.createLiteral(string);
	}

	public static org.openrdf.model.Literal toOpenRDF(PlainLiteral literal, ValueFactory factory) {
		return literal == null ? null : factory.createLiteral(literal.getValue());
	}

	public static org.openrdf.model.Literal toOpenRDF(LanguageTagLiteral literal, ValueFactory factory) {
		return literal == null ? null : factory.createLiteral(literal.getValue(), literal.getLanguageTag());
	}

	public static org.openrdf.model.Literal toOpenRDF(DatatypeLiteral literal, ValueFactory factory) {
		return literal == null ? null : factory.createLiteral(literal.getValue(), toOpenRDF(
				literal.getDatatype(), factory));
	}

	public static BNode toOpenRDF(BlankNode node, ValueFactory factory) {
		return node == null ? null : new BNodeImpl(
				String.valueOf(((BlankNodeImpl)node).getUnderlyingBlankNode()));
	}

	public static Value toOpenRDF(Variable variable, ValueFactory factory) {
		return null;
	}

	/**
	 * Converts an OpenRDF Value instance into its corresponding RDF2Go
	 * representation.
	 * 
	 * @param value
	 *        The Value to transform.
	 * @return An instance of an RDF2Go data type representing the specified
	 *         Value. Returns 'null' when the Value is null.
	 * @throws IllegalArgumentException
	 *         when the specified Value is of an unrecognized type.
	 */
	public static Node toRdf2go(Value value) {
		if (value == null) {
			return null;
		}
		else if (value instanceof org.openrdf.model.URI) {
			return toRdf2go((org.openrdf.model.URI)value);
		}
		else if (value instanceof org.openrdf.model.Literal) {
			return toRdf2go((org.openrdf.model.Literal)value);
		}
		else if (value instanceof BNode) {
			return toRdf2go((BNode)value);
		}
		else {
			throw new IllegalArgumentException("Unexpected Value type: " + value.getClass().getName());
		}
	}

	public static URI toRdf2go(org.openrdf.model.URI uri) {
		return uri == null ? null : new URIImpl(uri.toString(),false);
	}

	public static Literal toRdf2go(org.openrdf.model.Literal literal) {
		if (literal == null) {
			return null;
		}

		String label = literal.getLabel();
		String language = literal.getLanguage();
		Object dataType = literal.getDatatype();

		if (language != null) {
			return new LanguageTagLiteralImpl(label, language);
		}
		else if (dataType != null) {
			return new DatatypeLiteralImpl(label, new URIImpl(dataType.toString(),false));
		}
		else {
			return new PlainLiteralImpl(label);
		}
	}

	public static BlankNode toRdf2go(BNode node) {
		return node == null ? null : new BlankNodeImpl(node);
	}

	public static QueryLanguage toOpenRDFQueryLanguage(String querylanguage) throws QueryLanguageNotSupportedException {
		querylanguage = querylanguage.toLowerCase();
		if (querylanguage.equals("sparql"))
			return QueryLanguage.SPARQL;
		else if (querylanguage.equals("serql"))
			return QueryLanguage.SERQL;
		throw new QueryLanguageNotSupportedException("Query language '"+querylanguage+"' not supported. Valid values are sparql and serql.");
	}
}
