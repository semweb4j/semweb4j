package org.ontoware.semversion.usecase.protege;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.semversion.SemVersion;
import org.ontoware.semversion.Session;
import org.ontoware.semversion.VersionedModel;

/**
 * This demonstrates some features of the SemVersion API
 * 
 * @author voelkel
 * 
 */
public class TudorExample {

	/**
	 * Importing and retrieving a model from the storage. Here is a short RDFS
	 * file. Please take some time and write me exactly how are you ?importing?
	 * by using addStatement(). The question is not because I don?t know how to
	 * use the method, but to eliminate ambiguity in my head for when I?m
	 * supposed to extract the statements in order to create the visualization
	 * clusters.
	 * @throws Exception 
	 */
	@Test
	public void readModel() throws Exception {
		// set up semversion
		SemVersion sv = new SemVersion();
		// tell semversion where the storage directory for sesame is
		sv.startup( new File("./target/tudor"));
		// KILL ALL DATA
		sv.deleteStore();
		sv.createUser("tudor","secret");
		Session session = sv.login("tudor","secret");
		VersionedModel vm = session.createVersionedModel("Tudor Ontology");
		assert vm != null;
		
		// get an empty model in semversion
		Model myModel = session.getModel();
		assert myModel != null;
		// read into semversions model
		myModel.readFrom( new FileInputStream(new File("./src/test/resources/tudor.rdf.xml")));

		// add model to semversion
		vm.commitRoot( myModel, "GO v0.1" );
		
		// dump the repository
		sv.dump();

	}
	
}
