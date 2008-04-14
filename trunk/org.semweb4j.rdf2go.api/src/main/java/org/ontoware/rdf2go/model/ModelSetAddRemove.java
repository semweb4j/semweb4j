package org.ontoware.rdf2go.model;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;

/**
 * Allows to add and removes statements from a ModelSet. Statements without a
 * context are added/removed from the default model.
 * 
 * For plain triples models, this is modeled as a number of interfaces:
 * ClosableIterable<Statement>, ModelWriter, Lockable
 * 
 * @author voelkel
 * 
 */
public interface ModelSetAddRemove extends ClosableIterable<Statement>,
		Lockable {

	/**
	 * This method creates a Model named 'contextURI' if needed. Then the
	 * statement (s,p,o) is inserted into that model.
	 * 
	 * @param contextURI
	 *            a URI or null
	 * @param subject
	 *            a Resource (URI or BlankNode)
	 * @param predicate
	 * @param object
	 *            a Node
	 * @throws ModelRuntimeException
	 *             if any internal (I/O related) exception occurs
	 */
	void addStatement(URI contextURI, Resource subject, URI predicate,
			Node object) throws ModelRuntimeException;

	/**
	 * This method creates a Model named statement.getContextURI if needed. Then
	 * the statement (s,p,o) is inserted into that model.
	 * 
	 * @param statement
	 * @throws ModelRuntimeException
	 *             if any internal (I/O related) exception occurs
	 */
	void addStatement(Statement statement) throws ModelRuntimeException;

	/**
	 * For each statement in the iterator, this method creates a Model named
	 * statement.getContextURI if needed. Then the statement (s,p,o) is inserted
	 * into that model.
	 * 
	 * @param statement
	 * @throws ModelRuntimeException
	 *             if any internal (I/O related) exception occurs
	 */
	void addAll(Iterator<? extends Statement> statement)
			throws ModelRuntimeException;

	/**
	 * Removes the statement (s,p,o) from a model named contextURI. If the model
	 * named 'contextURI' becomes empty, it remains in the ModelSet.
	 * 
	 * @param context
	 *            a URI or null
	 * @param subject
	 *            a Resource (URI or BlankNode)
	 * @param predicate
	 * @param object
	 *            a Node
	 * @throws ModelRuntimeException
	 *             if any internal (I/O related) exception occurs
	 */
	void removeStatement(URI contextURI, Resource subject, URI predicate,
			Node object) throws ModelRuntimeException;

	/**
	 * Removes the statement (s,p,o) from a model named statement.getContext().
	 * If the model named statement.getContext() becomes empty, it remains in
	 * the ModelSet.
	 * 
	 * @param statement
	 *            a Statement
	 * @throws ModelRuntimeException
	 *             if any internal (I/O related) exception occurs
	 */
	void removeStatement(Statement statement) throws ModelRuntimeException;

	/**
	 * For each statement in the iterator, the statement is removed form the
	 * model named statement.getContext(); If the model named
	 * statement.getContext() becomes empty, it remains in the ModelSet.
	 * 
	 * @param statement
	 * @throws ModelRuntimeException
	 *             if any internal (I/O related) exception occurs
	 */
	void removeAll(Iterator<? extends Statement> statement)
			throws ModelRuntimeException;

	/**
	 * Find all models matching the context of the pattern and remove all
	 * matching triple patterms from them
	 * 
	 * @param quadPattern
	 * @throws ModelRuntimeException
	 */
	void removeStatements(QuadPattern quadPattern) throws ModelRuntimeException;

	/**
	 * Find all models matching the context, and remove all (subject, property
	 * ,object)-statements from these model
	 * 
	 * @throws ModelRuntimeException
	 */
	void removeStatements(UriOrVariable context, ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException;

	/**
	 * Apply the changes given by this diff <emph>in one atomic operation</emph>
	 * 
	 * Implementations must check that all statements to be removed are still in
	 * the Model. Otherwise an exception is thrown.
	 * 
	 * First all triples to be removed are removed, then triples to be added are
	 * added.
	 * 
	 * In this modelset, this means (s,p,o) is removed from the graph named c,
	 * for all statements (c,s,p,o). Note: Models becoming empty are not
	 * removed.
	 * 
	 * @param diff
	 * @throws ModelRuntimeException
	 *             if a model or statement in a model to be removed does not
	 *             exist.
	 */
	void update(DiffReader diff) throws ModelRuntimeException;

	
	
//	/**
//	 * Adds all statements contained in 'model' to this ModelSet. Set the
//	 * context to all these statements to 'contextURI'.
//	 * 
//	 * If the model belongs to the same implementation family as the ModelSet,
//	 * this add might be very fast.
//	 * 
//	 * @param context
//	 * @param model
//	 */
//	void addModel(URI contextURI, Model model);

}
