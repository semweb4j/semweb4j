package org.ontoware.rdf2go;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.ontoware.rdf2go.model.ModelSetTest;
import org.ontoware.rdf2go.model.ModelTest;
import org.ontoware.rdf2go.model.NotifyingModelSetTest;
import org.ontoware.rdf2go.model.NotifyingModelTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { GenericTest.class, ModelTest.class, ModelSetTest.class,
		NotifyingModelTest.class, NotifyingModelSetTest.class })
public class AllTests {
	// the class remains completely empty,
	// being used only as a holder for the above annotations
}
