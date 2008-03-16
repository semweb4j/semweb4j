package org.ontoware.semversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.RDFDataException;

@SuppressWarnings("unused")
public class VersionedModelTest {

	VersionedModel vmi;

	SemVersion svi;

	Session session;
	
	@Before
	public void setUp() throws Exception {
		svi = new SemVersion();
		svi.startup(new File("./target/VersionedModelTest"));
		svi.clear();
		session = svi.createAnonymousSession();
		vmi = new VersionedModel(svi.getMainModel(), session, new URIImpl("test://vmi"),
				true);
	}

//	@Test
//	public void testDelete() {
//		vmi.delete();
//		assertTrue("expected deletion time to be earlier then now", 
//				vmi.getTransactionTime().getEnd().compareTo(
//				Calendar.getInstance()) <= 0);
//	}

	@Test
	public void testSetGetRoot() throws RDFDataException {
		Version vi = new Version(svi.getMainModel(), session, svi.getMainModel()
				.newRandomUniqueURI(), true);
		vmi.setRoot(vi);
		Version root = vmi.getRoot();
		assertNotNull(vmi.getRoot());
	}

	@Test
	public void testAddGetVersion() throws RDFDataException {
		assertNotNull(vmi.getAllVersions());

		assertEquals(0, vmi.getAllVersions().size());
		Version vi = new Version(svi.getMainModel(), session,  svi.getMainModel()
				.newRandomUniqueURI(), true);
		vmi.addVersion(vi);
		assertNotNull(vmi.getAllVersions());
		assertEquals(1, vmi.getAllVersions().size());
	}

}
