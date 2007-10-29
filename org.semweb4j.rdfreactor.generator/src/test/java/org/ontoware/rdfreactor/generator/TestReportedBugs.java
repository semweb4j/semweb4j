package org.ontoware.rdfreactor.generator;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;

public class TestReportedBugs {

	@Test
	public void testRangeXsdAnyUri() throws Exception {
		CodeGenerator.generate("./src/test/resources/test001.n3", 
				"./src/test-gen/java", "gen.rangexsdanyuri", Reasoning.rdfs, true, true);
	}

	@Test
	public void testRangeXsdInt() throws Exception {
		CodeGenerator.generate("./src/test/resources/test002.n3", 
				"./src/test-gen/java", "gen.rangexsdint", Reasoning.rdfs, true, true);
	}

	/** handling of props without domain */
	@Test
	public void testTagGeneration() throws Exception {
		CodeGenerator.generate("./src/test/resources/TagFilter.n3",
				"target/gen", "org.ontoware.tag", Reasoning.rdfs, true, false);
	}

	@Test
	public void testReac15() throws Exception {

		CodeGenerator.generate("./src/test/resources/reac15.rdfs",
				"./src/test-gen", "org.ontoware.rdfreactor.test.reac15",
				"RDFS", true, true, "");

	}

}
