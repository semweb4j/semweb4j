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

package org.ontoware.rdf2go.exception;

/**
 * The intended type of reasoning is not supported by this Adapter.
 * @author sauermann
 */
public class ReasoningNotSupportedException extends ModelRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -4932937519782507187L;

    /**
     * 
     */
    public ReasoningNotSupportedException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public ReasoningNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ReasoningNotSupportedException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ReasoningNotSupportedException(Throwable cause) {
        super(cause);
    }

    
}
