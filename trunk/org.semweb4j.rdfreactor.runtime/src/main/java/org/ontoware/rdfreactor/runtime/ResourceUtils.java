package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;

public class ResourceUtils {

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

}
