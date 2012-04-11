package org.ontoware.rdf2go.impl.jena29;

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

/**
 * A Jena 2.9 implementation of the {@linkplain Statement} interface.
 * 
 * @version $Revision$
 *
 */
public class StatementJena29Impl extends AbstractStatement implements Statement {
	
	private static final long serialVersionUID = -144220629238562048L;
	
	private static Logger log = LoggerFactory.getLogger(StatementJena29Impl.class);
	
	private Node c, s, p, o;
	
	private org.ontoware.rdf2go.model.Model model;
	
	/**
	 * Contruct a quadruple or supply {@code null} as context.
	 * 
	 * @param c
	 * @param s may not be {@code null}
	 * @param p may not be {@code null}
	 * @param o may not be {@code null}
	 */
	public StatementJena29Impl(Node c, Node s, Node p, Node o) {
		assert s != null;
		assert p != null;
		assert o != null;
		this.c = c;
		this.s = s;
		this.p = p;
		this.o = o;
	}

	/**
	 * Construct a statement.
	 * 
	 * @param model
	 * @param s may not be {@code null}
	 * @param p may not be {@code null}
	 * @param o may not be {@code null}
	 */
	public StatementJena29Impl(org.ontoware.rdf2go.model.Model model, Node s, Node p, Node o) {
		assert s != null;
		assert p != null;
		assert o != null;
		this.model = model;
		this.s = s;
		this.p = p;
		this.o = o;
	}

	public com.hp.hpl.jena.rdf.model.Statement toJenaStatement(Model jenaModel) {
		Triple t = new Triple(this.s, this.p, this.o);
		return jenaModel.asStatement(t);
	}
	
	@Override
    public Resource getSubject() {
		try {
			return (Resource)TypeConversion.toRDF2Go(this.s);
		} catch(ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
    public URI getPredicate() {
		try {
			return (URI)TypeConversion.toRDF2Go(this.p);
		} catch(ModelRuntimeException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	@Override
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
			if(e && stmt.getContext() != null && this.getContext() != null)
				return stmt.getContext().equals(this.getContext());
			else
				return e;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
    public URI getContext() {
		/**
		 * Return the graph URI or {@code null} depending on how this Statement
		 * was contructed
		 */
		if (this.c != null) {
			return (URI)TypeConversion.toRDF2Go(this.c);
		}
		else if (this.model != null) {
			return this.model.getContextURI();
		}
		else {
			return null;
		}
	}
	
}
