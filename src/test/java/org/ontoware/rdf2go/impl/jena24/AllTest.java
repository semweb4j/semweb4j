package org.ontoware.rdf2go.impl.jena24;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.ontoware.rdf2go.TestTestDataFromRemoteCode;
import org.ontoware.rdf2go.model.ModelTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { TestResourceLoading.class, 
	ModelTest.class, DataTypeTesting.class, TestTestDataFromRemoteCode.class })
public class AllTest {
	// the class remains completely empty,
	// being used only as a holder for the above annotations

}
