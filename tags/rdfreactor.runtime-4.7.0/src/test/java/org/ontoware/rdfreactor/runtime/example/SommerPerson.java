package org.ontoware.rdfreactor.runtime.example;

import net.java.rdf.annotations.maprdf;
import net.java.rdf.annotations.rdf;

@rdf("urn:foo:bar")
@maprdf(name = "urn:foaf#name")
@SuppressWarnings("unused")
public class SommerPerson {

	String getName() {
		return null;
	}

	void setName(String value) {

	}

}
