package org.ontoware.semweb4j.lessons.lesson1;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;

public class Step1 {

	//TODO check if ModelRuntimeException is used in the web tutorial everywhere
	public static void main(String[] args) throws ModelRuntimeException {

		// getting model factory
		ModelFactory modelFactory = RDF2Go.getModelFactory();
		
		// getting model
		Model model = modelFactory.createModel();
		model.open();
		assert model.isOpen();

		// creating URIs
		String foafURI = "http://xmlns.com/foaf/0.1/";
		URI max = model.createURI("http://xam.de/foaf.rdf.xml#i");
		URI currentProject = model.createURI(foafURI + "#term_currentProject");
		URI name = model.createURI(foafURI + "#term_name");
		URI semweb4j = model.createURI("http://semweb4j.org");

		// adding a statement to the model
		model.addStatement(max, currentProject, semweb4j);
		model.addStatement(max, name, "Max Völkel");

		// dumping model to the screen
		model.dump();

		// removing a statement from the model
		model.removeStatement(max, currentProject, semweb4j);

		// dumping model to the screen
		model.dump();

	}

}
