package org.ontoware.jrest;

import java.io.IOException;

import org.ontoware.aifbcommons.xml.XmlOut;


public interface ToXML {

	/**
	 * XML is written to xo, according to required content type.
	 * @param xo
	 * @param contentType - may be null = use default
	 * @throws IOException
	 */
	public void toXML( XmlOut xo, RestParams params ) throws IOException;
	
}
