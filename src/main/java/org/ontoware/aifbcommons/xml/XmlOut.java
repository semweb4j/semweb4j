package org.ontoware.aifbcommons.xml;

import java.io.IOException;

/**
 * Impls must write out the XML declaration
 * @author voelkel
 *
 */
public interface XmlOut {
	
public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	
	public void open( String elementName ) throws IOException;
	
	public void attribute( String name, String value ) throws IOException;
	
	public void content( String rawContent ) throws IOException;

	public void close( String elementName ) throws IOException;

	public void comment(String string) throws IOException;

	/** circumvent xml and just write as-is to writer 
	 * @throws IOException */
	public void write( String s) throws IOException;

	public void openProcessingInstruction( String processingInstruction ) throws IOException;

	public void closeProcessingInstruction() throws IOException;

	public void doctype(String doctype, String publicID, String url) throws IOException;

	public void flush() throws IOException;

	public void close() throws IOException;
}
