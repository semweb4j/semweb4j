package org.ontoware.rdfreactor.runtime;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

public interface NewBridge {


	Object[] getAllInstances(Model model, java.lang.Class javaClass);

	boolean containsInstance(Model model, URI classURI);

	boolean isInstanceOf(Model model, Resource subject, URI classURI);

	// operations
	
	boolean addValue(Model model, Resource o, URI propertyURI, Object value)
	throws ModelRuntimeException;

	void addAllValues(Model model, Resource subject, URI propertyURI, Object[] values)
			throws ModelRuntimeException;

	boolean removeValue(Model model, Resource subject, URI propertyURI,
			Object value);

	boolean removeAllValues(Model model, Resource subject, URI propertyURI, Object[] values)
	throws ModelRuntimeException;

	boolean removeAllValues(Model model, Resource subject, URI propertyURI)
			throws ModelRuntimeException;

	void setValue(Model model, Resource resourceObject, URI propertyURI,
			Object value) throws ModelRuntimeException;

	void setAllValues(Model model, Resource subject, URI propertyURI, Object[] values)
			throws ModelRuntimeException;

	boolean updateValue(Model model, Resource subject, URI propertyURI,
			Object oldValue, Object newValue) throws ModelRuntimeException;


	ClosableIterator<? extends Object> getSparqlSelectSingleVariable(Model model,
			Class returnType, String sparqlSelectQuery)
			throws ModelRuntimeException;
	
	

}
