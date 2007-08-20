package org.ontoware.semversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelAddRemove;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.impl.ModelAddRemoveMemoryImpl;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.ModelUtils;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.semversion.impl.ModelCompareUtils;
import org.ontoware.semversion.impl.UserImpl;

@SuppressWarnings("unused")
public class UsageTest {

	static final URI a = new URIImpl("test://a");

	static final URI b = new URIImpl("test://b");

	static final URI c = new URIImpl("test://c");

	private SemVersion semVersion;

	File persistenceDir = new File("./target/UsageTest");

	@Before
	public void setUp() throws Exception {
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		semVersion.deleteStore();
		semVersion.createUser("Sebastian", "secret");
		semVersion.createUser("admin", "admin");
	}

	@Test
	public void testBranchError() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		Model m = session.getModel();
		m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
				"rdfs://has"), new URIImpl("things://bag"));
		vm.commitRoot(m, "mein erstes Modell");
		session.close();

		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);

		// login
		session = semVersion.login("admin", "admin");
		// get versioned model
		VersionedModel versionedModel = session
				.getVersionedModel("GeneOntology");
		// get most recent version
		Version rootVersion = versionedModel.getRoot();
		// get real model content (actually a copy of it)
		Model recentMainModel = rootVersion.getContent();
		// change
		recentMainModel.addStatement(new URIImpl("gene://cell"), RDF.type,
				new URIImpl("gene://term"));
		// commit
		rootVersion.commitAsBranch(recentMainModel, "branch1",
				"celltypes added", false);
		recentMainModel.close();
		session.close();

		// login
		session = semVersion.login("Sebastian", "secret");
		// get versioned model
		VersionedModel versionedModel2 = session
				.getVersionedModel("GeneOntology");
		// get most recent version
		Version rootVersion2 = versionedModel2.getRoot();
		// get real model content (actually a copy of it)
		Model recentMainModel2 = rootVersion2.getContent();
		// change
		recentMainModel2.addStatement(new URIImpl("user://sebastian"),
				new URIImpl("rdfs://has"), new URIImpl("things://paper"));
		// commit

		boolean exceptionThrown = false;
		try {
			rootVersion.commitAsBranch(recentMainModel, "branch1",
					"paper added", false);
			session.close();
		} catch (BranchlabelAlreadyUsedException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);

	}

	@Test
	public void testCommitConflict() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		Model m = session.getModel();
		m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
				"rdfs://has"), new URIImpl("things://bag"));
		vm.commitRoot(m, "mein erstes Modell");
		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);

		// login
		session = semVersion.login("admin", "admin");
		// get versioned model
		VersionedModel versionedModel = session
				.getVersionedModel("GeneOntology");
		// get most recent version
		Version rootVersion = versionedModel.getRoot();
		// get real model content (actually a copy of it)
		Model recentMainModel = rootVersion.getContent();
		// change
		recentMainModel.addStatement(new URIImpl("gene://cell"), RDF.type,
				new URIImpl("gene://term"));
		// commit
		rootVersion.commit(recentMainModel, "celltypes added", false);
		session.close();

		// login
		session = semVersion.login("Sebastian", "secret");
		// get versioned model
		VersionedModel versionedModel2 = session
				.getVersionedModel("GeneOntology");
		// get most recent version
		Version rootVersion2 = versionedModel2.getRoot();
		// get real model content (actually a copy of it)
		Model recentMainModel2 = rootVersion2.getContent();
		// change
		recentMainModel2.addStatement(new URIImpl("user://sebastian"),
				new URIImpl("rdfs://has"), new URIImpl("things://paper"));
		// commit

		boolean exceptionThrown = false;
		try {
			rootVersion2.commit(recentMainModel2, "Paper added", false);
			session.close();
		} catch (CommitConflictException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

	@Test
	public void testCommitRoot() throws URISyntaxException, Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		assertNotNull(vm);
		assertNull(vm.getRoot());
		session.close();

		session = semVersion.login("Sebastian", "secret");
		vm = session.getVersionedModel("GeneOntology");
		assertNotNull(vm);
		assertNull(vm.getRoot());
		Model m = session.getModel();
		m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
				"rdfs://has"), new URIImpl("things://bag"));
		assertNull(vm.getRoot());
		vm.commitRoot(m, "mein erstes Modell");
		assertNotNull(vm.getRoot());
		session.close();
		semVersion.shutdown();
		
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		session = semVersion.login("admin", "admin");
		vm = session.getVersionedModel("GeneOntology");
		assertEquals("mein erstes Modell", vm.getRoot().getComment());
		assertFalse(vm.getRoot().isSuggestion());
		assertEquals("Sebastian", vm.getLastModifiedBy());
	}

	@Test
	public void testCompareTwoLatestVersions() throws Exception {
		String FIRST_MODEL = "first model";
		String SECOND_MODEL = "second model";
		String GENE_ONTOLOGY = "GeneOntology";

		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel(GENE_ONTOLOGY);
		Model rootModel = session.getModel();
		assertEquals(0, rootModel.size());
		rootModel.addStatement(a, RDFS.subClassOf, b);
		rootModel.addStatement(b, RDFS.subClassOf, c);
		assertEquals(2, rootModel.size());
		Version firstVersion = vm.commitRoot(rootModel, FIRST_MODEL);

		// verify
		Version firstVersion_ = vm.getRoot();
		assertEquals(firstVersion_.getURI(), firstVersion_.getURI());
		Model rootModel2 = firstVersion_.getContent();
		assertEquals(2, rootModel2.size());
		rootModel2.addStatement(a, RDFS.subClassOf, c);

		assertEquals(3, rootModel2.size());
		Version secondVersion = firstVersion_.commit(rootModel2, SECOND_MODEL,
				false);
		Model secondModel = secondVersion.getContent();
		assertEquals(3, secondModel.size());

		// rootModel should still have only 2 statements
		rootModel2 = vm.getRoot().getContent();
		assertEquals(2, rootModel2.size());
		Version secondVersion_ = vm.getLastMainbranchVersion();
		assertEquals(secondVersion.getURI(), secondVersion_.getURI());
		Model secondModel_ = secondVersion_.getContent();

		// FIXME
		semVersion.dump();

		assertEquals(3, secondModel_.size());

		// logout, login
		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		session = semVersion.login("Sebastian", "secret");

		// get versioned model
		VersionedModel versionedModel = session
				.getVersionedModel(GENE_ONTOLOGY);
		assertEquals(GENE_ONTOLOGY, versionedModel.getLabel());

		// get most recent version and first parent
		Version headVersion = versionedModel.getLastMainbranchVersion();
		assertNotNull(headVersion.getFirstParent());
		assertEquals(SECOND_MODEL, headVersion.getComment());
		Model headModel = headVersion.getContent();
		assertEquals(3, headModel.size());

		Version previousMainVersion = headVersion.getFirstParent();
		assertNotNull(previousMainVersion);
		assertEquals(FIRST_MODEL, previousMainVersion.getComment());
		Model previousMainModel = previousMainVersion.getContent();
		assertEquals(2, previousMainModel.size());

		// calculate diff between the models
		Diff diff = semVersion.getSemanticDiff_RDFS(previousMainModel,
				headModel);
		assertEquals(0, ModelUtils.size(diff.getAdded()));
		assertEquals(0, ModelUtils.size(diff.getRemoved()));

		// vm.dump();

		diff = semVersion.getSyntacticDiff(previousMainModel, headModel);

		// System.out.println("Added:");
		// diff.getAdded().dump(null);
		// System.out.println("Removed:");
		// diff.getRemoved().dump(null);
		assertEquals(1, ModelUtils.size(diff.getAdded()));
		assertEquals(0, ModelUtils.size(diff.getRemoved()));
	}

	@Test
	public void testCreateBranch() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		Model m = session.getModel();
		m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
				"rdfs://has"), new URIImpl("things://bag"));
		vm.commitRoot(m, "meine erstes Modell");
		session.close();

		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);

		// login
		session = semVersion.login("Sebastian", "secret");
		// get versioned model
		VersionedModel versionedModel = session
				.getVersionedModel("GeneOntology");

		// get most recent version
		Version recentMainVersion = versionedModel.getLastMainbranchVersion();

		// get real model content (actually a copy of it)
		Model recentMainModel = recentMainVersion.getContent();

		// change
		recentMainModel.addStatement(new URIImpl("gene://cell"), RDF.type,
				new URIImpl("gene://term"));

		// vm.dump();

		// commit
		recentMainVersion.commitAsBranch(recentMainModel, "branchLabels",
				"celltypes", false);
		// vm.dump();
	}

	@Test
	public void testCreateFinalAsChildOfSuggestion() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		Model m = session.getModel();
		m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
				"rdfs://has"), new URIImpl("things://bag"));
		vm.commitRoot(m, "mein erstes Modell");
		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		// login
		session = semVersion.login("admin", "admin");
		// get versioned model
		VersionedModel versionedModel = session
				.getVersionedModel("GeneOntology");
		// get most recent version
		Version rootVersion = versionedModel.getRoot();
		// get real model content (actually a copy of it)
		Model recentMainModel = rootVersion.getContent();
		// change
		recentMainModel.addStatement(new URIImpl("gene://cell"), RDF.type,
				new URIImpl("gene://term"));
		// commit
		Version v = rootVersion
				.commit(recentMainModel, "celltypes added", true);
		Model newModel = v.getContent();
		newModel.addStatement(new URIImpl("gene://cell"), RDF.type,
				new URIImpl("gene://term2"));
		boolean exceptionThrown = false;
		try {
			v.commit(newModel, "another cellType added", false);
		} catch (InvalidChildOfSuggestionException iae) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		session.close();
	}

	@Test
	public void testCreateRoot() throws Exception {
		semVersion.createUser("carlos", "secret");

		Session session = semVersion.login("carlos", "secret");

		// create a versioned model
		VersionedModel vm = session.createVersionedModel("Gene Ontology 2005");

		assertNull("should be null but is " + ((VersionedModel) vm).getRoot(),
				((VersionedModel) vm).getRoot());

		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		session = semVersion.login("carlos", "secret");
		assertEquals("carlos", session.getUser().getName());
		vm = session.getVersionedModel("Gene Ontology 2005");
		assertEquals("carlos", vm.getUser().getName());

		// create a triple set with the content of our root version
		// for testing purposes, we just create an empty triple set with
		// a random uri
		Model rootContent = session.getModel();

		// commit the triple set as the root content
		assertNull("should be null but is " + ((VersionedModel) vm).getRoot(),
				((VersionedModel) vm).getRoot());
		vm.commitRoot(rootContent, // triple set
				"My Root Version", // label
				new URIImpl("urn:semversion.ontoware.org/GO14072005/V1"), // version
				// uri
				new URIImpl("urn:semversion.ontoware.org") // provenance
				);

		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		session = semVersion.login("Sebastian", "secret");
		vm = session.getVersionedModel("Gene Ontology 2005");

		assertTrue(vm instanceof VersionedModel);
		assertNotNull(((VersionedModel) vm).getRoot());

		assertEquals("There should be exactly 1 (the root version)", 1, vm
				.getVersionCount());

		Version vers = vm.getFirstVersion();
		assertNotNull(vers);
		assertEquals("carlos", vers.getUser().getName());
	}

	@Test
	public void testCreateVersionedModel() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		assertNotNull(vm);
		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		session = semVersion.login("admin", "admin");
		vm = session.getVersionedModel("GeneOntology");
		assertNotNull(vm);
		assertEquals(0, vm.getAllVersions().size());
		assertNull(vm.getFirstVersion());
		assertNull(vm.getLastMainbranchVersion());
		session.close();
	}

	@Test
	public void testCreateVersionedModelWithSameName() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		assertNotNull(vm);
		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		session = semVersion.login("admin", "admin");
		vm = session.createVersionedModel("GeneOntology");
		assertNull(vm);
		session.close();
	}

	@Test
	public void testCreateVersionedModelWithValidTime() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");

		VersionedModel vm = session.createVersionedModel(new URIImpl(
				"urn:semversion.ontoware.org/GO14072005"), "GeneOntology");
		assertNotNull(vm);
		VersionedModel vm2 = session.getVersionedModel("GeneOntology");
		assertTrue(vm.equals(vm2));
		session.close();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGettersVersionedModel() throws Exception {
		semVersion.createUser("carlos", "secret");
		Session session = semVersion.login("carlos", "secret");

		VersionedModel vm = session
				.createVersionedModel(new URIImpl(
						"urn:semversion.ontoware.org/GO14082005"),
						"Gene Ontology 2005");

		vm.commitRoot(session.getModel(), "My Root Version", new URIImpl(
				"urn:semversion.ontoware.org/GO14082005/V1"), new URIImpl(
				"urn:semversion.ontoware.org"));

		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		session = semVersion.login("Sebastian", "secret");
		vm = session.getVersionedModel("Gene Ontology 2005");

		assertNotNull(vm.getLastVersions());
		assertTrue(vm.getLastVersions().size() > 0);
		assertEquals("carlos", vm.getLastModifiedBy());

		List<UserImpl> theUsers = vm.getListLastModifiedBy();
		assertEquals("carlos", theUsers.get(0).getName());

		List<Version> lst1 = vm.getVersions();
		assertEquals(1, lst1.size());
		Version vers1 = lst1.get(0);
		assertNotNull(vers1);
		assertEquals("My Root Version", vers1.getComment());
	}

	@Test
	public void testGetVersionedModel() throws Exception {
		// basically: do what users will do

		// check count of versions
		// get first and last and compare by uri
		// set flags

		semVersion.createUser("carlos", "secret");
		Session session = semVersion.login("carlos", "secret");

		// create VersionedModel
		VersionedModel vm = session
				.createVersionedModel(new URIImpl(
						"urn:semversion.ontoware.org/GO14072005"),
						"Gene Ontology 2005");

		// commit root/initial version
		Model m = session.getModel();
		assertNotNull(m);
		m.addStatement(new URIImpl("gene://cell_1"), RDF.type, new URIImpl(
				"gene://cell"));
		assertEquals(1, m.size());
		ClosableIterator<Statement> it = m.iterator();
		Statement s = it.next();
		it.close();
		assertEquals(new URIImpl("gene://cell_1"), s.getSubject());
		assertEquals(RDF.type, s.getPredicate());
		assertEquals(new URIImpl("gene://cell"), s.getObject());
		vm.commitRoot(m, "My root Version");
		assertTrue(ModelCompareUtils.equals(m, vm.getLastMainbranchVersion()
				.getContent()));
		m.close();

		// commit a diff
		DiffImpl diff = new DiffImpl();
		diff.addStatement(new URIImpl("gene://cell_2"), RDF.type, new URIImpl(
				"gene://cell"));
		diff.removeStatement(new URIImpl("gene://cell_1"), RDF.type,
				new URIImpl("gene://cell"));

		// FIXME remove
		assert diff.getRemoved().iterator().hasNext();
		
		Version lastMainBranch = vm.getLastMainbranchVersion();
		Version diffCommitedVersion = lastMainBranch.commit(diff, "second Version",
				new URIImpl("model://diff"), null, false);

		Model m_ = diffCommitedVersion.getContent();
		
		m_.dump();
		
		assertEquals(1, m_.size());
		it = m_.iterator();
		s = it.next();
		it.close();
		assertEquals(new URIImpl("gene://cell_2"), s.getSubject());
		assertEquals(RDF.type, s.getPredicate());
		assertEquals(new URIImpl("gene://cell"), s.getObject());
		m_.close();
		
		
		Model m2 = vm.getLastMainbranchVersion().getContent();
		assertNotNull(m2);
		assertEquals(1, m2.size());
		it = m2.iterator();
		Statement stmt = it.next();
		it.close();
		m2.close();

		assertEquals(new URIImpl("gene://cell_2"), stmt.getSubject());
		assertEquals(new URIImpl("gene://cell"), stmt.getObject());
		assertEquals(RDF.type, stmt.getPredicate());

		Diff newDiff = semVersion.getSyntacticDiff(vm.getRoot().getContent(),
				vm.getLastMainbranchVersion().getContent());
		assertEquals(diff.getAdded(), newDiff.getAdded());
		assertEquals(diff.getRemoved(), newDiff.getRemoved());

		// commit another diff
		Diff diff2 = new DiffImpl();
		diff2.addStatement(new URIImpl("gene://cell_3"), RDF.type, new URIImpl(
				"gene://cell"));
		diff2.addStatement(new URIImpl("gene://cell_4"), RDF.type, new URIImpl(
				"gene://cell"));
		// FIXME hangs
		Version lastMainBranchVersion = vm.getLastMainbranchVersion();
		lastMainBranchVersion.commit(diff2, "third Version", new URIImpl(
				"model://diff2"), null, false);

		Model m3 = vm.getLastMainbranchVersion().getContent();
		assertNotNull(m3);
		assertEquals(3, m3.size());
		assertEquals("third Version", vm.getLastMainbranchVersion()
				.getComment());

		assertEquals("commit", vm.getLastMainbranchVersion().getChangeCause());

		List<Change> changes = vm.getChangeLog();
		assertEquals(vm.getAllVersions().size(), changes.size());
		for (Change change : changes) {
			System.out.println(change.getCreationCause());
		}
	}

	/**
	 * Get a List of all VersionedModels, checkout one, change that model and
	 * commit it
	 */
	@Test
	public void testListCheckoutChangeCommit() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		assertNotNull(vm);
		Version rootVersion = vm.getRoot();
		assertNull(rootVersion);
		Model m = session.getModel();
		m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
				"rdfs://has"), new URIImpl("things://bag"));
		vm.commitRoot(m, "meine erstes Modell");

		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		session = semVersion.login("admin", "admin");

		// semVersion.dump();

		// get versioned model
		VersionedModel versionedModel = session
				.getVersionedModel("GeneOntology");
		assertNotNull(versionedModel);
		// get most recent version
		Version recentMainVersion = versionedModel.getLastMainbranchVersion();
		// get real model content (actually a copy of it)
		Model recentMainModel = recentMainVersion.getContent();
		// change
		recentMainModel.addStatement(new URIImpl("gene://cell"), RDF.type,
				new URIImpl("gene://term"));
		// commit
		recentMainVersion.commit(recentMainModel, "celltypes", false);

		// semVersion.dump();

	}

	@Test
	public void testMemoryModelCommit() throws Exception {
		semVersion.createUser("carlos", "secret");
		Session session = semVersion.login("carlos", "secret");

		VersionedModel vm = session
				.createVersionedModel(new URIImpl(
						"urn:semversion.ontoware.org/GO14072005"),
						"Gene Ontology 2005");

		Model m = RDF2Go.getModelFactory().createModel();
		m.open();
		m.addStatement(new URIImpl("gene://cell_1"), RDF.type, new URIImpl(
				"gene://cell"));
		vm.commitRoot(m, "root Version");
		assertTrue(ModelCompareUtils.equals(m, vm.getLastMainbranchVersion()
				.getContent()));
	}

	// TODO test branch & then merge
	// @Test
	// public void testMerge() throws Exception {
	// Session session = semVersion.login("Sebastian", "secret");
	// VersionedModel vm = session.createVersionedModel("GeneOntology");
	// Model m = session.getModel();
	// m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
	// "rdfs://has"), new URIImpl("things://bag1"));
	// vm.commitRoot(m, "mein erstes Modell");
	// session.close();
	// semVersion.shutdown();
	// semVersion = new SemVersion();
	// semVersion.startup(persistenceDir);
	//
	// /*
	// * login, merge Models, logout
	// */
	// session = semVersion.login("Sebastian", "secret");
	// vm = session.getVersionedModel("GeneOntology");
	// m = session.getModel();
	// m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
	// "rdfs://has"), new URIImpl("things://ball"));
	//
	// Version latest = vm.getLastMainbranchVersion();
	// assertNotNull(latest);
	// assertEquals(0, latest.getAllChildren().length);
	// Version child = latest.merge(m, "added", true);
	// semVersion.shutdown();
	// semVersion = new SemVersion();
	// semVersion.startup(persistenceDir);
	// session = semVersion.login("admin", "admin");
	// vm = session.getVersionedModel("GeneOntology");
	// latest = vm.getLastMainbranchVersion();
	// child = vm.getLastMainbranchVersion().getAllChildren()[0];
	// assertEquals(1, latest.getAllChildren().length);
	// assertEquals(2, child.getContent().size());
	// ClosableIterator<? extends Statement> iter = child.getContent()
	// .findStatements(Variable.ANY, Variable.ANY,
	// new URIImpl("things://bag1"));
	// assertTrue(iter.hasNext());
	// Statement s = iter.next();
	// assertEquals(new URIImpl("user://sebastian"), s.getSubject());
	// assertFalse(iter.hasNext());
	//
	// List<Change> changes = vm.getChangeLog();
	// assertEquals(2, changes.size());
	// for (Change change : changes) {
	// Version v = change.getNewVersion();
	// if (v.getComment().equals("added")) {
	// assertEquals("merge", change.getCreationCause());
	// } else {
	// assertEquals("commit", change.getCreationCause());
	// }
	// }
	// session.close();
	// }

	@Test
	public void testNavigationalGettersVersion() throws LoginException,
			ModelException {
		User carlos = semVersion.createUser("carlos", "secret");
		Session session = semVersion.login("carlos", "secret");

		VersionedModel vm = session
				.createVersionedModel(new URIImpl(
						"urn:semversion.ontoware.org/GO14072005"),
						"Gene Ontology 2005");

		vm.commitRoot(session.getModel(), "My Root Version", new URIImpl(
				"urn:semversion.ontoware.org/GO14072005/V1"), new URIImpl(
				"urn:semversion.ontoware.org"));

		Version vRoot = vm.getFirstVersion();
		assertNotNull(vRoot);

		vRoot.commit(session.getModel(), "My Second Version", new URIImpl(
				"urn:semversion.ontoware.org/GO14072005/V2"), new URIImpl(
				"urn:semversion.ontoware.org"), false);

		List<Version> lst1 = vm.getLastVersions();
		Version v2 = lst1.get(0);
		assertNotNull(v2);

		Version v3 = v2.commit(session.getModel(), "My Third Version",
				new URIImpl("urn:semversion.ontoware.org/GO14072005/V3"),
				new URIImpl("urn:semversion.ontoware.org"), false);
		assertNotNull(v3);

		List<Version> lst2 = vm.getLastVersions();
		assertEquals(v3, lst2.get(0));

		Version theV2 = v3.getPrevVersion();
		assertNotNull(theV2);
		assertEquals(v2, theV2);
		Object theVNull = v3.getSecondParent();
		assertNull(theVNull);

		Version lst3 = v3.getPrevVersion();
		assertNotNull(lst3);
		assertEquals(v2, lst3);

		Version lst4 = v2.getPrevVersion();
		assertNotNull(lst4);
		assertEquals(vRoot, lst4);

		Version lst5 = vRoot.getPrevVersion();
		assertNull(lst5);

		List<Version> lst6 = v3.getNextVersions();
		assertEquals(0, lst6.size());

	}

	@Test
	public void testRootIsNull() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		assertNotNull(vm);
		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);
		session = semVersion.login("admin", "admin");
		vm = session.getVersionedModel("GeneOntology");
		assertNotNull(vm);
		assertNull(vm.getRoot());
		session.close();
	}

	@Test(expected = RuntimeException.class)
	public void testUserCreation() {

		User carlos = semVersion.createUser("carlos", "secret");
		assertNotNull(carlos);
		assertEquals(carlos.getName(), "carlos");
		assertEquals(carlos.getPassword(), "secret");
		semVersion.createUser("admin", "anything");
	}

	@Test
	public void testViewChangeLog() throws Exception {

		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		Model m = session.getModel();
		m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
				"rdfs://has"), new URIImpl("things://bag"));
		// vm.commitRoot(m, "mein erstes Modell", URIImpl
		// ("version://1"), new ValidTime(new Date(),
		// ValidTime.NOW), VersionedItem.NO_PROVENANCE);
		vm.commitRoot(m, "mein erstes Modell");
		session.close();
		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);

		// login
		session = semVersion.login("Sebastian", "secret");

		// get versioned model
		VersionedModel versionedModel = session
				.getVersionedModel("GeneOntology");

		List<Change> changes = versionedModel.getChangeLog();
		assertEquals(1, changes.size());
		for (Change change : changes) {
			Version v = change.getNewVersion();
			assertEquals("mein erstes Modell", v.getComment());
			assertEquals("commit", change.getCreationCause());
		}
	}

	@Test
	public void testViewSuggestions() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		Model m = session.getModel();
		m.addStatement(new URIImpl("user://sebastian"), new URIImpl(
				"rdfs://has"), new URIImpl("things://bag"));
		vm.commitRoot(m, "meine erstes Modell");
		session.close();

		semVersion.shutdown();
		semVersion = new SemVersion();
		semVersion.startup(persistenceDir);

		// login
		session = semVersion.login("Sebastian", "secret");

		// get versioned model
		VersionedModel versionedModel = session
				.getVersionedModel("GeneOntology");

		Version latestVersion = versionedModel.getLastMainbranchVersion();

		List<Version> suggestions = latestVersion.getSuggestions();

		for (Version v : suggestions) {
			assertTrue(v.isSuggestion());
			assertNull(v.getBranchLabel());
			System.out.println("User " + v.getUser()
					+ " has made a suggestion at " + v.getCreationTime());
		}
	}

}