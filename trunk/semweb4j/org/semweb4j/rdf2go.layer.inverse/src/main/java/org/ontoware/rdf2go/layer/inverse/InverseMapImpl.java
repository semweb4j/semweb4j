package org.ontoware.rdf2go.layer.inverse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

public class InverseMapImpl implements InverseMap {
	
	/**
	 * This URI should not be used for anything else (as usual :-) )
	 * 
	 * @return http://www.xam.de/2006/08-inverseProperties#hasInverse
	 * 
	 * CDS: http://www.xam.de/2006/01-cds#hasInverse
	 */
	public static final URI HAS_INVERSE_PROPERTY = new URIImpl("http://www.xam.de/2006/01-cds#hasInverse",false);

	private static final Log log = LogFactory.getLog(InverseMapImpl.class);

	/** acts as a cache: from property URI to it's inverse property URI*/
	private Map<URI, URI> inversePropertyMap;

	private Model model;

	public InverseMapImpl(Model model) {
		this.model = model;
		log.debug("initialising inverse-property cache");
		inversePropertyMap = new HashMap<URI, URI>();
		try {
			log.debug("model size = " + model.size());
			for (QueryRow row : model.sparqlSelect("SELECT ?p WHERE {?p <" + RDF.type + "> <"
					+ RDF.Property + "> . }")) {
				URI propertyURI = (URI) row.getValue("p");
				URI inversePropertyURI = _getInverseProperty(propertyURI);
				log.debug("initialising inverse-prop-cache with " + propertyURI + " --> "
						+ inversePropertyURI);
				inversePropertyMap.put(propertyURI, inversePropertyURI);
			}
			log.debug("cache ready");
		} catch (ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		} catch (InversePropertyException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public URI _getInverseProperty(URI p) throws ModelRuntimeException, InversePropertyException {
		QueryResultTable result = model.sparqlSelect("SELECT ?inv WHERE { <" + p + "> <"
				+ HAS_INVERSE_PROPERTY + "> ?inv . }");

		Iterator<QueryRow> it = result.iterator();

		if (it.hasNext()) {
			QueryRow solution = it.next();
			if (it.hasNext())
				throw new InversePropertyException(
						"Model contains more than one inverse for property " + p);

			assert solution.getValue("inv") != null;

			if (solution.getValue("inv") instanceof URI) {
				URI inverse = (URI) solution.getValue("inv");
				inversePropertyMap.put(p, inverse);
				return inverse;
			} else
				throw new InversePropertyException("Model contains a non-URI ("
						+ solution.getValue("inv").getClass() + ") as inverse for property " + p);

		} else {
			log.info("Found no inverse property for " + p);
			return null;
		}
	}

	/**
	 * @param inverse
	 * @return the preferred property to an inverse property. getInverseProperty
	 *         of the result is 'inverse' again.
	 * @throws InversePropertyException
	 * @throws ModelRuntimeException
	 */
	public URI getPropertyOfInverseProperty(URI inverse) throws InversePropertyException,
			ModelRuntimeException {
		// there is no cache for this

		QueryResultTable result = model.sparqlSelect("SELECT ?p WHERE { ?p <"
				+ HAS_INVERSE_PROPERTY + "> <" + inverse + "> . }");

		Iterator<QueryRow> it = result.iterator();

		if (it.hasNext()) {
			QueryRow solution = it.next();
			if (it.hasNext())
				throw new InversePropertyException(
						"Model contains more than one inverse-inverse for inverse " + inverse);

			assert solution.getValue("p") != null;

			if (solution.getValue("p") instanceof URI) {
				URI p = (URI) solution.getValue("p");
				inversePropertyMap.put(p, inverse);
				return p;
			} else
				throw new InversePropertyException("Model contains a non-URI ("
						+ solution.getValue("p").getClass() + ") as inverse-inverse for inverse "
						+ inverse);

		} else
			return null;

	}
	
	public URI getInverseProperty(URI p) {
		// FIXME use cache
		try {
			URI inverse = _getInverseProperty(p);
			log.info("returning inverse prop of "+p+" without cache: "+inverse);
			return inverse;
		} catch (ModelRuntimeException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException( e );
		} catch (InversePropertyException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException( e );
		}
		
		
//		URI cached = inversePropertyMap.get(p);
//		log.debug("Inverse of " + p + " is " + cached);
//		return cached;
	}

	public void registerInverse(URI propertyURI, URI inversePropertyURI) throws ModelRuntimeException {
		model.addStatement(propertyURI, HAS_INVERSE_PROPERTY, inversePropertyURI);
		inversePropertyMap.put( propertyURI, inversePropertyURI );
	}

	public boolean isMeta(URI predicate) {
		return predicate.equals(HAS_INVERSE_PROPERTY);
	}

	public void unregisterInverse(URI propertyURI, URI inversePropertyURI) throws ModelRuntimeException {
		model.removeStatement(propertyURI, HAS_INVERSE_PROPERTY, inversePropertyURI);
		inversePropertyMap.remove(propertyURI);
	}

	
}
