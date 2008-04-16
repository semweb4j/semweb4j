package org.ontoware.rdfreactor.generator;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;

public class TemplateTest {

	public static final String outdir = "./src/test/java";

	@Test
	public void testGeneration() throws Exception {
		CodeGenerator.generate(
//				"./src/test/resources/simpleSchema.n3",
				"./src/test/resources/testTemplate.n3",
				outdir,
				"com.example",
				Reasoning.rdfs, 
				true, 
				"");
	}

}
