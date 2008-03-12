package org.ontoware.semversion;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;


public class ThreadDeleteTest {

	private SemVersion semVersion;
	
	@Before
	public void setUp() throws Exception {
		semVersion = new SemVersion();
		semVersion.startup( new File("./target/test/ThreadDelete"));
		semVersion.deleteStore();
		semVersion.createUser("User", "secret");
		semVersion.createUser("Admin", "secret");
		
		Session session = semVersion.login("Admin", "secret");
		VersionedModel vm = session.createVersionedModel("NewThread");
		
		Model model = session.getModel();
		model.addStatement("http://first", new URIImpl("http://test/uri"), "http://triple");
		model.addStatement("http://second", new URIImpl("http://test/uri"), "http://triple");
		vm.commitRoot(model, "FirstMainVersion");
		session.close();
	}

	@After
	public void tearDown() throws Exception {
		semVersion.shutdown();
	}

	@Test
	public void test() throws Exception {
		Session session = semVersion.login("User", "secret");
		VersionedModel vm = session.getVersionedModel("NewThread");

		assertNotNull(vm);
		
		vm.delete();
		
		vm = session.getVersionedModel("NewThread");
		assertNull(vm);            
	}
        

}