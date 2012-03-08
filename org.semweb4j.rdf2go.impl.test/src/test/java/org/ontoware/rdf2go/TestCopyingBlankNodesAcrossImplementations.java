package org.ontoware.rdf2go;

// import org.junit.Test;
// import org.ontoware.aifbcommons.collection.ClosableIterator;
// import org.ontoware.rdf2go.impl.jena27.ModelFactoryImpl;
// import org.ontoware.rdf2go.model.Model;
// import org.ontoware.rdf2go.model.Statement;
// import org.ontoware.rdf2go.model.node.BlankNode;
// import org.ontoware.rdf2go.model.node.URI;
// import org.openrdf.rdf2go.RepositoryModelFactory;

public class TestCopyingBlankNodesAcrossImplementations {
	
	// @Test
	// public void testSesame2Jena() {
	// ModelFactory sesame = new RepositoryModelFactory();
	// Model sesameModel = sesame.createModel();
	// sesameModel.open();
	//
	// ModelFactory jena = new ModelFactoryImpl();
	// Model jenaModel = jena.createModel();
	// jenaModel.open();
	//
	// BlankNode sBnode1 = sesameModel.createBlankNode();
	// BlankNode sBnode2 = sesameModel.createBlankNode();
	// URI hasId = sesameModel.createURI("urn:test:hasId");
	// URI p = sesameModel.createURI("urn:test:p");
	//
	// sesameModel.addStatement(sBnode1, hasId, "1");
	// sesameModel.addStatement(sBnode2, hasId, "2");
	// sesameModel.addStatement(sBnode1, p, sBnode2);
	//
	// ClosableIterator<Statement> it = sesameModel.iterator();
	// jenaModel.addAll(it);
	// it.close();
	//
	// jenaModel.dump();
	// }
	//
	// @Test
	// public void testJena2Sesame() {
	// ModelFactory sesame = new RepositoryModelFactory();
	// Model sesameModel = sesame.createModel();
	// sesameModel.open();
	//
	// ModelFactory jena = new ModelFactoryImpl();
	// Model jenaModel = jena.createModel();
	// jenaModel.open();
	//
	// BlankNode sBnode1 = jenaModel.createBlankNode();
	// BlankNode sBnode2 = jenaModel.createBlankNode();
	// URI hasId = jenaModel.createURI("urn:test:hasId");
	// URI p = jenaModel.createURI("urn:test:p");
	//
	// jenaModel.addStatement(sBnode1, hasId, "1");
	// jenaModel.addStatement(sBnode2, hasId, "2");
	// jenaModel.addStatement(sBnode1, p, sBnode2);
	//
	// ClosableIterator<Statement> it = jenaModel.iterator();
	// sesameModel.addAll(it);
	// it.close();
	//
	// sesameModel.dump();
	// }
	
}
