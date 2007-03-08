package org.ontoware.rdfreactor.runtime.example;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.RDFDataException;
import org.ontoware.rdfreactor.runtime.ReactorBaseImpl;

public class Person extends ReactorBaseImpl {

	/**
	 * class uri
	 */
	public static final URI PERSON = URIImpl.createURIWithoutChecking("schema://person");

	// property uris

	public static final URI AGE = URIImpl.createURIWithoutChecking("schema://age");

	public static final URI NAME = URIImpl.createURIWithoutChecking("schema://name");

	public static final URI HAS_FRIEND = URIImpl.createURIWithoutChecking("schema://hasFriend");

	/**
	 * @param model -
	 *            rdf2go model
	 * @param uri -
	 *            instance URI
	 */
	public Person(Model model, URI uri, boolean write) {
		super(model, PERSON, uri, write);
	}

	public int getAge() throws RDFDataException {
		return (Integer) get(AGE, int.class);
	}

	public void setAge(int age) {
		set(AGE, age);
	}

	public String getName() throws RDFDataException {
		return (String) get(NAME, String.class);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	public void addFriend(Person p) {
		add(HAS_FRIEND, p);
	}

	public boolean removeFriend(Person p) {
		return remove(HAS_FRIEND, p);
	}

	public Person[] getAllFriend() {
		return (Person[]) getAll(HAS_FRIEND, Person.class);
	}

	public Person[] getAllInstances() {
		return (Person[]) getAllInstances(super.model, Person.class);
	}

}
