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
package org.ontoware.rdf2go.exception;

/**
 * An exception that is thrown by Model functions.
 * @author voelkel, sauermann
 */
public class ModelException extends Exception implements AnyModelException {

	private static final long serialVersionUID = 9146437799850363415L;
	
	public ModelException() {
		super();
	}

	public ModelException(String string) {
		super(string);
	}

    /**
     * @param message
     * @param cause
     */
    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ModelException(Throwable cause) {
        super(cause);
    }
    
    

}
