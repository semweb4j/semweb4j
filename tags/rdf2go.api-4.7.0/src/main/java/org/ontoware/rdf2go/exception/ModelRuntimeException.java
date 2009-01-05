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

package org.ontoware.rdf2go.exception;

/**
 * An runtime exception that is thrown by Model functions.
 * @author voelkel, sauermann
 */
public class ModelRuntimeException extends RuntimeException implements AnyModelException {

	private static final long serialVersionUID = 9146437799850363415L;
	
	public ModelRuntimeException() {
		super();
	}

	public ModelRuntimeException(String string) {
		super(string);
	}

    /**
     * @param message
     * @param cause
     */
    public ModelRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ModelRuntimeException(Throwable cause) {
        super(cause);
    }
    
    

}
