package org.ontoware.rdfreactor.model;

import java.net.URISyntaxException;
import java.net.URL;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.ResourceEntity;

public class TypeUtils {

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

}
