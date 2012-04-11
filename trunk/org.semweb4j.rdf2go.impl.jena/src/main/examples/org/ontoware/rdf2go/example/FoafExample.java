package org.ontoware.rdf2go.example;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.impl.jena26.DumpUtils;
import org.ontoware.rdf2go.impl.jena26.ModelImplJena26;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;


/**
 * This is a simple example of how to use RDF2GO to create some statments about
 * persons using the FOAF vocabulary.
 * 
 * It shows
 * <ul>
 * <li>how to create a model</li>
 * <li>how to add some statements to the model</li>
 * <li>how to query the model
 * <li>
 * </ul>
 * 
 * @author mvo (Max V�lkel), wth (Werner Thiemann)
 * 
 */
public class FoafExample {
	
	/**
	 * Create a foaf file
	 * 
	 * @param args None
	 */
	public static void main(String[] args) {
		
		/*
		 * first of all we have to instantiate a RDF2GO model with an underlying
		 * framework here we choose Jena
		 */

		// no inferencing
		Model model = new ModelImplJena26(Reasoning.none);
		// alternatives
		// new ModelImplYars();
		// new ModelImplNG4J();
		
		/*
		 * before we can do anything useful, we have to define the uris we want
		 * to use
		 */
		// use the uris defined by foaf
		URI foafName = model.createURI("http://xmlns.com/foaf/0.1/name");
		URI foafPerson = model.createURI("http://xmlns.com/foaf/0.1/Person");
		URI foafTitle = model.createURI("http://xmlns.com/foaf/0.1/title");
		URI foafKnows = model.createURI("http://xmlns.com/foaf/0.1/knows");
		URI foafHomepage = model.createURI("http://xmlns.com/foaf/0.1/homepage");
		// use a blank node for the person
		BlankNode werner = model.createBlankNode();
		
		/*
		 * now we can add statements to the model (for easier reading we
		 * replaced the blank nodes cryptical letters with a human readable
		 * version - you will see something different when exectuing this
		 * example
		 */
		// _:blankNodeWerner
		// <http://xmlns.com/foaf/0.1/homepage>
		// <http://www.blue-agents.com> .
		model.addStatement(werner, foafHomepage, model.createURI("http://www.blue-agents.com"));
		// _:blankNodeWerner
		// <http://xmlns.com/foaf/0.1/title>
		// "Mr" .
		model.addStatement(werner, foafTitle, "Mr");
		// _:blankNodeWerner
		// <http://xmlns.com/foaf/0.1/name>
		// "Werner Thiemann" .
		model.addStatement(werner, foafName, "Werner Thiemann");
		
		// _:blankNodeWerner
		// <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
		// <http://xmlns.com/foaf/0.1/Person> .
		model.addStatement(werner, RDF.type, foafPerson);
		
		BlankNode max = model.createBlankNode();
		// _:blankNodeMax
		// <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
		// <http://xmlns.com/foaf/0.1/Person> .
		model.addStatement(max, RDF.type, foafPerson);
		// _:blankNodeMax
		// <http://xmlns.com/foaf/0.1/name>
		// "Max Voelkel" .
		model.addStatement(max, foafName, "Max V�lkel");
		// _:blankNodeMax
		// <http://www.w3.org/2000/01/rdf-schema#seeAlso>
		// <http://www.aifb.uni-karlsruhe.de/WBS/mvo/foaf.rdf.xml> .
		model.addStatement(max, RDFS.seeAlso, model.createURI("http://www.xam.de/foaf.rdf.xml"));
		
		// link via foaf:knows
		// _:blankNodeWerner
		// <http://xmlns.com/foaf/0.1/knows>
		// _:blankNodeMax.
		model.addStatement(werner, foafKnows, max);
		
		// default dump
		DumpUtils.dump(model, null);
		
		// queries
		// get all persons
		
		ClosableIterator<? extends Statement> it = model.findStatements(Variable.ANY, RDF.type,
		        foafPerson);
		while(it.hasNext()) {
			Resource person = it.next().getSubject();
			System.out.println(person + " is a person");
			
			// get foaf:name
			ClosableIterator<? extends Statement> it2 = model.findStatements(person, foafName,
			        Variable.ANY);
			while(it2.hasNext()) {
				System.out.println(person + " has the foaf:name " + it2.next().getObject());
			}
		}
	}
	
}
