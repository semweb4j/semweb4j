package org.ontoware.rdfreactor.generator;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Helper class to hold Protege extension URIs
 * 
 * @author voelkel
 * 
 */
public class Protege {

	public static final String NS = "http://protege.stanford.edu/system#";

	public static final URI MAX_CARDINALITY = new URIImpl(NS + "maxCardinality");

	public static final URI MIN_CARDINALITY = new URIImpl(NS + "minCardinality");

	public static final URI OVERRIDING_PROPERTY = new URIImpl(NS
			+ "OverridingProperty");

	public static final URI OVERRIDDEN_PROPERTY = new URIImpl(NS
			+ "overriddenProperty");

	public static final URI MAX_VALUE = new URIImpl(NS + "maxValue");

	public static final URI MIN_VALUE = new URIImpl(NS + "minValue");

	public static final URI ALLOWED_CLASSES = new URIImpl(NS + "allowedClasses");

}
