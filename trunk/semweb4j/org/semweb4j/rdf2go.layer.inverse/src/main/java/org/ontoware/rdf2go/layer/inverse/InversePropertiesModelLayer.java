package org.ontoware.rdf2go.layer.inverse;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.aifbcommons.collection.impl.UnionIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.DelegatingModel;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.LanguageTagLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

/**
 * Transparent handling of inverse properties. Each property has a defined
 * inverse property.
 * 
 * Note that if p has inverse -p, -p DOES NOT have inverse p.
 * 
 * The underlying RDF store will only contain the triple (p, hasInverse, -p).
 * All addStatement(x,-p,y) statements will be normalized to (y,p,x).
 * 
 * Thus put the more often used direction as the subject in (p, inverse, -p).
 * 
 * TODO things to check - p has at least one inverse OR IS an inverse - p and q
 * don't have both inverses - a relation can have itself as its inverse
 * 
 * @author voelkel
 * 
 */
public class InversePropertiesModelLayer extends DelegatingModel implements
		Model {

	private static final Log log = LogFactory
			.getLog(InversePropertiesModelLayer.class);

	private InverseMapImpl inverseMap;

	public InversePropertiesModelLayer(Model model) {
		super(model);
		this.inverseMap = new InverseMapImpl(model);
	}

	public ClosableIterable<? extends Statement> sparqlConstruct(
			String queryString) throws ModelRuntimeException {
		Query query = QueryFactory.create(queryString);
		query.getConstructTemplate().visit(
				new TwistingTemplateVisitor(inverseMap));
		String modifiedQuery = query.toString();
		log.debug("rewritten query: " + modifiedQuery);
		return super.sparqlConstruct(modifiedQuery);
	}

	public QueryResultTable sparqlSelect(String queryString)
			throws ModelRuntimeException {
		log.debug("query string = \n" + queryString);
		Query query = QueryFactory.create(queryString);
		// FIXME was: query.getQueryBlock().getPatternElement().visit(new
		// TwistingElementVisitor(inverseMap));
		query.getQueryPattern().visit(new TwistingElementVisitor(inverseMap));
		String modifiedQuery = query.toString();
		log.debug("rewritten query: " + modifiedQuery);
		if (log.isDebugEnabled()) {
			log.debug("underlying persisted rdf model dump ---");
			super.dump();
		}
		return super.sparqlSelect(modifiedQuery);
	}

	public boolean sparqlAsk(String queryString) throws ModelRuntimeException {
		log.debug("ASK query string = \n" + queryString);
		Query query = QueryFactory.create(queryString);
		// FIXME was: query.getQueryBlock().getPatternElement().visit(new
		// TwistingElementVisitor(inverseMap));
		query.getQueryPattern().visit(new TwistingElementVisitor(inverseMap));
		String modifiedQuery = query.toString();
		log.debug("rewritten query: " + modifiedQuery);

		boolean result = super.sparqlAsk(modifiedQuery);
		log.debug("is " + result);
		return result;
	}

	/**
	 * Dump the 'real' model, showing how the underlying triple store sees it
	 * 
	 */
	public void dump() {
		super.dump();
	}

	/**
	 * If a property has no inverse yet and is also not the inverse of another
	 * property, create an artificial inverse.
	 * 
	 * @param property
	 * @throws ModelRuntimeException
	 * @throws InversePropertyException
	 */
	public void ensureInverseProperty(URI property) throws ModelRuntimeException,
			InversePropertyException {
		if (inverseMap.getInverseProperty(property) == null) {
			// maybe this property is the inverse of another one
			if (inverseMap.getPropertyOfInverseProperty(property) == null) {
				// property has no inverse AND property IS no inverse of another
				// property
				// => add one
				inverseMap.registerInverse(property, new URIImpl(""
						+ property + "-inverse"));
			} else {
				log.debug("property is the inverse of another property");
			}
		} else
			log.debug(property + " had already an inverse, "
					+ inverseMap.getInverseProperty(property));
	}

	@Override
	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		log.debug("Adding statement to InversePropertyLayer");

		if (inverseMap.isMeta(predicate)) {

			log.debug("This changes the property-structure itself: " + subject
					+ " -- " + predicate + " -- " + object);
			// TODO: enforce invariant that INVERSE_PROPERTY is anti-symmetric

			if (inverseMap.getInverseProperty(subject.asURI()) != null
					&& inverseMap.getInverseProperty(object.asURI()) != null) {

				// if not trying to insert the exact same statment: error
				if (subject instanceof URI && object instanceof URI) {
					URI inverse;
					try {
						inverse = inverseMap.getInverseProperty((URI) subject);
						if (inverse.equals((URI) object)) {
							// fine, do nothing
						} else
							throw new ModelRuntimeException(
									new InversePropertyException(
											"Property "
													+ predicate
													+ " has already a different inverse, namely "
													+ inverse
													+ ", so you cannot set "
													+ object));
					} catch (ModelRuntimeException e) {
						throw new RuntimeException(e);
					}
				} else
					throw new ModelRuntimeException(new InversePropertyException(
							"You cannot set a non-URI as an inverse property"));
			} else if (!(subject instanceof URI)) {
				throw new ModelRuntimeException(new InversePropertyException(
						"Subject is not a URI"));
			} else if (!(object instanceof URI)) {
				throw new ModelRuntimeException(new InversePropertyException(
						"Object is not a URI"));
			} else {
				// predicate is "hasInverse" and subject and object are URIs
				if (inverseMap.getInverseProperty(object.asURI()) != null) {
					// trying to set inverse of inverse?
					if (inverseMap.getInverseProperty(object.asURI()).equals(
							subject)) {
						log
								.info("Redundant information, we need to know inverses only in one direction.");
					} else {
						log.warn("We know that " + object + " hasInverse "
								+ inverseMap.getInverseProperty(object.asURI())
								+ " and you try to tell me " + subject
								+ " -hasInverse-> " + object + ". Ignored.");
					}

				} else {
					if (inverseMap.getInverseProperty(subject.asURI()) != null
							&& inverseMap.getInverseProperty(subject.asURI())
									.equals(object)) {
						log
								.debug("Trying to add subject--hasInverse-->object, but we know exactly that already: IGNORED");
					} else {
						assert inverseMap.getInverseProperty(subject.asURI()) == null : "expected no known inverse for "
								+ subject;
						log.debug("\n[add] Adding inverse: " + subject
								+ " --> " + object);
						// add to cache
						inverseMap.registerInverse((URI) subject, (URI) object);
						super.addStatement(subject, predicate, object);
					}
				}
			}
		} else
		// predicate is harmless
		// -------------------------------------------------
		{
			try {
				URI pInv = inverseMap.getInverseProperty(predicate);

				if (pInv == null) {
					// no inverse defined
					log.debug("No inverse known for " + predicate
							+ " just adding.\n[add] " + subject + ", "
							+ predicate + ", " + object);
					super.addStatement(subject, predicate, object);

				} else {
					// inverse defined

					boolean twist = true;
					if (object instanceof Resource) {

						// symmetric:
						if (predicate.equals(pInv)
								&& object.compareTo(subject) < 0)
							twist = false;
					} else
						throw new ModelRuntimeException(new InversePropertyException(
								"propery has an inverse, but object is literal: "
										+ object));

					if (twist) {
						// twist triples (change order, change predicate)
						log.info("Adding twisted triple \n[add] " + object
								+ ", " + pInv + ", " + subject);
						super.addStatement((Resource) object, pInv, subject);
					} else {
						// just add
						log.info("Adding plain (unchanged) triple \n[add] "
								+ subject + ", " + predicate + ", " + object);
						super.addStatement(subject, predicate, object);
					}
				}
			} catch (ModelRuntimeException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public InverseMap getInverseMap() {
		return this.inverseMap;
	}

	@Override
	public void addAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		log.debug("bulk insert");
		while (other.hasNext()) {
			this.addStatement(other.next());
		}
	}

	@Override
	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		if (inverseMap.isMeta(predicate)) {
			// TODO: enforce invariant that INVERSE_PROPERTY is always defined
			// update cache
			// remove from model
			inverseMap.unregisterInverse(subject.asURI(), object.asURI());
		} else {
			if (object instanceof Resource
					&& inverseMap.getInverseProperty(predicate) != null) {
				// twist triples (change order, change predicate)
				super.removeStatement((Resource) object, inverseMap
						.getInverseProperty(predicate), subject);
			} else
				// just removed
				super.removeStatement(subject, predicate, object);
		}
	}

	@Override
	public void addStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		addStatement(subject, predicate, new PlainLiteralImpl(literal));
	}

	/**
	 * Override this method for performance reasons, to avoid object creation.
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param languageTag
	 * @throws ModelRuntimeException
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		addStatement(subject, predicate, new LanguageTagLiteralImpl(literal,
				languageTag));
	}

	/*
	 * (wth) for information on typed literals see this very good how to
	 * http://jena.sourceforge.net/how-to/typedLiterals.html
	 * 
	 * (none javadoc) Override this method for performance reasons, to avoid
	 * object creation.
	 * 
	 * @see org.ontoware.rdf2go.Model#addStatement(Object, URI, String, URI)
	 */
	@Override
	public void addStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		addStatement(subject, predicate, new DatatypeLiteralImpl(literal,
				datatypeURI));
	}

	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		addStatement(new URIImpl(subjectURIString), predicate,
				new PlainLiteralImpl(literal));
	}

	/**
	 * Override this method for performance reasons, to avoid object creation.
	 * 
	 * @param subject
	 * @param predicate
	 * @param literal
	 * @param languageTag
	 * @throws ModelRuntimeException
	 */
	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		addStatement(new URIImpl(subjectURIString), predicate,
				new LanguageTagLiteralImpl(literal, languageTag));
	}

	/*
	 * (wth) for information on typed literals see this very good how to
	 * http://jena.sourceforge.net/how-to/typedLiterals.html
	 * 
	 * (none javadoc) Override this method for performance reasons, to avoid
	 * object creation.
	 * 
	 * @see org.ontoware.rdf2go.Model#addStatement(Object, URI, String, URI)
	 */
	@Override
	public void addStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		addStatement(new URIImpl(subjectURIString), predicate,
				new DatatypeLiteralImpl(literal, datatypeURI));
	}

	/*
	 * (non-Javadoc) Override this method for performance reasons, to avoid
	 * object creation.
	 * 
	 * @see org.ontoware.rdf2go.Model#addStatement(org.ontoware.rdf2go.Statement)
	 */
	@Override
	public void addStatement(Statement statement) throws ModelRuntimeException {
		addStatement(statement.getSubject(), statement.getPredicate(),
				statement.getObject());
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClosableIterator<? extends Statement> findStatements(
			ResourceOrVariable s, UriOrVariable p, NodeOrVariable o)
			throws ModelRuntimeException {
		log.debug("find: " + s + " -- " + p + " -- " + o);
		if (p instanceof Variable) {
			log.debug("maybe expensive: p is var");
			// we might have to do an expensive UNION :-(
			ClosableIterator<Statement> normal = (ClosableIterator<Statement>) super
					.findStatements(s, p, o);
			if (o instanceof URI || o instanceof Variable
					|| o instanceof BlankNode) {
				// we have to do an expensive UNION :-(
				ClosableIterator<Statement> twisted = (ClosableIterator<Statement>) super
						.findStatements((ResourceOrVariable) o, p, s);
				return new UnionIterator<Statement>(normal, twisted);
			} else
				return normal;
		} else {
			// p is URI
			URI pURI = (URI) p;

			// special case: asking for the inverse of a relation
			if (inverseMap.isMeta(pURI)) {
				log.debug("special case: asking for inverse of " + s);
				ClosableIterator<Statement> normal = (ClosableIterator<Statement>) super
						.findStatements(s, p, o);
				ClosableIterator<Statement> twisted = new StatementTwistingIterator(
						this, (ClosableIterator<Statement>) super
								.findStatements((ResourceOrVariable) o, p, s));

				return new UnionIterator<Statement>(normal, twisted);
			} else {
				log.debug("simple query for a given p-URI");
				URI pInverseURI = inverseMap.getInverseProperty(pURI);
				if (pInverseURI != null) {
					// we have an inverse
					// can we twist?
					if (o instanceof URI || o instanceof Variable
							|| o instanceof BlankNode) {
						// we can twist

						// symmetric:
						if (pURI.equals(pInverseURI) && o instanceof Node
								&& s instanceof Node
								&& ((Node) o).compareTo((Node) s) < 0) {
							// do not twist symmetric statement
							log
									.debug("asking not twisted, because its symmetric and s < o: "
											+ s + " " + p + " " + o);
							return super.findStatements(s, pURI, o);
						} else {
							log.debug("asking twisted: " + o + " "
									+ pInverseURI + " " + s);
							return super.findStatements((ResourceOrVariable) o,
									pInverseURI, s);
						}

					} else {
						throw new IllegalArgumentException(
								"The query "
										+ s
										+ " -- "
										+ p
										+ " -- "
										+ o
										+ " is problematic. P has an inverse ("
										+ pInverseURI
										+ ") but the object is a literal, so we cannot ask the inverse query");
					}
				} else {
					// no inverse defined, ask plain query
					log.debug("asking normal");
					return super.findStatements(s, pURI, o);
				}
			}

		}
	}

	/**
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return true if the statement (subject, predicate, object) is in the
	 *         model
	 * @throws ModelRuntimeException
	 */
	public boolean contains(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		ClosableIterator<? extends Statement> it = findStatements(subject, predicate, object);
		boolean result = it.hasNext();
		it.close();
		return result;
	}

	public boolean contains(ResourceOrVariable subject,
			UriOrVariable predicate, String plainLiteral) throws ModelRuntimeException {
		// i can never twist with a literal-object
		return super.contains(subject, predicate, plainLiteral);
	}
}
