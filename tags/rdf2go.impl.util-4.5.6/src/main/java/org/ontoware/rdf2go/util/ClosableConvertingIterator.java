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

import org.ontoware.aifbcommons.collection.ClosableIterator;

/**
 * A converting iterator that is closable/closeable (both is correct).
 * 
 * You have to implement both {@link #convert(Object)} and 
 * {@link ClosableIterator#close()}.
 * 
 * @author sauermann
 * @param <FROM> the source class 
 * @param <TO> the target class
 */
public abstract class ClosableConvertingIterator<FROM, TO> extends ConvertingIterator<FROM, TO>
  implements ClosableIterator<TO> {

    /**
     * The iterator takes the wrapped class and converts it to other
     * classes on the fly.
     * You have to implement the "convert" method, though.
     * @param wrapped the wrapped iterator
     */
    public ClosableConvertingIterator(ClosableIterator<FROM> wrapped) {
        super(wrapped);
    }

    /**
     * The iterator takes the wrapped class and converts it to other
     * classes on the fly. Pass a converter that handles the conversion.
     * @param wrapped the wrapped iterator
     * @param converter the converter
     */
    public ClosableConvertingIterator(ClosableIterator<FROM> wrapped, Converter<FROM, TO> converter) {
        super(wrapped, converter);
    }
    
    



}
