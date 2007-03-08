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
package org.ontoware.rdf2go.impl.sesame2;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.AbstractModelFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryImpl;
import org.openrdf.sail.SailInitializationException;
import org.openrdf.sail.inferencer.MemoryStoreRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

/**
 * 
 * @author voelkel
 * 
 */
public class ModelFactoryImpl extends AbstractModelFactory implements
		ModelFactory {

	private static final Log log = LogFactory.getLog(ModelFactoryImpl.class);

	private boolean rdfsReasoning = false;

	private void setFlags(Properties p) {
		// check for Reasoning
		String reasoning = p.getProperty(ModelFactory.REASONING);
		if (reasoning != null) {
			rdfsReasoning = reasoning.equalsIgnoreCase(Reasoning.rdfs
					.toString());
		}

		// TODO: check for other persistence settings, assume in-memory for now
	}

	/**
	 * @return a Model, ready to use (open() has been executed already)
	 */
	public Model createModel(Properties p) throws ModelRuntimeException {
		setFlags(p);

		RepositoryModel impl = new RepositoryModel(rdfsReasoning);

		log.debug("Reasoning = "+rdfsReasoning);
		
		return impl;
	}

	public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
		setFlags(p);

		Repository sesameRepository;

		if (rdfsReasoning) {
			sesameRepository = new RepositoryImpl(
					new MemoryStoreRDFSInferencer(new MemoryStore()));
		} else {
			sesameRepository = new RepositoryImpl(new MemoryStore());
		}

		try {
			sesameRepository.initialize();
		} catch (SailInitializationException e) {
			throw new ModelRuntimeException(e);
		}
		ModelSetImplSesame impl = new ModelSetImplSesame(sesameRepository);
		impl.open();
		return impl;
	}

}
