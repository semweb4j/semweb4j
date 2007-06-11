package org.ontoware.rdfreactor.generator;

import java.io.File;
import java.net.URL;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.impl.jena24.IOUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * This class is used to generate the RDFS and OWL runtime parts.
 * 
 * @author voelkel
 * 
 */
public class InternalRuntimeGenerator {

	public static void main(String[] args) throws Exception {

		//generateRDFS();
		//generateOWL();

	}

	@SuppressWarnings("unused")
	private static void generateOWL() throws Exception {
		Model owl = (Model) IOUtils.read(
				new URL("http://www.w3.org/2002/07/owl#"))
				.getUnderlyingModelImplementation();
		
		CodeGenerator.generate(owl, new File("./src/main/java"),
				"org.ontoware.rdfreactor.schema.owl", Reasoning.rdfsAndOwl,
				false, false, "");
	}

	@SuppressWarnings("unused")
	private static void generateRDFS() throws Exception {
		Model rdf = (Model) IOUtils.read(
				new URL("http://www.w3.org/1999/02/22-rdf-syntax-ns#"))
				.getUnderlyingModelImplementation();

		Model rdfs = (Model) IOUtils.read(
				new URL("http://www.w3.org/2000/01/rdf-schema#"))
				.getUnderlyingModelImplementation();

		Model rdfAndRdfs = ModelFactory.createDefaultModel();
		rdfAndRdfs.add(rdf);
		rdfAndRdfs.add(rdfs);

		CodeGenerator.generate(rdfAndRdfs, new File("./src/main/java"),
				"org.ontoware.rdfreactor.schema.rdfschema", Reasoning.rdfs,
				false, false, "");
	}
}
