package org.ontoware.rdf2go.util;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.TriplePattern;
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
import org.ontoware.rdf2go.model.node.impl.URIImpl;


/**
 * Provides some static methods in order to create {@link RDF2Go} objects.
 * 
 * @author Laurent Pellegrino, 2009
 * 
 * @version $Id$
 */
public class Builder {
	
	private static class LazyInitializer {
		public static Model instance = org.ontoware.rdf2go.RDF2Go.getModelFactory().createModel();
	}
	
	public static BlankNode createBlankNode() {
		return LazyInitializer.instance.createBlankNode();
	}
	
	public static BlankNode createBlankNode(String value) {
		return LazyInitializer.instance.createBlankNode(value);
	}
	
	public static DatatypeLiteral createDataTypeLiteral(String arg0, URI arg1) {
		return LazyInitializer.instance.createDatatypeLiteral(arg0, arg1);
	}
	
	public static LanguageTagLiteral createLanguageTagLiteral(String arg0, String arg1) {
		return LazyInitializer.instance.createLanguageTagLiteral(arg0, arg1);
	}
	
	public static PlainLiteral createPlainliteral(String arg0) {
		return LazyInitializer.instance.createPlainLiteral(arg0);
	}
	
	public static Statement createStatement(Resource subject, URI predicate, Node object) {
		return LazyInitializer.instance.createStatement(subject, predicate, object);
	}
	
	public static TriplePattern createTriplePattern(ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) {
		return LazyInitializer.instance.createTriplePattern(subject, predicate, object);
	}
	
	public static URI createURI(String uri) {
		return LazyInitializer.instance.createURI(uri);
	}
	
	/**
	 * Parses a specified subject as String in order to create a new subject
	 * using RDF2Go types.
	 * 
	 * @param subject the subject to parse.
	 * @return the RDF2Go subject created.
	 * @throws NullPointerException if subject is null.
	 */
	public static Resource toSubject(String subject) {
		if(subject == null) {
			throw new NullPointerException("subject cannot be null");
		}
		
		Resource mySubject = null;
		
		if(subject.equals("_:")) {
			mySubject = LazyInitializer.instance.createBlankNode();
		} else if(subject.startsWith("_:")) {
			mySubject = LazyInitializer.instance.createBlankNode(subject.replaceFirst("_:", ""));
		} else {
			mySubject = LazyInitializer.instance.createURI(subject);
		}
		
		return mySubject;
	}
	
	/**
	 * Parses a specified predicate as String in order to create a new predicate
	 * using RDF2Go types.
	 * 
	 * @param predicate the predicate to parse.
	 * @return the RDF2Go predicate created.
	 * @throws NullPointerException if predicate is null.
	 */
	public static URI toPredicate(String predicate) {
		if(predicate == null) {
			throw new NullPointerException("predicate cannot be null");
		}
		
		return LazyInitializer.instance.createURI(predicate);
	}
	
	/**
	 * Parses a specified object as String in order to create a new object using
	 * RDF2Go types.
	 * 
	 * @param object the object to parse.
	 * @return the RDF2Go object created.
	 * @throws NullPointerException if object is null.
	 */
	public static Node toObject(String object) {
		if(object == null) {
			throw new NullPointerException("object cannot be null");
		}
		
		Node myObject = null;
		
		if(object.equals("_:")) {
			myObject = LazyInitializer.instance.createBlankNode();
		} else if(object.startsWith("_:")) {
			myObject = LazyInitializer.instance.createBlankNode(object.replaceFirst("_:", ""));
		} else {
			try {
				// check if a valid URI
				// openrdf URIImpl does not not work propertly in this case
				myObject = new URIImpl(object);
			} catch(Exception e) {
				// when no valid uri create plain literal
				myObject = LazyInitializer.instance.createPlainLiteral(object);
			}
		}
		
		return myObject;
	}
	
	/**
	 * Parses a single statement from a given Statement as a triple of Strings.
	 * 
	 * @param subject must be a blank node or URI.
	 * @param predicate must be a valid URI.
	 * @param object must be must be a valid URI, blank node or literal.
	 * @return a new {@link Statement}.
	 * @throws NullPointerException if triple is not valid.
	 */
	public static Statement toStatement(String subject, String predicate, String object) {
		if(subject == null || predicate == null || object == null) {
			throw new NullPointerException("subject, predicate and object cannot be null");
		}
		
		return LazyInitializer.instance.createStatement(toSubject(subject), toPredicate(predicate),
		        toObject(object));
	}
	
	public static Model getModel() {
		return LazyInitializer.instance;
	}
	
	public static void setModelFactory(String modelFactoryClass) {
		org.ontoware.rdf2go.RDF2Go.register(modelFactoryClass);
		LazyInitializer.instance = org.ontoware.rdf2go.RDF2Go.getModelFactory().createModel();
	}
	
}
