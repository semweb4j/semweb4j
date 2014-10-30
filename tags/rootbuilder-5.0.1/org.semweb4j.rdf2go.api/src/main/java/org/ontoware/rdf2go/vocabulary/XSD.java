/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de). Licensed under a BSD license
 * (http://www.opensource.org/licenses/bsd-license.php) <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe,
 * Germany <YEAR> = 2010
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go
 */

package org.ontoware.rdf2go.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;


/**
 * The XML Schema vocabulary as URIs
 * 
 * @author mvo
 * 
 */
public class XSD {
	
	/**
	 * The XML Schema Namespace
	 */
	public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	
	protected static final URI toURI(String local) {
		return new URIImpl(XSD_NS + local, false);
	}
	
	public static final URI _string = toURI("string");
	
	public static final URI _boolean = toURI("boolean");
	
	public static final URI _float = toURI("float");
	
	public static final URI _double = toURI("double");
	
	public static final URI _decimal = toURI("decimal");
	
	/**
	 * As discussed in
	 * http://www.w3.org/2001/sw/BestPractices/XSCH/xsch-sw-20050127
	 * /#section-duration an standardised in
	 * http://www.w3.org/TR/xpath-datamodel/#notation
	 * 
	 * Note: The XML namespace is 'http://www.w3.org/2001/XMLSchema', but RDF
	 * people seems to have agreed on using '#' atht eh end to create a
	 * URI-prefix
	 */
	public static final String XS_URIPREFIX = "http://www.w3.org/2001/XMLSchema#";
	
	/**
	 * According to <a href=
	 * "http://www.w3.org/2001/sw/BestPractices/XSCH/xsch-sw-20050127/#section-duration"
	 * >this</a> SHOULD NOT be used: xsd:duration does not have a well-defined
	 * value space.
	 * 
	 * Instead use _yearMonthDuration or _dayTimeDuration
	 */
	@Deprecated
	public static final URI _duration = toURI("duration");
	
	public static final URI _yearMonthDuration = new URIImpl(XS_URIPREFIX + "yearMonthDuration",
	        false);
	
	public static final URI _dayTimeDuration = new URIImpl(XS_URIPREFIX + "dayTimeDuration", false);
	
	public static final URI _dateTime = toURI("dateTime");
	
	public static final URI _time = toURI("time");
	
	public static final URI _date = toURI("date");
	
	public static final URI _gYearMonth = toURI("gYearMonth");
	
	public static final URI _gYear = toURI("gYear");
	
	public static final URI _gMonthDay = toURI("gMonthDay");
	
	public static final URI _gDay = toURI("gDay");
	
	public static final URI _gMonth = toURI("gMonth");
	
	public static final URI _hexBinary = toURI("hexBinary");
	
	public static final URI _base64Binary = toURI("base64Binary");
	
	public static final URI _anyURI = toURI("anyURI");
	
	public static final URI _QName = toURI("QName");
	
	public static final URI _normalizedString = toURI("normalizedString");
	
	public static final URI _token = toURI("token");
	
	public static final URI _language = toURI("language");
	
	public static final URI _IDREFS = toURI("IDREFS");
	
	public static final URI _ENTITIES = toURI("ENTITIES");
	
	public static final URI _NMTOKEN = toURI("NMTOKEN");
	
	public static final URI _NMTOKENS = toURI("NMTOKENS");
	
	public static final URI _Name = toURI("Name");
	
	public static final URI _NCName = toURI("NCName");
	
	public static final URI _ID = toURI("ID");
	
	public static final URI _IDREF = toURI("IDREF");
	
	public static final URI _ENTITY = toURI("ENTITY");
	
	public static final URI _integer = toURI("integer");
	
	public static final URI _nonPositiveInteger = toURI("nonPositiveInteger");
	
	public static final URI _negativeInteger = toURI("negativeInteger");
	
	public static final URI _long = toURI("long");
	
	/**
	 * http://www.w3.org/TR/xmlschema-2/datatypes.html#int
	 * 
	 * [Definition:] int is derived from long by setting the value of
	 * maxInclusive to be 2147483647 and minInclusive to be -2147483648. The
	 * base type of int is long.
	 * 
	 */
	public static final URI _int = toURI("int");
	
	public static final URI _short = toURI("short");
	
	public static final URI _byte = toURI("byte");
	
	public static final URI _nonNegativeInteger = toURI("nonNegativeInteger");
	
	public static final URI _unsignedLong = toURI("unsignedLong");
	
	public static final URI _unsignedInt = toURI("unsignedInt");
	
	public static final URI _unsignedShort = toURI("unsignedShort");
	
	public static final URI _unsignedByte = toURI("unsignedByte");
	
	public static final URI _positiveInteger = toURI("positiveInteger");
	
	/**
	 * For convenience: An array of all types in this interface
	 */
	public static final URI[] ALL = new URI[] { _string, _boolean, _float, _double, _decimal,
	        _duration, _dateTime, _time, _date, _gYearMonth, _gYear, _gMonthDay, _gDay, _gMonth,
	        _hexBinary, _base64Binary, _anyURI, _QName, _normalizedString, _token, _language,
	        _IDREFS, _ENTITIES, _NMTOKEN, _NMTOKENS, _Name, _NCName, _ID, _IDREF, _ENTITY,
	        _integer, _nonPositiveInteger, _negativeInteger, _long, _int, _short, _byte,
	        _nonNegativeInteger, _unsignedLong, _unsignedInt, _unsignedShort, _unsignedByte,
	        _positiveInteger

	};
	
}
