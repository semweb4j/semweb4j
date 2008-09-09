/*
 * Created on 14.02.2005
 *
 */
package org.ontoware.rdfreactor.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.bootstrap.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for creating legal and unique Java Bean Identifiers for
 * resources.
 * 
 * Use toBeanName() as the main entry point to the helper functions.
 * 
 * @author mvo
 */
public class JavaNamingUtils {

	private static Logger log = LoggerFactory.getLogger(JavaNamingUtils.class);

	/**
	 * Create a legal and unique Java Bean Identifier for a resource.
	 * 
	 * @param rresource -
	 *            Resource for which to create the Identifier
	 * @param usedNames -
	 *            Set of already used names
	 * @return a new unique name from label or URI
	 */
	public static String toBeanName(Resource rresource, Set<String> usedNames) {
		// if we have at least one label, we use that
		List<String> labels = rresource.getAllLabel_asList();
		// TODO improve: language handling in labels
		if (labels.size() > 0) {
			log.debug("Found a label, using first label: " + labels.get(0));
			String labelName = toLegalJavaIdentifier(labels.get(0));
			if (!usedNames.contains(labelName))
				return labelName;
			// IMPROVE: might try other labels
			else {
				String result = uri2beanname(rresource.getResource(), usedNames);
				log.debug("NAME     " + rresource.getResource().toSPARQL()
						+ " label '"+labelName+"' is already used. Using '" + result+"' computed from URI.");
				return result;
			}
		} else {
			String result = uri2beanname(rresource.getResource(), usedNames);
			log.debug("NAME     " + rresource.getResource().toSPARQL()
					+ " found no label. Using '" + result+"' computed from URI.");
			return result;
		}
	}

	/**
	 * Convert any String into a legal Java Bean Identifier (no spaces,
	 * everything concatenated)
	 * 
	 * @param illegal -
	 *            String which has to be converted
	 * @return legal Java Bean Identifier
	 */
	public static String toLegalJavaIdentifier(String illegal) {
		assert illegal != null;

		// make rawname bean-style
		// remove all spaces/underlines and concatenate the result
		String beanname = illegal;

		beanname = beanname.replaceAll("is ", "");
		beanname = beanname.replaceAll("has ", "");
		beanname = beanname.replaceAll("is_", "");
		beanname = beanname.replaceAll("has_", "");
		beanname = beanname.replaceAll(" ", "");
		beanname = beanname.replaceAll("_", "");

		beanname = beanname.replaceAll("[^a-zA-Z0-9]", "_");
		beanname = beanname.trim();

		// force a letter as first character (e.g. no number)
		if (!"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
				.contains(beanname.substring(0, 1)))
			beanname = "a_" + beanname;

		// first letter upperacse
		beanname = beanname.substring(0, 1).toUpperCase()
				+ beanname.substring(1);

		return beanname;
	}

	/**
	 * Take a URI and generate a legal and unique Java Bean Identifier from it.
	 * 
	 * @param uriOrBlankNode -
	 *            URI or BlankNode for which a bean identifiers has to be
	 *            created.
	 * @param usedNames -
	 *            Set of alread used bean identifiers
	 * @return a legal and unique Java Bean Identifier
	 */
	public static String uri2beanname(Object uriOrBlankNode,
			Set<String> usedNames) {

		if (uriOrBlankNode instanceof BlankNode) {
			return "blankNode";
			// TODO create names for anonymous resource
			// throw new RuntimeException(
			// "Cannot create names for anonymous resources (yet)");
		}

		URI uri = (URI) uriOrBlankNode;

		String rawname;
		if (uri != null) {
			if (getLocalPart(uri.toString()) != null) {
				rawname = getLocalPart(uri.toString());
			} else {
				rawname = uri.toString();
			}
		} else {
			// generate name! only needed for blank nodes
			rawname = "genBean" + System.currentTimeMillis();
		}

		// remove preceeding 'has'
		if (rawname.toLowerCase().startsWith("has") && rawname.length() > 3) {
			rawname = rawname.substring(3);
		}

		// make rawname bean-style
		String beanname = toLegalJavaIdentifier(rawname);

		// now we have a nice bean name - but is it unique?
		if (usedNames.contains(beanname)) {
			
			// TODO check when this happens and deal better with it
			if(uri == null) {
				throw new IllegalStateException("");
			}

			// try to use namespace prefix for disambiguation
			String namespacePrefix = guessNSPrefix(uri.toString());
			String prefixedBeanName = toLegalJavaIdentifier(namespacePrefix
					+ beanname);
			if (usedNames.contains(prefixedBeanName)) {
				// fallback to plain uri
				beanname = toLegalJavaIdentifier(uri.toString());
			} else {
				beanname = prefixedBeanName;
			}
		}

		return beanname;
	}

	/**
	 * Get the local part of a URI, which is the fragment identifier (after #)
	 * or part after the last / .
	 * 
	 * @param uriString -
	 *            URI given as String
	 * @return URI fragment identifier OR part after last slash OR null
	 */
	public static String getLocalPart(String uriString) {

		String fragment = null;
		if (uriString.contains("#")) {
			fragment = uriString.substring(uriString.lastIndexOf('#') + 1);
			if (fragment != null && fragment.length() > 0) {
				return fragment;
			}
		}

		// IV: no fragment, but some URI
		int slashPos = uriString.lastIndexOf('/');
		if (slashPos > 0 && slashPos + 1 < uriString.length()) {
			// take after last slash
			return uriString.substring(slashPos + 1);
		} else {
			int colonPos = uriString.lastIndexOf(':');
			if (colonPos > 0 && colonPos + 1 < uriString.length()) {
				// take after last slash
				return uriString.substring(colonPos + 1);
			} else {
				return null;
			}
		}
	}

	/**
	 * Get the Name Space Part of an URI (before the # or last / )
	 * 
	 * @param uriString -
	 *            URI given as String
	 * @return the part BEFORE the # or last slash
	 */
	public static String getNamespacePart(String uriString) {
		String local = getLocalPart(uriString);
		if (local == null)
			return null;

		return uriString.substring(0, uriString.length() - local.length());
	}

	/**
	 * try to find a suitable prefix to represent a uri, much like the prefixed
	 * used in N3 notation.
	 * 
	 * @param uriString
	 *            a URI given as String
	 * @return a short, lowercase name without spaces, usable as a N3 namespace
	 *         prefix
	 */
	public static String guessNSPrefix(String uriString) {

		// TODO improve: later loaded from file
		Map<String, String> namespace2prefix = new HashMap<String, String>();
		namespace2prefix.put("http://xmlns.com/foaf/0.1/", "foaf");
		namespace2prefix.put("http://www.w3.org/2002/07/owl#", "owl");
		namespace2prefix.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#",
				"rdf");
		namespace2prefix.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
		namespace2prefix.put("http://www.w3.org/2002/12/cal/ical#", "ical");
		namespace2prefix.put("http://xmlns.com/wordnet/1.6/", "wordnet");
		namespace2prefix.put("http://musicbrainz.org/mm/mm-2.1#",
				"musicbrainz21");

		// look up known namespaces
		String nspart = getNamespacePart(uriString);
		if (namespace2prefix.containsKey(nspart.toLowerCase())) {
			return namespace2prefix.get(nspart.toLowerCase());
		} else {

			String[] slashparts = nspart.substring(0, nspart.length() - 1)
					.split("/");
			int i = slashparts.length - 1;
			boolean foundAlphabetic = false;
			String name = "";
			while (i > 0 && !foundAlphabetic) {

				if (slashparts[i].matches("[a-zA-Z]+")) {
					foundAlphabetic = true;
				}

				name = slashparts[i] + "_" + name;
				i--;
			}
			log.debug("made name |" + name + "|");
			return name;
		}
	}

}
