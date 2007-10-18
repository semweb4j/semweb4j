package org.ontoware.semversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.RDFDataException;
import org.ontoware.semversion.impl.SemVersionImpl;

@SuppressWarnings("unused")
public class VersionedModelTest {

	VersionedModel vmi;

	SemVersionImpl svi;

	@Before
	public void setUp() throws Exception {
		svi = new SemVersionImpl();
		svi.startup(new File("./target/VersionedModelTest"));
		svi.clear();
		vmi = new VersionedModel(svi.getMainModel(), new URIImpl("test://vmi"),
				true);
	}

	@Test
	public void testDelete() {
		vmi.delete();
		assertTrue("expected deletion time to be earlier then now", 
				vmi.getTransactionTime().getEnd().compareTo(
				Calendar.getInstance()) <= 0);
	}

	@Test
	public void testSetGetRoot() throws RDFDataException {
		Version vi = new Version(svi.getMainModel(), svi.getMainModel()
				.newRandomUniqueURI(), true);
		vmi.setRoot(vi);
		Version root = vmi.getRoot();
		assertNotNull(vmi.getRoot());
	}

	@Test
	public void testAddGetVersion() throws RDFDataException {
		assertNotNull(vmi.getAllVersion());

		assertEquals(0, vmi.getAllVersion().length);
		Version vi = new Version(svi.getMainModel(), svi.getMainModel()
				.newRandomUniqueURI(), true);
		vmi.addVersion(vi);
		assertNotNull(vmi.getAllVersion());
		assertEquals(1, vmi.getAllVersion().length);
	}

	public void testRemoveVersion() {
		// TODO
	}

	public void testAddVersion() {
		// TODO
	}

	public void testGetAllVersion() {
		// TODO
	}

	/*
	 * Class under test for void commitRoot(User, TripleSetImpl, String, URI,
	 * ValidTime, URI)
	 */
	public void testCommitRootUserTripleSetImplStringURIValidTimeURI() {
		// TODO
	}

	public void testGetTimestampLastModified() {
		// TODO
	}

	public void testGetLastModifiedBy() {
		// TODO
	}

	public void testGetListLastModifiedBy() {
		// TODO
	}

	public void testGetVersionCount() {
		// TODO
	}

	/*
	 * Class under test for List<Version> getVersions(TransactionTime)
	 */
	public void testGetVersionsTransactionTime() {
		// TODO
	}

	/*
	 * Class under test for List<Version> getVersions(ValidTime)
	 */
	public void testGetVersionsValidTime() {
		// TODO
	}

	/*
	 * Class under test for List getVersions(String, String, TransactionTime,
	 * ValidTime)
	 */
	public void testGetVersionsStringStringTransactionTimeValidTime() {
		// TODO
	}

	public void testGetFirstVersion() {
		// TODO
	}

	public void testGetLastVersions() {
		// TODO
	}

	public void testGetChangeLog() {
		// TODO
	}

	public void testGetBranches() {
		// TODO
	}

	public void testGetStatistics() {
		// TODO
	}

	/*
	 * Class under test for void commitRoot(User, TripleSet, String, URI,
	 * ValidTime, URI)
	 */
	public void testCommitRootUserTripleSetStringURIValidTimeURI() {
		// TODO
	}

}
