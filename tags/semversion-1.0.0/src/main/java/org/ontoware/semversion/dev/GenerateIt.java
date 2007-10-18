package org.ontoware.semversion.dev;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdfreactor.generator.CodeGenerator;

public class GenerateIt {

	public static void main(String[] args) throws Exception {
		
		CodeGenerator.generate("./src/main/resources/semversion.n3",
				"./src/main/java",
				"org.ontoware.semversion.impl.generated",Reasoning.rdfs, true,true);
	}

}
