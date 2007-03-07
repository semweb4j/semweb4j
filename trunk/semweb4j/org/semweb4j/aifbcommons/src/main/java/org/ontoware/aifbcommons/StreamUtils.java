package org.ontoware.aifbcommons;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class StreamUtils {

	public static final void readWrite(InputStream in, String inputCharSet, OutputStream out,
			String outputCharSet) throws IOException {

		OutputStreamWriter outw = new OutputStreamWriter(out, outputCharSet);
		InputStreamReader inr = new InputStreamReader(in, inputCharSet);
		readWrite(inr, outw);
	}

	/**
	 * 
	 * @param r
	 * @param out
	 * @param outputCharSet
	 * @return number of transferred characters
	 * @throws IOException
	 */
	public static long readWrite(Reader r, OutputStream out, String outputCharSet)
			throws IOException {
		OutputStreamWriter outw = new OutputStreamWriter(out, outputCharSet);
		return readWrite(r, outw);
	}

	/**
	 * 
	 * @param r
	 * @param w
	 * @return number of transferred characters
	 * @throws IOException
	 */
	public static long readWrite(Reader r, Writer w) throws IOException {
		char[] buffer = new char[8192];
		int count = r.read(buffer);
		long transferred = 0;
		while (count > 0) {
			transferred += count;
			w.write(buffer, 0, count);
			count = r.read(buffer);
		}
		return transferred;
	}
}
