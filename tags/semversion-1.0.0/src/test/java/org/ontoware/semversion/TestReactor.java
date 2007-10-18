package org.ontoware.semversion;

import java.io.File;
import java.util.Calendar;

import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.semversion.impl.SemVersionImpl;
import org.ontoware.semversion.impl.UserImpl;

public class TestReactor {

	/**
	 * @param args
	 * @throws Exception
	 * @throws MappingException
	 */
	@Test
	public void testReactorParts() throws Exception {

		SemVersionImpl sv = new SemVersionImpl();
		sv.startup(new File("./target/TestReactor"));
		Model mainModel = sv.getMainModel();

		User bob = new UserImpl(mainModel, new URIImpl("user://bob"), "bob", "secret");
		User goconsortium = new UserImpl(mainModel, new URIImpl("user://go-sonsortium"), "go",
				"secret");

		VersionedModel vm1 = new VersionedModel(mainModel, new URIImpl("data://vm1"),true);
		vm1.setLabel("Gene Ontology");

		Version version1 = new Version(mainModel, new URIImpl("data://version1"), true);
		version1.setLabel("initial version");
		version1.setUser(goconsortium);
		version1.setCreationTime(Calendar.getInstance());
		vm1.addVersion(version1);

		Version version1_1 = new Version(mainModel, new URIImpl("data://version1-1"), true);
		version1_1.setLabel("2nd version");
		version1_1.setUser(bob);
		version1_1.setCreationTime(Calendar.getInstance());
		version1_1.setFirstParent(version1);

		mainModel.dump();
	}

}
