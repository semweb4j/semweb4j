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
import org.ontoware.rdf2go.model.impl.DiffImpl;
import org.ontoware.rdf2go.model.impl.NotifyingModelLayer;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;

public class NotifyingModelTest extends AbstractModelTest {
	// abstract superclass for ModelchangeListeners in this test. By default,
	// no method of the changelistener should be called, if a method is expected
	// to be called, this method has to be overwritten.

	// TODO: as the model in AbstractModelTest is now protected, this field has
	// to go and be replaced by ((NotifyingModelLayer)this.model).method
	NotifyingModelLayer notifyingModel;

	@Override
	public ModelFactory getModelFactory() {
		return RDF2Go.getModelFactory();
	}

	@Override
	public void setUp() {
		super.setUp();
		Model plainModel = getModelFactory().createModel();
		assertNotNull(plainModel);
		this.notifyingModel = new NotifyingModelLayer(plainModel);
	}

	public void testModelConnection() {
		assertNotNull(this.notifyingModel);
		assertNotNull(this.notifyingModel.getDelegatedModel());
		assertFalse(this.notifyingModel.isOpen());
		this.notifyingModel.open();
		assertTrue(this.notifyingModel.isOpen());
		this.notifyingModel.close();
		assertFalse(this.notifyingModel.isOpen());
	}

	public void testAddStatement() {
		this.notifyingModel.open();
		this.notifyingModel.addStatement(predicate, object, subject); // this
																		// statement
		// should
		// not cause a
		// notification
		this.notifyingModel
				.addModelChangedListener(new AbstractModelChangeListener() {

					@Override
					public void addedStatement(Statement statement) {
						assertEquals(new StatementImpl(null, subject,
								predicate, object), statement);
					}

					@Override
					public void addedStatements(
							Iterator<? extends Statement> statements) {
						assertTrue(statements.hasNext());
						assertEquals(new StatementImpl(null, subject,
								predicate, object), statements.next());
						assertFalse(statements.hasNext());
					}
				});
		this.notifyingModel.addStatement(subject, predicate, object);
		this.notifyingModel.close();
	}

	public void testRemoveStatement() {
		this.notifyingModel.open();
		this.notifyingModel.addStatement(subject, predicate, object);

		this.notifyingModel
				.addModelChangedListener(new AbstractModelChangeListener() {

					@Override
					public void removedStatement(Statement statement) {
						assertEquals(new StatementImpl(null, subject,
								predicate, object), statement);
					}

					@Override
					public void removedStatements(
							Iterator<? extends Statement> statements) {
						assertTrue(statements.hasNext());
						assertEquals(new StatementImpl(null, subject,
								predicate, object), statements.next());
						assertFalse(statements.hasNext());
					}
				});
		this.notifyingModel.close();
	}

	public void testChangeStatement() {
		this.notifyingModel
				.addModelChangedListener(new AbstractModelChangeListener() {
					@Override
					public void performedUpdate(DiffReader diff) {
						int i = 0;
						for (Statement s : diff.getAdded()) {
							i++;
							assertEquals(new StatementImpl(null, subject,
									predicate, object), s);
						}
						assertEquals(1, i);
						assertFalse(diff.getRemoved().iterator().hasNext());
					}
				});
		this.notifyingModel.open();
		Diff diff = new DiffImpl();
		diff.addStatement(subject, predicate, object);
		this.notifyingModel.update(diff);
		this.notifyingModel.close();
	}

	public void testNotificationOnSubject() {
		this.notifyingModel.open();
		ModelChangedListener listener = new AbstractModelChangeListener() {
			@Override
			public void addedStatement(Statement statement) {
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
			}

			@Override
			public void addedStatements(Iterator<? extends Statement> statements) {
				assertTrue(statements.hasNext());
				Statement next = statements.next();
				assertEquals(subject, next.getSubject());
				assertEquals(predicate, next.getPredicate());
				assertFalse(statements.hasNext());
			}
		};
		this.notifyingModel.addModelChangedListener(listener,
				new TriplePatternImpl(subject, Variable.ANY, Variable.ANY));

		this.notifyingModel.addStatement(subject, predicate, "Test1");
		this.notifyingModel.addStatement(predicate, object, "Test2");
		this.notifyingModel.addStatement(predicate, subject, "Test3");
		this.notifyingModel.removeModelChangedListener(listener);
		listener = new AbstractModelChangeListener() {
			@Override
			public void removedStatement(Statement statement) {
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
			}

			@Override
			public void removedStatements(
					Iterator<? extends Statement> statements) {
				assertTrue(statements.hasNext());
				Statement next = statements.next();
				assertEquals(subject, next.getSubject());
				assertEquals(predicate, next.getPredicate());
				assertFalse(statements.hasNext());
			}
		};
		this.notifyingModel.removeStatement(subject, predicate, "Test1");
		this.notifyingModel.removeStatement(predicate, object, "Test2");
		this.notifyingModel.removeStatement(predicate, subject, "Test3");
		this.notifyingModel.close();
	}

	public void testNotificationOnLiteralObject() {
		this.notifyingModel.open();
		ModelChangedListener listener = new AbstractModelChangeListener() {
			@Override
			public void addedStatement(Statement statement) {
				assertEquals(new PlainLiteralImpl("Sebastian"), statement
						.getObject());
			}
		};
		this.notifyingModel.addModelChangedListener(listener,
				new TriplePatternImpl(Variable.ANY, Variable.ANY,
						new PlainLiteralImpl("Sebastian")));
		this.notifyingModel.addStatement(subject, predicate, "Sebastian Gerke");
		this.notifyingModel.addStatement(predicate, object, "Sebastian");
		this.notifyingModel.close();
	}

	public void testNotificationOnPredicateAndObject() {
		this.notifyingModel.open();
		ModelChangedListener listener = new AbstractModelChangeListener() {
			@Override
			public void addedStatement(Statement statement) {
				System.out.println(statement.getObject().getClass());
				assertEquals(new PlainLiteralImpl("Sebastian"), statement
						.getObject());
				assertEquals(predicate, statement.getPredicate());
			}
		};
		this.notifyingModel.addModelChangedListener(listener,
				new TriplePatternImpl(Variable.ANY, predicate,
						new PlainLiteralImpl("Sebastian")));
		this.notifyingModel.addStatement(subject, predicate, "Sebastian Gerke");
		this.notifyingModel.addStatement(subject, predicate, "Sebastian");
		this.notifyingModel.addStatement(predicate, object, "Sebastian");
		this.notifyingModel.close();
	}
}
