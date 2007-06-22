/*
 * Created on 26.11.2004
 *
 */
package org.ontoware.rdfreactor.runtime;

import org.ontoware.rdf2go.model.node.Resource;


/**
 * instances of this interfaces can be used as rdf-objects in the RDFReactor
 * framework
 * 
 * @author mvo
 * 
 */
@Patrolled
public interface ResourceEntity {

	/**
	 * @return the RDF resource as an RDF2GO resource
	 */
	public Resource getResource();

}
