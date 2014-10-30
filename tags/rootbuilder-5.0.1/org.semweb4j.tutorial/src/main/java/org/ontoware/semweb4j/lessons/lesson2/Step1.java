package org.ontoware.semweb4j.lessons.lesson2;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;

public class Step1 {

	private static Model model;
	private static final String FOAFNS = "http://xmlns.com/foaf/0.1/";
	
	private static void init() throws ModelRuntimeException {
		// getting model
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
		
		// serializing model to model.rdf
		model.writeTo( new FileWriter("model.rdf") );
		
		// removing statements
		model.removeAll();
		
		// proving the model has no more statements
		assert model.size() == 0 : "model is empty after removal of all statements";
		model.dump();
		
		// reading model from model.rdf
		model.readFrom( new FileReader("model.rdf") );
		
		// proving the model has been read
		assert model.size() > 0 : "model contains statements after reading a serialized model";
		model.dump();

	}

}
