/**
 * generated by RDFReactor on 9:59 on 29.2005
 */
package org.ontoware.semversion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.runtime.Bridge;
import org.ontoware.rdfreactor.runtime.RDFDataException;
import org.ontoware.semversion.impl.BlankNodeEnrichmentModel;
import org.ontoware.semversion.impl.SyntacticDiffEngine;
import org.ontoware.semversion.impl.generated.RDFModel;

/**
 * A SemVersion Version. Each version is a non-mutable state of an RDF model. A
 * version has a number of metadata attached to it. Each version is identified
 * by a URI. A {@link VersionedModel} has none or one root version. Each version
 * can have a number of chuld versions. Versions can have branch labels. The
 * default branch is called 'main'.
 * 
 * Each model can have a number of 'suggested' children. Suggestions can have
 * further children, but these must also be suggestions. <code><pre>
 * Example:
 * 
 * root
 *  - version 1
 *  - version 2
 *    - version 2.1 (suggestion)
 *      - version 2.1.1 (must be a suggestion)
 *    - version 2.2 
 *    - version 2.3
 * </pre></code>
 * 
 * @author voelkel
 */
public class Version extends VersionedItem {

	private org.ontoware.semversion.impl.generated.Version version;

	/**
	 * SHOULD NOT BE CALLED FROM API USERS
	 * 
	 * @param model
	 * @param uri
	 * @param write
	 */
	public Version(Model model, Session session, URI uri, boolean write) {
		super(model, session, uri);
		this.version = new org.ontoware.semversion.impl.generated.Version(
				model, uri, write);
	}

	/**
	 * SHOULD NOT BE CALLED FROM API USERS
	 * 
	 * @param version
	 */
	public Version(org.ontoware.semversion.impl.generated.Version version,
			Session session) {
		super(version.getModel(), session, version.getResource().asURI());
		this.version = version;
	}

	private boolean branchLabelExists(String branchLabel) {
		try {
			for (Version v : this.getVersionedModel().getAllVersions()) {
				if (v.getBranchLabel().equals(branchLabel))
					return true;
			}
		} catch (RDFDataException e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	/**
	 * @param diff
	 * @param comment
	 * @param versionURI
	 *            the contextURI of the new model
	 * @param provenance
	 * @param suggestion
	 *            if true, the the version is a suggestion
	 * @return
	 * @throws CommitConflictException
	 */
	public Version commit(Diff diff, String comment, URI versionURI,
			URI provenance, boolean suggestion) throws CommitConflictException {
		if (!suggestion && isSuggestion())
			throw new InvalidChildOfSuggestionException();
		checkForCommitConflicts(suggestion);
		// apply diff
		Model content = getContent();
		Model temp = SyntacticDiffEngine.applyDiff(getSemVersion()
				.getTripleStore(), content, diff);
		content.close();
		return commit(temp, comment, versionURI, provenance, suggestion);
	}

	/**
	 * @param suggestion
	 *            true if someone wants to commit a suggestion
	 * @return
	 */
	private void checkForCommitConflicts(boolean suggestion) {
		if (!suggestion && this.hasValidChildren()) {
			throw new CommitConflictException();
		}
	}

	/**
	 * Create a new child version by committing explicitly the content of the
	 * child
	 * 
	 * @param childContent
	 * @param comment
	 * @param suggestion
	 *            if true, the new version has not been accepted yet, it is just
	 *            a suggestion
	 * @return the child version of this version that has the content
	 *         'childContent'
	 * @throws CommitConflictException
	 * @throws InvalidChildOfSuggestionException
	 */
	public Version commit(Model childContent, String comment, boolean suggestion)
			throws CommitConflictException, InvalidChildOfSuggestionException {
		if (!suggestion && isSuggestion())
			throw new InvalidChildOfSuggestionException();
		checkForCommitConflicts(suggestion);
		return commit(childContent, comment, getSemVersion().getTripleStore()
				.newRandomUniqueURI(), null, suggestion);
	}

	/**
	 * Create a new child version by committing explicitly the content of the
	 * child. The new version has the given URI.
	 * 
	 * @param childContent
	 * @param comment
	 * @param versionURI
	 * @param provenance
	 * @param suggestion
	 * @return
	 */
	public Version commit(Model childContent, String comment, URI versionURI,
			URI provenance, boolean suggestion) {
		// Impl: commits a suggestion, setValid then creates a 'real' version
		if (!suggestion && isSuggestion())
			throw new InvalidChildOfSuggestionException();
		checkForCommitConflicts(suggestion);

		Version child = new Version(getSemVersion().getMainModel(),
				getSession(), versionURI, true);
		// add meta-data
		try {
			// store model
			Model childModel = getSemVersion().getTripleStore()
					.addModelAndPersist(childContent);
			childContent.close();
			childModel.close();

			child.setUser(getSession().getUser());
			child.setProvenance(provenance);
			child.setCreationTime(Calendar.getInstance());

			child.setChangeCause("commit");

			child.setComment(comment);
			child.setContainer(getVersionedModel());
			if (!suggestion)
				child.setValid();

			// carry over branch label
			child.setBranchLabel(this.getBranchLabel());

			// link
			RDFModel childContentModel = new RDFModel(getSemVersion()
					.getMainModel(), childModel.getContextURI());
			child.setContent(childContentModel);
			this.version.addChild(child.version);
			child.setFirstParent(this);
			// add to root
			getVersionedModel().addVersion(child);
			return child;
		} catch (RDFDataException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Commit a new version by applying a diff to this versions content. The new
	 * version lives in a new branch, named 'branchLabel'
	 * 
	 * @param diff
	 * @param branchLabel
	 *            may not be null
	 * @param comment
	 * @param versionURI
	 *            may not be null
	 * @param provenance
	 *            may be null
	 * @param suggestion
	 * @return
	 * @throws BranchlabelAlreadyUsedException
	 * @throws InvalidChildOfSuggestionException
	 */
	public Version commitAsBranch(Diff diff, String branchLabel,
			String comment, URI versionURI, URI provenance, boolean suggestion)
			throws BranchlabelAlreadyUsedException,
			InvalidChildOfSuggestionException {
		if (versionURI == null)
			throw new IllegalArgumentException("URI may not be null");
		if (!suggestion && isSuggestion())
			throw new InvalidChildOfSuggestionException();
		if (branchLabelExists(branchLabel))
			throw new BranchlabelAlreadyUsedException();
		Version childVersion = commit(diff, comment, versionURI, provenance,
				suggestion);
		childVersion.setBranchLabel(branchLabel);
		return childVersion;
	}

	/**
	 * Create a new child version in a different branch by explicitly committing
	 * 'childContent' as the new content
	 * 
	 * @param childContent
	 * @param branchLabel
	 * @param comment
	 * @param suggestion
	 * @return
	 * @throws BranchlabelAlreadyUsedException
	 * @throws InvalidChildOfSuggestionException
	 */
	public Version commitAsBranch(Model childContent, String branchLabel,
			String comment, boolean suggestion)
			throws BranchlabelAlreadyUsedException,
			InvalidChildOfSuggestionException {
		if (!suggestion && isSuggestion())
			throw new InvalidChildOfSuggestionException();
		if (branchLabelExists(branchLabel))
			throw new BranchlabelAlreadyUsedException();
		Version childVersion = commit(childContent, comment, suggestion);
		childVersion.setBranchLabel(branchLabel);
		return childVersion;
	}

	/**
	 * Creates a new child version with the given URI.
	 * 
	 * @param childContent
	 * @param branchLabel
	 * @param comment
	 * @param versionURI
	 * @param provenance
	 * @param suggestion
	 * @return
	 * @throws BranchlabelAlreadyUsedException
	 * @throws InvalidChildOfSuggestionException
	 */
	public Version commitAsBranch(Model childContent, String branchLabel,
			String comment, URI versionURI, URI provenance, boolean suggestion)
			throws BranchlabelAlreadyUsedException,
			InvalidChildOfSuggestionException {
		if (versionURI == null)
			throw new IllegalArgumentException("URI may not be null");
		if (!suggestion && isSuggestion())
			throw new InvalidChildOfSuggestionException();
		if (branchLabelExists(branchLabel))
			throw new BranchlabelAlreadyUsedException();
		Version childVersion = commit(childContent, comment, versionURI,
				provenance, suggestion);
		childVersion.setBranchLabel(branchLabel);
		return childVersion;
	}

	/**
	 * @return a String which contains all content of this model in a
	 *         self-invented strange format.
	 */
	public String dump() {
		StringBuffer buf = new StringBuffer();
		try {
			Model content = getContent();
			ClosableIterator<Statement> iter = content.iterator();
			while (iter.hasNext()) {
				Statement s = iter.next();
				buf.append("\t" + s.getSubject() + "\t" + s.getPredicate()
						+ "\t" + s.getObject() + "\n");
			}
			iter.close();
			content.close();
			return buf.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean equals(Object other) {
		return ((other instanceof Version) && this.version.getResource()
				.equals(((Version) other).version.getResource()));
	}

	// //////////////////////////////////////////////////
	// versioning specific methods

	/**
	 * @return all child versions (suggestions and accepted) of this version
	 */
	public List<Version> getAllChildren() {
		org.ontoware.semversion.impl.generated.Version[] genChildVersions = (org.ontoware.semversion.impl.generated.Version[]) Bridge
				.getAllValues(version.getModel(), version.getResource(),
						org.ontoware.semversion.impl.generated.Version.CHILD,
						org.ontoware.semversion.impl.generated.Version.class);

		List<Version> result = new ArrayList<Version>(genChildVersions.length);
		for (org.ontoware.semversion.impl.generated.Version genChild : genChildVersions) {
			result.add(new Version(genChild, getSession()));
		}
		return result;
	}

	/**
	 * @return the branch label of this version
	 */
	public String getBranchLabel() {
		return this.version.getBranchLabel();
	}

	/**
	 * @return the change cause
	 */
	public String getChangeCause() {
		return this.version.getChangeCause();
	}

	/**
	 * @return a an in-memory copy of the RDF content of this model
	 */
	public Model getContent() {
		return new BlankNodeEnrichmentModel(
		getSemVersion().getTripleStore().getAsTempCopy(getContentURI()));
	}

	/**
	 * @return the URI of the named graph which is used to store this versions
	 *         RDF model
	 */
	private URI getContentURI() {
		return version.getContent().getResource().asURI();
	}

	/**
	 * @return predecessor, which is always a Version
	 */
	protected Version getFirstParent() {
		org.ontoware.semversion.impl.generated.Version reactorVersion = version
				.getFirstParent();
		if (reactorVersion == null)
			return null;
		else
			return new Version(reactorVersion, getSession());
	}

	/**
	 * @return all accepted child versions (including suggestions)
	 */
	public List<Version> getNextVersions() {
		List<Version> result = new ArrayList<Version>();
		for (Version v : getAllChildren()) {
			result.add(v);
		}
		return result;
	}

	/**
	 * @return the first parent
	 */
	public Version getPrevVersion() {
		return getFirstParent();
	}

	protected org.ontoware.semversion.impl.generated.Version getReactorVersion() {
		return this.version;
	}

	/**
	 * @return the second parent (a Version) if this version has been the result
	 *         of a merge. Return null otherwise.
	 */
	public Version getSecondParent() {
		org.ontoware.semversion.impl.generated.Version secondParent = this.version
				.getSecondParent();

		if (secondParent != null
				&& org.ontoware.semversion.impl.generated.Version.hasInstance(
						version.getModel(), secondParent.getResource().asURI())) {
			return new Version(secondParent, getSession());
		} else
			return null;
	}

	/**
	 * @return the size of the versioned RDF graph of this version
	 */
	public long getStatementCount() {
		Model m = getSemVersion().getTripleStore().getPersistentModel(
				getContentURI());
		long size = m.size();
		m.close();
		return size;
	}

	/**
	 * @return all suggestions to this version
	 */
	public List<Version> getSuggestions() {
		List<Version> suggestions = new ArrayList<Version>();
		for (Version child : getAllChildren()) {
			if (!child.isValid())
				suggestions.add(child);
		}
		return suggestions;
	}

	/**
	 * @return all valid children of this version (i.e. children that are not a
	 *         suggestion)
	 */
	public List<Version> getValidChildren() {
		List<Version> result = new ArrayList<Version>();
		for (Version v : getAllChildren()) {
			if (v.isValid())
				result.add(v);
		}
		return result;
	}

	/**
	 * @return the {@link VersionedModel} in which this version lives.
	 */
	public VersionedModel getVersionedModel() throws RDFDataException {
		return new VersionedModel(this.version.getContainer(), getSession());
	}

	protected boolean hasChildWithSameBranchLabel() {
		List<Version> validChildren = getValidChildren();
		for (Version v : validChildren) {
			if (v.getBranchLabel().equals(getBranchLabel()))
				return true;
		}
		return false;
	}

	/**
	 * @return true if this version has at least one valid child version
	 */
	public boolean hasValidChildren() {
		return getValidChildren().size() > 0;
	}

	/**
	 * @param other
	 * @return true if this version has the same branch label as the other
	 *         version
	 */
	public boolean isInSameBranch(Version other) {
		return getBranchLabel().equals(other.getBranchLabel());
	}

	/**
	 * @return true if this version is just a suggested version that has not
	 *         been accepted yet
	 */
	public boolean isSuggestion() {
		return !isValid();
	}

	/**
	 * @return true if this version is an accepted version (i.e. not a
	 *         suggestion)
	 */
	public boolean isValid() {
		try {
			return getTag() != null && getTag().equals(SemVersion.VALID);
		} catch (RDFDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param otherVersion
	 * @param comment
	 * @param provenance
	 * @param suggestion
	 * @return a new version which is the result of the merge (union) of two
	 *         other versions (this version and 'other' version)
	 * @throws CommitConflictException
	 * @throws InvalidChildOfSuggestionException
	 */
	public Version merge(Version otherVersion, String comment, URI provenance,
			boolean suggestion) throws CommitConflictException,
			InvalidChildOfSuggestionException {
		if (!suggestion && isSuggestion())
			throw new InvalidChildOfSuggestionException();
		checkForCommitConflicts(suggestion);
		try {
			URI childURI = getSemVersion().getMainModel().newRandomUniqueURI();
			Version child = new Version(getSemVersion().getMainModel(),
					getSession(), childURI, true);

			// set metadata

			child.setUser(getSession().getUser());
			child.setProvenance(provenance);
			child.setCreationTime(Calendar.getInstance());

			// link merge data
			child.setFirstParent(this);
			child.setSecondParent(otherVersion);

			child.setChangeCause("merge");
			this.version.addChild(child.version);

			Model childModel = getSemVersion().getTripleStore().getAsTempCopy(
					getContentURI());
			// add otherVersion
			Model content = otherVersion.getContent();
			ClosableIterator<Statement> it = content.iterator();
			childModel.addAll(it);
			it.close();
			content.close();

			RDFModel childContent = new RDFModel(
					getSemVersion().getMainModel(), childModel.getContextURI());
			child.setContent(childContent);
			child.setContainer(getVersionedModel());

			child.setComment(comment);

			if (!suggestion)
				child.setValid();

			getVersionedModel().addVersion(child);
			return child;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 'Changes the flag from "suggestion" to "released"
	 */
	public void setAsRelease() {
		setValid();
	}

	private void setBranchLabel(String branchLabel) {
		version.setBranchLabel(branchLabel);
	}

	protected void setChangeCause(String value) {
		this.version.setChangeCause(value);
	}

	protected void setContainer(VersionedModel value) {
		version.setContainer(value.getReactorVersionedModel());
	}

	protected void setContent(RDFModel rdfmodel) throws RDFDataException {
		version.setContent(rdfmodel);
	}

	protected void setFirstParent(Version value) throws RDFDataException {
		version.setFirstParent(value.getReactorVersion());
	}

	/**
	 * Sets this version as invalid (= suggestion)
	 */
	protected void setInvalid() {
		version.removeAllTag();
	}

	private void setSecondParent(Version value) throws RDFDataException {
		version.setSecondParent(value.getReactorVersion());
	}

	protected void setValid() {
		try {
			setTag(SemVersion.VALID);

			// inherit branch label from parent
			if (this.getFirstParent() != null)
				this.setBranchLabel(this.getFirstParent().getBranchLabel());
			else
				// first version must be main branch
				this.setBranchLabel(SemVersion.MAIN_BRANCH);

		} catch (RDFDataException e) {
			throw new RuntimeException(e);
		}
	}

}
