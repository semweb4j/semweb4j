package org.ontoware.aifbcommons.xml;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Assert;
import org.junit.Test;

public class DocumentComposerTest {

	@Test
	public void testComposing() {
		Document master = DocumentHelper.createDocument();
		master.addElement("master").addElement("replace").addAttribute("key", "a");
		Document a = DocumentHelper.createDocument();
		a.addElement("aaa").addElement("bbb").addAttribute("ccc", "ddd");

		Map<String, Document> mapping = new HashMap<String, Document>();
		mapping.put("a", a);

		Document composed = DocumentComposer.compose(master, mapping);
		
		Assert.assertTrue( composed.selectNodes("//bbb") != null );
		Assert.assertEquals( 1, composed.selectNodes("//bbb").size() );
		
	}

}
