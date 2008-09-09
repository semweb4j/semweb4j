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

	public static final String outdir = "./target/test-gen";

	@Test
	public void testWrite() throws Exception {
		JModel jm = new JModel(JClass.RDFS_CLASS);
		JPackage jp = new JPackage("test");
		jm.addPackage(jp);
		JClass jc1 = new JClass(jp, "Person", new URIImpl("urn:ex:Person"));
		jc1.setComment("All persons in the world");
		jc1.setJavaSuperclass(new JClass(new JPackage(""),"org.ontoware.rdfreactor.runtime.ReactorRuntimeEntity",new URIImpl("urn:ex:object")));
		jp.getClasses().add(jc1);

		// deprecated
		// JProperty jprop1 = new JProperty("age", new
		// JAttribute(Integer.class),
		// URIUtils.createURI("schema://age"), 1, 1);
		// jc1.getProperties().add(jprop1);
		JProperty jprop2 = new JProperty(jc1, "friend", new URIImpl("urn:ex:knows"), JProperty.NOT_SET, JProperty.NOT_SET );
		jprop2.setComment("A persons knows other persons. They can be considered friends.");
		jprop2.addType(jc1);
		jc1.getProperties().add(jprop2);
		SourceCodeWriter.write(jm, new File(outdir), SourceCodeWriter.TEMPLATE_CLASS,"Prefix");

	}
}
