package org.ontoware.rdfreactor.generator;

import org.junit.Assert;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.generator.java.JClass;
import org.ontoware.rdfreactor.generator.java.JModel;
import org.ontoware.rdfreactor.generator.java.JPackage;

public class TestJModel {
	
	@Test
	public void testMapping() {
		
		JPackage jp = new JPackage("Package");
		JClass root = new JClass(jp,"Rootclass", new URIImpl("urn:java:class.Rootclass"));
		JModel jm = new JModel(root);
		
		URI testURI = new URIImpl("urn:test:classX");
		JClass testClass = new JClass(jp,"Testclass", testURI);
		
		jm.addMapping( testURI, testClass );
		
		Assert.assertNotNull(jm.getMapping(testURI));
	}

}
