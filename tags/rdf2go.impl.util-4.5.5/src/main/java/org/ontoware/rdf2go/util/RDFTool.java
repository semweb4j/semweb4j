/*
 * LICENSE INFORMATION
 * Copyright (c) 2003 - 2007 Leo Sauermann and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */

package org.ontoware.rdf2go.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RDFTool, a helper utility to cope with RDF in general.
 * 
 * The accessor methods are also handled by the RDFReactor runtime.
 * 
 * <p>
 * This tool has been part of gnowsis.org.
 * </p>
 * 
 * @author Leo Sauermann (leo@gnowsis.com), 2003-2007
 * @author Max Völkel
 */
public class RDFTool {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(RDFTool.class);

	/**
	 * datetime format.
	 */
	private static DateFormat dateTimeFormat = null;

	/**
	 * format to express dates in ISO 8601
	 */
	private static DateFormat dateFormat = null;

	/**
	 * @param m
	 *            the model to copy
	 * @return a copy of the model in a memory model
	 */
	public static Model copyModel(Model m) {
		Model res = RDF2Go.getModelFactory().createModel();
		res.open();
		res.addAll(m.iterator());
		return res;
	}

	/**
	 * format the given date in a good date format: ISO 8601, using only the
	 * date and not the T seperator example: 2003-01-22 Timezone is ignored.
	 * 
	 * @param date
	 * @return a formatted string.
	 */
	public static String dateTime2DateString(Date date) {
		return getDateFormat().format(date);
	}

	/**
	 * format the given date in a good dateTime format: ISO 8601, using the T
	 * seperator and the - and : seperators accordingly. example:
	 * 2003-01-22T17:00:00
	 * 
	 * @param date
	 * @return a formatted string.
	 */
	public static String dateTime2String(Date date) {
		return getDateTimeFormat().format(date);
	}

	/**
	 * find the first statement that fits the passed triple pattern and return
	 * it.
	 * 
	 * @param model
	 *            the model to search on
	 * @param subject
	 *            subject
	 * @param predicate
	 *            predicate
	 * @param object
	 *            object
	 * @return a statement (the first found) or null, if nothing matched
	 * @throws RuntimeException
	 *             if the model throws an exception
	 */
	public static Statement findStatement(Model model,
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) {
		ClosableIterator<? extends Statement> i = model.findStatements(subject,
				predicate, object);
		if (i.hasNext()) {
			Statement s = i.next();
			i.close();
			return s;
		}
		// else
		return null;
	}

	/**
	 * format to express dates in ISO 8601. Timezone is ignored.
	 * 
	 * @return the DateFormat to format dates (without time) according to
	 *         ISO8601.
	 */
	public static DateFormat getDateFormat() {
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		}
		return dateFormat;
	}

	/**
	 * get a DateFormat to format dates according to ISO 8601. This ignored
	 * timezones.
	 * 
	 * @return a dateformat.
	 */
	public static DateFormat getDateTimeFormat() {
		if (dateTimeFormat == null) {
			dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		}
		return dateTimeFormat;
	}

	/**
	 * Get the Displaylabel of a Node. For resources, the "rdfs:label" property
	 * is returned, if there is none, the url is shortened to a localname. If it
	 * is a Literal, return the Lexical Form. If it is null, returns null
	 * Results may vary.
	 * 
	 * @param o
	 *            the node to check
	 * @param source
	 *            the model to ask
	 * @return a string representation of the node or null.
	 */
	public static String getGoodLabel(Node o, Model source) {
		if (o == null)
			return null;
		if (o instanceof Resource) {
			// resource
			Node rdfslabel = RDFTool.getSingleValue(source, (Resource) o,
					RDFS.label);
			if (rdfslabel != null) {
				if (rdfslabel instanceof Literal)
					return ((Literal) rdfslabel).getValue();
				// else
				return rdfslabel.toString();
			}
			// else
			return RDFTool.getShortName(o.toString());
		} else if (o instanceof Literal)
			return ((Literal) o).getValue();
		else
			// else ? well, just string it
			return o.toString();
	}

	/**
	 * Get the label of a Node. If it is a resource, return the local name (the
	 * last part of the URI). If it is a Literal, return the Lexical Form (the
	 * value). If it is <code>null</code>, returns <code>null</code>.
	 * 
	 * @param o
	 *            the node to check
	 * @return a string representation of the node or <code>null</code>.
	 */
	public static String getLabel(Node o) {
		if (o == null)
			return null;
		if (o instanceof Resource) {
			// resource
			return getShortName(o.toString());
		} else if (o instanceof Literal)
			// Literal
			return ((Literal) o).getValue();
		// else ? well, just string it
		return o.toString();
	}

	/**
	 * The passed uri identifies something on the web, probably a namespace. To
	 * shorten this, parse the url for something like a localname. Returns the
	 * last string after a '#' or a '/'.
	 * 
	 * @param uri
	 *            a URI
	 * @return a short name for it, for display.
	 */
	public static String getShortName(String uri) {
		if (uri.indexOf('#') > 0)
			uri = uri.substring(uri.lastIndexOf('#') + 1);
		else if (uri.indexOf('/') > 0)
			uri = uri.substring(uri.lastIndexOf('/') + 1);
		return uri;
	}

	/**
	 * get the property pred of the resource res. If there is none, return null.
	 * If there are mutliple, return any.
	 * 
	 * @param m
	 *            the model to read from
	 * @param res
	 *            the resource
	 * @param pred
	 *            the predicate to read
	 * @return the value or null
	 */
	public static Node getSingleValue(Model m, Resource res, URI pred) {
		ClosableIterator<? extends Statement> i = m.findStatements(res, pred,
				Variable.ANY);
		try {
			if (i.hasNext()) {
				return i.next().getObject();
			} 
			//else
			return null;
		} finally {
			i.close();
		}
	}

	/**
	 * read the values of a predicate of a resource. If a value exists, return a
	 * string representation of it. When multiple triples with this
	 * subject/predicate exist, choose one at random.
	 * 
	 * @param m
	 *            the model to read from
	 * @param res
	 *            the resource
	 * @param pred
	 *            the predicate to read
	 * @return a string representation of the value, or null. Literals are
	 *         returned using their Value (not toString()).
	 */
	public static String getSingleValueString(Model m, Resource res, URI pred) {
		Node n = getSingleValue(m, res, pred);
		if (n == null)
			return null;

		if (n instanceof Literal)
			// Literal
			return ((Literal) n).getValue();
		return n.toString();
	}

	/**
	 * guess the RDF syntax of a filename inspired by
	 * com.hp.hpl.jena.graph.impl.FileGraph#guessLang with the addition of
	 * toLowerCase
	 * 
	 * @param filenname
	 *            the filename, we will look at the suffix after "."
	 * @return the guessed RDF syntax, fallback is RDF/XML
	 */
	public static Syntax guessSyntax(String filenname) {
		String suffix = filenname.substring(filenname.lastIndexOf('.') + 1);
		if (suffix != null) {
			suffix = suffix.toLowerCase();

			if (suffix.equals("n3"))
				return Syntax.Turtle;
			else if (suffix.equals("nt"))
				return Syntax.Ntriples;
			else if (suffix.equals("trig")) {
				return Syntax.Trig;
			} else if (suffix.equals("trix"))
				return Syntax.Trix;
		}
		return Syntax.RdfXml;
	}

	/**
	 * convert a model to a string RDF/XML for serialization
	 * 
	 * @param model
	 *            the model to convert
	 * @return
	 */
	public static String modelToString(Model model) {
		return modelToString(model, Syntax.RdfXml);
	}

	/**
	 * convert a model to a string for serialization
	 * 
	 * @param model
	 *            the model to convert
	 * @param syntax
	 *            the syntax to use
	 * @return a string of this model, according to the passed syntax
	 */
	public static String modelToString(Model model, Syntax syntax) {
		StringWriter buffer = new StringWriter();
		try {
			model.writeTo(buffer, syntax);
		} catch (Exception e) {
			throw new ModelRuntimeException(e);
		}
		return buffer.toString();
	}

	/**
	 * set the property pred of the resource res. If it exists, change it. If it
	 * not exists, create it. If the passed value is null, delete the statement
	 * 
	 * @param m
	 *            the model to manipulate
	 * @param res
	 *            the rsource
	 * @param pred
	 *            the predicate to set
	 * @param value
	 *            a value or null
	 */
	public static void setSingleValue(Model m, Resource res, URI pred,
			Node value) {
		m.removeStatements(res, pred, Variable.ANY);
		if (value != null) {
			m.addStatement(res, pred, value);
		}
	}

	/**
	 * set the property pred of the resource res. If it exists, change it. If it
	 * not exists, create it. If value is null, delete the triple.
	 * 
	 * @param m
	 *            the model to manipulate
	 * @param res
	 *            the rsource
	 * @param pred
	 *            the predicate to set
	 * @param value
	 *            a string or null
	 */
	public static void setSingleValue(Model m, Resource res, URI pred,
			String value) {
		m.removeStatements(res, pred, Variable.ANY);
		if (value != null) {
			m.addStatement(res, pred, value);
		}
	}

	/**
	 * compute the sha1sum of a string (useful for handling FOAF data).
	 * 
	 * @param data
	 *            the string to parse
	 * @return the sha1sum as string.
	 */
	public static String sha1sum(String data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		byte[] digest = md.digest(data.getBytes());

		StringBuffer res = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			int digByte = digest[i] & 0xFF;
			if (digByte < 0x10)
				res.append('0');
			res.append(Integer.toHexString(digByte));
		}

		return res.toString();
	}

	/**
	 * try to get a date out of a string. If this works, return it, otherwise
	 * return null. Btw: the namespace of dateTime is
	 * http://www.w3.org/2001/XMLSchema#dateTime Timezone is ignored.
	 * 
	 * @param isodate
	 *            the XSD date as string.
	 * @return a parsed date or null, if this breaks
	 */
	public static Date string2Date(String isodate) {
		try {
			return getDateTimeFormat().parse(isodate);
		} catch (ParseException e) {
			try {
				return getDateFormat().parse(isodate);
			} catch (ParseException e1) {
				return null;
			}
		}

	}

	/**
	 * format the given date in a good date format: ISO 8601, using only the
	 * date and not the T seperator example: 2003-01-22 This ignores timezones.
	 * 
	 * @param date
	 *            the date-string to parse
	 * @return a formatted string.
	 */
	public static Date string2DateTime(String date) throws ParseException {
		return getDateFormat().parse(date);
	}

	/**
	 * convenience function to create a memModel from an RDF/XML-ABBREV stream
	 * 
	 * @param rdfxml
	 *            the serialized form of the model
	 * @return a Model
	 */
	public static Model stringToModel(String rdfxml) {
		return stringToModel(rdfxml, Syntax.RdfXml);
	}

	/**
	 * convenience function to create a memModel from a string
	 * 
	 * @param string
	 *            the string with the serialized model
	 * @param syntax
	 *            the syntax to use
	 * @return the model that was serialised.
	 */
	public static Model stringToModel(String string, Syntax syntax) {
		Model m = RDF2Go.getModelFactory().createModel();
		m.open();
		StringReader s = new StringReader(string);
		try {
			m.readFrom(s, syntax);
			return m;
		} catch (Exception e) {
			throw new ModelRuntimeException(e);
		} finally {
			s.close();
		}
	}
}
