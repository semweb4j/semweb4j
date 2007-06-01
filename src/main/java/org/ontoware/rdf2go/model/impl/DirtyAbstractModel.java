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
							log.debug("Other model does not contain " + s);
							return false;
						}
					}
					return true;
				} else {
					log.debug("Models do not have the same size");
					return false;
				}

			} else {
				log.debug("object is not an instance of ModelAdapter, it's "
						+ other.getClass());
				return false;
			}
		} catch (ModelRuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * You should overrride this method!
	 */
	public boolean isValidURI(String uriString) {
		return true;
	}

}
