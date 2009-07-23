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
package org.ontoware.rdf2go.osgi;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activate the RDF2Go framework. Listens to registered OSGI services and
 * searches for registered {@link ModelFactory} instances. The found factories
 * are compared to a configuration value, if the configured factory is found, it
 * is set as default. This is the configuration value to set:
 * <code>org.ontoware.rdf2go.defaultmodelfactory=(classname of modelfactory)</code>
 * 
 * This class has been contributed by Deutsches Forschungszentrum fuer
 * Kuenstliche Intelligenz DFKI GmbH.
 * 
 * TODO sauermann why cannot just the implementation bundle register itself? It shouldn't
 * be the APIs job to register implementations.
 * 
 * @author sauermann
 */
public class RDF2GoActivator implements BundleActivator, ServiceListener {

	/**
	 * set this variable in the OSGI config to define the default driver for
	 * RDF2Go.
	 */
	public static final String DEFAULTMODELFACTORY_CFG = 
		"org.ontoware.rdf2go.defaultmodelfactory";
	
	/**
	 * The filter used to search for ModelFactories
	 */
	public static final String MODEL_FACTORY_FILTER = 
		"(objectclass=" + ModelFactory.class.getName() + ")";

	private BundleContext bc;

	private String defaultFactoryClassName = null;
	
	private ServiceReference currentFactorySR;

	private static final Logger log = 
		LoggerFactory.getLogger(RDF2GoActivator.class);

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.bc = context;
		this.defaultFactoryClassName = context.getProperty(DEFAULTMODELFACTORY_CFG);
		this.bc.addServiceListener(this, MODEL_FACTORY_FILTER);
		initalizeListener();
	}
	
	private void initalizeListener() {
		ServiceReference references[];
		try {
			references = this.bc.getServiceReferences(null,MODEL_FACTORY_FILTER);
			for (int i = 0; references != null && i < references.length; i++) {
				this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED,
						references[i]));
			}
		} catch (InvalidSyntaxException e) {
			// this will not happen
			log.error("Syntax error",e);
		}
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 * 
	 *      TODO leo/antoni document why context is not needed/used for stopping the bundle
	 */
	public void stop(@SuppressWarnings("unused") BundleContext context)
			throws Exception {
		this.bc = null;
	}

	/**
	 * Listen to changes in the ModelFactories.
	 * 
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
	public void serviceChanged(ServiceEvent event) {
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			handleRegisteredEvent(event);
			break;
		case ServiceEvent.UNREGISTERING:
			handleUnregisteredEvent(event);
			break;
		}
		// We do not care for modified
		// case ServiceEvent.MODIFIED:
	}

	private void handleRegisteredEvent(ServiceEvent event) {
		ServiceReference ref = event.getServiceReference();
		ModelFactory factory = (ModelFactory) this.bc.getService(ref);
		String currClass = getCurrentFactoryClassName();
		String newClass = factory.getClass().getName();
		if (this.currentFactorySR == null) {
			log.debug("Registering " + factory.getClass().getName()
					+ " as default ModelFactory");
			this.currentFactorySR = ref;
			RDF2Go.register(factory);
		} else if (this.defaultFactoryClassName != null && 
				  !currClass.equals(this.defaultFactoryClassName) &&
				  newClass.equals(this.defaultFactoryClassName)) {
			RDF2Go.register((ModelFactory) null);
			this.bc.ungetService(this.currentFactorySR);

			factory = (ModelFactory) this.bc.getService(ref);
			this.currentFactorySR = ref;
			RDF2Go.register(factory);
			log.debug("RDF2Go uses " + newClass + " as default ModelFactory");
		} else {
			this.bc.ungetService(ref);
		}
	}
	
	private void handleUnregisteredEvent(ServiceEvent event) {
		ServiceReference ref = event.getServiceReference();
		String currClass = getCurrentFactoryClassName();
		if (ref == this.currentFactorySR) {
			RDF2Go.register((ModelFactory) null);
			this.currentFactorySR = null;
			log.debug("RDF2Go unregistered the ModelFactory "
				+ currClass
				+ " as default ModelFactory. No ModelFactory available now. " 
				+ "The Bundle of the ModelFactory was unregistered.");
		}
		this.bc.ungetService(ref);
		initalizeListener();
	}
	
	private String getCurrentFactoryClassName() {
		ModelFactory currFactory = null;
		try {
			currFactory = RDF2Go.getModelFactory();
		} catch (RuntimeException e) {
			// do nothing, this means that no factory has been registered
		}
		return (currFactory == null ? null : currFactory.getClass().getName());
	}
}