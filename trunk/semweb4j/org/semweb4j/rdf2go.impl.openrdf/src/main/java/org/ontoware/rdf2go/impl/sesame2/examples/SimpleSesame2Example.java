/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.impl.sesame2.examples;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.vocabulary.RDF;

/**
 * Simple usage example for the Sesame2 driver for rdf2go
 * @author sauermann
 */
public class SimpleSesame2Example {

    public static void main(String[] args) throws MalformedURLException, ModelRuntimeException
    {
    	RDF2Go.register( new org.ontoware.rdf2go.impl.sesame2.ModelFactoryImpl() );
    	
        // create a model
        Model m = RDF2Go.getModelFactory().createModel();
        // read the rdf vocab
        URL url = new URL("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        Reader r;
        try {
            r = new InputStreamReader(url.openStream());
            m.readFrom(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // how many triples do we have?
            System.out.println("The RDF spec has "+m.size()+" triples.");
            
            // only keep the classes and propeties
            Model less = RDF2Go.getModelFactory().createModel();
            less.addAll(m.findStatements(null, RDF.type, null));
            // lets print it on system.out
            System.out.println("Let us see if I can print the type-triples in turtle.");
            Writer out = new OutputStreamWriter(System.out);
            less.writeTo(out, Syntax.Turtle);
                        
        } catch (ModelRuntimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
    }
}
