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
package org.ontoware.rdf2go.util;

/**
 * Thrown when an rdf2go adapter cannot convert from one class to another.
 * The source object that cannot be conveted and the target class that
 * was the goal of the conversion are passed.
 * 
 *  
 * @author sauermann <leo.sauermann@dfki.de> 
 */
public class ConversionException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1866748054068895807L;
	Object sourceObject;
    Class targetClass;

    /**
     * default constructor
     */
    public ConversionException() {
    	//empty default constructor
    }

    /**
     * constructor, with an injected message
     * 
     * @param message
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ConversionException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Generate a new conversion exception. The exception will start with
     * a string automatically generated from using the toString of the source
     * object and the targetclass. You can give a message or leave it empty.
     * @param message
     * @param sourceObject
     * @param targetClass
     */
    public ConversionException(String message, Object sourceObject, Class targetClass) {
        super(generateMessage(message, sourceObject, targetClass));
        this.sourceObject = sourceObject;
        this.targetClass = targetClass;
    }

    /**
     * generate the error message
     */
    protected static String generateMessage(String message, Object sourceObject2, Class targetClass2) {
        
        return "Could not convert '"+sourceObject2+"' "+
            ((sourceObject2 == null) ? "" : "of class "+sourceObject2.getClass().getName())
            + " to target class "+targetClass2
            + ((message != null) ? ": "+message : "");
    }

    /**
     * 
     * @return the source object that could not be converted.
     */
    public Object getSourceObject() {
        return this.sourceObject;
    }

    /**
     * 
     * @return the target class that should be converted to
     */
    public Class getTargetClass() {
        return this.targetClass;
    }

}
