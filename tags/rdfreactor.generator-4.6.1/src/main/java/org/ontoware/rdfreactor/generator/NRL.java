package org.ontoware.rdfreactor.generator;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Documented in http://www.semanticdesktop.org/ontologies/2007/08/15/nrl/
 * 
 * @author voelkel
 * 
 */
public class NRL {

	public static final String NS = "http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#";

	public static final URI CARDINALITY = new URIImpl(NS + "cardinality");

	public static final URI MIN_CARDINALITY = new URIImpl(NS + "minCardinality");
	
	public static final URI MAX_CARDINALITY = new URIImpl(NS + "maxCardinality");

	public static final URI INVERSE_PROPERTY = new URIImpl(NS
			+ "inverseProperty");

}
