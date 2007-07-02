package org.ontoware.rdfreactor.generator;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;

public class DomainlessProperties {

	@Test
	public void testTagGeneration() throws Exception {
		CodeGenerator.generate("./src/test/resources/TagFilter.n3",
				"target/gen", "org.ontoware.tag", Reasoning.rdfs, true, false);
	}

}
