package org.ontoware.rdfreactor.runtime;

import java.util.Map;
import java.util.WeakHashMap;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.ReactorException;

/**
 * 
 * Java Object <---> Node, Resource
 * 
 * TODO: test weak references
 * 
 * @author voelkel
 * 
 */
public class FORD {

	private static WeakHashMap<Resource, Object> r2o = new WeakHashMap<Resource, Object>();

	private static WeakHashMap<Object, Resource> o2r = new WeakHashMap<Object, Resource>();

	private static WeakHashMap<Object, Model> o2m = new WeakHashMap<Object, Model>();

	public static Object create(Model model, Class returnType, Resource name, boolean write) {

		try {
			Object o = returnType.newInstance();

			// check for RDF annotation
			if (write) {
				try {
					URI rdfsClassURI = getRDFasURI(o);
					model.addStatement(name, RDF.type, rdfsClassURI);
					// now forget the annotation and never us it again?
				} catch (ModelRuntimeException e) {
					throw new RuntimeException(e);
				}
			}
			r2o.put(name, o);
			o2r.put(o, name);
			o2m.put(o, model);
			return o;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

	}

	public static URI getRDFasURI(Object o) throws ModelRuntimeException {
		String annotation = o.getClass().getAnnotation(
				org.ontoware.rdfreactor.annotation.RDF.class).value();
		return new URIImpl(annotation);
	}

	public static Object get(Object obj, URI p, Class returnType) {
		try {
			return Bridge.getValue(o2m.get(obj), o2r.get(obj), p, returnType);
		} catch (RDFDataException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException( e );
		} catch (ModelRuntimeException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException( e );
		}
	}

	public static void set(Object obj, URI p, Object value) throws ReactorException {
		try {
			Bridge.setValue(o2m.get(obj), o2r.get(obj), p, toNode(value));
		} catch (ModelRuntimeException e) {
			throw new ReactorException( e );
		}
	}

	public static void add(Object obj, URI p, Object value) {
		// TODO Auto-generated method stub

	}

	public static Node toNode(Object o) {
		return null;
		// IMPROVE
	}

	public static void remove(Object obj, URI p, Object value) {
		// TODO Auto-generated method stub

	}

	public static Object[] getAll(Object obj, URI p, Class<?> returnType) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Object[] getAllInstances(Object obj, Class<?> returnType) {
		// TODO use annot
		return null;
	}

	public static Map<URI, Object> asMap(Object obj) {
		return new ReactorMap(o2m.get(obj), o2r.get(obj));
	}

	public static boolean removeAll(Object obj, URI p) {
		Resource r = o2r.get(obj);
		Model m = o2m.get(r);
		
		try {
			return Bridge.removeAllValues(m, r, p);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param model
	 *            RDF2Go model
	 * @return true if any statement (this,*,*) is contained in given model
	 */
	public static boolean in(Object obj, Model model) {
		try {
			return model.contains(o2r.get(obj), Variable.ANY, Variable.ANY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Model getModel(Object obj) {
		return o2m.get(obj);
	}

	/**
	 * remove all (this, rdf:type, ANY) statements
	 */
	public void delete(Object obj) {
		try {
			Bridge.delete(o2m.get(obj), o2r.get(obj));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param prop
	 *            property URI
	 * @param o
	 * @return a statement (this, uri, object) and thus bridges the gap between
	 *         the OO and RDF worlds.
	 */
	public Statement getStatement(Object obj, URI p, Object o) {
		return Bridge.getStatement(o2m.get(obj), o2r.get(obj), p, o);
	}
	
	
	public boolean equals( Object obj, Object other) {
		
		if (obj instanceof ResourceEntity) {
			ResourceEntity resourceEntity = (ResourceEntity) obj;
			if (other instanceof URI)
				return resourceEntity.getResource().equals(other);
			else if (other instanceof ResourceEntity)
				return resourceEntity.getResource().equals( ((ResourceEntity) other).getResource());
			else
				return false;
		} else if (obj instanceof URI) {
			// return true if o has same uri
			if (other instanceof ResourceEntity) {
				return ((ResourceEntity) other).getResource().equals(obj);
			} else if (other instanceof URI){
				return obj.equals(other);
			}
			else
				return false;
		} else if (obj instanceof String) {
			// return true if o has same value
			return obj.toString().equals(other.toString());
		} else if(obj instanceof LanguageTagLiteral || obj instanceof DatatypeLiteral) {
			// IMPROVE: better literal handling
			return obj.equals(other);
		} else
			// o is never == blank node or variable
			return false;
	}

}
