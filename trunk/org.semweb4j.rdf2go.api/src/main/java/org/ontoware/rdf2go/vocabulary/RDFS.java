/**
 * LICENSE INFORMATION
 *
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2010
 *
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * RDFS vocabulary items as URIs
 */
public class RDFS {

	/**
	 * The RDF Schema Namespace
	 */
	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

	protected static final URI toURI(String local) {
		return new URIImpl(RDFS_NS + local,false);
	}

	public static final URI Class = toURI("Class");

	public static final URI Datatype = toURI("Datatype");

	/**
	 * This URI was present in http://www.w3.org/TR/2000/CR-rdf-schema-20000327/ but removed for final 1.0 spec
	 * @deprecated
	 */
	@Deprecated
	public static final URI ConstraintProperty = toURI("ConstraintProperty");

	public static final URI Container = toURI("Container");

	public static final URI ContainerMembershipProperty = toURI("ContainerMembershipProperty");

	/**
	 * This URI was present in http://www.w3.org/TR/2000/CR-rdf-schema-20000327/ but removed for final 1.0 spec
	 * @deprecated
	 */
	@Deprecated
	public static final URI ConstraintResource = toURI("ConstraintResource");

	/** http://www.w3.org/2000/01/rdf-schema#Literal */
	public static final URI Literal = toURI("Literal");

	/** @deprecated This belongs to the 'rdf' namespace now */
	@Deprecated
	public static final URI XMLLiteral = toURI("XMLLiteral");

	/** http://www.w3.org/2000/01/rdf-schema#Resource * */
	public static final URI Resource = toURI("Resource");

	public static final URI comment = toURI("comment");

	public static final URI domain = toURI("domain");

	public static final URI label = toURI("label");

	public static final URI isDefinedBy = toURI("isDefinedBy");

	public static final URI range = toURI("range");

	public static final URI seeAlso = toURI("seeAlso");

	public static final URI subClassOf = toURI("subClassOf");

	public static final URI subPropertyOf = toURI("subPropertyOf");

	public static final URI member = toURI("member");

}