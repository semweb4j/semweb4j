package org.ontoware.semversion.example;

import java.io.File;

import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.semversion.SemVersion;
import org.ontoware.semversion.Session;
import org.ontoware.semversion.Version;
import org.ontoware.semversion.VersionedModel;

public class SimpleExample {

	@Test
	public void testSimpleExample() throws Exception {

		// prepare server
		SemVersion semVersion = new SemVersion();
		// initialise system
		semVersion.startup(new File("./target/test/SimpleExample"));
		semVersion.deleteStore();

		semVersion.createUser("admin", "password");
		semVersion.createUser("tom", "password");
		semVersion.createUser("joe", "password");

		// prepare versioned model
		Session adminSession = semVersion.login("admin", "password");
		adminSession.createVersionedModel(new URIImpl("vm://1"),
				"Gene Ontology");
		adminSession.close();

		// use it
		Session userSession = semVersion.login("tom", "password");
		VersionedModel vm = userSession.getVersionedModel("Gene Ontology");
		Model myFirstModel = userSession.getModel();
		URI tool = new URIImpl("http://example.com/#Tool");
		myFirstModel.addStatement(new URIImpl
				("http://semversion.ontoware.org"), RDF.type, tool);
		vm.commitRoot(myFirstModel, "version1");
		userSession.close();

		// suggest to add a label
		userSession = semVersion.login("joe", "password");
		Version root = userSession.getVersionedModel("Gene Ontology").getRoot();
		root.setComment("root version");
		Model rootModel = root.getContent();
		rootModel.addStatement(
				new URIImpl("http://semversion.ontoware.org"), RDFS.label,
				"rdf-based versioning tool");
		root.commit(rootModel, "version2", true);
		// root.commit(rootModel, "version2", URIUtils.createURI("version://2"),
		// new ValidTime(), VersionImpl.NO_PROVENANCE, true);

		semVersion.getSemVersionImpl().getTripleStore().dump();

		// free resources
		semVersion.shutdown();
	}

}
