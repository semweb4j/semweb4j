package org.ontoware.rdfreactor.generator;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class GenerateFromSchemas {

	private static Logger log = LoggerFactory
			.getLogger(GenerateFromSchemas.class);
	
	public static final String outdir = "./target/test-gen";
	
	public static void main(String[] args) throws Exception {
		File testGenDir = new File(outdir);
		testGenDir.mkdirs();
		// tests
		//generate("Class Cyles", "./test-data/classCycle.n3", "classcycle", "rdfs", true);
		///generate("RDFS Inferencing", "./test-data/InferencingTest.n3", "rdfsinferencing", "rdfs", true);
		generate("Cardinality", "./test-data/Cardinality.n3", "cardinality", "rdfs", true);
		// generate("Test for inverse properties", "./test-data/InverseTest.n3",
		// "inverse", "rdfs",
		// true);

		// examples
		generateExamples();
	}

	public static void generateExamples() throws Exception {
		// generate("RDF Schema itself", "./data/rdf+rdfs.rdf.xml", "rdfrdfs",
		// "rdfs", false);
		// generate("OWL Schema itself", "./data/owl.rdf.xml", "owl", "rdfs",
		// false);

		generate("ScutterVocab", "./data/http---www.ldodds.com-projects-slug-scuttervocab.rdfs",
				"scutter", "RDFS", true);
		generate("Dublin Core 1.1", "./data/dublin-core11.rdfs.rdf.xml", "dublincore", "RDFS", true);
		generate("Example", "./data/example.n3", "example", "RDFS", true);
		generate("MarcOnt", "./data/marcont.xml", "marcont", "OWL", true);
		generate("SchemaDoc", "./data/schemadoc.n3", "schemadoc", "RDFS", true);
		generate("SIOC", "./data/sioc.rdfs.xml", "sioc", "RDFS", true);
		generate("Tag", "./data/tag.n3", "tag", "RDFS", true);

		generate("Simplified FOAF", "./data/foaf.n3", "foafsimplified", "rdfs", true);

		generate("Full FOAF", "./data/foaf.rdf.xml", "foaf", "rdfs", true);
		// generate("Fresnel from http://www.w3.org/2004/09/fresnel",
		// "./data/fresnel.owl.xml",
		// "fresnel", "owl", true);
		// generate("Werner Thiemann", "./test-data/ronny.owl.n3", "ronny",
		// "owl", true);

	}

	public static void generate(String name, String schemaFile, String localPackagename,
			String semantics, boolean skipbuiltins) throws Exception {
		log.info("Generating " + name);
		CodeGenerator.generate(schemaFile, outdir, "org.ontoware.rdfreactor.testgen."
				+ localPackagename, semantics, skipbuiltins, true,"");
	}

}
