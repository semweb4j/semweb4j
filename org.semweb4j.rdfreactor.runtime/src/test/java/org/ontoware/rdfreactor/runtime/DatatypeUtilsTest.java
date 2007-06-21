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
		
		assertDate("2006-12-07T00:00:00",2006,12,07,0,0,0);
		// with timezone 
		assertDate("1999-05-31T13:20:00-05:00",1999,05,31,19,20,0);

	}
	
	@Test
	public void testXsdDateTime() {

		Model m = RDF2Go.getModelFactory().createModel();
		m.open();
		int year = 105;
		int month = 11;
		int date = 5;
		int hrs = 20;
		int min = 59;
		int sec = 12;
		
		
		Calendar cal = Calendar.getInstance( TimeZone.getTimeZone("UCT"));
		cal.set(year+1900, month, date, hrs, min, sec);
		assertEquals(hrs, cal.get(Calendar.HOUR_OF_DAY));
		log.debug("      cal = "+SimpleDateFormat.getInstance().format(cal.getTime()));
		String encoded = DatatypeUtils.encodeCalendar_toXSDDateTime(cal);
		log.debug("as xsd:date: "+encoded);
		
		Calendar localCal = Calendar.getInstance( TimeZone.getTimeZone("Europe/Berlin"));
		Assert.assertNotSame( cal.get(Calendar.HOUR_OF_DAY), localCal.get(Calendar.HOUR_OF_DAY));
		log.debug(" localCal = "+SimpleDateFormat.getInstance().format(localCal.getTime()));
		String localEncoded = DatatypeUtils.encodeCalendar_toXSDDateTime(localCal);
		log.debug("as xsd:date: "+localEncoded);

		Calendar returnCal = DatatypeUtils.parseXSDDateTime_toCalendar(encoded);		
		log.debug("returnCal = "+SimpleDateFormat.getInstance().format(returnCal.getTime()));
	
		Assert.assertEquals(cal.get(Calendar.YEAR), returnCal.get(Calendar.YEAR));
		Assert.assertEquals(cal.get(Calendar.MONTH), returnCal.get(Calendar.MONTH));
		Assert.assertEquals(cal.get(Calendar.DAY_OF_MONTH), returnCal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(cal.get(Calendar.HOUR), returnCal.get(Calendar.HOUR));
		Assert.assertEquals(cal.get(Calendar.MINUTE), returnCal.get(Calendar.MINUTE));
		Assert.assertEquals(cal.get(Calendar.SECOND), returnCal.get(Calendar.SECOND));

		Assert.assertEquals(cal.getTimeInMillis(), returnCal.getTimeInMillis() );
	}
	
	public void assertDate(String datestring, int year, int month, int day, int h, int m, int s) {
		Calendar c = DatatypeUtils.parseXSDDateTime_toCalendar(datestring);

		DateFormat df = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG, Locale.GERMANY);
		df.setTimeZone( TimeZone.getTimeZone("UTC"));
		String result = df.format(c.getTime());
		System.out.println("Parsed '"+datestring+"' to "+result);
		
		
		assertEquals(year, c.get(Calendar.YEAR));
		assertEquals(month-1, c.get(Calendar.MONTH));
		assertEquals(day, c.get(Calendar.DAY_OF_MONTH));
		assertEquals(h, c.get(Calendar.HOUR_OF_DAY));
		assertEquals(m, c.get(Calendar.MINUTE));
		assertEquals(s, c.get(Calendar.SECOND));
	}

}
