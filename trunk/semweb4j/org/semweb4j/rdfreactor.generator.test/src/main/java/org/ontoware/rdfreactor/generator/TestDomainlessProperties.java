package org.ontoware.rdfreactor.generator;

import org.ontoware.rdf2go.Reasoning;

public class TestDomainlessProperties {

	public static void main(String[] args) throws Exception {
		CodeGenerator.generate("./src/main/resources/TagFilter.n3",
				"target/gen", "org.ontoware.tag", Reasoning.rdfs, true, false);
	}

}
