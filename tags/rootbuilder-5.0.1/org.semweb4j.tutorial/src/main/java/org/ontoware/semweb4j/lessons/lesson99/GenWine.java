package org.ontoware.semweb4j.lessons.lesson99;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdfreactor.generator.CodeGenerator;

/**
 * Generate from the famous wine ontology.
 * @author voelkel
 */

public class GenWine {
	public static void main(String[] args) throws Exception {
		CodeGenerator
				.generate(
						"./src/main/java/org/ontoware/semweb4j/lessons/lesson99/wine.rdf.xml",
						"./src/main/java", "org.ontoware.wine", Reasoning.rdfs,
						true, false, "");
		
	}
	
}