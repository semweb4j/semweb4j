package org.ontoware.rdf2go.impl;

import junit.framework.TestCase;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.jena24.ModelImplJena24;
import org.ontoware.rdf2go.layer.inverse.InverseMapImpl;
import org.ontoware.rdf2go.layer.inverse.InversePropertiesModelLayer;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public class InverseTest extends TestCase {

	public void setUp() {
		Model m = new ModelImplJena24(Reasoning.none);
		layer = new InversePropertiesModelLayer(m);
	}

	InversePropertiesModelLayer layer;

	URI lautern = new URIImpl("test://test.com/lautern", false);

	URI hasInhabitant = new URIImpl("test://test.com/hasInhabitant", false);

	URI leo = new URIImpl("test://test.com/leo", false);

	URI hasHomeCity = new URIImpl("test://test.com/hasHomeCity", false);

	URI berlin = new URIImpl("test://test.com/berlin", false);

	URI knownBy = new URIImpl("test://test.com/knownBy", false);

	URI max = new URIImpl("test://test.com/max", false);

	URI h = new URIImpl("test://test.com/h", false);

	URI sub = new URIImpl("test://test.com/sub", false);

	URI knows = new URIImpl("test://test.com/knows", false);

	/*
	 * Test method for
	 * 'org.ontoware.rdf2go.impl.InversePropertiesModelLayer.sparqlConstruct(String)'
	 */
	public void testSparqlConstruct() throws ModelRuntimeException {

		layer.addStatement(lautern, hasInhabitant, leo);
		layer.addStatement(hasHomeCity, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasInhabitant);

		// implied: c,d,a
		@SuppressWarnings("unused")
		String query = "SELECT ?q WHERE { <" + leo + "> ?q <" + lautern
				+ "> } ";
		String query2 = "SELECT ?x ?y WHERE { ?x <" + hasHomeCity + "> ?y } ";
		QueryResultTable result = layer.sparqlSelect(query2);
		for (QueryRow row : result) {
			for (String s : result.getVariables())
				System.out.println(row.getValue(s));
		}

	}

	/*
	 * Test method for 'org.ontoware.rdf2go.DelegatingModel.getNewBlankNode()'
	 */
	public void testGetNewBlankNode() {

	}

	/*
	 * Test method for 'org.ontoware.rdf2go.DelegatingModel.addStatement(Object,
	 * URI, Object)'
	 */
	public void testAddStatement() throws ModelRuntimeException {
		// this test assumesthat jena 24 is used
		Model plainModel = new ModelImplJena24(
				(com.hp.hpl.jena.rdf.model.Model) layer
						.getUnderlyingModelImplementation());

		layer.addStatement(hasHomeCity, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasInhabitant);

		layer.addStatement(lautern, hasInhabitant, leo);
		assertTrue("plain triple, with inverse", plainModel.contains(lautern,
				hasInhabitant, leo));

		layer.addStatement(leo, h, berlin);
		assertTrue("plain triple, no inverse", plainModel.contains(leo, h,
				berlin));
	}

	public void testAddStatement2() throws ModelRuntimeException {
		layer.addStatement(hasHomeCity, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasInhabitant);

		System.out.println("---------");

		layer.addStatement(lautern, hasHomeCity, leo);
		assertTrue(layer.getDelegatedModel().contains(leo, hasInhabitant,
				lautern));
		assertTrue(layer.contains(lautern, hasHomeCity, leo));

		layer.addStatement(lautern, berlin, leo);
		assertTrue(layer.getDelegatedModel().contains(lautern, berlin, leo));
		assertTrue(layer.contains(lautern, berlin, leo));

		layer.addStatement(berlin, hasInhabitant, knownBy);
		assertTrue(layer.getDelegatedModel().contains(berlin, hasInhabitant,
				knownBy));
		assertTrue(layer.contains(berlin, hasInhabitant, knownBy));

		layer.getDelegatedModel().dump();

	}

	public void testAddStatement3() throws ModelRuntimeException {
		layer.addStatement(hasHomeCity, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasInhabitant);
		layer.addStatement(hasInhabitant, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasHomeCity);

		System.out.println("---------");

		layer.addStatement(lautern, hasHomeCity, leo);
		assertTrue(layer.getDelegatedModel().contains(leo, hasInhabitant,
				lautern));
		assertTrue(layer.contains(lautern, hasHomeCity, leo));

		layer.addStatement(lautern, berlin, leo);
		assertTrue(layer.getDelegatedModel().contains(lautern, berlin, leo));
		assertTrue(layer.contains(lautern, berlin, leo));

		layer.addStatement(berlin, hasInhabitant, knownBy);
		assertTrue(layer.getDelegatedModel().contains(berlin, hasInhabitant,
				knownBy));
		assertTrue(layer.contains(berlin, hasInhabitant, knownBy));

		layer.getDelegatedModel().dump();

	}

	/*
	 * Test method for
	 * 'org.ontoware.rdf2go.DelegatingModel.removeStatement(Object, URI,
	 * Object)'
	 */

	public void testRemoveStatement() throws ModelRuntimeException {
		layer.addStatement(hasHomeCity, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasInhabitant);
		layer.addStatement(lautern, hasInhabitant, leo);
		layer.addStatement(berlin, hasHomeCity, knownBy);

		Model plainModel = new ModelImplJena24(
				(com.hp.hpl.jena.rdf.model.Model) layer
						.getUnderlyingModelImplementation());

		assertTrue(plainModel.contains(lautern, hasInhabitant, leo));
		assertTrue("twisted triple should be in", plainModel.contains(knownBy,
				hasInhabitant, berlin));

		layer.removeStatement(leo, hasHomeCity, lautern);
		assertFalse("twisted triple should be out", plainModel.contains(leo,
				hasHomeCity, lautern));
		assertFalse("twisted triple should be out", plainModel.contains(
				lautern, hasInhabitant, leo));
	}

	/*
	 * 
	 * Test method for
	 * 'org.ontoware.rdf2go.DelegatingModel.sparqlSelect(String)' <pre>
	 * 
	 * a-b-c
	 * 
	 * e-f-g
	 * 
	 * b is a subpropertyOf f
	 * 
	 * QUERY: ( a ?p c. ?p sub f) QUERY: ( c ?p a. ?p sub f)
	 * 
	 * </pre>
	 * 
	 */
	public void testSparqlSelect() throws ModelRuntimeException {

		// the leo test
		layer.addStatement(lautern, hasInhabitant, leo);
		layer.addStatement(berlin, knownBy, max);
		layer.addStatement(hasInhabitant, sub, knownBy);
		org.ontoware.aifbcommons.collection.ClosableIterator<QueryRow> result = layer
				.sparqlSelect("SELECT ?p WHERE {"// .
						+ "<" + lautern + "> ?p <" + leo + "> ." // .
						+ "?p <" + sub + "> <" + knownBy + "> ."// .
						+ "}").iterator(); // .
		assertTrue(result.hasNext());
		while (result.hasNext()) {
			Node n = result.next().getValue("p");
			assertEquals(n.asURI(), hasInhabitant);
		}
		result.close();
	}

	public void testSparqlSelect2() throws ModelRuntimeException {
		// the leo test

		layer.addStatement(hasInhabitant, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasHomeCity);
		layer.addStatement(lautern, hasInhabitant, leo);

		assertTrue(layer.contains(leo, hasHomeCity, lautern));

		layer.addStatement(berlin, knownBy, max);
		layer.addStatement(hasInhabitant, sub, knownBy);
		layer.addStatement(hasHomeCity, sub, knownBy);

		layer.dump();

		org.ontoware.aifbcommons.collection.ClosableIterator<QueryRow> result = layer
				.sparqlSelect("SELECT ?p WHERE {"// .
						+ "<" + lautern + "> ?p <" + leo + "> ." // .
						+ "?p <" + sub + "> <" + knownBy + "> ."// .
						+ "}").iterator(); // .
		assertTrue(result.hasNext());
		while (result.hasNext()) {
			Node n = result.next().getValue("p");
			assertEquals(n.asURI(), hasInhabitant);
		}
		result.close();

		result = layer.sparqlSelect("SELECT ?p WHERE {"// .
				+ "<" + leo + "> ?p <" + lautern + "> ." // .
				+ "?p <" + sub + "> <" + knownBy + "> ."// .
				+ "}").iterator(); // .
		assertTrue(result.hasNext());
		result.close();
	}

	public void testSparqlSelect3() throws ModelRuntimeException {
		// the leo test

		layer.addStatement(hasInhabitant, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasHomeCity);
		layer.addStatement(lautern, hasInhabitant, leo);
		layer.addStatement(hasInhabitant, sub, knownBy);

		layer.dump();

		org.ontoware.aifbcommons.collection.ClosableIterator<QueryRow> result = layer
				.sparqlSelect("SELECT ?p WHERE {"// .
						+ "<" + lautern + "> ?p <" + leo + "> ." // .
						+ "?p <" + sub + "> <" + knownBy + "> ."// .
						+ "}").iterator(); // .
		assertTrue(result.hasNext());
		while (result.hasNext()) {
			Node n = result.next().getValue("p");
			assertEquals(n.asURI(), hasInhabitant);
		}
		result.close();
	}

	public void testSparqlSelect4() throws ModelRuntimeException {
		// the leo test

		layer.addStatement(hasInhabitant, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasHomeCity);
		layer.addStatement(lautern, hasInhabitant, leo);
		layer.addStatement(hasInhabitant, sub, knownBy);

		layer.dump();

		// SELECT ?p WHERE
		// <leo> ?p <lautern> .
		// ?p <sub> <knownBy> .
		org.ontoware.aifbcommons.collection.ClosableIterator<QueryRow> result = layer
				.sparqlSelect("SELECT ?p WHERE {"// .
						+ "<" + leo + "> ?p <" + lautern + "> ." // .
						+ "?p <" + sub + "> <" + knownBy + "> ."// .
						+ "}").iterator(); // .
		boolean hasNext = result.hasNext();
		String errorMsg = "";
		if (hasNext)
			errorMsg = "There should be no result, but result contain: "
					+ result.next();
		assertFalse(errorMsg, hasNext);
		result.close();
	}

	public void testSymmetry() throws ModelRuntimeException {
		// the leo test

		layer.addStatement(knows, InverseMapImpl.HAS_INVERSE_PROPERTY, knows);
		layer.addStatement(leo, knows, max);

		assertTrue(layer.contains(leo, knows, max));
		assertTrue(layer.contains(max, knows, leo));
		assertTrue(layer.contains(leo, Variable.ANY, max));
		assertTrue(layer.contains(max, Variable.ANY, leo));

		// TODO check with non-symmetric AND non-inverse
		layer.addStatement(hasHomeCity, InverseMapImpl.HAS_INVERSE_PROPERTY,
				hasInhabitant);
		layer.addStatement(leo, hasHomeCity, lautern);
		assertTrue(layer.contains(leo, hasHomeCity, lautern));
		assertFalse(layer.contains(lautern, hasHomeCity, leo));

	}
}
