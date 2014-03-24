package org.ontoware.semweb4j.lessons.lesson1;

import java.util.ArrayList;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;

public class Step2 {

	private static Model model;
	
	private static void init() throws ModelRuntimeException {
		// specify to use Jena here:
		RDF2Go.register( new org.ontoware.rdf2go.impl.jena24.ModelFactoryImpl() );
		// if not specified, RDF2Go.getModelFactory() looks into your classpath for ModelFactoryImpls to register.
		
		model = RDF2Go.getModelFactory().createModel();		
		model.open();
	}
	
	public static void main(String[] args) throws ModelRuntimeException {

		init();
		
		// creating resources
		String foafURI = "http://xmlns.com/foaf/0.1/";
		URI max = model.createURI("http://xam.de/foaf.rdf.xml#i");
		URI name = model.createURI(foafURI+"#term_name");
		URI icqId = model.createURI(foafURI+"#term_icqChatID");
		URI typeInteger = XSD._integer; 
		BlankNode konrad = model.createBlankNode();
		PlainLiteral maxNameAsPlainLiteral = model.createPlainLiteral("Max Völkel");
		DatatypeLiteral number = model.createDatatypeLiteral("123", typeInteger);
		LanguageTagLiteral konradNameEnglish = model.createLanguageTagLiteral("Konrad Voelkel", "en");
		LanguageTagLiteral konradNameGerman = model.createLanguageTagLiteral("Konrad Völkel", "de");

		// adding statements to the model
		
		// adding a bnode statement
		model.addStatement(konrad, name, "Konrad");
		
		// adding a statement with a DatatypeLiteral
		model.addStatement(konrad, icqId, number);
		
		// adding a Statement object
		Statement maxNameStatement = model.createStatement(max, name, maxNameAsPlainLiteral);
		model.addStatement(maxNameStatement);
		
		// adding a set of Statement objects
		Statement konradNameEnglishStatement = model.createStatement(konrad, name, konradNameEnglish);
		Statement konradNameGermanStatement = model.createStatement(konrad, name, konradNameGerman);
		ArrayList<Statement> konradNameStatements = new ArrayList<Statement>();
		konradNameStatements.add(konradNameGermanStatement);
		konradNameStatements.add(konradNameEnglishStatement);
		model.addAll(konradNameStatements.iterator());
		
		// dumping model to the screen
		model.dump();

	}

}
