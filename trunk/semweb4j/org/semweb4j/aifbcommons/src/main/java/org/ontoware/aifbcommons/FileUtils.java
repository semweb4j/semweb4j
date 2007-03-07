package org.ontoware.aifbcommons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

	/**
	 * Might add one line-end too much atthe end of the file
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static String loadFileInString(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuffer content = new StringBuffer();
		String line = br.readLine();
		while (line != null) {
			content.append(line).append("\n");
			line = br.readLine();
		}
		return content.toString();
	}

}
