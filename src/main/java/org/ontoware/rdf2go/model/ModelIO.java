/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;

public interface ModelIO {

	// /////////////////
	// IO

	/**
	 * Read from Reader assuming in UTF8 encoding. Models are read with a
	 * default syntax of RDF/XML
	 * 
	 * @param in
	 *            the input to read
	 * @throws IOException
	 *             on IOErrors
	 * @throws ModelRuntimeException
	 *             on RDF serialization errors or model errors
	 */
	void readFrom(Reader in) throws IOException, ModelRuntimeException;

	/**
	 * Reads assuming the given syntax. Encoding defaults to UTF8. When reading
	 * TRiX into a Model, the context URI is ignored.
	 * 
	 * @param in
	 *            the input to read
	 * @param syntax
	 *            syntax to use
	 * @throws IOException
	 *             on IOErrors
	 * @throws ModelRuntimeException
	 *             on RDF serialization errors or model errors
	 */
	void readFrom(Reader in, Syntax syntax) throws IOException,
			ModelRuntimeException;

	/**
	 * Reads RDF data from the given {@link Reader} in the given {@link Syntax}.
	 * The baseURI is used to dereference URIs with the syntax ":name".
	 * 
	 * @param in
	 * @param syntax
	 * @param baseURI
	 * @throws IOException
	 * @throws ModelRuntimeException
	 */
	void readFrom(Reader in, Syntax syntax, String baseURI) throws IOException,
			ModelRuntimeException;

	/**
	 * Read from InputStream assuming to read an RDF/XML stream.
	 * 
	 * @param in
	 *            the input to read
	 * @throws IOException
	 *             on IOErrors
	 * @throws ModelRuntimeException
	 *             on RDF serialization errors or model errors
	 */
	void readFrom(InputStream in) throws IOException, ModelRuntimeException;

	/**
	 * Reads assuming the given syntax. Encoding defaults to UTF8.
	 * 
	 * @param in
	 *            the input to read
	 * @param syntax
	 *            syntax to use
	 * @throws IOException
	 *             on IOErrors
	 * @throws ModelRuntimeException
	 *             on RDF serialization errors or model errors
	 */
	void readFrom(InputStream reader, Syntax syntax) throws IOException,
			ModelRuntimeException;

	/**
	 * Reads RDF data from the given {@link InputStream} in the given {@link Syntax}.
	 * The baseURI is used to dereference URIs with the syntax ":name".
	 * 
	 * @param in
	 * @param syntax
	 * @param baseURI
	 * @throws IOException
	 * @throws ModelRuntimeException
	 */
	void readFrom(InputStream in, Syntax syntax, String baseURI)
			throws IOException, ModelRuntimeException;

	/**
	 * Writing an RDF/XML stream in UTF8 encoding
	 * 
	 * @param out
	 *            the output to write to
	 * @throws IOException
	 *             on IOErrors
	 * @throws ModelRuntimeException
	 *             on RDF serialization errors or model errors
	 */
	void writeTo(Writer out) throws IOException, ModelRuntimeException;

	/**
	 * Write the model to the passed writer, using the passed syntax.
	 * 
	 * @param out
	 *            the output to write to
	 * @param syntax
	 *            syntax to use
	 * @throws IOException
	 *             on IOErrors
	 * @throws ModelRuntimeException
	 *             on RDF serialization errors or model errors
	 */
	void writeTo(Writer out, Syntax syntax) throws IOException,
			ModelRuntimeException;

	/**
	 * Writing an RDF/XML stream in UTF8 encoding
	 * 
	 * @param out
	 *            the output to write to
	 * @throws IOException
	 *             on IOErrors
	 * @throws ModelRuntimeException
	 *             on RDF serialization errors or model errors
	 */
	void writeTo(OutputStream out) throws IOException, ModelRuntimeException;

	/**
	 * Write the model to the passed writer, using the passed syntax.
	 * 
	 * @param out
	 *            the output to write to
	 * @param syntax
	 *            syntax to use
	 * @throws IOException
	 *             on IOErrors
	 * @throws ModelRuntimeException
	 *             on RDF serialization errors or model errors
	 */
	void writeTo(OutputStream out, Syntax syntax) throws IOException,
			ModelRuntimeException;

	/**
	 * Convenience method to export a Model to a String in a given syntax.
	 * 
	 * @param syntax
	 * @return a String, containing the Model content in the given syntax.
	 * @throws SyntaxNotSupportedException
	 *             if the syntax is not supported
	 */
	String serialize(Syntax syntax) throws SyntaxNotSupportedException;

}
