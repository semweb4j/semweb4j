package org.ontoware.rdfreactor.runtime;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfschema.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeUtils {
	
	private static Logger log = LoggerFactory.getLogger(TypeUtils.class);

	public static Resource toResource(URL url) throws ModelRuntimeException {
		try {
			return new URIImpl(url.toURI().toString());
		} catch (URISyntaxException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public static Resource toResource(ResourceEntity re) throws ModelRuntimeException {
		return re.getResource();
	}

	public static Resource toResource(Object o) throws ModelRuntimeException {
		if (o instanceof URL)
			return toResource((URL) o);
		else if (o instanceof ResourceEntity)
			return toResource((ResourceEntity) o);
		else
			throw new ModelRuntimeException("Could not convert to an RDF2Go node, class: " + o.getClass());
	}

	public static Node toNode(Object o) throws ModelRuntimeException {
		return toResource(o);
		// TODO more
	}
	
	/**
	 * @return all 'real' super classes, ignore restrictions
	 * @throws Exception
	 */
	public static org.ontoware.rdfreactor.schema.owl.Class[] getAllRealSuperclasses( org.ontoware.rdfreactor.schema.owl.Class owlclass,
			Set<org.ontoware.rdfreactor.schema.owl.Class> wanted)
			throws Exception {
		java.util.List<org.ontoware.rdfreactor.schema.owl.Class> real = new ArrayList<org.ontoware.rdfreactor.schema.owl.Class>();
		for (org.ontoware.rdfreactor.schema.owl.Class c : owlclass.getAllSubClassOf()) {
			if (c.isInstanceof(OWL.Restriction)) {
				// do nothing
				log.debug("Restriction as supertype");
			} else if (wanted.contains(c)) {
				real
						.add((org.ontoware.rdfreactor.schema.owl.Class) c
								.castTo(org.ontoware.rdfreactor.schema.owl.Class.class));
			} else
				log.debug("un-wanted class " + c.getResource());
		}
		return real.toArray(new org.ontoware.rdfreactor.schema.owl.Class[0]);
	}

	

	/**
	 * @return all OWL restrictions
	 */
	public static Restriction[] getAllRestriction( Property property) {
		return (Restriction[]) property.getAll_Inverse(Restriction.ONPROPERTY, property.getResource(), Restriction.class);
	}	
	
}
