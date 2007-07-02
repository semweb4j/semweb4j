package org.ontoware.rdfreactor.generator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;

public class RangeXsdAnyUri {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRangeXsdAnyUri() throws Exception {
		CodeGenerator.generate("./src/test/java/org/ontoware/rdfreactor/generator/bug001.n3", 
				"./src/test/java", "gen.rangexsdanyuri", Reasoning.rdfs, true, true);
	}
}
