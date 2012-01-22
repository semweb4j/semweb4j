/*
 * LICENSE INFORMATION Copyright 2005-2007 by FZI (http://www.fzi.de). Licensed
 * under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel <ORGANIZATION> = FZI Forschungszentrum Informatik
 * Karlsruhe, Karlsruhe, Germany <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.testdata;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;


/**
 * Class that loads test-data models for JUnit tests.
 * 
 * @author sauermann
 */
public class TestData {
	
	/**
	 * triples in foaf
	 */
	public static final long FOAFSIZE = 536;
	
	/**
	 * triples in ical
	 */
	public static final long ICALSIZE = 1317;
	
	private static Model foafBuffered = null;
	
	private static Model icalBuffered = null;
	
	/**
	 * loads foaf once, and then returns the model again and again. Use this if
	 * you need the same model over and over in JUnit tests.
	 * 
	 * @param factory the factory to use to create the model
	 * @return the loaded model
	 */
	public static Model loadFoafBuffered(ModelFactory factory) throws IOException,
	        ModelRuntimeException {
		if(factory == null)
			throw new IllegalArgumentException("Factory may not be null");
		if(foafBuffered == null) {
			foafBuffered = loadFoaf(factory);
		}
		return foafBuffered;
	}
	
	/**
	 * load the foaf vocabulary from resource-stream.
	 * 
	 * The consumer must close the model!
	 * 
	 * @param factory factory to create model
	 * @return a loaded model
	 * @throws ModelRuntimeException ...
	 * @throws IOException ...
	 */
	public static Model loadFoaf(ModelFactory factory) throws IOException, ModelRuntimeException {
		Model result = factory.createModel();
		result.open();
		InputStream in = getFoafAsStream();
		try {
			result.readFrom(in);
		} finally {
			in.close();
		}
		return result;
	}
	
	/**
	 * To get this testcase to run in Eclipse, make sure to patch your compiler
	 * settings to NOT exclude *.xml files.
	 * 
	 * @return as RDF/XML
	 */
	public static InputStream getFoafAsStream() {
		String FOAFRESOURCE = "org/ontoware/rdf2go/testdata/foaf.xml";
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream in = cl.getResourceAsStream(FOAFRESOURCE);
		return in;
	}
	
	/**
	 * loads ICAL once, and then returns the model again and again. Use this if
	 * you need the same model over and over in JUnit tests.
	 * 
	 * @param factory the factory to use to create the model
	 * @return the loaded model
	 */
	public static Model loadICALBuffered(ModelFactory factory) throws IOException,
	        ModelRuntimeException {
		if(icalBuffered == null) {
			icalBuffered = loadICAL(factory);
		}
		return icalBuffered;
	}
	
	/**
	 * load the icaltzd vocabulary from resource-stream
	 * 
	 * @param factory factory to create model
	 * @return a loaded model
	 * @throws ModelRuntimeException ...
	 * @throws IOException ...
	 */
	public static Model loadICAL(ModelFactory factory) throws IOException, ModelRuntimeException {
		Model result = factory.createModel();
		result.open();
		InputStream in = getICALAsStream();
		try {
			result.readFrom(in);
		} finally {
			in.close();
		}
		return result;
	}
	
	/**
	 * 
	 * @return icaltzd.rdfs as an InputStream
	 */
	public static InputStream getICALAsStream() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream in = cl.getResourceAsStream("org/ontoware/rdf2go/testdata/icaltzd.rdfs");
		return in;
	}
	
	/**
	 * A simple self-check for OSGi-debugging
	 * 
	 * @param args ...
	 */
	public static void main(String[] args) {
		if(getFoafAsStream() == null)
			throw new RuntimeException("Could not load FOAF from internally packaged file");
		
		// else
		System.out.println("FOAF found.");
	}
	
}
