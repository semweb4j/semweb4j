package org.ontoware.rdfreactor.runtime;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	
	private static final Log log = LogFactory.getLog(DatatypeUtils.class);

	public static Date parseXSDDateTime_toDate(String s) throws RDFDataException {
		return parseXSDDateTime_toCalendar(s).getTime();
	}

	public static Calendar parseXSDDateTime_toCalendar(String s) throws RDFDataException {
		XSDDateTime xsddate = (XSDDateTime) XSDDatatype.XSDdateTime.parse(s);

		if (xsddate == null)
			throw new RDFDataException("Could not parse '" + s + "' as an xsd:DateTime.");

		log.debug(xsddate.getYears() + "-" + xsddate.getMonths() + "-" + xsddate.getDays()
				+ "-" + xsddate.getHours() + "-" + xsddate.getMinutes() + "-"
				+ xsddate.getFullSeconds());

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UCT"));
		c.set(Calendar.YEAR, xsddate.getYears());
		c.set(Calendar.MONTH, xsddate.getMonths() - 1);
		c.set(Calendar.DATE, xsddate.getDays());

		log.debug("Hour = " + xsddate.getHours());

		c.set(Calendar.HOUR_OF_DAY, xsddate.getHours() - 1);
		c.set(Calendar.MINUTE, xsddate.getMinutes());
		c.set(Calendar.SECOND, (int) xsddate.getSeconds());
		// IMPROVE ... c.set(Calendar.MILLISECOND, )   currently we have only second-precision

		return c;
	}

}
