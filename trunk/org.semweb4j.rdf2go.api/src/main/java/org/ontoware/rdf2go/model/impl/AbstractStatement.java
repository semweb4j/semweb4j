/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2010
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.model.impl;

import java.util.Arrays;

import org.ontoware.rdf2go.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractStatement implements Statement {
	
	private static final long serialVersionUID = 4151365048947620653L;
	private static Logger log = LoggerFactory.getLogger(StatementImpl.class);
	
	public void dump(String[] options) {
		boolean sysout = true;
		boolean _log = false;
		if(options != null) {
			sysout = Arrays.asList(options).contains("sysout");
			_log = Arrays.asList(options).contains("log");
		}
		
		String s = this.toString();
		if(sysout)
			System.out.println(s);
		if(_log)
			log.trace(s);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Statement) {
			Statement oStmt = (Statement)o;
			boolean subjects = this.getSubject().equals(oStmt.getSubject());
			if(!subjects)
				log.trace("Subjects differ: " + this.getSubject() + " vs " + oStmt.getSubject());
			boolean predicates = this.getPredicate().equals(oStmt.getPredicate());
			if(!predicates)
				log.trace("Prediactes differ: " + this.getPredicate() + " vs "
				        + oStmt.getPredicate());
			boolean objects = this.getObject().equals(oStmt.getObject());
			if(!objects)
				log.trace("Objects differ: " + this.getObject() + " vs " + oStmt.getObject());
			return subjects && predicates && objects;
		}
		// else
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getSubject().hashCode() + this.getPredicate().hashCode()
		        + this.getObject().hashCode();
	}
	
	public int compareTo(Statement o) {
		log.trace("Comparing " + this + " to " + o);
		if(this.getSubject().equals(o.getSubject())) {
			if(this.getPredicate().equals(o.getPredicate()))
				// only objects differ (maybe)
				return this.getObject().compareTo(o.getObject());
			// else: difference is in prediactes (maybe objects, too)
			return this.getPredicate().compareTo(o.getPredicate());
		}
		// else: subjects differ
		return this.getSubject().compareTo(o.getSubject());
	}
	
	public boolean matches(Statement statement) {
		return equals(statement);
	}
}
