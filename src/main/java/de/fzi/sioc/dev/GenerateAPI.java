package de.fzi.sioc.dev;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.impl.jena24.IOUtils;
import org.ontoware.rdfreactor.generator.CodeGenerator;

import com.hp.hpl.jena.rdf.model.Model;

public class GenerateAPI {
	public static void main(String[] args) throws Exception {

		URL url = new URL("http://rdfs.org/sioc/ns");
		Model model = (Model) IOUtils.read(url)
				.getUnderlyingModelImplementation();

		model.write( new FileWriter( new File("./src/main/resources/sioc.rdfs.xml")));
		model.write( new FileWriter( new File("./src/main/resources/sioc.n3")),"N3");
		
		CodeGenerator.generate(model, new File("./src/main/java"),
				"org.rdfs.sioc", Reasoning.rdfs, true, true, "Sioc");

	}
}
