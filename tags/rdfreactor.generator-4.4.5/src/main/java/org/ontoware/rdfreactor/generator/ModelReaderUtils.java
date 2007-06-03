package org.ontoware.rdfreactor.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

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
	 * 			location of the schema file. can end be in N3 (*.n3) or N-TRIPLE (*.nt) notation
	 * @return a Jena Model
	 */
	public static Model read(String filename) {
		Model m = ModelFactory.createDefaultModel();

		FileInputStream fis;
		File f = new File(filename);
		try {
			fis = new FileInputStream(f);
			
			String lang = "RDF/XML";

			if (filename.endsWith(".n3"))
				lang = "N3";
			else if (filename.endsWith(".nt"))
				lang = "N-TRIPLE";

			m.read(fis, "", lang);

			return m;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not find file: "
					+ f.getAbsolutePath() + " Exception: " + e);
		}

	}

}
