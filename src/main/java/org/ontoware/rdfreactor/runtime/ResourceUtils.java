package org.ontoware.rdfreactor.runtime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtils {

	private static Logger log = LoggerFactory.getLogger(ResourceUtils.class);

	/**
	 * Check if the resource identified by resourceID, has a property
	 * identidified by propertyURI which has at least one value.
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resource -
	 *            must be an URI or a BlankNode
	 * @param propertyURI -
	 *            URI of the property
	 * @return true if the property has at least one value defined
	 * @throws Exception
	 */
	public static boolean containsAnyValue(Model model, Resource resource,
			URI propertyURI) throws ModelRuntimeException {

		synchronized (model) {
			ClosableIterator<? extends Statement> it = model.findStatements(
					resource, propertyURI, Variable.ANY);
			boolean result = it.hasNext();
			it.close();
			return result;
		}
	}

	/**
	 * Delete a resource from the model, meaning: delete all (resource, *, *)
	 * triples from the model
	 * 
	 * @param model -
	 *            the underlying RDF2Go model
	 * @param resource -
	 *            URI or BlankNode of the resource
	 * @throws Exception
	 */
	@Patrolled
	public static void delete(Model model, Resource resource)
			throws ModelRuntimeException {
		// delete triple (this.uri, ANY, ANY )
		synchronized (model) {
			ClosableIterator<? extends Statement> it = model.findStatements(
					resource, Variable.ANY, Variable.ANY);
			Set<Statement> temp = new HashSet<Statement>();
			while (it.hasNext()) {
				temp.add(it.next());
			}
			it.close();
			// now, delete
			Iterator<Statement> tempIterator = temp.iterator();
			while (tempIterator.hasNext()) {
				model.removeStatement(tempIterator.next());
			}
		}
	}

	/**
	 * @param model
	 * @param subject
	 * @param propertyURI
	 * @return the single value of the given property, or null
	 * @throws RDFDataException if multiple values are found
	 */
	public static Node getSingleValue(Model model, Resource subject,
			URI propertyURI) {
		log.debug("looking for ( <" + subject + "> <" + propertyURI + "> *)");

		Node result = null;
		synchronized (model) {
			// get value
			ClosableIterator<? extends Statement> it;
			it = model.findStatements(subject, propertyURI, Variable.ANY);
			if (it.hasNext()) {
				Statement o = it.next();
				if (it.hasNext())
					throw new RDFDataException(
							"Found more than one value for property "
									+ propertyURI + " and resource " + subject
									+ " but you asked for *the* value.");

				result = o.getObject();
			} else {
				log.debug("no matching nodes found");
			}
			it.close();
		}
		return result;
	}

}
