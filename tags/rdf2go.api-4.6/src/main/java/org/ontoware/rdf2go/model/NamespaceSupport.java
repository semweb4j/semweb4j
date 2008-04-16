package org.ontoware.rdf2go.model;

import java.util.Map;

/**
 * This interface leaves it open to implementations where the prefixes go, e.g.
 * if prefixes added to a {@link Model} change the mapping in the surrounding
 * {@link ModelSet} if any. As namespaces do not really exist *in RDF*, only in
 * syntaxes, there are no definitive specifications on this subject.
 * 
 * This API is modeled after the OpenRDF 2.0 API.
 * 
 * Discussion about this interfaces at http://issues.semweb4j.org/RTGO-20
 * 
 * @author voelkel
 * @since 4.6
 */
public interface NamespaceSupport {

	/**
	 * @param prefix
	 * @return the namespace that is associated with the specified prefix, if
	 *         any. Null otherwise.
	 */
	String getNamespace(String prefix);

	/**
	 * Get all namespaces as a map of prefix to namespace.
	 * 
	 * @return An unmodifiable view on the namespaces. (compare to
	 *         Collections.unmodifiableMap()). Never returns null.
	 */
	Map<String, String> getNamespaces();

	/**
	 * Throws no exception if the prefix was not present.
	 * 
	 * @param prefix
	 */
	void removeNamespace(String prefix);

	/**
	 * @param prefix
	 * @param namespaceURI
	 * @throws IllegalArgumentException
	 *             if the given namespaceURI is not a valid URI in this
	 *             environment.
	 */
	void setNamespace(String prefix, String namespaceURI)
			throws IllegalArgumentException;

}
