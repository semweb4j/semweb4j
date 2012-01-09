package org.ontoware.rdf2go.impl.jena29;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.ontoware.rdf2go.GenericTest;
import org.ontoware.rdf2go.model.ModelTest;
import org.ontoware.rdf2go.model.NotifyingModelTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// shared tests
		GenericTest.class, ModelTest.class, NotifyingModelTest.class, 
		// additionally local tests
		DataTypeTesting.class, TestResourceLoading.class

})
public class AllTests {
	// the class remains completely empty,
	// being used only as a holder for the above annotations
}
