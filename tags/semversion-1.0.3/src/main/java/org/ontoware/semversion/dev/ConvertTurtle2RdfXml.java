package org.ontoware.semversion.dev;

import java.io.File;
import java.io.FileNotFoundException;

import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.util.ModelUtils;

public class ConvertTurtle2RdfXml {

	public static void main(String[] args) throws FileNotFoundException {
		File in  = new File("./src/main/resources/semversion.n3");
		File out = new File("./src/main/resources/semversion.rdf.xml");
		ModelUtils.convert(in, Syntax.Turtle, out, Syntax.RdfXml);
	}

}
