package org.ontoware.semweb4j.lessons.lesson2;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;

public class Step2 {

	private static Model model;
	private static final String FOAFNS = "http://xmlns.com/foaf/0.1/";
	
	private static void init() throws ModelRuntimeException {
		model = RDF2Go.getModelFactory().createModel();		
		model.open();
	}
	
	public static void main(String[] args) throws ModelRuntimeException, IOException {

		init();

		// creating URIs
		URI max = model.createURI("http://xam.de/foaf.rdf.xml#i");
		URI currentProject = model.createURI(FOAFNS+"#term_currentProject");
		URI name = model.createURI(FOAFNS+"#term_name");
		URI semweb4j = model.createURI("http://semweb4j.org");

		// adding statements
		model.addStatement(max, currentProject, semweb4j);
		model.addStatement(max, name, "Max Völkel");
		
		// serializing model with every syntax possible
		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		writer.write("Ntriples following:\n");
		model.writeTo( writer, Syntax.Ntriples);
		writer.write("\nRdfXml following:\n");
		model.writeTo( writer, Syntax.RdfXml);
		writer.flush();
		
		// it works also with System.out (which is an OutStream):
		writer.write("\nTurtle following:\n");
		model.writeTo( System.out, Syntax.Turtle);
		writer.write("\nTrig following:\n");
		model.writeTo( System.out, Syntax.Trig);
		//TODO add TriG output to web
		
		// because Trix is not implemented in Jena24, we don't do this:
		// model.writeTo( writer, Syntax.Trix);

		// It's also possible to serialize into a String:
		@SuppressWarnings("unused")
		String turtleSerialization = model.serialize(Syntax.Turtle);
		
	}

}
