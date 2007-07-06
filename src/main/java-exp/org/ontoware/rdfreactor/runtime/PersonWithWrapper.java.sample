package org.ontoware.rdfreactor.runtime.example;

import java.util.Set;

import org.ontoware.rdfreactor.annotation.Property;
import org.ontoware.rdfreactor.annotation.RDF;

@RDF("schema://person")
public class PersonWithWrapper {

	@Property("schema://age")
	int age;
	
	@Property("schema://name")
	String name;

	@Property("schema://hasFriend")
	Set<PersonWithWrapper> friends;
	
	public void addFriend( PersonWithWrapper p) {
		friends.add(p);
	}

}
