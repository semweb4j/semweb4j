package org.semanticdesktop.common.binstore;

import java.io.InputStream;

public interface IBinStore {

	InputStream readStream(String resourceURI);

	void writeStream(InputStream source, String resourceURI);
	
	void delete( String resourceURI );

}
