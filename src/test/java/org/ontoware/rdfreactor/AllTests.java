package org.ontoware.rdfreactor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.ontoware.rdfreactor.runtime.MicroBridgeTest;
import org.ontoware.rdfreactor.runtime.PersonTest;
import org.ontoware.rdfreactor.runtime.ReflectionUtilsTest;
import org.ontoware.rdfreactor.runtime.converter.CalendarConverterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { MicroBridgeTest.class, CalendarConverterTest.class,
		PersonTest.class, ReflectionUtilsTest.class })
public class AllTests {
	// the class remains completely empty,
	// being used only as a holder for the above annotations
}
