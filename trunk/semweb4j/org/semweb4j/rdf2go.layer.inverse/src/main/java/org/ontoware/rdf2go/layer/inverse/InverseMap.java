package org.ontoware.rdf2go.layer.inverse;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.URI;

public interface InverseMap {

	/**
	 * 
	 * @param propertyURI
	 * @return the inverse property
	 */
	public URI getInverseProperty(URI propertyURI);

	public URI getPropertyOfInverseProperty(URI inversePropertyURI) throws InversePropertyException, ModelRuntimeException;

	public void registerInverse(URI propertyURI, URI inversePropertyURI) throws ModelRuntimeException;

	public void unregisterInverse(URI propertyURI, URI inversePropertyURI) throws ModelRuntimeException;

	public boolean isMeta(URI predicate);

}
