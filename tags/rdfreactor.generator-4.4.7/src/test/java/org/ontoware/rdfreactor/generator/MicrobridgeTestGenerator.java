package org.ontoware.rdfreactor.generator;


public class MicrobridgeTestGenerator {

	/**
	 * generate code for test
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CodeGenerator.generate("./data/tag.n3", "./test-src", "org.ontoware.rdfreactor.test.tag",
				"RDFS", true, true,"");
	}

}
