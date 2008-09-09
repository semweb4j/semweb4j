package org.ontoware.rdfreactor.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.runtime.example.Person;

@SuppressWarnings("unused")
public class PersonTest {

	protected org.ontoware.rdf2go.model.node.URI instanceURI;

	protected org.ontoware.rdf2go.model.Model model;

	@Before
	public void setUp() throws Exception {
		this.model = RDF2Go.getModelFactory().createModel();
		this.model.open();
		this.model.removeAll();
		this.instanceURI = new URIImpl("data://person-1");
	}

	public void testTyping() throws Exception {
		URI jim = new URIImpl("data://jim");
		Person p = new Person(this.model, jim, true);
		Assert.assertTrue(this.model.contains(jim, RDF.type, Person.PERSON));
	}

	// //////////////
	// specific tests

	public void testSetAge() throws RDFDataException {
		Person p = new Person(this.model, this.instanceURI, true);
		p.setAge(18);
		int age = p.getAge();
		assertEquals(18, age);
	}

	public void testSetName() throws RDFDataException {
		Person p = new Person(this.model, this.instanceURI, true);
		p.setName("Max Mustermann");
		assertEquals("Max Mustermann", p.getName());
	}

	public void testHashCode() throws ModelRuntimeException {
		Person p1 = new Person(this.model, this.instanceURI, true);
		Person p2 = new Person(this.model, this.instanceURI, true);
		Person p3 = new Person(this.model, new URIImpl("test://otheruri"), true);
		assertEquals(p1.hashCode(), p2.hashCode());
		assertNotSame(p1.hashCode(), p3.hashCode());
	}

	public void testReactorBaseNamed() {
	}

	/*
	 * Class under test for boolean equals(Object)
	 */
	public void testEqualsObject() throws ModelRuntimeException {
		Person p1 = new Person(this.model, this.instanceURI, true);
		Person p2 = new Person(this.model, this.instanceURI, true);
		Person p3 = new Person(this.model, new URIImpl("test://otheruri"), true);
		assertEquals(p1, p2);
		assertNotSame(p1, p3);
	}

	public void testGetURI() {
		Person p1 = new Person(this.model, this.instanceURI, true);
		assertEquals(this.instanceURI, p1.getResource());
	}

	/*
	 * Class under test for String toString()
	 */
	public void testToString() {
	}

	public void testGet() throws RDFDataException {
		Person p1 = new Person(this.model, this.instanceURI, true);
		Integer a = p1.getAge();
		Assert.assertNull(a);
		p1.setAge(21);
		assertEquals(21, (int) p1.getAge());
	}

	public void testGetAll() {
	}

	public void testSet() {
	}

	public void testSetAll() {
	}

	public void testUpdate() {
	}

	public void testTwoStatements() throws Exception {
		this.model.addStatement(this.instanceURI, this.instanceURI, "a");
		this.model.addStatement(this.instanceURI, this.instanceURI, "b");
	}

	@Ignore("outdated")
	@Test
	public void testAdd() throws Exception {
		// create Person p
		Person p = new Person(this.model, new URIImpl("data://jim"), true);
		assertTrue("model contains a Person after add", this.model.contains(p
				.getResource(), RDF.type, Person.PERSON));

		// set name
		p.setName("Jim");

		assert this.model.contains(p.getResource(), RDF.type, Person.PERSON);
		assert this.model.contains(p.getResource(), Person.NAME, "Jim");

		// create Persons q1 and q2
		Person q1 = new Person(this.model, new URIImpl("data://jon"), true);
		q1.setName("Jon");
		Person q2 = new Person(this.model, new URIImpl("data://joe"), true);
		q2.setName("Joe");

		// add friends
		assertEquals(0, p.getAllFriend().length);
		p.addFriend(q1);
		assertEquals(1, p.getAllFriend().length);
		p.addFriend(q2);
		assertEquals(2, p.getAllFriend().length);
	}

	@Ignore("outdated")
	@Test
	public void testRemove() throws ModelRuntimeException {
		Person p = new Person(this.model, this.instanceURI, true);
		Person q = new Person(this.model, new URIImpl("data://p1"), true);
		Person q2 = new Person(this.model, new URIImpl("data://p2"), true);
		p.addFriend(q);
		p.addFriend(q2);
		assertEquals(2, p.getAllFriend().length);

		assertTrue(this.model.contains(p.getResource(), Person.HAS_FRIEND, q2
				.getResource()));

		assertTrue(p.removeFriend(q2));
		assertEquals(1, p.getAllFriend().length);
	}

	public void testRemoveAll() {
	}

	// /*
	// * Class under test for Statement getStatement(URI, ReactorBaseNamed)
	// */
	// public void testGetStatementURIReactorBaseNamed() {
	// Person p = new Person(model, instanceURI);
	// Statement s = p.getStatement(Person.NAME, "Jim");
	// assertEquals("Jim", s.getObject());
	// assertEquals(p.getURI(), s.getSubject());
	// assertEquals(Person.NAME, s.getPredicate());
	// }

	public void testDelete() {
	}

	/*
	 * Class under test for Statement getStatement(URI, String)
	 */
	public void testGetStatementURIString() {
	}

}
