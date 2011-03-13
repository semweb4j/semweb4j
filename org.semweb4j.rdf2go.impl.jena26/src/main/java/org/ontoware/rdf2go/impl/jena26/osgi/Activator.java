/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max VÃ¶lkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */
package org.ontoware.rdf2go.impl.jena26.osgi;

import java.util.Hashtable;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.impl.jena26.ModelFactoryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


/**
 * Register the ModelFactory at OSGI
 * 
 * @author sauermann
 */
public class Activator implements BundleActivator {
	
	protected static BundleContext bc;
	
	private ModelFactory factory;
	
	private ServiceRegistration registration;
	
	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		bc = context;
		
		this.factory = new ModelFactoryImpl();
		this.registration = bc.registerService(ModelFactory.class.getName(), this.factory,
		        new Hashtable<Object,Object>());
	}
	
	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		this.registration.unregister();
	}
	
}
