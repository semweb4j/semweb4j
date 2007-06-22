package org.ontoware.rdfreactor.runtime;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;

/**
 * Handles ISO 8601 Date and Time Formats
 * 
 * http://jena.sourceforge.net/javadoc/com/hp/hpl/jena/datatypes/xsd/XSDDatatype.html
 * 
 * http://jena.sourceforge.net/how-to/typedLiterals.html#xsd
 * 
 * @author voelkel
 * 
 */
public class DatatypeUtils {

	private static final Logger log = LoggerFactory.getLogger(DatatypeUtils.class);

	/** return all normalized to UTC */
	public static String encodeCalendar_toXSDDateTime(Calendar cal) {

		// convert cal to UTC
		
	    Calendar utcCalendar = new GregorianCalendar( TimeZone.getTimeZone("UTC"));
	    utcCalendar.setTimeInMillis(cal.getTimeInMillis());

		XSDDateTime x = new XSDDateTime(utcCalendar);

		StringBuffer buff = new StringBuffer();

		int years = x.getYears();
		buff.append(years);
		buff.append("-");

		if (x.getMonths() < 10)
			buff.append("0");
		buff.append(x.getMonths());
		buff.append("-");

		if (x.getDays() < 10)
			buff.append("0");
		buff.append(x.getDays());
		buff.append("T");

		if (x.getHours() < 10)
			buff.append("0");
		buff.append(x.getHours());

		buff.append(":");
		if (x.getMinutes() < 10)
			buff.append("0");
		buff.append(x.getMinutes());

		buff.append(":");
		if (x.getFullSeconds() < 10)
			buff.append("0");
		buff.append(x.getFullSeconds());

		// TODO append milliseconds
		// double milliseconds = ((double) x.getSeconds() - (double)
		// x.getFullSeconds());

		buff.append("Z");
		return buff.toString();

	}

	public static Calendar parseXSDDateTime_toCalendar(String s)
			throws RDFDataException {
		log.debug("Trying to parse '"+s+"' as an xsd:dateTime");
		
		XSDDateTime xsddate = (XSDDateTime) XSDDatatype.XSDdateTime.parse(s);

		if (xsddate == null)
			throw new RDFDataException("Could not parse '" + s
					+ "' as an xsd:DateTime.");

		log.debug(xsddate.getYears() + "-" + xsddate.getMonths() + "-"
				+ xsddate.getDays() + "-" + xsddate.getHours() + "-"
				+ xsddate.getMinutes() + "-" + xsddate.getFullSeconds());

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UCT"));
		c.set(Calendar.YEAR, xsddate.getYears());
		c.set(Calendar.MONTH, xsddate.getMonths() - 1);
		c.set(Calendar.DATE, xsddate.getDays());

		log.debug("Hour = " + xsddate.getHours());

		// FIXME hour .- 1 ?
		c.set(Calendar.HOUR_OF_DAY, xsddate.getHours());
		c.set(Calendar.MINUTE, xsddate.getMinutes());
		c.set(Calendar.SECOND, (int) xsddate.getSeconds());
		// IMPROVE ... c.set(Calendar.MILLISECOND, ) currently we have only
		// second-precision

		return c;
	}
	
}
