package org.ontoware.semweb4j.lessons.lesson5;

import org.ontoware.rdfreactor.generator.CodeGenerator;

public class GenCode { // aka Step0

	public static void main(String[] args) throws Exception {
		generateRDFReactor();
	}
	
	//TODO answer the Q&A questions in web
	private static void generateRDFReactor() throws Exception {
		String rdfsFile = "./src/main/java/org/ontoware/semweb4j/lessons/lesson5/peopletag.rdfs.n3";
		String outDir = "./src/main/java/";
		String targetPackage = "org.ontoware.semweb4j.lessons.lesson5.gen";
		String semantics = CodeGenerator.SEMANTICS_RDFS;
		//String semantics = CodeGenerator.SEMANTICS_OWL;
		//String semantics = CodeGenerator.SEMANTICS_RDFS_AND_OWL;
		boolean skipBuiltins = true;
		boolean alwaysWriteToModel = true;
		String prefix = "";
		CodeGenerator.generate(rdfsFile, outDir, targetPackage, semantics, skipBuiltins, alwaysWriteToModel, prefix);
	}

}
