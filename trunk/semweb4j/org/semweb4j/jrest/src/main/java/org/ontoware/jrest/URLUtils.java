package org.ontoware.jrest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

public class URLUtils {

	private static URLCodec codec = new URLCodec("UTF-8");

	public static String urldecode(String in) {
		try {
			return codec.decode(in);
		} catch (DecoderException e) {
			throw new RuntimeException(e);
		}
	}

	public static String urlencode(String in) {
		try {
			return codec.encode(in);
		} catch (EncoderException e) {
			throw new RuntimeException(e);
		}
	}

}
