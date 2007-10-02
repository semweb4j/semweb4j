package org.ontoware.rdfreactor.generator;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;

public class ModelGeneratorTest {

	@Test
	public void testGeneration() throws Exception {
		CodeGenerator.generate("./src/test/resources/test001.n3",
				"target/tmp/gen", "com.example", Reasoning.owl, true, true);
	}

}
