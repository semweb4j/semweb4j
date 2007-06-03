package org.ontoware.rdfreactor.runtime;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

public class DatatypeUtilsTest extends TestCase {

	/*
	 * Test method for
	 * 'org.ontoware.rdfreactor.runtime.DatatypeUtils.parseXSDDateTime(String)'
	 */
	public void testParseXSDDateTime() {
		
		assertDate("2006-12-07T00:00:00",2006,12,07,0,0,0);
		// with timezone 
		assertDate("1999-05-31T13:20:00-05:00",1999,05,31,19,20,0);

	}
	
	public void assertDate(String datestring, int year, int month, int day, int h, int m, int s) {
		Date d = DatatypeUtils.parseXSDDateTime_toDate(datestring);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d.getTime());

		DateFormat df = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG, Locale.GERMANY);
		df.setTimeZone( TimeZone.getTimeZone("UTC"));
		String result = df.format(d);
		System.out.println("Parsed '"+datestring+"' to "+result);
		
		
		assertEquals(year, c.get(Calendar.YEAR));
		assertEquals(month-1, c.get(Calendar.MONTH));
		assertEquals(day, c.get(Calendar.DAY_OF_MONTH));
		assertEquals(h, c.get(Calendar.HOUR_OF_DAY));
		assertEquals(m, c.get(Calendar.MINUTE));
		assertEquals(s, c.get(Calendar.SECOND));
	}

}
