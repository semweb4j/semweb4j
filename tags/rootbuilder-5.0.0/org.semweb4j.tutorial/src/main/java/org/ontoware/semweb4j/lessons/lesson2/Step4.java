package org.ontoware.semweb4j.lessons.lesson2;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
//import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;

public class Step4 {

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
		
		// simple SPARQL SELECT
		System.out.println("Query 1:");
		// select every resorce which is tagged with tagJava
		String queryString = "SELECT ?person WHERE { ?person <"+hasTag+"> "+tagJava.toSPARQL()+" }";
		QueryResultTable results = model.sparqlSelect(queryString);
		for(QueryRow row : results) {
			System.out.println(row);
		}
		
		// another SPARQL SELECT
		System.out.println("Query 2:");
		// select resource and tag in every tagging statement 
		queryString = "SELECT ?resource ?tag WHERE { ?resource <"+hasTag+"> ?tag }";
		results = model.sparqlSelect(queryString);
		for(String var : results.getVariables()) {
			System.out.println(var);
		}
		for(QueryRow row : results) {
			System.out.println(row.getValue("resource") + " is tagged as " + row.getValue("tag"));
		}
		
		// SPARQL SELECT with typed literal
		System.out.println("Query 3:");
		// select name of all resources with integer age of 19:
		queryString = "SELECT ?name WHERE { ?resource <"+hasAge+"> \"19\"^^<"+XSD._integer+"> . ?resource <"+hasName+"> ?name }";
		//works also:
		DatatypeLiteral nineteen = model.createDatatypeLiteral("19",XSD._integer);
		String queryString2 = "SELECT ?name WHERE { ?resource <"+hasAge+"> "+nineteen.toSPARQL()+" . ?resource <"+hasName+"> ?name }";
		assert queryString.equals(queryString2);
		
		results = model.sparqlSelect(queryString);
		for(QueryRow row : results) {
			System.out.println(row);
		}
		
		// SPARQL SELECT with regex FILTER
		System.out.println("Query 4:");
		// select name and age of all resources with a name like ".*? Völkel" (case-insensitive)
		queryString = "SELECT ?name ?age WHERE { ?resource <"+hasName+"> ?name . FILTER regex(?name, \".*? Völkel\", \"i\") . ?resource <"+hasAge+"> ?age }";
		System.out.println("All results of this query formatted:");
		results = model.sparqlSelect(queryString);
		for(QueryRow row : results) {
			System.out.println(row);
		}
	}

}
