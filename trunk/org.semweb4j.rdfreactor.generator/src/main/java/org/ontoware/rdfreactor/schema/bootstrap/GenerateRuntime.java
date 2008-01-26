package org.ontoware.rdfreactor.schema.bootstrap;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;

public class GenerateRuntime {

	public static final String outdir = "./target/test-gen";

	public static void main(String[] args) throws Exception {
//		Model model = RDF2Go.getModelFactory().createModel();
//		model.open();
//		Model rdf = loadFromClassPathResource("rdf.xml");
//		Model rdfs = loadFromClassPathResource("rdfs.xml");
//		model.addAll(rdf.iterator());
//		model.addAll(rdfs.iterator());
//		
//		CodeGenerator.generate(model, new File(outdir),
//				"org.ontoware.rdfreactor.schema.rdfs", Reasoning.rdfs,
//				false, "");
		
//		Model model = RDF2Go.getModelFactory().createModel();
//		model.open();
//		Model owl = loadFromClassPathResource("owl.xml");
//		model.addAll(owl.iterator());
//		
//		CodeGenerator.generate(model, new File(outdir),
//				"org.ontoware.rdfreactor.schema.owl", Reasoning.rdfs,
//				false, "");
	}
	
	public static Model loadFromClassPathResource( String resourceName ) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream in = cl.getResourceAsStream(resourceName);
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		try {
			model.readFrom(in);
		} catch (ModelRuntimeException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return model;
		
	}

}
