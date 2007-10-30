package org.ontoware.rdf2go.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ToN3 {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Model m = ModelFactory.createDefaultModel();
		m
				.read(
						new FileInputStream(
								new File(
										"P:\\__sirdf\\rdf2go\\src\\org\\ontoware\\rdf2go\\example\\thiemann.foaf.rdf.xml")),
						"");
		m.write( System.out, "N-TRIPLES");
	}
}
