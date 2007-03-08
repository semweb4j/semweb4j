/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.impl.sesame2;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.ModelSet;
import org.openrdf.repository.RepositoryImpl;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 * Generates ModelSets and Models always backed with 
 * File-based Model Implementations.
 * <b>This uses Jave temp files, the temp directory on your computer
 * may get crowded.</b>
 * Use this for testing
 * @author sauermann
 */
public class SesameModelFactoryFileBasedForTest extends ModelFactoryImpl {

	private static final Log log = LogFactory.getLog(SesameModelFactoryFileBasedForTest.class);
	
    File tempBase = null;
    /**
     * 
     */
    public SesameModelFactoryFileBasedForTest() {
    }
    
    /**
     * generate a temp directory
     * @return
     * @throws IOException
     */
    protected File getTempDir() throws IOException {
        if (tempBase == null)
        {
            tempBase = File.createTempFile("rdf2gosesame2unittest", null);
            tempBase.delete();
            tempBase.mkdirs();
        }
        File result = File.createTempFile("model", null, tempBase);
        result.delete();
        result.mkdirs();
        return result;
    }

    @Override
    public ModelSet createModelSet() throws ModelRuntimeException {
        try {
            File f = getTempDir();
            NativeStore store = new NativeStore(f);
            RepositoryImpl repo = new RepositoryImpl(store);
            repo.initialize();
            log.info("using temp-folder for test factory: "+f);
            ModelSet result = new ModelSetImplSesame(repo);
            return result;
        } catch (Exception e) {
            throw new ModelRuntimeException(e);
        }
    }

}
