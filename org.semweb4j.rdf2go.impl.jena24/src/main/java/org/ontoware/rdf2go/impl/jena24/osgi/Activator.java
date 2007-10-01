/*
 * Copyright (c) 2006 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 */
package org.ontoware.rdf2go.impl.jena24.osgi;

import java.util.Hashtable;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.impl.jena24.ModelFactoryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Register the ModelFactory at OSGI
 * @author sauermann
 */
public class Activator implements BundleActivator {

    protected static BundleContext bc;

    private ModelFactory factory;

    private ServiceReference reference;

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @SuppressWarnings("unchecked")
	public void start(BundleContext context) throws Exception {

        bc = context;

        factory = new ModelFactoryImpl();
        ServiceRegistration registration = bc.registerService(ModelFactory.class.getName(), factory,
            new Hashtable());
        reference = registration.getReference();
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        bc.ungetService(reference);
    }

}