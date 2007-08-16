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

package org.ontoware.rdf2go.model;

import java.util.Iterator;

abstract class AbstractModelChangeListener implements ModelChangedListener
{
	public void addedStatement(Statement statement)
	{
		NotifyingModelTest.assertTrue(false);
	}

	public void addedStatements(Iterator<? extends Statement> statements)
	{
		NotifyingModelTest.assertTrue(false);
	}

	public void performedUpdate(Diff diff)
	{
		NotifyingModelTest.assertTrue(false);
	}

	public void removedStatement(Statement statement)
	{
		NotifyingModelTest.assertTrue(false);
	}

	public void removedStatements(Iterator<? extends Statement> statements)
	{
		NotifyingModelTest.assertTrue(false);
	}
}