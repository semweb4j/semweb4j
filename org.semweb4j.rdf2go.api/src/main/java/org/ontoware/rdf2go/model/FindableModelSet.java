/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2010
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.UriOrVariable;


/**
 * 
 * @author voelkel
 */
public interface FindableModelSet {
	
	/**
	 * Search across all existing models
	 * 
	 * @param contextURI
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return all matching statements in all models
	 * @throws ModelRuntimeException
	 */
	ClosableIterator<Statement> findStatements(UriOrVariable contextURI,
	        ResourceOrVariable subject, UriOrVariable predicate, NodeOrVariable object)
	        throws ModelRuntimeException;
	
	/**
	 * Search across all existing models
	 * 
	 * @param pattern
	 * @return all statements matching the quad pattern
	 * @throws ModelRuntimeException
	 */
	ClosableIterator<Statement> findStatements(QuadPattern pattern) throws ModelRuntimeException;
	
	/**
	 * @param contextURI
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return true, if a Model named 'contextURI' contains the statement
	 *         (s,p,o)
	 * @throws ModelRuntimeException
	 */
	boolean containsStatements(UriOrVariable contextURI, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException;
	
	/**
	 * @param s a Statement
	 * @return true if the modelset contains a model with context s.getContext()
	 *         which contains the statement s. If the context is null, the
	 *         default graph is checked.
	 * @throws ModelRuntimeException
	 */
	boolean contains(Statement s) throws ModelRuntimeException;
	
	/**
	 * @param pattern
	 * @return the number of statements matchingthe pattern. This is for all
	 *         graphs matching the context of the pattern (this is none, one or
	 *         all graphs). In matching graphs the number of matching statements
	 *         is accumulated and returned.
	 * @throws ModelRuntimeException
	 */
	long countStatements(QuadPattern pattern) throws ModelRuntimeException;
	
	/**
	 * @param context
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return a QuadPattern
	 */
	QuadPattern createQuadPattern(UriOrVariable context, ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object);
	
}
