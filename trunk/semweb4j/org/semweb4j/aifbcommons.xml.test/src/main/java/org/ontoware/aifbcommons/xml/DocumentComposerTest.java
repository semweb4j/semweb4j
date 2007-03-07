package org.ontoware.aifbcommons.xml;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.ontoware.aifbcommons.xml.DocumentComposer;

public class DocumentComposerTest extends TestCase {

	public void testComposing() {
		Document master = DocumentHelper.createDocument();
		master.addElement("master").addElement("replace").addAttribute("key", "a");
		Document a = DocumentHelper.createDocument();
		a.addElement("aaa").addElement("bbb").addAttribute("ccc", "ddd");

		Map<String, Document> mapping = new HashMap<String, Document>();
		mapping.put("a", a);

		Document composed = DocumentComposer.compose(master, mapping);
		
		assertTrue( composed.selectNodes("//bbb") != null );
		assertEquals( 1, composed.selectNodes("//bbb").size() );
		
	}

}
