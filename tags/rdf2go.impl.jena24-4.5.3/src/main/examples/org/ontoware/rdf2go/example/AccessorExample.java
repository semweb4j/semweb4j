package org.ontoware.rdf2go.example;

import java.util.HashSet;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.jena24.ModelImplJena24;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

public class AccessorExample {

	public static HashSet<?> getAllValues(Model m, URI resource, URI property,
			Class<?> returnType) throws ModelRuntimeException {
		return new HashSet<String>();
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ModelRuntimeException {
		Model m = new ModelImplJena24(Reasoning.rdfs);
		URI u = RDF.Bag;
		URI p = RDF.type;
		@SuppressWarnings("unused")
		HashSet<String> names = (HashSet<String>) getAllValues(m, u, p,
				String.class);
	}

}
