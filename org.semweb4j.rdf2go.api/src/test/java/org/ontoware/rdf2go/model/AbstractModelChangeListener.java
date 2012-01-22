/*
 * LICENSE INFORMATION Copyright 2005-2007 by FZI (http://www.fzi.de). Licensed
 * under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max V�lkel <ORGANIZATION> = FZI Forschungszentrum Informatik
 * Karlsruhe, Karlsruhe, Germany <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */

package org.ontoware.rdf2go.model;

import java.util.Iterator;

import junit.framework.Assert;


public abstract class AbstractModelChangeListener implements ModelChangedListener {
	@Override
    public void addedStatement(Statement statement) {
		Assert.fail();
	}
	
	@Override
    public void addedStatements(Iterator<? extends Statement> statements) {
		Assert.fail();
	}
	
	@Override
    public void performedUpdate(DiffReader diff) {
		Assert.fail();
	}
	
	@Override
    public void removedStatement(Statement statement) {
		Assert.fail();
	}
	
	@Override
    public void removedStatements(Iterator<? extends Statement> statements) {
		Assert.fail();
	}
	
}
