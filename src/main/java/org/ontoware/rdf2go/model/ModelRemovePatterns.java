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

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.UriOrVariable;

public interface ModelRemovePatterns extends ModelAddRemove, FindableModel {

	/**
	 * remove all matching triple patterms from the model
	 * 
	 * @param triplePattern
	 * @throws ModelRuntimeException
	 */
	void removeStatements(TriplePattern triplePattern) throws ModelRuntimeException;

	/**
	 * remove a (subject, property ,object)-statement from the model
	 * 
	 * @param subject
	 *            URI or Object (= blankNode)
	 * @param predicate
	 * @param object
	 *            URI or String (=plainLiteral) or BlankNode (=blankNode) or
	 *            TypedLiteral or LanguageTagLiteral
	 * @throws ModelRuntimeException
	 */
	void removeStatements(ResourceOrVariable subject, UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException;

}
