/*
 * Created on 21.09.2005
 *
 */
package org.ontoware.semversion.usecase.marcont;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.ontoware.rdf2go.impl.jena24.ModelImplJena24;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.ModelUtils;
import org.ontoware.semversion.SemVersion;
import org.ontoware.semversion.SemanticDiffEngine;
import org.ontoware.semversion.Session;
import org.ontoware.semversion.Version;
import org.ontoware.semversion.VersionedModel;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class MarcOntUseCase {
	@Test
	public void testMarcOntUseCase() throws Exception {
		SemVersion semVersion = new SemVersion();
		semVersion.startup(new File("./target/test/MarcOntUseCase"));
		semVersion.deleteStore();
		semVersion.createUser("User a", "secret");
		semVersion.createUser("User b", "secret");
		semVersion.createUser("User c", "secret");
		semVersion.createUser("User d", "secret");

		// -----------------------------------------------------------------------------------
		// user A creates a Versioned Model and commits a first Version
		// -----------------------------------------------------------------------------------

		// login to the SemVersion system
		Session session1 = semVersion.login("User a", "secret");
		// create a new VersionedModel
		VersionedModel vm1 = session1.createVersionedModel("MarcOnt 2005");
		// get an empty Model where you can put your statements.
		Model rootModel = session1.getModel();
		// add your Statements into the model by calling
		// model.addStatement(Object subject , URI predicate , Object object),
		// model.addStatement(Object subject , URI predicate , String literal,
		// String languageTag),
		// or model.addStatement(Object subject, URI predicate, String literal,
		// URI dataTypeURI).

		// commit the initial model
		vm1.commitRoot(rootModel, "My first Version");

		// -----------------------------------------------------------------------------------
		// User B commits a suggestion to this Versioned Model
		// -----------------------------------------------------------------------------------

		// login to the SemVersion system
		Session session2 = semVersion.login("User b", "secret");
		// get the MarcOnt VersionedModel
		VersionedModel vm2 = session2.getVersionedModel("MarcOnt 2005");
		// get an empty Model where you can put your statements.
		Model suggestion2 = session2.getModel();
		// add your Statements into the model by calling
		// model.addStatement(Object subject , URI predicate , Object object),
		// model.addStatement(Object subject , URI predicate , String literal,
		// String languageTag),
		// or model.addStatement(Object subject, URI predicate, String literal,
		// URI dataTypeURI).

		// commit the suggestion
		vm2.getLastMainbranchVersion().commit(suggestion2,
				"Suggestion of user b", true);

		// -----------------------------------------------------------------------------------
		// User C commits a suggestion to this Versioned Model
		// -----------------------------------------------------------------------------------

		// login to the SemVersion system
		Session session3 = semVersion.login("User c", "secret");
		// get the MarcOnt VersionedModel
		VersionedModel vm3 = session3.getVersionedModel("MarcOnt 2005");
		// get an empty Model where you can put your statements.
		Model suggestion3 = session3.getModel();
		// add your Statements into the model by calling
		// model.addStatement(Object subject , URI predicate , Object object),
		// model.addStatement(Object subject , URI predicate , String literal,
		// String languageTag),
		// or model.addStatement(Object subject, URI predicate, String literal,
		// URI dataTypeURI).

		// commit the suggestion
		vm3.getLastMainbranchVersion().commit(suggestion3,
				"Suggestion of user c", true);

		// -----------------------------------------------------------------------------------
		// User D commits a suggestion to this Versioned Model
		// -----------------------------------------------------------------------------------

		// login to the SemVersion system
		Session session4 = semVersion.login("User d", "secret");
		// get the MarcOnt VersionedModel
		VersionedModel vm4 = session4.getVersionedModel("MarcOnt 2005");
		// get an empty Model where you can put your statements.
		Model suggestion4 = session4.getModel();
		// add your Statements into the model by calling
		// model.addStatement(Object subject , URI predicate , Object object),
		// model.addStatement(Object subject , URI predicate , String literal,
		// String languageTag),
		// or model.addStatement(Object subject, URI predicate, String literal,
		// URI dataTypeURI).

		// commit the suggestion
		vm4.getLastMainbranchVersion().commit(suggestion4,
				"Suggestion of user d", true);

		// -----------------------------------------------------------------------------------
		// User A accepts the Suggestion of User C
		// -----------------------------------------------------------------------------------

		// login to the SemVersion system
		Session session5 = semVersion.login("User a", "secret");
		// get the MarcOnt VersionedModel
		VersionedModel vm5 = session5.getVersionedModel("MarcOnt 2005");
		// get a list of all versions in this Versioned Model
		List<Version> allVersions = vm5.getLastMainbranchVersion()
				.getSuggestions();
		for (Version version : allVersions) {
			// accept the Suggestion of User c
			if (version.getUser().equals("User c"))
				version.setAsRelease();
		}

		// -----------------------------------------------------------------------------------
		// User B commits another Suggestion as a diff to the most recent
		// version
		// -----------------------------------------------------------------------------------

		// login to the SemVersion system
		Session session6 = semVersion.login("User b", "secret");
		// get the MarcOnt VersionedModel
		VersionedModel vm6 = session6.getVersionedModel("MarcOnt 2005");
		// create Models with added and removed statements for diff
		Model added = session6.getModel();
		Model removed = session6.getModel();
		// add your Statements into theses models by calling
		// model.addStatement(Object subject , URI predicate , Object object),
		// model.addStatement(Object subject , URI predicate , String literal,
		// String languageTag),
		// or model.addStatement(Object subject, URI predicate, String literal,
		// URI dataTypeURI).

		Diff diff = new DiffImpl(added.iterator(), removed.iterator());
		// commit the diff
		vm6.getLastMainbranchVersion().commit(diff, "Another suggestion",
				new URIImpl("data://suggestion6"), null, true);

		// -----------------------------------------------------------------------------------
		// User A wants to see the differences between the first version and the
		// latest version
		// -----------------------------------------------------------------------------------

		// login to the SemVersion system
		Session session7 = semVersion.login("User a", "secret");
		// get the MarcOnt VersionedModel
		VersionedModel vm7 = session7.getVersionedModel("MarcOnt 2005");
		// calculate the semantic diff of the root Version and the latest
		// main-branch version
		Diff diff2 = SemanticDiffEngine.getSemanticDiff_RDFS(vm7.getRoot()
				.getContent(), vm7.getLastMainbranchVersion().getContent());
		// Print out all added Statements
		diff2.dump();
		semVersion.shutdown();

	}

	@Test
	public void testJenaConversion() throws Exception {
		// create a Jena Model
		com.hp.hpl.jena.rdf.model.Model jenaModel = ModelFactory
				.createDefaultModel();

		// for ease of use, wrap it in an RDF2Go model
		Model jenaModelAsRdf2Go = new ModelImplJena24(jenaModel);
		jenaModelAsRdf2Go.open();

		// add statements with blank nodes
		URI u1 = new URIImpl("urn:test:u1");
		URI u2 = new URIImpl("urn:test:u2");
		URI u3 = new URIImpl("urn:test:u3");
		BlankNode b1 = jenaModelAsRdf2Go.createBlankNode();
		BlankNode b2 = jenaModelAsRdf2Go.createBlankNode();
		jenaModelAsRdf2Go.addStatement(u1, u2, b1);
		jenaModelAsRdf2Go.addStatement(u1, u3, b2);

		// dump to system out
		jenaModelAsRdf2Go.writeTo(System.out, Syntax.Ntriples);

		// wrap jenaModel as jenaOntModel
		OntModel ontModel = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_DL_MEM, jenaModel);

		// wrap ontmodel in RDF2Go
		Model ontModelAsRDF2Go = new ModelImplJena24(ontModel);
		ontModelAsRDF2Go.open();

		// dump ontmodelAsRDF2Go
		ontModelAsRDF2Go.writeTo(System.out, Syntax.Ntriples);

		// /////////// now try to store it in semversion
		SemVersion sv = new SemVersion();
		sv.startup(new File("./target/test"));
		sv.clear();
		Session session = sv.createAnonymousSession();
		VersionedModel vm = session.createVersionedModel("test");
		assertNotNull(vm);
		Model semversionModel = session.getModel();
		ModelUtils.copy(ontModelAsRDF2Go, semversionModel);
		vm.commitRoot(semversionModel, "root");
		session.close();

		// load from semversion
		session = sv.createAnonymousSession();
		vm = session.getVersionedModel("test");
		Model fromSemversion = vm.getRoot().getContent();
		// dump retrieved content
		fromSemversion.writeTo(System.out, Syntax.Ntriples);
		// create a new Jena Model

		// create a Jena Model
		com.hp.hpl.jena.rdf.model.Model secondJenaModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_DL_MEM);

		// for ease of use, wrap it in an RDF2Go model
		Model secondJenaModelAsRdf2Go = new ModelImplJena24(secondJenaModel);
		secondJenaModelAsRdf2Go.open();

		ModelUtils.copy(fromSemversion, secondJenaModelAsRdf2Go);
		session.close();

		fromSemversion.close();
		ontModelAsRDF2Go.close();
		jenaModelAsRdf2Go.close();

		secondJenaModelAsRdf2Go.writeTo(System.out, Syntax.Ntriples);
		secondJenaModelAsRdf2Go.close();
		sv.shutdown();
	}

}
