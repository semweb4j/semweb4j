package org.ontoware.semversion.impl;

import java.util.Date;

@Deprecated
public class DateUtils {

	public static Date asDate(long l) {
		return new Date(l);
	}

	public static long asLong(Date d) {
		return d.getTime();
	}

	public static long asLong(String s) {
		assert s != null;
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public static Date asDate(String s) {
		return asDate(asLong(s));
	}

}
