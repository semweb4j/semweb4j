package org.ontoware.semversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.semversion.impl.BlankNodeEnrichmentModel;

/**
 * 
 * branch - to tylko etykieta dla zbioru wersji, nie ma wlasciwie takiego
 * obiektu w systemie jak branch poza zwyklym stringiem jako property dla
 * ontologii
 * 
 * root (model) - branch (model) - branch (model)
 */
public class MarcOntTests {

	private SemVersion semVersion;

	URI s1 = new URIImpl("urn:test:s1");
	URI s2 = new URIImpl("urn:test:s2");
	URI s3 = new URIImpl("urn:test:s3");
	URI s4 = new URIImpl("urn:test:s4");
	URI s5 = new URIImpl("urn:test:s5");
	URI s6 = new URIImpl("urn:test:s6");
	URI p = new URIImpl("urn:test:p");
	URI o = new URIImpl("urn:test:o");

	/**
	 * Create versionedmodel "NewThread", create a new model with two triples,
	 * commit it as root
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		semVersion = new SemVersion();
		semVersion.startup(new File("./target/tmp/MarcOntUseCase"));
		semVersion.deleteStore();
		semVersion.createUser("User", "secret");
		semVersion.createUser("Admin", "secret");

		Session session = semVersion.login("Admin", "secret");
		VersionedModel vm = session.createVersionedModel("NewThread");

		Model model = session.getModel();
		model.addStatement(s1, p, o);
		model.addStatement(s2, p, o);
		assertEquals(2, model.size());
		vm.commitRoot(model, "FirstMainVersion");
		session.close();
	}

	@After
	public void tearDown() throws Exception {
		semVersion.shutdown();
	}

	@Test
	public void testModel() throws LoginException {
		Session session = semVersion.login("User", "secret");

		Model model = session.getModel();

		Statement s = model.createStatement(s1, p, o);
		model.addStatement(s);
		assertTrue(model.contains(s1, p, o));
		model.removeStatement(s);

		model.addStatement(s1, p, o);
		assertTrue(model.contains(s1, p, o));

		model.addStatement(s2, p, o);
		assertTrue(model.contains(s2, p, o));

		assertEquals(2, model.size());
		session.close();
	}

	/**
	 * This one works fine
	 */
	@Test
	public void addingFewSuggestions() throws LoginException {

		Session session = semVersion.login("User", "secret");
		VersionedModel vm = session.getVersionedModel("NewThread");

		Version v = vm.getRoot();
		Model suggestion1 = v.getContent();
		assertEquals(2, suggestion1.size());
		assertTrue(suggestion1.contains(s1, p, o));
		assertTrue(suggestion1.contains(s2, p, o));
		suggestion1.addStatement(s3, p, o);
		assertTrue(suggestion1.contains(s3, p, o));

		// commit s1 as a suggestion to root
		// result: root > suggestion1
		v.commit(suggestion1, "first sug. comment", true);

		Model suggestion2 = v.getContent();
		assertEquals(2, suggestion2.size());
		suggestion2.addStatement(s4, p, o);
		v.commit(suggestion2, "second sug. comment", true);
		// result: root has two children > suggestion1, suggestion2

		Model suggestion3 = v.getContent();
		suggestion3.addStatement(s5, p, o);
		v.commit(suggestion3, "third sug. comment", true);
		// result: root has three children > suggestion1, suggestion2,
		// suggestion3

		Model suggestion4 = v.getContent();
		suggestion4.addStatement(s6, p, o);
		v.commit(suggestion4, "fourth sug. comment", true);
		// result: root has four children > suggestion1, suggestion2,
		// suggestion3, suggestion4

		System.out.println(vm.getAllBranches().size());
		System.out.println(vm.getAllVersions().size());

		assertTrue(vm.getAllBranches().size() == 1);
		assertTrue(vm.getAllVersions().size() == 5);

		// semVersion.dump();

		session.close();
	}

	/**
	 * Here I add to a root model: 1) One suggestion 2) One sub-version 3)
	 * Second suggestion - and I get exception - why?
	 * 
	 * I assume that one version can have n suggestions but only 1 sub-version
	 * and after adding this sub-version I can't add anything more to the parent
	 * node - suggestion, nor sub-version. Am I right?
	 * 
	 * @throws LoginException
	 */
	@Test
	public void addingFewVersions() throws LoginException {

		Session session = semVersion.login("User", "secret");
		VersionedModel vm = session.getVersionedModel("NewThread");

		Version v = vm.getRoot();
		assertNotNull(v);

		Model suggestion1 = v.getContent();
		suggestion1.addStatement(s3, p, o);
		v.commit(suggestion1, "first sug. comment", true);
		// result: root > suggestion1

		Model childVersion1 = v.getContent();
		childVersion1.addStatement(s4, p, o);
		v.commit(childVersion1, "first subver. comment", false);
		// result: root > suggestion1, childVersion1

		Model suggestion2 = v.getContent();
		suggestion2.addStatement(s5, p, o);
		v.commit(suggestion2, "second sug. comment", true);
		// result: root > suggestion1, childVersion1, suggestion2

		session.close();
	}

	/**
	 * This one works fine
	 */
	@Test
	public void addingVersions2LvlDepth() throws LoginException {

		Session session = semVersion.login("User", "secret");
		VersionedModel vm = session.getVersionedModel("NewThread");

		Version v = vm.getRoot();

		Model s1 = v.getContent();
		s1.addStatement("http://third", new URIImpl("http://test/uri"),
				"http://triple");
		Version child = v.commit(s1, "first ver. comment", false);
		URI uri = child.getURI();

		Version ver = vm.getVersion(uri);

		Model s2 = ver.getContent();
		s2.addStatement("http://fourth", new URIImpl("http://test/uri"),
				"http://triple");
		ver.commit(s2, "second ver. comment", true);

		Model s3 = ver.getContent();
		s3.addStatement("http://fifth", new URIImpl("http://test/uri"),
				"http://triple");
		ver.commit(s3, "second ver. comment", false);

		// semVersion.dump();

		session.close();
	}

	/**
	 * Here I try to add a sub-version to root version, successfully. Then I try
	 * to add a sub-version to added sub-version, successfully. But when I try
	 * to add another sub-version to the latest added node, I get an exception.
	 * Why?
	 * 
	 * @throws LoginException
	 */
	@Test
	public void addingVersions3LvlDepth() throws LoginException {

		Session session = semVersion.login("User", "secret");
		VersionedModel vm = session.getVersionedModel("NewThread");

		Version rootVersion = vm.getRoot();

		Model childVersion1 = rootVersion.getContent();
		assertTrue(childVersion1 instanceof BlankNodeEnrichmentModel);
		childVersion1.addStatement(s3, p, o);
		Version child = rootVersion.commit(childVersion1, "first ver. comment",
				false);
		URI uri = child.getURI();

		Version ver = vm.getVersion(uri);

		Model childVersion2 = ver.getContent();
		assertTrue(childVersion2 instanceof BlankNodeEnrichmentModel);
		childVersion2.addStatement(s4, p, o);
		Version ver2 = ver.commit(childVersion2, "second ver. comment", false);

		Model childVersion3 = ver2.getContent();
		assertTrue(childVersion3 instanceof BlankNodeEnrichmentModel);
		childVersion3.addStatement(s5, p, o);
		ver2.commit(childVersion3, "second ver. comment", false);

		// semVersion.dump();

		session.close();
	}

	/**
	 * Here I add to a root model: 1) One suggestion 2) One sub-version 3)
	 * Suggestion to sub-version added in point 2 and I get exception - why? I
	 * assume it should work. Do I understand right?
	 * 
	 * @throws LoginException
	 */
	@Test
	public void addingSuggestionVersionSuggestion() throws LoginException {

		Session session = semVersion.login("User", "secret");
		VersionedModel vm = session.getVersionedModel("NewThread");

		Version v = vm.getRoot();

		Model s1 = v.getContent();
		s1.addStatement("http://third", new URIImpl("http://test/uri"),
				"http://triple");
		v.commit(s1, "first sug. comment", true);

		Model s2 = v.getContent();
		s2.addStatement("http://fourth", new URIImpl("http://test/uri"),
				"http://triple");
		v.commit(s2, "first subver. comment", true);

		Model s3 = v.getContent();
		s3.addStatement("http://fifth", new URIImpl("http://test/uri"),
				"http://triple");
		Version ver = v.commit(s3, "second sug. comment", false);

		Model s4 = ver.getContent();
		s4.addStatement("http://sixth", new URIImpl("http://test/uri"),
				"http://triple");
		ver.commit(s4, "comment", true);

		// semVersion.dump();

		session.close();
	}
}