package org.ontoware.semweb4j.lessons.lesson4;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDFS;


public class Step1 {
	
	public static void main(String[] args) throws Exception {
		// getting a ModelFactory, declaring a model 
		ModelFactory modelFactory = RDF2Go.getModelFactory();
		Model model;
		
		// enabling reasoning:
		Reasoning reasoning;
		reasoning = Reasoning.rdfs;
		model = modelFactory.createModel(reasoning);
		model.open();

		// using reasoning (here: assuming RDFS semantics)
		URI A = model.createURI("urn:A");
		URI B = model.createURI("urn:B");
		URI C = model.createURI("urn:C");
		model.addStatement(B, RDFS.subClassOf, A);
		model.addStatement(C, RDFS.subClassOf, B);
		// now let's see who is a superclass of C:
		System.out.println("All superclasses of "+C+":");
		ClosableIterator<? extends Statement> it = model.findStatements(C, RDFS.subClassOf, Variable.ANY);
		while (it.hasNext()) {
			System.out.println(it.next().getObject());
		}
		it.close();
	}

}
