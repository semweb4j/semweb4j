package org.ontoware.semversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.semversion.impl.SyntacticDiffEngine;
import org.ontoware.semversion.impl.TripleStore;

@SuppressWarnings("unused")
public class DiffTest {

	private SemVersion semVersion;

	URI a = new URIImpl("test://a");

	URI b = new URIImpl("test://b");

	URI c = new URIImpl("test://c");

	URI d = new URIImpl("test://d");

	@Before
	public void setUp() throws Exception {
		semVersion = new SemVersion();
		semVersion.startup(new File("./target/test/DiffTest"));
		semVersion.deleteStore();
		semVersion.createUser("Sebastian", "secret");
		semVersion.createUser("admin", "admin");
	}

	@Test
	public void testSyntacticDiff() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");
		Model modelA = session.getModel();
		modelA.addStatement(a, b, c);
		Model modelB = session.getModel();
		modelB.addStatement(b, c, d);

		Diff synDiff = SyntacticDiffEngine.getSyntacticDiff(modelA, modelB);

		assertNotNull(synDiff.getAdded().iterator());
		assertTrue(synDiff.getAdded().iterator().hasNext());
		Statement added = synDiff.getAdded().iterator().next();
		Statement removed = synDiff.getRemoved().iterator().next();
		Statement expedtRemoved = new StatementImpl(modelA.getContextURI(), a,
				b, c);

		assertEquals(removed.getSubject(), expedtRemoved.getSubject());
		assertEquals(removed.getPredicate(), expedtRemoved.getPredicate());
		assertEquals(removed.getObject(), expedtRemoved.getObject());

		session.close();

	}

	@Test
	public void testSemanticDiff() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");

		// ///////////////////
		// A subclass B
		// B subclass C
		Model modelA = session.getModel();
		modelA.addStatement(a, RDFS.subClassOf, b);
		modelA.addStatement(b, RDFS.subClassOf, c);
		// ///////////////////
		// A subclass B
		// B subclass C
		// A subclass C
		Model modelB = session.getModel();
		modelB.addStatement(a, RDFS.subClassOf, b);
		modelB.addStatement(b, RDFS.subClassOf, c);
		modelB.addStatement(a, RDFS.subClassOf, c);

		// just for fun
		Diff synDiff = SyntacticDiffEngine.getSyntacticDiff(modelA, modelB);
		synDiff.dump();

		// sem diff
		Diff semDiff = SemanticDiffEngine.getSemanticDiff_RDFS(modelA, modelB);
		semDiff.dump();

		session.close();

	}

	@Test
	public void testBlankNodeEnrichment() throws Exception {
		Session session = semVersion.login("Sebastian", "secret");
		VersionedModel vm = session.createVersionedModel("GeneOntology");

		// ///////////////////
		// A subclass B
		// B subclass C
		Model modelA = session.getModel();
		BlankNode b1 = modelA.createBlankNode();
		BlankNode b2 = modelA.createBlankNode();

		modelA.addStatement(a, RDFS.subClassOf, b1);
		modelA.addStatement(b1, RDFS.subClassOf, c);
		session.close();

		System.out.println("plain");
		modelA.dump();

		TripleStore.bnodeEnrichment(modelA);

		System.out.println("enriched");
		modelA.dump();

		// /////////////////////////

	}
}
