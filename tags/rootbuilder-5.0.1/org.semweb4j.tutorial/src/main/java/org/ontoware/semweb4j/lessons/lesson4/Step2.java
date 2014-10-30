package org.ontoware.semweb4j.lessons.lesson4;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDFS;


class Point {
	
	private static URI WGS84_Point;
	private static URI WGS84_lat;
	private static URI WGS84_long;
	private static URI WGS84_alt;

	private static Model model;
	
	private URI resource;
	
	//TODO explain this initialization (why & how) in web
	public static void init(Model newModel) throws ModelRuntimeException {
		model = newModel;
		WGS84_Point = model.createURI("http://www.w3.org/2003/01/geo/wgs84_pos.rdf#Point");
		WGS84_lat = model.createURI("http://www.w3.org/2003/01/geo/wgs84_pos.rdf#lat");
		WGS84_long = model.createURI("http://www.w3.org/2003/01/geo/wgs84_pos.rdf#long");
		WGS84_alt = model.createURI("http://www.w3.org/2003/01/geo/wgs84_pos.rdf#alt");
	}
	
	public Point(String latitude, String longitude, String altitude) throws ModelRuntimeException {
		resource = model.newRandomUniqueURI();
		model.addStatement(resource, RDFS.Class, WGS84_Point);
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(altitude);
	}
	
	/**
	 * @param property
	 * @return a properties value (if there are many, chosen randomly)
	 */
	private String getProperty(URI property) {
			ClosableIterator<? extends Statement> it = model.findStatements(resource, property, Variable.ANY);
			if (it.hasNext()) {
				String result = it.next().getObject().toString();
				it.close();
				return result;
			}
			else return null;
	}

	private void setProperty(URI property, String value) throws ModelRuntimeException {
		try {
			// removing any values found
			model.removeStatements(resource, property, Variable.ANY);
		} catch (ModelRuntimeException e) {
			// ModelException on removeAll or findStatements => no such statements
		} finally {
			// add one property statement
			model.addStatement(resource, property, value);
		}
	}
	
	public String getAltitude() {
		return getProperty(WGS84_alt);
	}

	public void setAltitude(String altitude) throws ModelRuntimeException {
		setProperty(WGS84_alt, altitude);
	}

	public String getLatitude() {
		return getProperty(WGS84_lat);
	}

	public void setLatitude(String latitude) throws ModelRuntimeException {
		setProperty(WGS84_lat, latitude);
	}

	public String getLongitude() {
		return getProperty(WGS84_long);
	}

	public void setLongitude(String longitude) throws ModelRuntimeException {
		setProperty(WGS84_long, longitude);
	}

	public URI getResource() {
		return resource;
	}
	
}


public class Step2 {
	
	public static void main(String[] args) throws Exception {
		/*
		 * See http://www.w3.org/2003/01/geo/
		 *  and http://www.w3.org/2003/01/geo/wgs84_pos.rdf
		 *  
		 * In WGS84_pos, Point is a subclass of SpatialThing which has
		 *   the properties lat,long,alt which stands for
		 *     latitude (in decimal degrees), a String
		 *     longitude (in decimal degrees), a String
		 *     altitude (in decimal meters above the local reference ellipsoid), a String
		 */
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();

		Point.init(model);
		
		@SuppressWarnings("unused")
		Point A = new Point("0","0","0");
		@SuppressWarnings("unused")
		Point B = new Point("1,4","10,1","0");
		
		model.writeTo(System.out, Syntax.Turtle);
	}

}
