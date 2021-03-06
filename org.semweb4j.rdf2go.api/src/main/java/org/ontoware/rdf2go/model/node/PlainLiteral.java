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

package org.ontoware.rdf2go.model.node;

/**
 * Marker interface for plain literals, i.e. those Literals that are neither
 * LanguageTagLiteral nor DatatypeLiteral
 * 
 * Implementations are expected to have valid implementations of equals( Object )
 * and hashCode()
 * @author voelkel
 * 
 */
public interface PlainLiteral extends Literal {

}
