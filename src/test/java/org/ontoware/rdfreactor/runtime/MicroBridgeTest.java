package org.ontoware.rdfreactor.runtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.AllTests;
import org.ontoware.rdfreactor.runtime.example.Person;
import org.ontoware.rdfreactor.test.tag.File;
import org.ontoware.rdfreactor.test.tag.Tag;
import org.ontoware.rdfreactor.test.tag.TagAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class MicroBridgeTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(MicroBridgeTest.class);

	Model m;

	@Before
	public void setUp() {
		m = RDF2Go.getModelFactory().createModel();
		m.open();
		assert m != null;

	}

	@Test
	public void testAdd() throws Exception {
		log.debug("----------------------");
		URI resource = new URIImpl("data://r1");
		URI prop = new URIImpl("data://p1");
		Bridge.addValue(m, resource, prop, "Jim");

		Iterator<? extends Statement> it = m.findStatements(resource, prop,
				Variable.ANY);
		assertTrue(it.hasNext());
	}

	@Test
	public void testEqual() throws Exception {

		Person p = new Person(m, new URIImpl("data://p1"), true);
		Person q = new Person(m, new URIImpl("data://p1"), true);
		URI u = new URIImpl("data://p1");

		assertTrue(p.equals(q));
		assertTrue(p.equals(u));
	}

	@Test
	public void testSet() throws Exception {
		URI resource = new URIImpl("data://r1");
		URI prop1 = new URIImpl("data://p1");
		URI prop2 = new URIImpl("data://p1");
		Bridge.addValue(m, resource, prop1, "Jon");
		Bridge.setValue(m, resource, prop2, "Jim");

		assertTrue(m.contains(resource, prop1, Variable.ANY));
	}

	@Test
	public void testRemove() throws Exception {
		URI resource = new URIImpl("data://r1");
		URI prop = new URIImpl("data://p1");

		m.addStatement(resource, prop, "Jim");
		assertTrue(m.iterator().hasNext());

		Iterator<? extends Statement> it = m.findStatements(resource, prop,
				Variable.ANY);
		assertTrue(it.hasNext());
		assertEquals(1, Bridge.getAllValues_asSet(m, resource, prop,
				String.class).size());
		assertEquals(1,
				Bridge.getAllValues(m, resource, prop, String.class).length);

		Iterator<? extends Statement> it2 = m.findStatements(resource, prop,
				Variable.ANY);
		assertTrue(it2.hasNext());
		while (it2.hasNext()) {
			Statement s = it2.next();
			// compare rdfnode to value
			assertTrue(s.getObject().equals("Jim"));
		}

		assertTrue(Bridge.removeValue(m, resource, prop, "Jim"));

		assertEquals(0,
				Bridge.getAllValues(m, resource, prop, String.class).length);
	}

	@Test
	public void testSparqlSelect() throws Exception {

		// test

		File fileA = new File(m, new URIImpl("file://a"));
		File fileB = new File(m, new URIImpl("file://b"));

		Tag tagSemweb = new Tag(m, new URIImpl("tag://semweb"));
		Tag tagPaper = new Tag(m, new URIImpl("tag://paper"));

		TagAssignment ass1 = new TagAssignment(m, new URIImpl("ass://1"));
		TagAssignment ass2 = new TagAssignment(m, new URIImpl("ass://2"));
		TagAssignment ass3 = new TagAssignment(m, new URIImpl("ass://3"));

		// a = 'paper'
		ass1.setTag(tagPaper);
		ass1.setFile(fileA);

		// a = 'semweb'
		ass2.setTag(tagSemweb);
		ass2.setFile(fileA);
		// b = 'semweb'
		ass3.setTag(tagSemweb);
		ass3.setFile(fileB);

		Map<String, Class> returnTypes = new HashMap<String, Class>();
		returnTypes.put("file", File.class);
		OOQueryResultTable result = Bridge.getSparqlSelect(m, returnTypes,
				"SELECT ?file WHERE " + "{ ?a <" + TagAssignment.FILE
						+ "> ?file. " + "?a <" + TagAssignment.TAG + "> <"
						+ tagSemweb + "> . }");

		for (OOQueryRow row : result) {
			assertEquals(result.getVariables().size(), 1);
			assertEquals(File.class, row.getValue("file").getClass());
		}

	}

}
