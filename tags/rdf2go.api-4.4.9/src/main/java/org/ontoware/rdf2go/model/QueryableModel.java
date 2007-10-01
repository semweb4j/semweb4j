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
package org.ontoware.rdf2go.model;

import org.ontoware.aifbcommons.collection.ClosableIterable;

/**
 * The closable iterable/iterators hould be auto-close, i.e. when the last
 * element is retrieved close() hast to be called automatically.
 * 
 * @author voelkel
 * 
 */
public interface QueryableModel extends ClosableIterable<Statement>, Sparqlable, FindableModel {

}
