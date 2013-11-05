package org.ontoware.rdfreactor.generator;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;

public class ModelGeneratorTest {

	public static final String outdir = "target/generated-test-sources/rdfs-classes";

	@Test
	public void testGeneration() throws Exception {
		CodeGenerator.generate("src/test/resources/test001.n3",
				outdir, "com.example.modelgeneratortest", Reasoning.owl, true);
	}

}
