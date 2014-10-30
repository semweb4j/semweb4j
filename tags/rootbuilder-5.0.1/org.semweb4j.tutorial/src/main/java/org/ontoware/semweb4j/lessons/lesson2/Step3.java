package org.ontoware.semweb4j.lessons.lesson2;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.TriplePattern;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;

public class Step3 {

	private static Model model;
	private static URI hasTag;
	
	private static void init() throws ModelRuntimeException {
		model = RDF2Go.getModelFactory().createModel();		
		model.open();
	}
	
	public static void tag(Resource resource, Node tag) throws ModelRuntimeException {
		model.addStatement(resource, hasTag, tag);
	}
	
	public static void main(String[] args) throws ModelRuntimeException {

		init();
		
		// creating URIs
		// persons
		URI max = model.createURI("http://xam.de/foaf.rdf.xml#i");
		URI konrad = model.createURI("http://example.com/persons#konrad");
		URI guido = model.createURI("http://example.com/persons#guido");
		URI james = model.createURI("http://example.com/persons#james");
		// relations
		hasTag = model.createURI("http://example.com/relations#hasTag");
		// tags
		PlainLiteral tagJava = model.createPlainLiteral("Java");
		PlainLiteral tagPython = model.createPlainLiteral("Python");
		
		// adding statements
		// tagging
		tag(max, tagJava);
		tag(james, tagJava);
		tag(konrad, tagJava);
		tag(konrad, tagPython);
		tag(guido, tagPython);
		
		// finding statements 'tagged with "Java"'
		ClosableIterator<? extends Statement> foundTaggedJava;
		foundTaggedJava = model.findStatements(Variable.ANY, hasTag, tagJava);
		System.out.println("Everything tagged 'Java':");
		while (foundTaggedJava.hasNext()) {
			System.out.println(foundTaggedJava.next().getSubject());
		}
		foundTaggedJava.close();
		
		// finding statements 'tagged with "Python"' with a triple pattern
		TriplePattern taggedAsPythonPattern = model.createTriplePattern(Variable.ANY, hasTag, tagPython);
		ClosableIterator<? extends Statement> foundTaggedPython;
		foundTaggedPython = model.findStatements(taggedAsPythonPattern);
		System.out.println("Everything tagged 'Python':");
		while (foundTaggedPython.hasNext()) {
			System.out.println(foundTaggedPython.next().getSubject());
		}
		foundTaggedPython.close();

	}

}
