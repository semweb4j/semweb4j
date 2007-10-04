package org.ontoware.rdf2go.impl.autopersist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.impl.DelegatingModel;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.persistent.PersistentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes down the RDF model all n uncommited writes.
 * 
 * @author voelkel
 * 
 */
public class ModelImplAutoPersist extends DelegatingModel implements
		PersistentModel {

	private File modelfile;

	private int openChanges = 0;

	private int allowedOpenChanges;

	public static final Syntax DEFAULT_SERIALISATION_LANGUAGE = Syntax.Turtle;

	private static Logger log = LoggerFactory.getLogger(ModelImplAutoPersist.class);

	/**
	 * 
	 * @param model
	 *            a Model
	 * @param openChanges
	 *            number of allowed uncommited writes. Setting this to 0 turns
	 *            the model to a write-through model.
	 * @throws IOException
	 * @throws ModelImplException
	 */
	public ModelImplAutoPersist(Model model, File modelfile, int openChanges)
			throws ModelRuntimeException, IOException {
		super();

		// encodign!

		if (modelfile == null)
			throw new IllegalArgumentException("A modelfile must be specified");
		this.modelfile = modelfile;
		if (!model.isOpen()) {
			model.open();
		}
		super.setDelegatedModel(model);
		this.allowedOpenChanges = openChanges;
		load();
	}

	@Override
	public BlankNode createBlankNode() {
		BlankNode bnode = super.createBlankNode();
		checkChanges();
		return bnode;
	}

	public int openchanges() {
		return this.openChanges;
	}

	public void addAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		while (other.hasNext()) {
			super.addStatement(other.next());
			checkChanges();
		}
	}

	public void addStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		super.addStatement(subject, predicate, object);
		checkChanges();
	}

	public void addStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		super.addStatement(subjectURIString, predicate, literal);
		checkChanges();
	}

	public void removeStatement(Resource subject, URI predicate, Node object)
			throws ModelRuntimeException {
		super.removeStatement(subject, predicate, object);
		checkChanges();
	}

	public ClosableIterator<? extends Statement> findStatements(
			ResourceOrVariable subject, UriOrVariable predicate,
			NodeOrVariable object) throws ModelRuntimeException {
		return super.findStatements(subject, predicate, object);
	}

	public QueryResultTable sparqlSelect(String query)
			throws ModelRuntimeException {
		log.debug("delegating to a " + super.getClass());

		log.debug("model size: " + super.size());

		return super.sparqlSelect(query);
	}

	public ClosableIterable<? extends Statement> sparqlConstruct(String query)
			throws ModelRuntimeException {
		return super.sparqlConstruct(query);
	}

	public Model newInstance() throws ModelRuntimeException {
		throw new UnsupportedOperationException(
				"cannot instantiate two RDF2Go models on the same file");
	}

	public Object getUnderlyingModelImplementation() {
		return super.getUnderlyingModelImplementation();
	}

	public void setUnderlyingModelImplementation(Object o) {
		super.setUnderlyingModelImplementation(o);
	}

	private void checkChanges() {
		openChanges++;
		if (openChanges > allowedOpenChanges) {
			try {
				log.info("Reached limit of " + allowedOpenChanges
						+ " open changes. Writing model to "
						+ modelfile.getCanonicalPath());
				save(modelfile, DEFAULT_SERIALISATION_LANGUAGE);
				openChanges = 0;
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (ModelRuntimeException e) {
				throw new ModelRuntimeException(e);
			}

		}
	}

	public void save() throws ModelRuntimeException {
		try {
			save(modelfile, DEFAULT_SERIALISATION_LANGUAGE);
		} catch (IOException e) {
			throw new ModelRuntimeException(e);
		}
	}

	public void load() throws ModelRuntimeException {
		assert modelfile != null;

		if (!modelfile.exists()) {
			File tmpFile = getTmpFile(modelfile);
			if (tmpFile.exists()) {
				tmpFile.renameTo(modelfile);
				log.warn("Restored data from temp file.");
			}
		}

		if (modelfile.exists())
			try {
				FileReader fr = new FileReader(modelfile);
				assert fr != null;
				try {
					readFrom(fr, DEFAULT_SERIALISATION_LANGUAGE);
				} catch (IOException e) {
					log.error("exception might be an XML problem",e);
					// exception might be an XML problem
					throw new RuntimeException(e);
				} finally {
					try {
						fr.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
	}

	private static File getTmpFile(File f) {
		return new File(f + ".tmp");
	}

	private static File getOldFile(File f) {
		return new File(f + ".old");
	}

	public void save(File persistentFile, Syntax syntax) throws IOException,
			ModelRuntimeException {

		// TODO use Jakarta IOUtils and macke a backup copy

		// file => old
		File oldFile = getOldFile(persistentFile);
		if (persistentFile.exists()) {
			if (oldFile.exists())
				oldFile.delete();
			assert !oldFile.exists();

			boolean renamed = persistentFile.renameTo(oldFile);
			assert renamed;
			assert !persistentFile.exists();
			assert oldFile.exists();
		}

		// ...RDF... => tmp
		File tmpFile = getTmpFile(persistentFile);
		// make dirs
		tmpFile.getParentFile().mkdirs();
		if (tmpFile.exists())
			tmpFile.delete();

		log.debug("writing");
		FileWriter fw = new FileWriter(tmpFile);
		super.getDelegatedModel().writeTo(fw, syntax);
		fw.close();

		// tmp => file
		assert tmpFile.exists();
		// FIXME if the target file exists this might fail
		persistentFile.delete();

		boolean renamed = tmpFile.renameTo(persistentFile);
		assert renamed;
		assert !tmpFile.exists();
		assert persistentFile.exists();

		// del old
		if (oldFile.exists())
			oldFile.delete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	public void addStatement(Resource subject, URI predicate, String literal,
			String languageTag) throws ModelRuntimeException {
		super.addStatement(subject, predicate, literal, languageTag);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	public void addStatement(Resource subject, URI predicate, String literal,
			URI datatypeURI) throws ModelRuntimeException {
		super.addStatement(subject, predicate, literal, datatypeURI);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	public void addStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		super.addStatement(subject, predicate, literal);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelWriter#addStatement(S)
	 */
	public void addStatement(Statement statement) throws ModelRuntimeException {
		super.addStatement(statement);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	public void addStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		super.addStatement(subjectURIString, predicate, literal, languageTag);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelWriter#addStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	public void addStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		super.addStatement(subjectURIString, predicate, literal, datatypeURI);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#newRandomUniqueURI()
	 */
	public URI newRandomUniqueURI() {
		URI u = super.newRandomUniqueURI();
		checkChanges();
		return u;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelAddRemove#removeAll()
	 */
	public void removeAll() throws ModelRuntimeException {
		super.removeAll();
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelAddRemove#removeAll(org.ontoware.rdf2go.core.common.CommonModelReader)
	 */
	public void removeAll(Iterator<? extends Statement> other)
			throws ModelRuntimeException {
		super.removeAll(other);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModelAddRemove#removeStatement(S)
	 */
	public void removeStatement(Statement statement)
			throws ModelRuntimeException {
		super.removeStatement(statement);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.common.CommonModel#setProperty(org.ontoware.rdf2go.core.node.URI,
	 *      java.lang.Object)
	 */
	public void setProperty(URI propertyURI, Object value) {
		super.setProperty(propertyURI, value);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	public void removeStatement(Resource subject, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		super.removeStatement(subject, predicate, literal, languageTag);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	public void removeStatement(Resource subject, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		super.removeStatement(subject, predicate, literal, datatypeURI);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(org.ontoware.rdf2go.core.node.Resource,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	public void removeStatement(Resource subject, URI predicate, String literal)
			throws ModelRuntimeException {
		super.removeStatement(subject, predicate, literal);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      java.lang.String)
	 */
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, String languageTag) throws ModelRuntimeException {
		super
				.removeStatement(subjectURIString, predicate, literal,
						languageTag);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI)
	 */
	public void removeStatement(String subjectURIString, URI predicate,
			String literal, URI datatypeURI) throws ModelRuntimeException {
		super
				.removeStatement(subjectURIString, predicate, literal,
						datatypeURI);
		checkChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ontoware.rdf2go.core.triple.ModelAddRemove#removeStatement(java.lang.String,
	 *      org.ontoware.rdf2go.core.node.URI, java.lang.String)
	 */
	public void removeStatement(String subjectURIString, URI predicate,
			String literal) throws ModelRuntimeException {
		super.removeStatement(subjectURIString, predicate, literal);
		checkChanges();
	}

	public void update(Diff diff) throws ModelRuntimeException {
		super.update(diff);
		checkChanges();
	}

	public void removeStatements(ResourceOrVariable subject,
			UriOrVariable predicate, NodeOrVariable object)
			throws ModelRuntimeException {
		super.removeStatements(subject, predicate, object);
		checkChanges();
	}

}
