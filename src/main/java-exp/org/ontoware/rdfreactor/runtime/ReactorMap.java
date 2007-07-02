package org.ontoware.rdfreactor.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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

public class ReactorMap implements Map<URI, Object> {

	private static final Logger log = LoggerFactory.getLogger(ReactorMap.class);

	private Model model;

	private Resource id;

	public ReactorMap(Model model, Resource id) {
		this.model = model;
		this.id = id;
	}

	// ////////////////////
	// implement java.util.Map

	/**
	 * warning, this is a costly operation, as it iterates over all content
	 * rewriting rdf2go with Sets would maybe help
	 * 
	 * @return number of triples matching (this, ANY, ANY)
	 */
	public int size() {
		int count = 0;
		try {
			ClosableIterator<? extends Statement> it = this.model
					.findStatements(this.id, Variable.ANY, Variable.ANY);
			while (it.hasNext()) {
				it.next();
				count++;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return count;
	}

	/**
	 * @return true if model contains at least one triple (this, ANY, ANY)
	 */
	public boolean isEmpty() {
		try {
			return !this.model.contains(this.id, Variable.ANY, Variable.ANY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return true if model contains at least one triple (this, key, ANY)
	 */
	public boolean containsKey(Object key) {
		if (key instanceof URI)
			try {
				return !this.model.contains(this.id, (URI) key, Variable.ANY);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		else {
			throw new ClassCastException("an instance of " + key.getClass()
					+ " can never be contained in this map, only URIs can");
		}
	}

	/**
	 * @return true if model contains at least one triple (this, ANY, value)
	 * @throws ModelRuntimeException
	 *             if object could not be converted to an RDF object
	 */
	public boolean containsValue(Object value) {
		try {
			Node node = TypeUtils.toNode(value);
			// if no exception, node not null
			assert node != null;
			return this.model.contains(this.id, Variable.ANY, node);
		} catch (ModelRuntimeException e) {
			log.debug("an instance of " + value.getClass()
					+ " can never be contained in this map");
			return false;
		}
	}

	/**
	 * @return the first instance of (this, key, ANY)
	 */
	public Object get(Object key) {
		if (key instanceof URI) {
			try {
				return FORD.get(this.id, (URI) key, Object.class);
			} catch (RDFDataException e) {
				// TODO: is this what users want?
				throw new RuntimeException(e);
			}
		} else
			throw new ClassCastException("an instance of " + key.getClass()
					+ " can never be contained in this map, only URIs can");
	}

	/**
	 * Add triple (this, key, value) to the model
	 * 
	 * @param key
	 * @param value
	 * @return old value x of triple (this, key, x)
	 */
	public Object put(URI key, Object value) {
		if (value == null)
			throw new NullPointerException(
					"This RDF backed Map cannot store null values");

		if (key instanceof URI) {
			Object oldValue = get(key);
			try {
				FORD.set(this.id, (URI) key, value);
			} catch (RuntimeException e) {
				throw new IllegalArgumentException(
						"Could not store value of type " + value.getClass());
			}
			return oldValue;
		} else
			throw new ClassCastException("an instance of " + key.getClass()
					+ " can never be contained in this map, only URIs can");
	}

	/**
	 * Remove all triples in the model matching (this, key, ANY)
	 * 
	 * @param key,
	 *            is Subject of the triple
	 * @return old value x of (this, key, x)
	 */
	public Object remove(Object key) {
		if (key == null)
			throw new NullPointerException("key may not be null");

		if (key instanceof URI) {
			try {
				Object oldValue = FORD.get(this.id, (URI) key, Object.class);
				FORD.removeAll(this.id, (URI) key);
				return oldValue;
			} catch (RDFDataException e) {
				// TODO: is this what users want?
				throw new RuntimeException(e);
			}
		} else
			throw new ClassCastException("an instance of " + key.getClass()
					+ " can never be contained in this map, only URIs can");
	}

	/**
	 * Put all URI/Object pairs in the given Map t into the model
	 * 
	 * @param t,
	 *            Map with URI/Object pairs
	 */
	public void putAll(Map<? extends URI, ? extends Object> t) {
		// TODO use generics better?
		Iterator<?> it = t.entrySet().iterator();
		while (it.hasNext()) {
			Entry<?, ?> entry = (Entry) it.next();
			URI uri = (URI) entry.getKey();
			put(uri, entry.getValue());
		}
	}

	/**
	 * remove all triples (this, ANY, ANY) from the model
	 */
	public void clear() {
		this.model.removeStatements(this.id, Variable.ANY, Variable.ANY);
	}

	/**
	 * @return all Predicates x of triples matching (this, x, ANY)
	 */
	public Set<URI> keySet() {
		Set<URI> keyset = new HashSet<URI>();
		try {
			ClosableIterator<? extends Statement> it = this.model
					.findStatements(this.id, Variable.ANY, Variable.ANY);
			while (it.hasNext()) {
				keyset.add(it.next().getPredicate());
			}
			it.close();
			return keyset;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return all Objects x of triples matching (this, ANY, x)
	 */
	public Collection<Object> values() {
		Collection<Object> values = new ArrayList<Object>();
		try {
			ClosableIterator<? extends Statement> it = this.model
					.findStatements(this.id, Variable.ANY, Variable.ANY);
			while (it.hasNext()) {
				values.add(it.next().getObject());
			}
			it.close();
			return values;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return a Set of Entrys, each containing a Predicate x and Object y, for
	 *         every triple matching (this, x, y)
	 */
	public Set<Entry<URI, Object>> entrySet() {
		Set<Entry<URI, Object>> entries = new HashSet<Entry<URI, Object>>();
		try {

			ClosableIterator<? extends Statement> it = this.model.findStatements(this.id,
					Variable.ANY, Variable.ANY);
			while (it.hasNext()) {
				Statement stmt = it.next();
				Map.Entry<URI, Object> e = new EntryImpl<URI, Object>(this,
						stmt.getPredicate(), stmt.getObject());
				entries.add(e);
			}
			it.close();
			return entries;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * <b>EntryImpl</b> is used by ReactorBaseImpl.entrySet() to keep the
	 * connection between an URI x used as the subject and an Object y used as
	 * the object of a statement (this, x, y) in the model.
	 * 
	 * We habe to implement the Map.Entry<?,?> interface because we want to
	 * allow setting the value of the Object y, and we need to know the
	 * underlying ReactorBase instance for that.
	 * 
	 * @author mvo
	 * 
	 * @param <K>
	 * @param <V>
	 */
	class EntryImpl<K, V> implements Map.Entry<URI, Object> {

		private URI predicate;

		private Object object;

		private ReactorMap base;

		public EntryImpl(ReactorMap base, URI predicate, Object object) {
			this.predicate = predicate;
			this.object = object;
			this.base = base;
		}

		public URI getKey() {
			return this.predicate;
		}

		public Object getValue() {
			return this.object;
		}

		/**
		 * Set the value x for the triple (this, predicate, x) corresponding to
		 * this EntryImpl instance. We need the underlying ReactorBase instance
		 * for this, because it is connected to the model.
		 * 
		 * @param value
		 *            is the Object of a triple
		 * @return the old value
		 */
		public Object setValue(Object value) {
			Object oldValue = this.base.get(predicate);
			FORD.set(base.id, predicate, value);
			return oldValue;
		}

	}

}
