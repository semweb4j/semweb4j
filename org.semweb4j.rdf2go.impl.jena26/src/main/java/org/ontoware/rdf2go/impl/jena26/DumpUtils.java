package org.ontoware.rdf2go.impl.jena26;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DumpUtils {
	
	private static Logger log = LoggerFactory.getLogger(DumpUtils.class);
	
	public static void addCommonPrefixesToJenaModel(com.hp.hpl.jena.rdf.model.Model jenaModel) {
		jenaModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		jenaModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		jenaModel.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		jenaModel.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		jenaModel.setNsPrefix("rnd", "rnd://rnd/");
		
		// SMW
		jenaModel.setNsPrefix("smw", "http://smw.ontoware.org/2005/smw#");
		jenaModel.setNsPrefix("crawl", "http://www.xam.de/2006/07/crawl#");
		
	}
	
	public static String toN3(Model m) {
		try {
			ModelImplJena26 jModel = new ModelImplJena26(Reasoning.none);
			jModel.addAll(m.iterator());
			StringWriter sw = new StringWriter();
			addCommonPrefixesToJenaModel(jModel.getInternalJenaModel());
			jModel.getInternalJenaModel().write(sw, "N3");
			return sw.toString();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * dumps the model at the logger of the implementation.
	 * 
	 * --usually only used for debugging--
	 * 
	 * @param options null for default. format: "n3"|"rdfxml"|"ntriple", output:
	 *            "sysout"|"log"
	 * @throws ModelRuntimeException Jena knows: "RDF/XML","RDF/XML-ABBREV",
	 *             "N-TRIPLE" and "N3".
	 */
	public static void dump(Model m, String[] options) throws ModelRuntimeException {
		boolean n3 = false;
		boolean rdf = false;
		boolean ntriples = false;
		boolean sysout = true;
		boolean _log = false;
		if(options != null) {
			n3 = Arrays.asList(options).contains("n3");
			rdf = Arrays.asList(options).contains("rdfxml");
			ntriples = Arrays.asList(options).contains("nt");
			sysout = Arrays.asList(options).contains("sysout");
			_log = Arrays.asList(options).contains("log");
		}
		if(!(sysout | _log))
			sysout = true;
		
		if(n3 | rdf | ntriples) {
			ModelImplJena26 jModel = new ModelImplJena26(Reasoning.none);
			
			// makes the N3 model look nicer
			// TODO guess all namespaces automatically
			addCommonPrefixesToJenaModel(jModel.getInternalJenaModel());
			
			jModel.addAll(m.iterator());
			StringWriter sw = new StringWriter();
			if(n3) {
				jModel.getInternalJenaModel().write(sw, "N3-PP");
				if(sysout)
					System.out.println(sw.getBuffer());
				if(_log)
					log.debug("dump: " + sw.getBuffer());
			}
			if(rdf) {
				jModel.getInternalJenaModel().write(sw, "RDF/XML");
				if(sysout)
					System.out.println(sw.getBuffer());
				if(_log)
					log.debug("dump: " + sw.getBuffer());
			}
			if(ntriples) {
				jModel.getInternalJenaModel().write(sw, "N-TRIPLE");
				if(sysout)
					System.out.println(sw.getBuffer());
				if(_log)
					log.debug("dump: " + sw.getBuffer());
			}
		} else {
			Iterator<Statement> sit = m.iterator();
			while(sit.hasNext()) {
				sit.next().dump(options);
			}
		}
		
	} // //////////////////////////////
	
	public static void dump(ModelSet modelSet, String[] options) throws ModelRuntimeException {
		Iterator<? extends Model> it = modelSet.getModels();
		assert it != null;
		while(it.hasNext()) {
			Model m = it.next();
			DumpUtils.dump(m, options);
		}
	}
	
}
