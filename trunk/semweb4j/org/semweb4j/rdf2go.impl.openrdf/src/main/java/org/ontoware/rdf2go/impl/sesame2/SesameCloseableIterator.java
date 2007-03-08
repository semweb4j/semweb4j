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
package org.ontoware.rdf2go.impl.sesame2;

import org.ontoware.rdf2go.util.ClosableConvertingIterator;
import org.ontoware.rdf2go.util.Converter;
import org.openrdf.util.iterator.CloseableIterator;

/**
 * Wraps a closeableiterator.
 * @author sauermann
 * @param <AA> source class
 * @param <BB> target class
 */
public class SesameCloseableIterator<AA, BB>
   extends ClosableConvertingIterator<AA, BB>
{
    // keep for type-safety
    CloseableIterator<AA> wrappedCloseable;

    /**
     * create a wrapped iterator that converts from resources to URIs
     * @param wrapped the wrapped iterator
     * @param converter the converter to use
     */
    public SesameCloseableIterator(CloseableIterator<AA> wrapped, Converter<AA, BB> converter) {
        super(wrapped, converter);
        this.wrappedCloseable = wrapped;
    }

    /**
     * create a wrapped closeable iterator.
     * You have to implement the convert() method yourself.
     * @param wrapped the wrapped iterator
     */
    public SesameCloseableIterator(CloseableIterator<AA> wrapped) {
        super(wrapped);
        this.wrappedCloseable = wrapped;
    }



    /**
     * @see org.ontoware.aifbcommons.collection.ClosableIterator#close()
     */
    public void close() {
        wrappedCloseable.close();
    }
}