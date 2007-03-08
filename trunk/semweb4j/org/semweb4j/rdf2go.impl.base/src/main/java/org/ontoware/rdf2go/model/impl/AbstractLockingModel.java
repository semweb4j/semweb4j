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
package org.ontoware.rdf2go.model.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;

/**
 * adapter that maps the rdf2go model functions to a smaller subset of methods
 * 
 * @author mvo
 * 
 */
public abstract class AbstractLockingModel extends AbstractModel implements Model {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(AbstractLockingModel.class);

	/**
	 * Using lock() and unlock()
	 */
	@Override
	public synchronized void update(Diff diff) throws ModelRuntimeException {
		assertModel();
		lock();
		for (Statement r : diff.getRemoved()) {
			removeStatement(r);
		}

		for (Statement a : diff.getAdded()) {
			addStatement(a);
		}
		unlock();
	}
}
