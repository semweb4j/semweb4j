package org.ontoware.rdf2go.impl.jena27;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.ModelFactory;


/**
 * @author voelkel
 * 
 */
public class IOUtils {
	
	private static final Logger log = LoggerFactory.getLogger(IOUtils.class);
	
	/**
	 * Read the file. Assume RDF/XML, unless file extension is '.nt' (N-TRIPLES)
	 * or '.n3' (N3).
	 * 
	 * @param filename to be read
	 * @return a Jena Model
	 */
	public static Model read(String filename) {
		File f = new File(filename);
		return read(f);
	}
	
	/** assume RDF/XML */
	public static void readIntoJenaModel(com.hp.hpl.jena.rdf.model.Model m, Reader reader) {
		m.read(reader, "", "RDF/XML");
	}
	
	public static void readIntoJenaModel(com.hp.hpl.jena.rdf.model.Model m, File f) {
		log.warn("reading model from " + f.getAbsoluteFile());
		try {
			FileInputStream fis = new FileInputStream(f);
			Reader r = new InputStreamReader(fis, "utf8");
			
			String lang = "RDF/XML";
			
			if(f.getName().endsWith(".n3")) {
				log.warn("file end with '.n3', reading as N3");
				lang = "N3";
			} else if(f.getName().endsWith(".nt")) {
				log.warn("file end with '.nt', reading as N-TRIPLE");
				lang = "N-TRIPLE";
			} else {
				log.warn("default: read as RDF/XML");
			}
			
			m.read(r, "", lang);
			
		} catch(FileNotFoundException e) {
			throw new RuntimeException("Could not find file: " + f.getAbsolutePath()
			        + " Exception: " + e);
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Model read(File f) {
		com.hp.hpl.jena.rdf.model.Model m = ModelFactory.createDefaultModel();
		readIntoJenaModel(m, f);
		return new ModelImplJena26(null, m);
	}
	
	public static Model read(URL url) {
		com.hp.hpl.jena.rdf.model.Model m = ModelFactory.createDefaultModel();
		
		// TODO guess language
		m.read("" + url, "" + url, "RDF/XML");
		return new ModelImplJena26(null, m);
	}
	
	public static Model read(Reader reader) {
		com.hp.hpl.jena.rdf.model.Model m = ModelFactory.createDefaultModel();
		readIntoJenaModel(m, reader);
		return new ModelImplJena26(null, m);
	}
	
	/**
	 * TODO test xml ecnoding
	 * 
	 * @throws IOException from underlying writer
	 */
	public static void writeJenaModel(com.hp.hpl.jena.rdf.model.Model jm, Writer w)
	        throws ModelRuntimeException, IOException {
		DumpUtils.addCommonPrefixesToJenaModel(jm);
		
		log.error("Writing RDF/XML in UTF-8...");
		// Jena is not adding this to the XML file, when choosing UTF-8
		// encoding
		w.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		jm.write(w, "RDF/XML", "");
	}
	
	public static void write(Model m, Writer w) throws ModelRuntimeException {
		Model jenaModel = new ModelImplJena26(Reasoning.none);
		jenaModel.addAll(m.iterator());
		com.hp.hpl.jena.rdf.model.Model jm = (com.hp.hpl.jena.rdf.model.Model)jenaModel
		        .getUnderlyingModelImplementation();
		try {
			writeJenaModel(jm, w);
		} catch(IOException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	public static void write(Model m, String filename, String format) throws ModelRuntimeException {
		Model jenaModel = new ModelImplJena26(Reasoning.none);
		jenaModel.addAll(m.iterator());
		com.hp.hpl.jena.rdf.model.Model jm = (com.hp.hpl.jena.rdf.model.Model)jenaModel
		        .getUnderlyingModelImplementation();
		DumpUtils.addCommonPrefixesToJenaModel(jm);
		
		try {
			// encoding!
			if(format.equalsIgnoreCase("rdf/xml")) {
				log.error("Writing RDF/XML in UTF-8...");
				FileOutputStream fos;
				fos = new FileOutputStream(new File(filename));
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				// Jena is not adding this to the XML file, when choosing UTF-8
				// encoding
				osw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
				jm.write(osw, "RDF/XML", "");
			} else
				jm.write(new FileWriter(new File(filename)), format, "");
			
		} catch(FileNotFoundException e) {
			throw new ModelRuntimeException(e);
		} catch(IOException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
}
