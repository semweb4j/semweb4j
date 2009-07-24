/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go.model.impl;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an abstract class which provides quick & dirty implementations to get
 * an adapter started quickly. Do not use this code in production environments.
 * 
 * @author voelkel
 * 
 */
public abstract class DirtyAbstractModel extends AbstractModel {

	private static Logger log = LoggerFactory.getLogger(DirtyAbstractModel.class);

	/**
	 * Adapter implementations are strongly encouraged to overwrite this method.
	 * It is slow and semantically not correct.
	 */
	public boolean isomorphicWith(Model other) {
		assertModel();
		try {
			if (other instanceof AbstractModel) {
				AbstractModel abstractModel = (AbstractModel) other;
				if (size() == abstractModel.size()) {

					for (Statement s : this) {
						if (!abstractModel.contains(s)) {
							log.trace("Other model does not contain " + s);
							return false;
						}
					}
					return true;
				} 
				//else 
				log.trace("Models do not have the same size");
				return false;

			} 
			//else 
			log.trace("object is not an instance of ModelAdapter, it's "
					+ other.getClass());
			return false;
		} catch (ModelRuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * You should overrride this method!
	 */
	public boolean isValidURI(@SuppressWarnings("unused")
	String uriString) {
		log.warn("You used method isValidURI() from DirtyAbstractModel, which always returns TRUE");
		return true;
	}

}
