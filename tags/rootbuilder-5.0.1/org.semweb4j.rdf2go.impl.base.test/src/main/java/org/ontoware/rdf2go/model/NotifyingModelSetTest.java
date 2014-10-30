/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max V�lkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */

package org.ontoware.rdf2go.model;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.impl.NotifyingModelSetLayer;
import org.ontoware.rdf2go.model.impl.QuadPatternImpl;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import static org.junit.Assert.*;

public class NotifyingModelSetTest extends AbstractModelSetTest
{
	private NotifyingModelSetLayer model;
	public static URI context1 = new URIImpl("test://test/context_1");
	public static URI context2 = new URIImpl("test://test/context_2");
	
	public NotifyingModelSetTest()
	{
		
	}
	
	@Override
	public ModelFactory getModelFactory()
	{
		return RDF2Go.getModelFactory();
	}
	
	
	@Override
	public void setUp() 
	{
		super.setUp();
		ModelSet plainModel = getModelFactory().createModelSet();
		assertNotNull(plainModel);
		this.model = new NotifyingModelSetLayer(plainModel);
	}
	
	public void testModelConnection()
	{
		assertNotNull(this.model);
		assertNotNull(this.model.getDelegatedModelSet());
		assertFalse(this.model.isOpen());
		this.model.open();
		assertTrue(this.model.isOpen());
		this.model.close();
		assertFalse(this.model.isOpen());
	}
	
	public void testContextNotification()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {
			@Override
			public void addedStatement(Statement statement)
			{
				assertEquals(context1, statement.getContext());
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
				assertEquals(object, statement.getObject());
			}

		};
		QuadPattern pattern = new QuadPatternImpl(context1, Variable.ANY, Variable.ANY, Variable.ANY);
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context1, subject, predicate, object);
		this.model.close();
	}
	
	public void testContextNotification2()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {};
		QuadPattern pattern = new QuadPatternImpl(context1, Variable.ANY, Variable.ANY, Variable.ANY);
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context2, subject, predicate, object);
		this.model.close();
	}
	
	public void testSubjectNotification()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {
			@Override
			public void addedStatement(Statement statement)
			{
				assertEquals(context1, statement.getContext());
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
				assertEquals(object, statement.getObject());
			}
		};
		QuadPattern pattern = new QuadPatternImpl(Variable.ANY, subject, Variable.ANY, Variable.ANY);
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context1, subject, predicate, object);
		this.model.close();
	}
	
	public void testSubjectNotification2()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {};
		QuadPattern pattern = new QuadPatternImpl(Variable.ANY, subject, Variable.ANY, Variable.ANY);
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context1, predicate, object, subject);
		this.model.close();
	}
	
	
	public void testLiteralNotification()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {
			@Override
			public void addedStatement(Statement statement)
			{
				assertEquals(context1, statement.getContext());
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
				assertEquals(new PlainLiteralImpl("Sebastian Gerke"), statement.getObject());
			}
		};
		QuadPattern pattern = new QuadPatternImpl(Variable.ANY, Variable.ANY, Variable.ANY, new PlainLiteralImpl("Sebastian Gerke"));
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context1, subject, predicate, new PlainLiteralImpl("Sebastian Gerke"));
		this.model.close();
	}
	
	
	public void testLiteralNotification2()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {};
		QuadPattern pattern = new QuadPatternImpl(Variable.ANY, Variable.ANY, Variable.ANY, new PlainLiteralImpl("Sebastian"));
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context1, subject, predicate, new PlainLiteralImpl("Sebastian Gerke"));
		this.model.close();
	}
	
	public void testContextAndSubjectNotification()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {
			@Override
			public void addedStatement(Statement statement)
			{
				assertEquals(context1, statement.getContext());
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
				assertEquals(object, statement.getObject());
			}
		};
		QuadPattern pattern = new QuadPatternImpl(context1, subject, Variable.ANY, Variable.ANY);
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context1, subject, predicate, object);
		this.model.close();
	}
	
	public void testContextAndSubjectNotification2()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {};
		QuadPattern pattern = new QuadPatternImpl(context1, subject, Variable.ANY, Variable.ANY);
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context1, predicate, object, subject);
		this.model.close();
	}
	
	public void testContextAndSubjectNotification3()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {};
		QuadPattern pattern = new QuadPatternImpl(context1, subject, Variable.ANY, Variable.ANY);
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context2, subject, predicate, object);
		this.model.close();
	}
	
	public void testPredicateNotification()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {
			@Override
			public void addedStatement(Statement statement)
			{
				assertEquals(context1, statement.getContext());
				assertEquals(subject, statement.getSubject());
				assertEquals(predicate, statement.getPredicate());
				assertEquals(object, statement.getObject());
			}
		};
		QuadPattern pattern = new QuadPatternImpl(Variable.ANY, Variable.ANY, predicate,  Variable.ANY);
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context1, subject, predicate, object);
		this.model.close();
	}
	
	public void testPredicateNotification2()
	{
		ModelChangedListener listener = new AbstractModelChangeListener() {};
		QuadPattern pattern = new QuadPatternImpl(Variable.ANY, Variable.ANY, predicate,  Variable.ANY);
		this.model.addModelSetChangedListener(listener, pattern);
		this.model.open();
		this.model.addStatement(context1, predicate, object, subject);
		this.model.close();
	}

}
