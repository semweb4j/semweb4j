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
package org.ontoware.rdf2go.model.node;

/**
 * sub-interface of RDFLiteral that represents Language Tag Literals
 * 
 * Implementations are expected to have valid implementations of equals( Object )
 * and hashCode()

 * @author mvo
 * @see org.ontoware.rdf2go.RDFLiteral
 */
public interface LanguageTagLiteral extends Literal {

	/**
	 * get the language tag
	 * 
	 * @return The language tag, always casted to lowercase
	 */
	public String getLanguageTag();

}