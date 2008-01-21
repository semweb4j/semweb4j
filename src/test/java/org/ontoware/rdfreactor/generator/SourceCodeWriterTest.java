package org.ontoware.rdfreactor.generator;

import java.io.File;

import org.junit.Test;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.generator.java.JClass;
import org.ontoware.rdfreactor.generator.java.JModel;
import org.ontoware.rdfreactor.generator.java.JPackage;
import org.ontoware.rdfreactor.generator.java.JProperty;

/**
 * TODO add assertions, currently this test test nothing
 * 
 * @author voelkel
 * 
 */
public class SourceCodeWriterTest  {

	@Test
	public void testWrite() throws Exception {
		JModel jm = new JModel(JClass.RDFS_CLASS, true);
		JPackage jp = new JPackage("test");
		jm.addPackage(jp);
		JClass jc1 = new JClass(jp, "MyClass", new URIImpl("schema://myclass"));
		jc1.setJavaSuperclass(JClass.REACTOR_BASE_NAMED);
		jp.getClasses().add(jc1);
		// deprecated
		// JProperty jprop1 = new JProperty("age", new
		// JAttribute(Integer.class),
		// URIUtils.createURI("schema://age"), 1, 1);
		// jc1.getProperties().add(jprop1);
		JProperty jprop2 = new JProperty(jc1, "friend", new URIImpl("schema://knows"), 0,
				Integer.MAX_VALUE);
		jprop2.addType(jc1);
		jc1.getProperties().add(jprop2);
		SourceCodeWriter.write(jm, new File("./target/temp/test-data"), SourceCodeWriter.TEMPLATE_CLASS);

	}
}
