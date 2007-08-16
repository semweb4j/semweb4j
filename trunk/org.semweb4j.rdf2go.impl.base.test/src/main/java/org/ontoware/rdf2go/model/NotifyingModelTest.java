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

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.impl.NotifyingModelLayer;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;

public class NotifyingModelTest extends AbstractModelTest
{
	// abstract superclass for ModelchangeListeners in this test. By default,
	// no method of the changelistener should be called, if a method is expected
	// to be called, this method has to be overwritten.

	NotifyingModelLayer	model;

	@Override
	public ModelFactory getModelFactory()
	{
		return RDF2Go.getModelFactory();
	}

	@Override
	public void setUp() throws ModelRuntimeException
	{
		super.setUp();
		Model plainModel = getModelFactory().createModel();
		assertNotNull(plainModel);
		model = new NotifyingModelLayer(plainModel);
	}

	public void testModelConnection()
	{
		assertNotNull(model);
		assertNotNull(model.getDelegatedModel());
		assertFalse(model.isOpen());
		model.open();
		assertTrue(model.isOpen());
		model.close();
		assertFalse(model.isOpen());
	}

	public void testAddStatement()
	{
		model.open();
		model.addStatement(predicate, object, subject); // this statement should
		// not cause a
		// notification
		model.addModelChangedListener(new AbstractModelChangeListener()
		{

			@Override
			public void addedStatement(Statement statement)
			{
				assertEquals(
						new StatementImpl(null, subject, predicate, object),
						statement);
			}

			@Override
			public void addedStatements(Iterator<? extends Statement> statements)
			{
				assertTrue(statements.hasNext());
				assertEquals(
						new StatementImpl(null, subject, predicate, object),
						statements.next());
				assertFalse(statements.hasNext());
			}
		});
		model.addStatement(subject, predicate, object);
		model.close();
	}

	public void testRemoveStatement()
	{
		model.open();
		model.addStatement(subject, predicate, object);

		model.addModelChangedListener(new AbstractModelChangeListener()
		{

			@Override
			public void removedStatement(Statement statement)
			{
				assertEquals(
						new StatementImpl(null, subject, predicate, object),
						statement);
			}

			@Override
			public void removedStatements(
					Iterator<? extends Statement> statements)
			{
				assertTrue(statements.hasNext());
				assertEquals(
						new StatementImpl(null, subject, predicate, object),
						statements.next());
				assertFalse(statements.hasNext());
			}
		});
		model.close();
	}

	public void testChangeStatement()
	{
		model.addModelChangedListener(new AbstractModelChangeListener()
		{
			@Override
			public void performedUpdate(Diff diff)
			{
				int i = 0;
				for (Statement s : diff.getAdded())
				{
					i++;
					assertEquals(new StatementImpl(null, subject, predicate,
							object), s);
				}
				assertEquals(1, i);
				assertFalse(diff.getRemoved().iterator().hasNext());
			}
		});
		model.open();
		Diff diff = new DiffImpl();
		diff.addStatement(subject, predicate, object);
		model.update(diff);
		model.close();
	}

	public void testNotificationOnSubject()
	{
		model.open();
		ModelChangedListener listener = new AbstractModelChangeListener()
		{
			@Override
			public void addedStatement(Statement statement)
			{
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
			}

			@Override
			public void addedStatements(Iterator<? extends Statement> statements)
			{
				assertTrue(statements.hasNext());
				Statement next = statements.next();
				assertEquals(subject, next.getSubject());
				assertEquals(predicate, next.getPredicate());
				assertFalse(statements.hasNext());
			}
		};
		model.addModelChangedListener(listener, new TriplePatternImpl(subject,
				Variable.ANY, Variable.ANY));

		model.addStatement(subject, predicate, "Test1");
		model.addStatement(predicate, object, "Test2");
		model.addStatement(predicate, subject, "Test3");
		model.removeModelChangedListener(listener);
		listener = new AbstractModelChangeListener()
		{
			public void removedStatement(Statement statement)
			{
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
			}

			public void removedStatements(
					Iterator<? extends Statement> statements)
			{
				assertTrue(statements.hasNext());
				Statement next = statements.next();
				assertEquals(subject, next.getSubject());
				assertEquals(predicate, next.getPredicate());
				assertFalse(statements.hasNext());
			}
		};
		model.removeStatement(subject, predicate, "Test1");
		model.removeStatement(predicate, object, "Test2");
		model.removeStatement(predicate, subject, "Test3");
		model.close();
	}

	public void testNotificationOnLiteralObject()
	{
		model.open();
		ModelChangedListener listener = new AbstractModelChangeListener()
		{
			@Override
			public void addedStatement(Statement statement)
			{
				assertEquals(new PlainLiteralImpl("Sebastian"), statement.getObject());
			}
		}; 
		model.addModelChangedListener(listener, new TriplePatternImpl(Variable.ANY, Variable.ANY, new PlainLiteralImpl("Sebastian")));
		model.addStatement(subject, predicate, "Sebastian Gerke");
		model.addStatement(predicate, object, "Sebastian");
		model.close();
	}
	
	
	public void testNotificationOnPredicateAndObject()
	{
		model.open();
		ModelChangedListener listener = new AbstractModelChangeListener()
		{
			@Override
			public void addedStatement(Statement statement)
			{
				System.out.println(statement.getObject().getClass());
				assertEquals(new PlainLiteralImpl("Sebastian"), statement.getObject());
				assertEquals(predicate, statement.getPredicate());
			}
		};
		model.addModelChangedListener(listener, new TriplePatternImpl(Variable.ANY, predicate, new PlainLiteralImpl("Sebastian")));
		model.addStatement(subject, predicate, "Sebastian Gerke");
		model.addStatement(subject, predicate, "Sebastian");
		model.addStatement(predicate, object, "Sebastian");
		model.close();
	}
}
