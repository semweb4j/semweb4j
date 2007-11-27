package org.ontoware.semversion.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.TriplePattern;
import org.ontoware.rdf2go.model.impl.DelegatingModel;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;

/**
 * Wraps a model into a read-only model
 * 
 * @author voelkel
 * 
 */
public class ReadOnlyModelLayer extends DelegatingModel implements Model {

	public ReadOnlyModelLayer(Model model) {
		super(model);
	}

	public void setProperty(URI propertyURI, Object value) {
		throw new UnsupportedOperationException("This is a read-only model");
	}

	public BlankNode createBlankNode() {
		throw new UnsupportedOperationException("This is a read-only model");
	}

	public DatatypeLiteral createDatatypeLiteral(String literal, URI datatypeURI)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");
	}

	public LanguageTagLiteral createLanguageTagLiteral(String literal,
			String langugeTag) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");
	}

	public PlainLiteral createPlainLiteral(String literal) {
		throw new UnsupportedOperationException("This is a read-only model");
	}

	public Statement createStatement(Resource subject, URI predicate,
			Node object) {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public URI createURI(String uriString) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public URI newRandomUniqueURI() {
		throw new UnsupportedOperationException("This is a read-only model");
	}

	public void removeAll() throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");
	}

	public void removeAll(Iterator<? extends Statement> statements)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");
	}

	public void removeStatement(Statement statement)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void removeStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void removeStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void removeStatement(Resource subject, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void removeStatement(Resource subject, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void removeStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void removeStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void update(Diff diff) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void addAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void addStatement(Statement statement) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void addStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void addStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void addStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void removeStatements(TriplePattern triplePattern)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void removeStatements(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public TriplePattern createTriplePattern(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object) {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void readFrom(Reader in) throws IOException, ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void readFrom(InputStream in) throws IOException,
			ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void readFrom(Reader in, Syntax syntax) throws IOException,
			ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

	public void readFrom(InputStream reader, Syntax syntax) throws IOException,
			ModelRuntimeException {
		throw new UnsupportedOperationException("This is a read-only model");

	}

}
