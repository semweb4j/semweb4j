package org.ontoware.rdf2go.impl.jena24;



import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.AbstractStatement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;

public class StatementJena24Impl extends AbstractStatement implements Statement {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger( StatementJena24Impl.class); 
	
	private Node s, p, o;

	private org.ontoware.rdf2go.model.Model model;

	public StatementJena24Impl(org.ontoware.rdf2go.model.Model model, Node s, Node p, Node o) {
		this.model = model;
		assert s != null;
		assert p != null;
		assert o != null;
		this.s = s;
		this.p = p;
		this.o = o;
	}

	public com.hp.hpl.jena.rdf.model.Statement toJenaStatement(Model jenaModel) {
		Triple t = new Triple(s, p, o);
		return jenaModel.asStatement(t);
	}

	public Resource getSubject() {
		try {
			return (Resource) TypeConversion.toRDF2Go(s);
		} catch (ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public URI getPredicate() {
		try {
			return (URI) TypeConversion.toRDF2Go(p);
		} catch (ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public org.ontoware.rdf2go.model.node.Node getObject() {
		try {
			return (org.ontoware.rdf2go.model.node.Node) TypeConversion.toRDF2Go(o);
		} catch (ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	public String toString() {
		return getSubject()+"--"+getPredicate()+"--"+getObject();
	}

	public org.ontoware.rdf2go.model.Model getModel() {
		return this.model;
	}	
	
	
	
	@Override
	public boolean equals(Object o)
	{
		
		if (o instanceof Statement)
		{
			Statement stmt = (Statement) o;
			boolean e = super.equals(stmt);
			log.debug("statements are equal? "+e+" now the context");
			if (e && stmt.getContext() != null && this.getModel().getContextURI() != null) 
				return stmt.getContext().equals(this.getModel().getContextURI());
			else return e;
		}
		return false;
	}
	
	public int hashCode() {
		return super.hashCode();
	}

	public URI getContext() {
		return this.model.getContextURI();
	}

}
