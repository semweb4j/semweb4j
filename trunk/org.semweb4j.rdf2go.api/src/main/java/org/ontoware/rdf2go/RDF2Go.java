/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class that allows registering and finding ModelFactories If you
 * use one adaptor in your application, you should always use the default
 * instance. If you use multiple frameworks, then you should instantiate the
 * ModelFactory implementations directly.
 * 
 * <h2>Usage</h2>
 * <p>
 * This is the main entry point to use RDF2Go, and in particular to create
 * Models. You have to register an adaptor once to use this. Example:
 * </p>
 * <code>
 * // register adapter of implementation XYZ, could be Jena or Sesame or anything.
 * RDF2Go.register( new org.ontoware.rdf2go.impl.XYZ.ModelFactoryImpl() );
 * 
 * // start using RDF2Go
 * Model m = RDF2Go.getModelFactory().createModel();
 * m.addTriple(...)
 * </code>
 * 
 * For each RDF2Go adapter, an ModelFactoy implementation is needed.
 * 
 * User can call the {@link #register()} method to do that.
 * 
 * As a fall-back, when a user calls getModelFactory() but no adapter has been
 * registered yet, RDF2Go looks for a class named
 * org.ontoware.rdf2go.impl.StaticBinding and tries to call a method named
 * "getModelFactory" to get a model factory. This approach has been inspired by
 * SL4Js static bindign approach. When multiple adapters are found, the first in
 * the class path is used. This fall-back allows one to simply pack together a
 * bunch of JARs an be ready to used RDF2Go without any configuration and
 * without any OSGi.
 * 
 * The expected class looks like this: <code><pre>
 * package org.ontoware.rdf2go.impl;
 * 
 * import org.ontoware.rdf2go.ModelFactory;
 * 
 * public class StaticBinding {
 *  public static ModelFactory getModelFactory() {
 *    return new org.ontoware.rdf2go.impl.XXXX.ModelFactoryImpl();
 *  }
 * }
 * </pre></code>
 * 
 * @author sauermann
 * @author voelkel
 */
public class RDF2Go {

	private static final Logger log = LoggerFactory.getLogger( RDF2Go.class );
	
	/**
	 * the default factory for RDF2Go. This is a singleton. The field is
	 * protected by design, JUnit tests inside RDF2Go may want to reset it.
	 */
	protected static ModelFactory modelFactory;

	private static Exception instanceRegistered;

	/**
	 * get the currently registered RDF2Go factory. Usually, you will use one
	 * adaptor in your application, register this adaptor using the .register()
	 * method and it is available here. If none is registered, this throws a
	 * RuntimeException.
	 * 
	 * @return the default RDF2Go instance.
	 */
	public static final ModelFactory getModelFactory() {
		checkModelFactory();
		return modelFactory;
	}

	/**
	 * register an implementation of the RDF2Go framework. you can only register
	 * one framework inside one Java application, it will throw an Exception if
	 * you register more than one.
	 * 
	 * @param modelFactory
	 *            the factory to register. You can pass <code>null</code> to
	 *            unregister a modelFactory. Unregistering is restricted to
	 *            frameworks, you will not need it in simple applications.
	 * @throws RuntimeException
	 *             if registered already. See the cause of this exception for a
	 *             stacktrace where you first registered.
	 */
	public static final void register(ModelFactory modelFactory) {
		if (modelFactory == null) {
			RDF2Go.modelFactory = null;
			return;
		}
		if ((RDF2Go.modelFactory != null)
				&& (!RDF2Go.modelFactory.getClass().equals(
						modelFactory.getClass())))
			throw new RuntimeException("already registered framework "
					+ modelFactory.getClass().getName()
					+ " in the attached Exception (see stacktrace).",
					instanceRegistered);
		RDF2Go.modelFactory = modelFactory;
		// store the stacktrace to help people find where the falsly registered
		// first
		instanceRegistered = new Exception("registered framework before");
	}

	public static final void register(String modelFactoryClassname)
			throws ModelRuntimeException {
		Class<?> c;
		try {
			c = Class.forName(modelFactoryClassname);
			Constructor<?> constructor;
			try {
				constructor = c.getConstructor(new Class[] {});
				ModelFactory modelBuilder;
				try {
					modelBuilder = (ModelFactory) constructor.newInstance();
					register(modelBuilder);
				} catch (IllegalArgumentException e) {
					throw new ModelRuntimeException(e);
				} catch (InstantiationException e) {
					throw new ModelRuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new ModelRuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new ModelRuntimeException(e);
				}
			} catch (SecurityException e) {
				throw new ModelRuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new ModelRuntimeException(e);
			}
		} catch (ClassNotFoundException e) {
			throw new ModelRuntimeException(e);
		}

	}

	private static void checkModelFactory() {
		// before failing, try to find an adapter via a static binding
		// approach, like the one used by SL4J.org
		if (modelFactory == null) {
			try {
				Class<?> clazz = Class
						.forName("org.ontoware.rdf2go.impl.StaticBinding");
				Method method = clazz.getMethod("getModelFactory",
						new Class[] {});
				Object result = method.invoke(clazz, new Object[] {});
				if (result instanceof ModelFactory) {
					modelFactory = (ModelFactory) result;
					log.info("Using ModelFactory '"
									+ result.getClass()
									+ "' which was loaded via org.ontoware.rdf2go.impl.StaticBinding.");
				}
			} catch (ClassNotFoundException e) {
				throw new ModelRuntimeException(e);
			} catch (SecurityException e) {
				throw new ModelRuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new ModelRuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new ModelRuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new ModelRuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new ModelRuntimeException(e);
			}

		}

		// if modelfactory is still null, give up
		if (modelFactory == null) {
			throw new IllegalStateException(
					"No ModelFactoy was registered. Please register one via RDF2Go.register(ModelFactory mf)");
		}
	}

}
