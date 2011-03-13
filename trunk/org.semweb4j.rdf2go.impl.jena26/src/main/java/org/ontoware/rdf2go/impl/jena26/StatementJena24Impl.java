package org.ontoware.rdf2go.impl.jena26;

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
	
	private static final long serialVersionUID = -144220629238562048L;
	
	private static Logger log = LoggerFactory.getLogger(StatementJena24Impl.class);
	
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
		Triple t = new Triple(this.s, this.p, this.o);
		return jenaModel.asStatement(t);
	}
	
	public Resource getSubject() {
		try {
			return (Resource)TypeConversion.toRDF2Go(this.s);
		} catch(ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	public URI getPredicate() {
		try {
			return (URI)TypeConversion.toRDF2Go(this.p);
		} catch(ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	public org.ontoware.rdf2go.model.node.Node getObject() {
		try {
			return TypeConversion.toRDF2Go(this.o);
		} catch(ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return getSubject() + "--" + getPredicate() + "--" + getObject();
	}
	
	public org.ontoware.rdf2go.model.Model getModel() {
		return this.model;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof Statement) {
			Statement stmt = (Statement)other;
			boolean e = super.equals(stmt);
			log.debug("statements are equal? " + e + " now the context");
			if(e && stmt.getContext() != null && this.getModel().getContextURI() != null)
				return stmt.getContext().equals(this.getModel().getContextURI());
			else
				return e;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public URI getContext() {
		return this.model.getContextURI();
	}
	
}
