package org.ontoware.semweb4j.lessons.lesson2;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;

public class Step7 {

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
		URI hasName = model.createURI("http://xmlns.com/foaf/0.1/#term_name");
		URI hasAge = model.createURI("http://example.com/relations#age");
		hasTag = model.createURI("http://example.com/relations#hasTag");
		// tags
		PlainLiteral tagJava = model.createPlainLiteral("Java");
		PlainLiteral tagPython = model.createPlainLiteral("Python");
		PlainLiteral tagComputers = model.createPlainLiteral("Computers");
		
		// adding statements
		// naming
		model.addStatement(max, hasName, "Max Völkel");
		model.addStatement(konrad, hasName, "Konrad Völkel");
		model.addStatement(guido, hasName, "Guido van Rossum");
		model.addStatement(james, hasName, "James Gosling");
		
		// a typed property, age
		model.addStatement(konrad, hasAge, model.createDatatypeLiteral("19", XSD._integer));
		model.addStatement(max, hasAge, model.createDatatypeLiteral("29", XSD._integer));
		
		// tagging
		tag(max, tagJava);
		tag(james, tagJava);
		tag(konrad, tagJava);
		tag(konrad, tagPython);
		tag(guido, tagPython);
		
		// simple SPARQL ASK
		System.out.println("Query 1:");
		// ask if max is subject or object of any statement
		String queryString = "ASK { { "+max.toSPARQL()+" ?p ?o } UNION { ?s ?p "+max.toSPARQL()+" } }";
		boolean result = model.sparqlAsk(queryString);
		if(result) {
			System.out.println(max+" is in the graph");
		}
		
		// another SPARQL ASK
		System.out.println("Query 2:");
		// ask if any resource is tagged with tagComputers 
		queryString = "ASK { ?resource <"+hasTag+"> "+tagComputers.toSPARQL()+" }";
		if(!model.sparqlAsk(queryString)) {
			System.out.println("nothing is tagged with "+tagComputers);
		}

	}

}
