package org.ontoware.rdfreactor.example;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdfreactor.generator.CodeGenerator;

public class GenerateSomething {

	public static void main(String[] args) throws Exception {
		// CodeGenerator.generate(schemafilename, outdir, packagename,
		// semantics,
		// skipbuiltins, alwaysWriteToModel)
		CodeGenerator.generate("./data/myschema.n3", "src/gen",
				"org.ontoware.myproject", Reasoning.rdfs, true, true);
	}
}
