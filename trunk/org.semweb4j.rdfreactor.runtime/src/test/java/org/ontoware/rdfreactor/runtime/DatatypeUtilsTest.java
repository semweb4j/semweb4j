package org.ontoware.rdfreactor.runtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatatypeUtilsTest extends TestCase {

	Logger log = LoggerFactory.getLogger(DatatypeUtils.class);

	/*
	 * Test method for
	 * 'org.ontoware.rdfreactor.runtime.DatatypeUtils.parseXSDDateTime(String)'
	 */
	public void testParseXSDDateTime() {

		assertDate("2006-12-07T00:00:00", 2006, 12, 07, 0, 0, 0);
		// with timezone
		assertDate("1999-05-31T13:20:00-05:00", 1999, 05, 31, 18, 20, 0);
		assertDate("1999-05-31T13:20:00Z", 1999, 05, 31, 13, 20, 0);
		assertDate("1999-05-31T13:20:00+01:00", 1999, 05, 31, 12, 20, 0);

	}

	@Test
	public void testXsdDateTime() {

		Model m = RDF2Go.getModelFactory().createModel();
		m.open();
		int year = 2005;
		int month = 9;
		int date = 5;
		int hrs = 20;
		int min = 59;
		int sec = 12;

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UCT"));
		cal.set(year, month - 1, date, hrs, min, sec);

		String encoded = DatatypeUtils.encodeCalendar_toXSDDateTime(cal);
		log.info("Cal = " + formatDate(cal)+" encoded as "+encoded);

		assertDate(encoded, year, month, date, hrs, min, sec);

		log.debug("as xsd:date: " + encoded);

		Calendar localCal = Calendar.getInstance(TimeZone
				.getTimeZone("Europe/Berlin"));
		Assert.assertNotSame(cal.get(Calendar.HOUR_OF_DAY), localCal
				.get(Calendar.HOUR_OF_DAY));
		log.debug(" localCal = "
				+ SimpleDateFormat.getInstance().format(localCal.getTime()));
		String localEncoded = DatatypeUtils
				.encodeCalendar_toXSDDateTime(localCal);
		log.debug("as xsd:date: " + localEncoded);

		Calendar returnCal = DatatypeUtils.parseXSDDateTime_toCalendar(encoded);
		log.debug("returnCal = "
				+ SimpleDateFormat.getInstance().format(returnCal.getTime()));

		Assert.assertEquals("year", cal.get(Calendar.YEAR), returnCal
				.get(Calendar.YEAR));
		Assert.assertEquals("month", cal.get(Calendar.MONTH), returnCal
				.get(Calendar.MONTH));
		Assert.assertEquals("day of month", cal.get(Calendar.DAY_OF_MONTH),
				returnCal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals("hours", cal.get(Calendar.HOUR_OF_DAY), returnCal
				.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals("minutes", cal.get(Calendar.MINUTE), returnCal
				.get(Calendar.MINUTE));
		Assert.assertEquals("seconds", cal.get(Calendar.SECOND), returnCal
				.get(Calendar.SECOND));

	}

	public String formatDate(Calendar cal) {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, Locale.GERMANY);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		String result = df.format(cal.getTime())+" in timezone "+cal.getTimeZone().getID();
		return result;
	}

	public void assertDate(String datestring, int year, int month, int day,
			int h, int m, int s) {
		Calendar c = DatatypeUtils.parseXSDDateTime_toCalendar(datestring);
		log.info("Parsed '" + datestring + "' to " + formatDate(c));

		assertEquals("year", year, c.get(Calendar.YEAR));
		assertEquals("month", month - 1, c.get(Calendar.MONTH));
		assertEquals("day of month", day, c.get(Calendar.DAY_OF_MONTH));
		assertEquals("hours", h, c.get(Calendar.HOUR_OF_DAY));
		assertEquals("minutes", m, c.get(Calendar.MINUTE));
		assertEquals("seconds", s, c.get(Calendar.SECOND));
	}

}
