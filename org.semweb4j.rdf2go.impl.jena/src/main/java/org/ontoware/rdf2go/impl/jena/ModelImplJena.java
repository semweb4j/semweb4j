package org.ontoware.rdf2go.impl.jena;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;
import org.ontoware.rdf2go.model.DiffReader;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.impl.AbstractModel;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.impl.AbstractBlankNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.shared.BadURIException;


// import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

/**
 * 
 * (wth) for information on typed literals see this very good how to
 * http://jena.sourceforge.net/how-to/typedLiterals.html
 * 
 */
public class ModelImplJena extends AbstractModel implements Model {
	private static final long serialVersionUID = -2993918177017878243L;
	
	protected static final Logger log = LoggerFactory.getLogger(ModelImplJena.class);
	
	protected com.hp.hpl.jena.rdf.model.Model jenaModel;
	
	/**
	 * used to check whether iterators work on the up-to-date model
	 */
	protected long modificationCount = 0;
	
	protected Reasoning reasoning;
	
	private final URI contextURI;
	
	private boolean locked;
	
	/**
	 * @param contextURI the first part of the quad, never null
	 * @param reasoning never null
	 */
	public ModelImplJena(URI contextURI, Reasoning reasoning) {
		this.contextURI = contextURI;
		this.reasoning = reasoning;
		this.jenaModel = ModelFactory.createDefaultModel();
		applyReasoning(this.reasoning);
		org.openjena.riot.RIOT.init(); //wires RIOT readers/writers into Jena
	}
	
	/**
	 * wraps a Jena Model in a rdf2go Model
	 * 
	 * @param jenaModel to be wrapped into an RDF2Go Model
	 */
	public ModelImplJena(URI contextURI, com.hp.hpl.jena.rdf.model.Model jenaModel) {
		this(contextURI, jenaModel, Reasoning.none);
	}
	
	public ModelImplJena(com.hp.hpl.jena.rdf.model.Model jenaModel) {
		this(null, jenaModel, Reasoning.none);
	}
	
	public ModelImplJena(URI contextURI, com.hp.hpl.jena.rdf.model.Model jenaModel,
	        Reasoning reasoning) {
		this.contextURI = contextURI;
		this.reasoning = reasoning;
		// re-use
		this.jenaModel = jenaModel;
		applyReasoning(reasoning);
	}
	
	public ModelImplJena(Reasoning reasoning) {
		this(null, reasoning);
	}
	
	@Override
	public void addAll(Iterator<? extends Statement> other) throws ModelRuntimeException {
		assertModel();
		if(other instanceof ModelImplJena) {
			com.hp.hpl.jena.rdf.model.Model otherJenaModel = (com.hp.hpl.jena.rdf.model.Model)((ModelImplJena)other)
			        .getUnderlyingModelImplementation();
			this.jenaModel.add(otherJenaModel);
		} else
			super.addAll(other);
	}
	
	void applyReasoning(Reasoning r) {
		switch(r) {
		case rdfs:
			this.jenaModel = ModelFactory.createRDFSModel(this.jenaModel);
			break;
		case owl:
			this.jenaModel = ModelFactory.createInfModel(ReasonerRegistry.getOWLReasoner(),
			        this.jenaModel);
			break;
		default:
			break;
		}
	}
	
	@Override
	public BlankNode createBlankNode() {
		// this.modificationCount++;
		// should be unique across models
		
		return new JenaBlankNode(com.hp.hpl.jena.graph.Node.createAnon());
	}
	
	@Override
	public BlankNode createBlankNode(String id) {
		// this.modificationCount++;
		// should be unique across models
		AnonId anonid = AnonId.create(id);
		return new JenaBlankNode(com.hp.hpl.jena.graph.Node.createAnon(anonid));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#addStatement(java.lang.Object,
	 * java.net.URI, java.lang.Object)
	 */
	@Override
	public void addStatement(org.ontoware.rdf2go.model.node.Resource subject, URI predicate,
	        org.ontoware.rdf2go.model.node.Node object) throws ModelRuntimeException {
		assertModel();
		try {
			log.debug("adding a statement (" + subject + "," + predicate + "," + object + ")");
			this.modificationCount++;
			if(!(object instanceof DatatypeLiteral)) {
				this.jenaModel.getGraph().add(
				        new Triple(TypeConversion.toJenaNode(subject, this.jenaModel),
				                TypeConversion.toJenaNode(predicate, this.jenaModel),
				                TypeConversion.toJenaNode(object, this.jenaModel)));
			} else
			// DatatypeLiteral
			{
				// build Resources/Literals
				Resource s = null;
				if(subject instanceof URI) {
					s = this.jenaModel.createResource(subject.toString());
				} else
				// subject is a BlankNode
				{
					s = this.jenaModel.createResource(((Node)((AbstractBlankNodeImpl)subject)
					        .getUnderlyingBlankNode()).getBlankNodeId());
				}
				
				Property p = this.jenaModel.createProperty(predicate.toString());
				
				String datatypeValue = ((DatatypeLiteral)object).getValue();
				String datatypeURI = ((DatatypeLiteral)object).getDatatype().toString();
				Literal o = this.jenaModel.createTypedLiteral(datatypeValue, datatypeURI);
				
				// Add the statement to the model
				this.jenaModel.add(s, p, o);
			}
		} catch(BadURIException e) {
			throw new ModelRuntimeException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#removeStatement(java.lang.Object,
	 * java.net.URI, java.lang.Object)
	 */
	@Override
	public void removeStatement(org.ontoware.rdf2go.model.node.Resource subject, URI predicate,
	        org.ontoware.rdf2go.model.node.Node object) throws ModelRuntimeException {
		assertModel();
		
		log.debug("removing a statement (" + subject + "," + predicate + "," + object + ")");
		this.modificationCount++;
		this.jenaModel.getGraph().delete(
		        new Triple(
		        
		        TypeConversion.toJenaNode(subject, this.jenaModel), TypeConversion.toJenaNode(
		                predicate, this.jenaModel), TypeConversion.toJenaNode(object,
		                this.jenaModel)));
	}
	
	@Override
	public QueryResultTable sparqlSelect(String queryString) throws ModelRuntimeException {
		assertModel();
		log.debug("Query " + queryString);
		Query query = QueryFactory.create(queryString);
		return new QueryResultTableImpl(query, this.jenaModel);
	}
	
	@Override
	public ClosableIterable<Statement> sparqlConstruct(String queryString)
	        throws ModelRuntimeException {
		assertModel();
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, this.jenaModel);
		
		if(query.isConstructType()) {
			com.hp.hpl.jena.rdf.model.Model m = qexec.execConstruct();
			Model resultModel = new ModelImplJena(null, m, Reasoning.none);
			resultModel.open();
			return resultModel;
		} else {
			throw new RuntimeException("Cannot handle this type of queries! Please use CONSTRUCT.");
		}
	}
	
	@Override
	public boolean sparqlAsk(String queryString) throws ModelRuntimeException {
		assertModel();
		log.debug("Query " + queryString);
		Query query = QueryFactory.create(queryString);
		
		if(!query.isAskType()) {
			throw new ModelRuntimeException("The given query is not an ASK query");
		}
		// else
		QueryExecution qexec = QueryExecutionFactory.create(query, this.jenaModel);
		return qexec.execAsk();
	}
	
	/**
	 * @return opened result Model
	 */
	@Override
	public ClosableIterable<Statement> sparqlDescribe(String queryString)
	        throws ModelRuntimeException {
		assertModel();
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, this.jenaModel);
		
		if(query.isDescribeType()) {
			com.hp.hpl.jena.rdf.model.Model m = qexec.execDescribe();
			Model resultModel = new ModelImplJena(null, m, Reasoning.none);
			resultModel.open();
			return resultModel;
		} else {
			throw new RuntimeException("Cannot handle this type of queries! Please use DESCRIBE.");
		}
		
	}
	
	/**
	 * handle with care, iterators based on this model might (silently!) throw
	 * concurrent modification exceptions
	 * 
	 * @return the underlying jena model
	 */
	public com.hp.hpl.jena.rdf.model.Model getInternalJenaModel() {
		assertModel();
		return this.jenaModel;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#size()
	 */
	@Override
	public long size() throws ModelRuntimeException {
		assertModel();
		return (int)this.jenaModel.size();
	}
	
	/**
	 * @return count of modifications, used to prevent iterators from accessing
	 *         old modle state
	 */
	public long getModificationCount() {
		return this.modificationCount;
	}
	
	@Override
	public Object getUnderlyingModelImplementation() {
		return this.jenaModel;
	}
	
	public void setUnderlyingModelImplementation(Object o) {
		assert o instanceof com.hp.hpl.jena.rdf.model.Model;
		this.jenaModel = (com.hp.hpl.jena.rdf.model.Model)o;
	}
	
	@Override
	public ClosableIterator<Statement> iterator() {
		assertModel();
		return new TripleIterator(this.jenaModel.getGraph().find(Node.ANY, Node.ANY, Node.ANY),
		        this.modificationCount, this);
	}
	
	@Override
	public URI getContextURI() {
		return this.contextURI;
	}
	
	@Override
	public void lock() throws LockException {
		this.locked = true;
		this.jenaModel.enterCriticalSection(true);
		
	}
	
	@Override
	public boolean isLocked() {
		return this.locked;
	}
	
	@Override
	public void unlock() {
		assertModel();
		if(this.isLocked()) {
			this.jenaModel.leaveCriticalSection();
			this.locked = false;
		}
	}
	
	@Override
	public synchronized void update(DiffReader diff) throws ModelRuntimeException {
		assertModel();
		lock();
		removeAll(diff.getRemoved().iterator());
		addAll(diff.getAdded().iterator());
		unlock();
	}
	
	@Override
	public ClosableIterator<Statement> findStatements(ResourceOrVariable subject,
	        UriOrVariable predicate, NodeOrVariable object) throws ModelRuntimeException {
		assertModel();
		
		return new TripleIterator(this.jenaModel.getGraph().find(
		        TypeConversion.toJenaNode(subject), TypeConversion.toJenaNode(predicate),
		        TypeConversion.toJenaNode(object)), this.modificationCount, this);
	}
	
	@Override
	public void readFrom(Reader r) {
		assertModel();
		this.jenaModel.read(r, "", "RDF/XML");
	}
	
	/**
	 * Might need this code fragment:
	 * 
	 * FileOutputStream fos = new FileOutputStream(tmpFile); OutputStreamWriter
	 * osw = new OutputStreamWriter(fos, "utf8"); synchronized
	 * (getDelegatedModel()) {
	 * DumpUtils.addCommonPrefixesToJenaModel(jenaModel); jenaModel.write(osw,
	 * lang, ""); } osw.close();
	 * 
	 */
	@Override
	public void readFrom(Reader reader, Syntax syntax) {
		assertModel();
		if(syntax == Syntax.RdfXml) {
			readFrom(reader);
		} else if(syntax == Syntax.Ntriples) {
			this.jenaModel.read(reader, "", "N-TRIPLE");
		} else if(syntax == Syntax.Turtle) {
			this.jenaModel.read(reader, "", "N3");
		} else if(syntax == Syntax.Trix) {
			throw new IllegalArgumentException("Not implemented in Jena");
		}
	}
	
	@Override
	public void readFrom(Reader reader, Syntax syntax, String baseURI) {
		assertModel();
		if(syntax == Syntax.RdfXml) {
			readFrom(reader);
		} else if(syntax == Syntax.Ntriples) {
			this.jenaModel.read(reader, baseURI, "N-TRIPLE");
		} else if(syntax == Syntax.Turtle) {
			this.jenaModel.read(reader, baseURI, "N3");
		} else if(syntax == Syntax.Trix) {
			throw new IllegalArgumentException("Not implemented in Jena");
		}
	}
	
	private static void registerNamespaces(com.hp.hpl.jena.rdf.model.Model jenaModel) {
		// beautify output
		jenaModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		jenaModel.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		jenaModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		jenaModel.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
	}
	
	// TODO: check valid XML output
	@Override
	public void writeTo(Writer w) {
		writeTo(w, Syntax.RdfXml);
	}
	
	@Override
	public void writeTo(Writer writer, Syntax syntax) {
		assertModel();
		registerNamespaces(this.jenaModel);
		org.openjena.riot.RIOT.init(); //wires RIOT readers/writers into Jena
		
		if(syntax == Syntax.RdfXml) {
			this.jenaModel.write(writer, "RDF/XML", "");
		} else if(syntax == Syntax.Ntriples) {
			this.jenaModel.write(writer, "N-TRIPLE", "");
		} else if(syntax == Syntax.Turtle) {
			if(this.jenaModel.size() < 1000) {
				log.debug("Model is small enough for pretty-print.");
				this.jenaModel.write(writer, "N3-PP", "");
			} else {
				this.jenaModel.write(writer, "N3", "");
			}
		} else if(syntax == Syntax.RdfJson) {
			this.jenaModel.write(writer, "RDF/JSON", "");
		} else {
			throw new IllegalArgumentException(syntax + " is not implemented in Jena");
		}
	}
	
	@Override
	public void dump() {
		assertModel();
		this.jenaModel.write(System.out, "N3-PP", "");
	}
	
	@Override
	public void readFrom(InputStream in) throws IOException, ModelRuntimeException {
		assertModel();
		this.jenaModel.read(in, "", "RDF/XML");
	}
	
	@Override
	public void writeTo(OutputStream out) throws IOException, ModelRuntimeException {
		assertModel();
		writeTo(out, Syntax.RdfXml);
	}
	
	/**
	 * Throws an exception if the syntax is not SPARQL
	 * 
	 * @throws IOException from underlying {@link OutputStream}
	 * @throws ModelRuntimeException for errors using the model
	 */
	@Override
	public void writeTo(OutputStream out, Syntax syntax) throws ModelRuntimeException, IOException {
		assertModel();
		if(syntax == Syntax.RdfXml) {
			this.jenaModel.write(out, "RDF/XML", "");
		} else if(syntax == Syntax.Ntriples) {
			this.jenaModel.write(out, "N-TRIPLE", "");
		} else if(syntax == Syntax.Turtle) {
			if(this.jenaModel.size() < 1000) {
				log.debug("Model is small enough for pretty-print.");
				this.jenaModel.write(out, "N3-PP", "");
			} else {
				this.jenaModel.write(out, "N3", "");
			}
		} else {
			throw new SyntaxNotSupportedException(syntax + " is not implemented in Jena");
		}
	}
	
	private static String getJenaSyntaxName(Syntax syntax) {
		if(syntax == Syntax.Ntriples)
			return "N-TRIPLE";
		if(syntax == Syntax.Turtle)
			return "N3";
		else if(syntax == Syntax.RdfXml)
			return "RDF/XML";
		else
			return null;
	}
	
	@Override
	public void readFrom(InputStream in, Syntax syntax) throws IOException, ModelRuntimeException {
		assertModel();
		assert in != null;
		String jenaSyntax = getJenaSyntaxName(syntax);
		if(jenaSyntax == null)
			throw new SyntaxNotSupportedException("Could not process syntax named <"
			        + syntax.getName() + "> directly, maybe the underlying Jena can...");
		
		this.jenaModel.read(in, "", jenaSyntax);
	}
	
	@Override
	public void readFrom(InputStream in, Syntax syntax, String baseURI) throws IOException,
	        ModelRuntimeException {
		assertModel();
		assert in != null;
		String jenaSyntax = getJenaSyntaxName(syntax);
		if(jenaSyntax == null)
			throw new SyntaxNotSupportedException("Could not process syntax named <"
			        + syntax.getName() + "> directly, maybe the underlying Jena can...");
		
		this.jenaModel.read(in, baseURI, jenaSyntax);
	}
	
	@Override
	public boolean isIsomorphicWith(Model other) {
		if(other instanceof ModelImplJena) {
			return this.jenaModel.isIsomorphicWith(((ModelImplJena)other).getInternalJenaModel());
		} else {
			// TODO: reasoning might be different
			ModelImplJena otherJenaModel = new ModelImplJena(Reasoning.none);
			otherJenaModel.addAll(other.iterator());
			return this.jenaModel.isIsomorphicWith(otherJenaModel.getInternalJenaModel());
		}
	}
	
	@Override
	public boolean isValidURI(String uriString) {
	    try {
	    	java.net.URI u = new java.net.URI(uriString);
	    	if (!u.isAbsolute()) {
	    		throw new URISyntaxException(uriString, "URI is not absolute");
	    	}
	        return true;
	    } catch (URISyntaxException e) {
	        log.debug("Only well-formed absolute URIrefs can be included in RDF/XML output: <"
                    + uriString + "> " + e.getMessage());
	        return false;
	    }
	}
	
	@Override
	public String getNamespace(String prefix) {
		return this.jenaModel.getNsPrefixURI(prefix);
	}
	
	@Override
	public Map<String,String> getNamespaces() {
		return this.jenaModel.getNsPrefixMap();
	}
	
	@Override
	public void removeNamespace(String prefix) {
		this.jenaModel.removeNsPrefix(prefix);
	}
	
	@Override
	public void setNamespace(String prefix, String namespaceURI) throws IllegalArgumentException {
		this.jenaModel.setNsPrefix(prefix, namespaceURI);
	}
	
	@Override
	public QueryResultTable querySelect(String queryString, String querylanguage)
	        throws QueryLanguageNotSupportedException, ModelRuntimeException {
		assertModel();
		if(log.isDebugEnabled()) {
			log.debug("Query " + queryString);
		}
		com.hp.hpl.jena.query.Syntax syntax = com.hp.hpl.jena.query.Syntax.lookup(querylanguage);
		if(syntax == null) {
			// delegate to super
			return super.querySelect(queryString, querylanguage);
		}
		Query query = QueryFactory.create(queryString, syntax);
		return new QueryResultTableImpl(query, this.jenaModel);
	}
	
	/**
	 * Throws an exception if the syntax is not SPARQL
	 */
	@Override
	public ClosableIterable<Statement> queryConstruct(String queryString, String querylanguage)
	        throws QueryLanguageNotSupportedException, ModelRuntimeException {
		assertModel();
		com.hp.hpl.jena.query.Syntax syntax = com.hp.hpl.jena.query.Syntax.lookup(querylanguage);
		if(syntax == null) {
			// delegate to super
			return super.queryConstruct(queryString, querylanguage);
		}
		Query query = QueryFactory.create(queryString, syntax);
		
		QueryExecution qexec = QueryExecutionFactory.create(query, this.jenaModel);
		
		if(query.isConstructType()) {
			com.hp.hpl.jena.rdf.model.Model m = qexec.execConstruct();
			Model resultModel = new ModelImplJena(null, m, Reasoning.none);
			resultModel.open();
			return resultModel;
		} else {
			throw new RuntimeException("Cannot handle this type of queries! Please use CONSTRUCT.");
		}
	}
	
	@Override
	public boolean isEmpty() {
		return this.jenaModel.isEmpty();
	}

	@Override
	public void removeAll() throws ModelRuntimeException {
		this.jenaModel.removeAll();
	}

	@Override
	public void addModel(Model model) {
		if (model.getUnderlyingModelImplementation() instanceof com.hp.hpl.jena.rdf.model.Model) {
			com.hp.hpl.jena.rdf.model.Model otherJenaModel = (com.hp.hpl.jena.rdf.model.Model) model
					.getUnderlyingModelImplementation();
			this.jenaModel.add(otherJenaModel);
		} else {
			super.addModel(model);
		}
	}
}
