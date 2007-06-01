package org.ontoware.rdf2go.util;

import java.net.URI;

import org.ontoware.rdf2go.model.node.DatatypeLiteral;

/**
 * Converts RDF2Go nodes into Java datatypes
 * @author voelkel
 *
 */
public class TypeConverter {

	public static boolean toBoolean( DatatypeLiteral datatypeLiteral) {
		return Boolean.parseBoolean(datatypeLiteral.getValue());
	}
	
	public static int toInt( DatatypeLiteral datatypeLiteral) {
		return Integer.parseInt(datatypeLiteral.getValue());
	}

	public static long toLong( DatatypeLiteral datatypeLiteral) {
		return Long.parseLong(datatypeLiteral.getValue());
	}

	public static double toDouble( DatatypeLiteral datatypeLiteral) {
		return Double.parseDouble(datatypeLiteral.getValue());
	}
	public static float toFloat( DatatypeLiteral datatypeLiteral) {
		return Float.parseFloat(datatypeLiteral.getValue());
	}

	public static byte toByte( DatatypeLiteral datatypeLiteral) {
		return Byte.parseByte(datatypeLiteral.getValue());
	}

	public static char toChar( DatatypeLiteral datatypeLiteral) {
		if (datatypeLiteral.getValue().length() == 1)
			return datatypeLiteral.getValue().charAt(0);
		else
			throw new IllegalArgumentException("\"" + datatypeLiteral.getValue()
					+ "\" is not a single char!");
	}

	public static URI toJavaNetUri( DatatypeLiteral datatypeLiteral) {
		return datatypeLiteral.asURI().asJavaURI();
	}

}
