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

import java.util.Iterator;

/**
 * An iterator that converts from "FROM" to "TO", by wrapping an existing
 * iterator. This is needed when converting statements or resource-uris from one
 * iterator to another, for the Adapter implementations. You still have to
 * implement the "convert" method, but the handling of the Java generics is
 * done.
 * 
 * @author sauermann <leo.sauermann@dfki.de>
 * @param <FROM>
 *            The class that is the source of conversion
 * @param <TO>
 *            The class that is converted to
 * 
 * Note: as this class is not RDF-specific in any way it is a good candidate for
 * AIFBcommons.
 */
public class ConvertingIterator<FROM, TO> implements Iterator<TO> {

	Iterator<FROM> wrapped;

	Converter<FROM, TO> converter;

	/**
	 * The iterator takes the wrapped class and converts it to other classes on
	 * the fly. You have to override the "convert" method, though.
	 * 
	 * @param wrapped
	 *            the wrapped iterator
	 */
	public ConvertingIterator(Iterator<FROM> wrapped) {
		this.wrapped = wrapped;
	}

	/**
	 * The iterator takes the wrapped class and converts it to other classes on
	 * the fly. Pass a converter that handles the conversion.
	 * 
	 * @param wrapped
	 *            the wrapped iterator
	 * @param converter
	 *            the converter
	 */
	public ConvertingIterator(Iterator<FROM> wrapped,
			Converter<FROM, TO> converter) {
		this.wrapped = wrapped;
		this.converter = converter;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
    public boolean hasNext() {
		return this.wrapped.hasNext();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
    public TO next() {
		FROM next = this.wrapped.next();
		return convert(next);
	}

	/**
	 * convert the passed object to the outgoing object
	 * 
	 * @param next
	 *            the next object to convert
	 * @return the converted object
	 */
	public TO convert(FROM next) {
		if (this.converter == null)
			throw new RuntimeException(
					"you have to override the convert() method or pass in a converter.");
		TO result = this.converter.convert(next);
		return result;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	@Override
    public void remove() {
		this.wrapped.remove();
	}

}
