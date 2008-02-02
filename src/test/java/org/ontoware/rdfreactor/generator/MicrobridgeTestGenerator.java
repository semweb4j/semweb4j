package org.ontoware.rdfreactor.generator;


public class MicrobridgeTestGenerator {

	public static final String outdir = "./target/test-gen";

	/**
	 * generate code for test
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CodeGenerator.generate("P:/swecr/org.semanticdesktop.swecr.model/src/main/resources/swcm.n3", 
				outdir,
				"org.semanticdesktop.swecr.model.rdf.generated",
				"RDFS", 
				true, 
				true,
				"");
//		CodeGenerator.generate("./src/test/resources/TagFilter.n3", "./src/test/java", "gen.org.ontoware.rdfreactor.test.tag",
//				"RDFS", false, true,"");
	}

}
