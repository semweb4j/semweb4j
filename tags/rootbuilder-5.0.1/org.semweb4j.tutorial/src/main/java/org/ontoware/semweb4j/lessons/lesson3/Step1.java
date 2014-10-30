package org.ontoware.semweb4j.lessons.lesson3;

import java.util.Iterator;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;

//TODO doesn't work because I need NG4J :-/
//TODO mention this NG4J problem in web
public class Step1 {

	private static ModelSet modelSet;
	private static URI max, konrad, knows;
	
	private static void init() throws ModelRuntimeException {
		modelSet = RDF2Go.getModelFactory().createModelSet();		
		modelSet.open();
	}
	
	public static void main(String[] args) throws ModelRuntimeException {
		init();
		
		URI AURI = modelSet.createURI("http://example.com/models#A");
		URI BURI = modelSet.createURI("http://example.com/models#B");
		
		Model A = modelSet.getModel(AURI);
		Model B = modelSet.getModel(BURI);
		assert A.getContextURI() == AURI;
		assert B.getContextURI() == BURI;
		
		Model C = modelSet.getDefaultModel();
		System.out.println(C.getContextURI());
		
		System.out.println("\nAll context URIs:");
		Iterator<URI> contextIt = modelSet.getModelURIs();
		while(contextIt.hasNext()) {
			System.out.println(contextIt.next());
		}
		//there is also modelSet.getModels() 
		//TODO: does getModelURIs return the null context?
		
		defaultModels(A,B);
		modelSet.dump();
		
		//TODO explain A.isomorphicWith(Model other) in web
		//TODO explain that ModelSet implements xyz (and therefore can be serialized, ...)
		
		// set union
		System.out.println("union of A and B (A \\Cap B):");
		A.addAll(B.iterator());
		A.dump();
		defaultModels(A,B);
		
		// set complement
		System.out.println("complement of B in A (A\\B):");
		A.removeAll(B.iterator());
		A.dump();
		defaultModels(A,B);
		
		// set intersection
		System.out.println("intersection of A and B (A \\Cup B):");
		// intersection(A,B) == complement(A, complement(A,B))
		C.addAll(A.iterator());
		C.removeAll(B.iterator());
		A.removeAll(C.iterator());
		A.dump();
		defaultModels(A,B);
		
		// finding the statement "max knows konrad" in any model
		System.out.println(modelSet.findStatements(Variable.ANY, max, knows, konrad));
		
		// does our modelSet contain any statements?
		System.out.println(modelSet.containsStatements(Variable.ANY, Variable.ANY, Variable.ANY, Variable.ANY));
		modelSet.removeAll();
		System.out.println(modelSet.containsStatements(Variable.ANY, Variable.ANY, Variable.ANY, Variable.ANY));

	}

	public static void defaultModels(Model A, Model B) throws ModelRuntimeException {
		A.removeAll();
		B.removeAll();
		knows = A.createURI("http://example.com#knows");
		max = A.createURI("http://example.com#max");
		konrad = A.createURI("http://example.com#konrad");
		URI james = A.createURI("http://example.com#james");
		URI guido = A.createURI("http://example.com#guido");
		A.addStatement(max, knows, konrad);
		A.addStatement(konrad, knows, max);
		B.addStatement(max, knows, konrad);
		B.addStatement(guido, knows, james);
	}

}
