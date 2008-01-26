package org.ontoware.rdfreactor.runtime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReflectionUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHasSuperClass() {
		assertTrue(ReflectionUtils.hasSuperClass(URI.class, Object.class));
		assertFalse(ReflectionUtils.hasSuperClass(Object.class, URI.class));
		assertTrue(ReflectionUtils.hasSuperClass(Object.class, Object.class));
		assertTrue(ReflectionUtils.hasSuperClass(URI.class, URI.class));

		assertTrue(ReflectionUtils.hasSuperClass(BufferedReader.class,
				Reader.class));
		assertFalse(ReflectionUtils.hasSuperClass(Reader.class,
				BufferedReader.class));

		assertTrue(ReflectionUtils.hasSuperClass(BufferedReader.class,
				Readable.class));
		assertTrue(ReflectionUtils.hasSuperClass(Reader.class, Readable.class));
		assertFalse(ReflectionUtils.hasSuperClass(URI.class, Readable.class));
		assertFalse(ReflectionUtils.hasSuperClass(URI.class, Serializable.class));
		assertTrue(ReflectionUtils.hasSuperClass(URI.class, Comparable.class));

	}

}
