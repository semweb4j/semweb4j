/*
 * Copyright (c) 2006 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 */
package org.ontoware.rdf2go.osgi;

import java.util.logging.Logger;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * Activate the RDF2Go framework.
 * Listens to registered OSGI services and searches for registered
 * {@link ModelFactory} instances.
 * The found factories are compared to a configuration value, if the
 * configured factory is found, it is set as default. This is the configuration
 * value to set:
 * <code>org.ontoware.rdf2go.defaultmodelfactory=(classname of modelfactory)</code>
 *  
 * @author sauermann
 */
public class RDF2GoActivator implements BundleActivator, ServiceListener {
    
    /**
     * set this variable in the OSGI config to define the default driver
     * for RDF2Go.
     */
    public static final String DEFAULTMODELFACTORY_CFG = "org.ontoware.rdf2go.defaultmodelfactory";
    
    private BundleContext bc;
    
    private String defaultFactoryClassName = null;
    
    private static final Logger log = Logger.getLogger(RDF2GoActivator.class.getName());

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        bc = context;
        
        defaultFactoryClassName = context.getProperty(DEFAULTMODELFACTORY_CFG);
        if (defaultFactoryClassName == null)
        {
            log.warning("RDF2Go cannot find configuration value for default RDF2Go factory. " +
                    "No Default ModelFactory will be available. Please set " +
                DEFAULTMODELFACTORY_CFG);
        }
        
        String filter = "(objectclass=" + ModelFactory.class.getName() + ")";
        context.addServiceListener(this, filter);

        ServiceReference references[] = context.getServiceReferences(null, filter);

        for (int i = 0; references != null && i < references.length; i++) {
            this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, references[i]));
        }
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        bc = null;
    }

    /**
     * Listen to changes in the ModelFactories.
     * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
     */
    public void serviceChanged(ServiceEvent event) {
        if (defaultFactoryClassName == null)
            return;
        
        ModelFactory factory;
        switch (event.getType()) {
        case ServiceEvent.REGISTERED:
            factory = (ModelFactory) bc.getService(event
                    .getServiceReference());
            if (factory.getClass().getName().equals(defaultFactoryClassName))
            {
                log.info("RDF2Go uses "+defaultFactoryClassName+" as default ModelFactory");
                RDF2Go.register(factory);
            }
            else
                bc.ungetService(event.getServiceReference());
            break;
        case ServiceEvent.UNREGISTERING:
            factory = (ModelFactory) bc.getService(event
                .getServiceReference());
            if (factory.getClass().getName().equals(defaultFactoryClassName))
            {
                log.fine("RDF2Go unregistered the ModelFactory "+defaultFactoryClassName+" as default ModelFactory. No ModelFactory available now. The Bundle of the ModelFactory was unregistered.");
                RDF2Go.register((ModelFactory)null);
            }
            bc.ungetService(event
                .getServiceReference());
            break;
        }
        // We do not care for modified
        //case ServiceEvent.MODIFIED:
    }


}