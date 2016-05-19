/*
 * Copyright (c) 2006 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz
 * DFKI GmbH. All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.openrdf.rdf2go.osgi;

import java.util.Hashtable;

import org.ontoware.rdf2go.ModelFactory;
import org.openrdf.rdf2go.RepositoryModelFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;


/**
 * Register the ModelFactory at OSGI
 * 
 * @author sauermann
 */
public class Activator implements BundleActivator {
	
	protected static BundleContext bc;
	
	private ModelFactory factory;
	
	private ServiceReference reference;
	
	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		
		bc = context;
		
		this.factory = new RepositoryModelFactory();
		ServiceRegistration registration = bc.registerService(ModelFactory.class.getName(),
		        this.factory, new Hashtable<String ,Object>());
		this.reference = registration.getReference();
	}
	
	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		bc.ungetService(this.reference);
	}
	
}
