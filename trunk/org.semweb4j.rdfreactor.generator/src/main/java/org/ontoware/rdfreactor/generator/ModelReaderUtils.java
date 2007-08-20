package org.ontoware.rdfreactor.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;

/**
 * Helper class to read and load a schema file into a Jena RDF model.
 * 
 * @author mvo
 */
public class ModelReaderUtils {

	/**
	 * Load a RDF schema file into a Jena RDF model and return it
	 * 
	 * @param filename -
	 *            location of the schema file. can end be in N3 (*.n3) or
	 *            N-TRIPLE (*.nt) notation
	 * @return a Jena Model
	 */
	public static Model read(String filename) {
		Model m = RDF2Go.getModelFactory().createModel();
		m.open();

		FileInputStream fis;
		File f = new File(filename);
		try {
			fis = new FileInputStream(f);

			Syntax syntax = Syntax.RdfXml;

			if (filename.endsWith(".n3"))
				syntax = Syntax.Turtle;
			else if (filename.endsWith(".nt"))
				syntax = Syntax.Ntriples;

			try {
				m.readFrom(fis, syntax);
			} catch (ModelRuntimeException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			return m;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not find file: "
					+ f.getAbsolutePath() + " Exception: " + e);
		}

	}

}
