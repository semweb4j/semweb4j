package org.ontoware.rdfreactor.runtime.example;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.annotation.RDF;
import org.ontoware.rdfreactor.runtime.FORD;
import org.ontoware.rdfreactor.runtime.RDFDataException;

@RDF("schema://person")
public class PersonWithWrapperNoClassRewriting {

	// property uris

	public static final URI AGE = new URIImpl("schema://age");

	public static final URI NAME = new URIImpl("schema://name");

	public static final URI HAS_FRIEND = new URIImpl("schema://hasFriend");

	public int getAge() throws RDFDataException {
		return (Integer) FORD.get(this, AGE, int.class);
	}

	public void setAge(int age) {
		FORD.set(this, AGE, age);
	}

	public String getName() throws RDFDataException {
		return (String) FORD.get(this,NAME, String.class);
	}

	public void setName(String name) {
		FORD.set(this,NAME, name );
	}

	public void addFriend(PersonWithWrapperNoClassRewriting p) {
		FORD.add(this, HAS_FRIEND, p);
	}

	public void removeFriend(PersonWithWrapperNoClassRewriting p) {
		FORD.remove(this, HAS_FRIEND, p);
	}

	public PersonWithWrapperNoClassRewriting[] getAllFriend() {
		return (PersonWithWrapperNoClassRewriting[]) FORD.getAll(this, HAS_FRIEND, PersonWithWrapperNoClassRewriting.class);
	}

	public PersonWithWrapperNoClassRewriting[] getAllInstances() {
		return (PersonWithWrapperNoClassRewriting[]) FORD.getAllInstances(this, PersonWithWrapperNoClassRewriting.class);
	}

}
