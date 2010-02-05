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

package org.ontoware.rdf2go.util;

/**
 * 
 * Converts instances from one type to another. 
 * Useful for implementing adapters.
 * @author sauermann <leo.sauermann@dfki.de>
 * @param <FROM> The class that is the source of conversion
 * @param <TO> The class that is converted to
 */
public interface Converter<FROM, TO> {
    
    /**
     * convert the passed object to the outgoing object
     * @param source the source object to convert
     * @return the converted object
     * @throws ConversionException if something goes wrong.
     */
    public TO convert(FROM source) throws ConversionException;   
    

}
