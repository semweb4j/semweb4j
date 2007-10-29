package org.ontoware.rdfreactor.generator;

import org.junit.Test;

public class TestReportedBugs {

	@Test
	public void testReac15() throws Exception {

		CodeGenerator.generate("./src/test/resources/reac15.rdfs",
				"./src/test-gen", "org.ontoware.rdfreactor.test.reac15",
				"RDFS", true, true, "");

	}

}
