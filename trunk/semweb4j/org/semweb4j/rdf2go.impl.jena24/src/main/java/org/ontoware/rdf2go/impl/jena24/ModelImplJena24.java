package org.ontoware.rdf2go.impl.jena24;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.LockException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.SyntaxNotSupportedException;
import org.ontoware.rdf2go.model.Diff;
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
import org.ontoware.rdf2go.model.node.impl.BlankNodeImpl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.iri.Violation;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
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
public class ModelImplJena24 extends AbstractModel implements Model {
	protected static final Log log = LogFactory.getLog(ModelImplJena24.class);

	protected com.hp.hpl.jena.rdf.model.Model jenaModel;

	/**
	 * used to check wheter iterators work on the up-to-date model
	 */
	protected long modificationCount = 0;

	protected Reasoning reasoning;

	private URI contextURI;

	private boolean locked;

	/**
	 * @param reasoning
	 */
	public ModelImplJena24(URI contextURI, Reasoning r) {
		this.contextURI = contextURI;
		this.reasoning = r;
		jenaModel = ModelFactory.createDefaultModel();
		applyReasoning(reasoning);
	}

	/**
	 * wraps a Jena Model in a rdf2go Model
	 * 
	 * @param jenaModel
	 */
	public ModelImplJena24(URI contextURI,
			com.hp.hpl.jena.rdf.model.Model jenaModel) {
		this(contextURI, jenaModel, Reasoning.none);
	}

	public ModelImplJena24(com.hp.hpl.jena.rdf.model.Model jenaModel) {
		this(null, jenaModel, Reasoning.none);
	}

	public ModelImplJena24(URI contextURI,
			com.hp.hpl.jena.rdf.model.Model jenaModel, Reasoning reasoning) {
		this.contextURI = contextURI;
		this.reasoning = reasoning;
		// re-use
		this.jenaModel = jenaModel;
		applyReasoning(reasoning);
	}

	public ModelImplJena24(Reasoning reasoning) {
		this(null, reasoning);
	}

	@Override
	public void addAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		assertModel();
		if (other instanceof ModelImplJena24) {
			com.hp.hpl.jena.rdf.model.Model otherJenaModel = (com.hp.hpl.jena.rdf.model.Model) ((ModelImplJena24) other)
					.getUnderlyingModelImplementation();
			this.jenaModel.add(otherJenaModel);
		} else
			super.addAll(other);
	}

	void applyReasoning(Reasoning r) {
		switch (r) {
		case rdfs:
			jenaModel = ModelFactory.createRDFSModel(jenaModel);
			break;
		case owl:
			jenaModel = ModelFactory.createInfModel(ReasonerRegistry
					.getOWLReasoner(), jenaModel);
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#getNewBlankNode()
	 */
	@Override
	public BlankNode createBlankNode() {
		// this.modificationCount++;
		// should be unique across models

		return new BlankNodeImpl(com.hp.hpl.jena.graph.Node.createAnon());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#addStatement(java.lang.Object,
	 *      java.net.URI, java.lang.Object)
	 */
	public void addStatement(org.ontoware.rdf2go.model.node.Resource subject,
			URI predicate, org.ontoware.rdf2go.model.node.Node object)
			throws ModelRuntimeException {
		assertModel();
		try {
			if (subject instanceof URI)
				checkStrictSyntax((URI) subject);

			if (predicate instanceof URI)
				checkStrictSyntax((URI) predicate);

			if (object instanceof URI)
				checkStrictSyntax((URI) object);

			log.debug("adding a statement (" + subject + "," + predicate + ","
					+ object + ")");
			this.modificationCount++;
			if (!(object instanceof DatatypeLiteral)) {
				this.jenaModel.getGraph().add(
						new Triple(TypeConversion
								.toJenaNode(subject, jenaModel), TypeConversion
								.toJenaNode(predicate, jenaModel),
								TypeConversion.toJenaNode(object, jenaModel)));
			} else
			// DatatypeLiteral
			{
				// build Resources/Literals
				Resource s = null;
				if (subject instanceof URI) {
					s = this.jenaModel.createResource(subject.toString());
				} else
				// subject is a BlankNode
				{
					s = this.jenaModel
							.createResource(((Node) ((BlankNodeImpl) subject)
									.getUnderlyingBlankNode()).getBlankNodeId());
				}

				Property p = this.jenaModel
						.createProperty(predicate.toString());

				String datatypeValue = ((DatatypeLiteral) object).getValue();
				String datatypeURI = ((DatatypeLiteral) object).getDatatype()
						.toString();
				Literal o = this.jenaModel.createTypedLiteral(datatypeValue,
						datatypeURI);

				// Add the statement to the model
				this.jenaModel.add(s, p, o);
			}
		} catch (BadURIException e) {
			throw new ModelRuntimeException(e);
		}
	}

	static IRIFactory factory = IRIFactory.jenaImplementation();

	private void checkStrictSyntax(URI uri) throws BadURIException {
		IRI iri = factory.create("" + uri);
		if (iri.hasViolation(false))
			throw new BadURIException(
					"Only well-formed absolute URIrefs can be included in RDF/XML output: <"
							+ uri
							+ "> "
							+ ((Violation) iri.violations(false).next())
									.getLongMessage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.Model#removeStatement(java.lang.Object,
	 *      java.net.URI, java.lang.Object)
	 */
	public void removeStatement(
			org.ontoware.rdf2go.model.node.Resource subject, URI predicate,
			org.ontoware.rdf2go.model.node.Node object)
			throws ModelRuntimeException {
		assertModel();

		log.debug("removing a statement (" + subject + "," + predicate + ","
				+ object + ")");
		this.modificationCount++;
		this.jenaModel.getGraph().delete(
				new Triple(

				TypeConversion.toJenaNode(subject, jenaModel), TypeConversion
						.toJenaNode(predicate, jenaModel), TypeConversion
						.toJenaNode(object, jenaModel)));
	}

	public QueryResultTable sparqlSelect(String queryString)
			throws ModelRuntimeException {
		assertModel();
		log.info("Query " + queryString);
		Query query = QueryFactory.create(queryString);
		return new QueryResultTableImpl(query, jenaModel);
	}

	public Model sparqlConstruct(String queryString)
			throws ModelRuntimeException {
		assertModel();
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, jenaModel);

		if (query.isConstructType()) {
			com.hp.hpl.jena.rdf.model.Model m = qexec.execConstruct();
			Model resultModel = new ModelImplJena24(null, m, Reasoning.none);
			resultModel.open();
			return resultModel;
		} else {
			throw new RuntimeException(
					"Cannot handle this type of queries! Please use CONSTRUCT.");
		}
	}

	public boolean sparqlAsk(String queryString) throws ModelRuntimeException {
		assertModel();
		log.debug("Query " + queryString);
		Query query = QueryFactory.create(queryString);

		if (!query.isAskType()) {
			throw new ModelRuntimeException(
					"The given query is not an ASK query");
		}
		// else
		QueryExecution qexec = QueryExecutionFactory.create(query, jenaModel);
		return qexec.execAsk();
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
		return (int) this.jenaModel.size();
	}

	/**
	 * @return count of modifications, used to prevent iterators from accessing
	 *         old modle state
	 */
	public long getModificationCount() {
		return this.modificationCount;
	}

	public Object getUnderlyingModelImplementation() {
		return jenaModel;
	}

	public void setUnderlyingModelImplementation(Object o) {
		assert o instanceof com.hp.hpl.jena.rdf.model.Model;
		jenaModel = (com.hp.hpl.jena.rdf.model.Model) o;
	}

	public ClosableIterator<Statement> iterator() {
		assertModel();
		return new TripleIterator(jenaModel.getGraph().find(Node.ANY, Node.ANY,
				Node.ANY), modificationCount, this);
	}

	public URI getContextURI() {
		return this.contextURI;
	}

	public void lock() throws LockException {
		locked = true;
		jenaModel.enterCriticalSection(true);

	}

	public boolean isLocked() {
		return locked;
	}

	public void unlock() {
		assertModel();
		jenaModel.leaveCriticalSection();
	}

	public void update(Diff diff) throws ModelRuntimeException {
		assertModel();
		lock();
		addAll(diff.getAdded().iterator());
		removeAll(diff.getRemoved().iterator());
		unlock();
	}

	public ClosableIterator<? extends Statement> findStatements(
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		assertModel();

		return new TripleIterator(jenaModel.getGraph().find(
				TypeConversion.toJenaNode(subject),
				TypeConversion.toJenaNode(predicate),
				TypeConversion.toJenaNode(object)), modificationCount, this);
	}

	/**
	 * @return opened result Model
	 */
	public ClosableIterable<Statement> sparqlDescribe(String queryString)
			throws ModelRuntimeException {
		assertModel();
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, jenaModel);

		if (query.isDescribeType()) {
			com.hp.hpl.jena.rdf.model.Model m = qexec.execDescribe();
			Model resultModel = new ModelImplJena24(null, m, Reasoning.none);
			resultModel.open();
			return resultModel;
		} else {
			throw new RuntimeException(
					"Cannot handle this type of queries! Please use DESCRIBE.");
		}

	}

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
	public void readFrom(Reader reader, Syntax syntax) {
		assertModel();
		if (syntax == Syntax.RdfXml) {
			readFrom(reader);
		} else if (syntax == Syntax.Ntriples) {
			this.jenaModel.read(reader, "", "N-TRIPLE");
		} else if (syntax == Syntax.Turtle) {
			this.jenaModel.read(reader, "", "N3");
		} else if (syntax == Syntax.Trix) {
			throw new IllegalArgumentException("Not implemented in Jena 2.4");
		}
	};

	// TODO: check valid XML output
	public void writeTo(Writer w) {
		assertModel();
		this.jenaModel.write(w, "RDF/XML", "");
	}

	public void writeTo(Writer writer, Syntax syntax) {
		assertModel();

		// beautify output
		jenaModel.setNsPrefix("rdf", "http://www.w3.org/2000/01/rdf-schema#");
		jenaModel.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		jenaModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		jenaModel.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");

		if (syntax == Syntax.RdfXml) {
			writeTo(writer);
		} else if (syntax == Syntax.Ntriples) {
			this.jenaModel.write(writer, "N-TRIPLE", "");
		} else if (syntax == Syntax.Turtle) {
			if (this.jenaModel.size() < 1000) {
				log.debug("Model is small enough for pretty-print.");
				this.jenaModel.write(writer, "N3-PP", "");
			} else {
				this.jenaModel.write(writer, "N3", "");
			}
		} else if (syntax == Syntax.Trix) {
			throw new IllegalArgumentException(
					"TRIX is not implemented in Jena 2.4");
		}
	}

	public void dump() {
		assertModel();
		jenaModel.write(System.out, "N3-PP", "");
	}

	public void readFrom(InputStream in) throws IOException,
			ModelRuntimeException {
		assertModel();
		this.jenaModel.read(in, "", "RDF/XML");
	}

	public void writeTo(OutputStream out) throws IOException,
			ModelRuntimeException {
		assertModel();
		jenaModel.write(out, "RDF/XML", "");
	}

	private String getJenaSyntaxName(Syntax syntax) {
		if (syntax == Syntax.Ntriples)
			return "N-TRIPLE";
		if (syntax == Syntax.Turtle)
			return "N3";
		else if (syntax == Syntax.RdfXml)
			return "RDF/XML";
		else
			return null;
	}

	public void readFrom(InputStream in, Syntax syntax) throws IOException,
			ModelRuntimeException {
		assertModel();
		assert in != null;
		String jenaSyntax = getJenaSyntaxName(syntax);
		if (jenaSyntax == null)
			throw new SyntaxNotSupportedException(
					"Could not process syntax named <" + syntax.getName()
							+ "> directly, maybe the underlying Jena can...");

		this.jenaModel.read(in, "", jenaSyntax);
	}

	public boolean isIsomorphicWith(Model other) {
		if (other instanceof ModelImplJena24) {
			return this.jenaModel.isIsomorphicWith( ((ModelImplJena24) other).getInternalJenaModel());
		}
		else {
			// TODO: reasoning might be different
			ModelImplJena24 otherJenaModel = new ModelImplJena24(Reasoning.none);
			otherJenaModel.addAll(other.iterator());
			return this.jenaModel.isIsomorphicWith(otherJenaModel.getInternalJenaModel());
		}
	}

	public boolean isValidURI(String uriString) {
		// TODO Auto-generated method stub
		return false;
	}
}
