package org.ontoware.aifbcommons.xml;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class DocumentComposer {
	
	public static final String ELEMENT_REPLACE = "replace";
	public static final String ATTRIBUTE_KEY = "key";
	
	/**
	 * 
	 * @param master an XML document with <replace key="..."> elements
	 * @param content - a ampping from replacement keys to XML documents
	 * @return the master documents with all <replace>-elements replaced by 
	 * the correcponding document form the 'content' mapping	 * 
	 */
	@SuppressWarnings("unchecked")
	public static Document compose( Document master, Map<String,Document> content) {
		Document result = DocumentHelper.createDocument();
		result.add( master.getRootElement().createCopy() );
		List list = result.selectNodes("//replace");
		
		for( Object o : list) {
			Element e = (Element) o;
			String key = e.attribute("key").getStringValue();
			Document d = content.get(key);
			Element root = d.getRootElement();
			root.detach();
			Element target = e.getParent();
			List targetContent = target.content();
			int i = target.indexOf(e);
			targetContent.remove(i);
			targetContent.add(i, root);
			target.setContent( targetContent);
		}
		
		return result;
	}

}
