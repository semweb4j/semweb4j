package org.ontoware.rdfreactor.generator;

import org.junit.Ignore;
import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;

public class TestReportedBugs {

	public static final String outdir = "./target/test-gen";

	@Test
	public void testRangeXsdAnyUri() throws Exception {
		CodeGenerator.generate("./src/test/resources/test001.n3", outdir,
				"gen.rangexsdanyuri", Reasoning.rdfs, true);
	}

	@Test
	public void testRangeXsdInt() throws Exception {
		CodeGenerator.generate("./src/test/resources/test002.n3", outdir,
				"gen.rangexsdint", Reasoning.rdfs, true);
	}

	/** handling of props without domain */
	@Test
	public void testTagGeneration() throws Exception {
		CodeGenerator.generate("./src/test/resources/TagFilter.n3", outdir,
				"org.ontoware.tag", Reasoning.rdfs, true);
	}

	@Test
	public void testReac15() throws Exception {

		CodeGenerator.generate("./src/test/resources/reac15.rdfs", outdir,
				"org.ontoware.rdfreactor.test.reac15", "RDFS", true, true, "");

	}

	// This test needs too much heap space when running in maven, but is OK when
	// running from Eclipse (and setting -Xmx1024M

	// @Test
	// public void testReac11() throws Exception {
	// CodeGenerator.generate("./src/test/resources/ncal_data.xml", outdir,
	// "org.ontoware.rdfreactor.test.reac11", "RDFS", true, true, "NCal");
	// }

	// @Test
	// // http://www.semanticdesktop.org/ontologies/2007/01/19/nie/nie_data.rdfs
	// public void testReac11() throws Exception {
	// URL url =new
	// URL("http://www.semanticdesktop.org/ontologies/2007/01/19/nie/nie_data.rdfs");
	// URLConnection conn = url.openConnection();
	// conn.connect();
	// InputStream in = conn.getInputStream();
	// Model m = RDF2Go.getModelFactory().createModel();
	// m.open();
	// m.readFrom(in);
	//		
	// CodeGenerator.generate(m, new File(outdir),
	// "org.ontoware.rdfreactor.test.reac15", Reasoning.rdfs, true, "");
	// }

	// @Test
	// // FIXME broken with rdfs+owl
	// public void testReac18() throws Exception {
	// String schemafile = "./src/test/resources/reac18.n3";
	// String packagename = "org.ontoware.rdfreactor.test.reac18";
	//		
	// CodeGenerator.generate("./src/test/resources/reac18.n3", outdir,
	// "org.ontoware.rdfreactor.test.reac18", Reasoning.rdfsAndOwl,
	// true, "");
	//
	// Model schemaDataModel = CodeGenerator.loadSchemaDataModel(schemafile);
	// JModel jm = ModelGenerator.createFromRDFS_AND_OWL(schemaDataModel,
	// packagename, true);
	// jm.addInverseProperties();
	//
	// System.out.println( jm.toReport() );
	//		
	//
	// }

	@Test
	@Ignore
	// takes ages to run and ran ok
	public void testGenerateNepomukUnifiedOntology() throws Exception {
		CodeGenerator.generate(
				"./src/test/resources/unified_nepomuk_ontologies_merged.rdfs",
				"./src/test_gen",
				"org.semanticdesktop.nepomuk.comp.rdfsbeans.rdfreactor",
				Reasoning.rdfs, true);
	}
}
