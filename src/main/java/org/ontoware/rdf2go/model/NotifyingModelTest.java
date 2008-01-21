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
	public void setUp() throws ModelRuntimeException, Exception
	{
		super.setUp();
		Model plainModel = getModelFactory().createModel();
		assertNotNull(plainModel);
		this.model = new NotifyingModelLayer(plainModel);
	}

	public void testModelConnection()
	{
		assertNotNull(this.model);
		assertNotNull(this.model.getDelegatedModel());
		assertFalse(this.model.isOpen());
		this.model.open();
		assertTrue(this.model.isOpen());
		this.model.close();
		assertFalse(this.model.isOpen());
	}

	public void testAddStatement()
	{
		this.model.open();
		this.model.addStatement(predicate, object, subject); // this statement should
		// not cause a
		// notification
		this.model.addModelChangedListener(new AbstractModelChangeListener()
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
		this.model.addStatement(subject, predicate, object);
		this.model.close();
	}

	public void testRemoveStatement()
	{
		this.model.open();
		this.model.addStatement(subject, predicate, object);

		this.model.addModelChangedListener(new AbstractModelChangeListener()
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
		this.model.close();
	}

	public void testChangeStatement()
	{
		this.model.addModelChangedListener(new AbstractModelChangeListener()
		{
			@Override
			public void performedUpdate(DiffReader diff)
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
		this.model.open();
		Diff diff = new DiffImpl();
		diff.addStatement(subject, predicate, object);
		this.model.update(diff);
		this.model.close();
	}

	public void testNotificationOnSubject()
	{
		this.model.open();
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
		this.model.addModelChangedListener(listener, new TriplePatternImpl(subject,
				Variable.ANY, Variable.ANY));

		this.model.addStatement(subject, predicate, "Test1");
		this.model.addStatement(predicate, object, "Test2");
		this.model.addStatement(predicate, subject, "Test3");
		this.model.removeModelChangedListener(listener);
		listener = new AbstractModelChangeListener()
		{
			@Override
			public void removedStatement(Statement statement)
			{
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
			}

			@Override
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
		this.model.removeStatement(subject, predicate, "Test1");
		this.model.removeStatement(predicate, object, "Test2");
		this.model.removeStatement(predicate, subject, "Test3");
		this.model.close();
	}

	public void testNotificationOnLiteralObject()
	{
		this.model.open();
		ModelChangedListener listener = new AbstractModelChangeListener()
		{
			@Override
			public void addedStatement(Statement statement)
			{
				assertEquals(new PlainLiteralImpl("Sebastian"), statement.getObject());
			}
		}; 
		this.model.addModelChangedListener(listener, new TriplePatternImpl(Variable.ANY, Variable.ANY, new PlainLiteralImpl("Sebastian")));
		this.model.addStatement(subject, predicate, "Sebastian Gerke");
		this.model.addStatement(predicate, object, "Sebastian");
		this.model.close();
	}
	
	
	public void testNotificationOnPredicateAndObject()
	{
		this.model.open();
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
		this.model.addModelChangedListener(listener, new TriplePatternImpl(Variable.ANY, predicate, new PlainLiteralImpl("Sebastian")));
		this.model.addStatement(subject, predicate, "Sebastian Gerke");
		this.model.addStatement(subject, predicate, "Sebastian");
		this.model.addStatement(predicate, object, "Sebastian");
		this.model.close();
	}
}
