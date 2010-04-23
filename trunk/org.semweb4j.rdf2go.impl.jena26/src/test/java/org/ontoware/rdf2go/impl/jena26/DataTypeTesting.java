package org.ontoware.rdf2go.impl.jena26;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.jena26.GeneralDataType;
import org.ontoware.rdf2go.impl.jena26.TypeConversion;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.impl.JenaParameters;


public class DataTypeTesting {
	
	@Test
	public void testDataTypeEqualness() throws Exception {
		
		// die erste DatenTyp URI
		URI testA = new URIImpl("test://somedata-A", false);
		// die zweite DatenTyp URI
		URI testB = new URIImpl("test://somedata-B", false);
		
		// der erste BaseDatatype wird von der ersten DatenTyp URI erzeugt
		BaseDatatype BDtestA1 = new BaseDatatype(testA + "");
		// der zweite BaseDatatype ebenso
		BaseDatatype BDtestA2 = new BaseDatatype(testA + "");
		// f�r den dritten BaseDatatype nehmen wir eine neue Datentyp URI
		BaseDatatype BDtestB = new BaseDatatype(testB + "");
		
		// alle Literals haben den gleichen Inhalt
		
		// das erste Literal kriegt den ersten BaseDatatype
		LiteralLabel litA11 = LiteralLabelFactory.create("teststring", "", BDtestA1);
		// das zweite Liertal kriegt den auch, es hat also komplett die gleichen
		// Eigenschaften wie das erste Literal aber in einem neuen Objekt
		LiteralLabel litA12 = LiteralLabelFactory.create("teststring", "", BDtestA1);
		// jetzt machen wir ein drittes Literal, welches aber den zweiten
		// BasedataType
		// bekommt, der aber eigentlich von der ersten DataTypeURI stammt
		LiteralLabel litA2 = LiteralLabelFactory.create("teststring", "", BDtestA2);
		// und jetzt machen wir noch nen vierten Literal, der von einem neuen
		// Basedatatype komt, welche wiederrum von einer neuen DatatypeURI ist
		LiteralLabel litB = LiteralLabelFactory.create("teststring", "", BDtestB);
		
		// dann wollen wir mal schauen was passiert:
		
		// reflexivit�t: A == A , passt
		assertTrue(litA11.sameValueAs(litA11));
		// gleicher Inhalt, in zwei versch. Objekten, passt auch
		assertTrue(litA11.sameValueAs(litA12));
		// zwei Objekte, mit untersch. BaseDatatypes, von der gleichen Datatype
		// URI:
		// nein
		assertFalse(litA11.sameValueAs(litA2));
		// und zur sicherheit: 2 versch Datentyp URIs: nein
		assertFalse(litA11.sameValueAs(litB));
		
		// und nochmal der negativ Test:
		assertTrue(litB.sameValueAs(litB));
		assertFalse(litB.sameValueAs(litA11));
		assertFalse(litB.sameValueAs(litA12));
		assertFalse(litB.sameValueAs(litA2));
		
		// weiterlesen ;)
		
		// das liegt an der Implementierung von BaseDatatype.isEqual()
		assertFalse(BDtestA1.isEqual(litA11, litA2));
		
		/**
		 * Compares two instances of values of the given datatype. This default
		 * requires value and datatype equality.
		 * 
		 * public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
		 * return value1.getDatatype() == value2.getDatatype() ^^^^^^ &&
		 * value1.getValue().equals(value2.getValue()); }
		 */
		
		// Es werden dirket 2 Object Referenzen verglichen, und 2 versch.
		// Basedatatype Objecte sind ebend unterschiedlich.
		// wir m�ssen entweder irgendwo auf der Seite von RDF2GO
		// BaseDatatypes recyclen, oder einen eigenen Datentyp schreiben,
		// bei dem isEqual() den Inhalt der Datentyp URIs der beiden
		// Basedatatypes
		// vergleicht.
	}
	
	@Test
	public void testGeneralDataTypeEqualness() throws Exception {
		
		// Die Idee mit dem GeneralDataType ist an sich echt gut, aber:
		// Es kann passieren das mit addStatement(object, object, object,
		// object)
		// als Object des Tripels ein Literal mit Daten Typ erzeugt wird.
		// Wenn ich nun mein Literal mit einem GeneralDataType erzeuge,
		// es in den Graphen einf�ge, und ich sp�ter abfragen will
		// ob dieses data typed Literal sich wirklich im Graphen befindet,
		// dann wird das literal mit dem GeneralDataType mit dem eigentlich
		// gleichen Literal verglichen, aber die zweite Instanz hat einen
		// BaseDatatype
		// weil sie automatisch in Search-Triple oder irgend sowas eingef�gt
		// wurde.
		// Da GeneralDataType auf BaseDatatype down-gecastet werden kann, werden
		// sie
		// dann mit dem BaseDatatype.equals() verglichen und dann passt es
		// wieder nicht.
		
		// L�sung siehe Funktion testDataTypesWithUnknownType()
		
		// die erste DatenTyp URI
		URI testA = new URIImpl("test://somedata-A", false);
		// die zweite DatenTyp URI
		URI testB = new URIImpl("test://somedata-B", false);
		
		// der erste BaseDatatype wird von der ersten DatenTyp URI erzeugt
		GeneralDataType BDtestA1 = new GeneralDataType(testA + "");
		// der zweite BaseDatatype ebenso
		GeneralDataType BDtestA2 = new GeneralDataType(testA + "");
		// f�r den dritten BaseDatatype nehmen wir eine neue Datentyp URI
		GeneralDataType BDtestB = new GeneralDataType(testB + "");
		
		// alle Literals haben den gleichen Inhalt
		
		// das erste Literal kriegt den ersten BaseDatatype
		LiteralLabel litA11 = LiteralLabelFactory.create("teststring", "", BDtestA1);
		// das zweite Liertal kriegt den auch, es hat also komplett die gleichen
		// Eigenschaften wie das erste Literal aber in einem neuen Objekt
		LiteralLabel litA12 = LiteralLabelFactory.create("teststring", "", BDtestA1);
		// jetzt machen wir ein drittes Literal, welches aber den zweiten
		// BasedataType
		// bekommt, der aber eigentlich von der ersten DataTypeURI stammt
		LiteralLabel litA2 = LiteralLabelFactory.create("teststring", "", BDtestA2);
		// und jetzt machen wir noch nen vierten Literal, der von einem neuen
		// Basedatatype komt, welche wiederrum von einer neuen DatatypeURI ist
		LiteralLabel litB = LiteralLabelFactory.create("teststring", "", BDtestB);
		
		// dann wollen wir mal schauen was passiert:
		
		// reflexivit�t: A == A , passt
		assertTrue(litA11.sameValueAs(litA11));
		// gleicher Inhalt, in zwei versch. Objekten, passt auch
		assertTrue(litA11.sameValueAs(litA12));
		// zwei Objekte, mit untersch. BaseDatatypes, von der gleichen Datatype
		// URI:
		
		// ACHTUNG: mit GeneralDataType passt das jetzt weil f�r die Gleichheit
		// der Datentypen
		// nun nur die Gleichheit der URIs der Datentypen notwendig ist
		assertTrue(litA11.sameValueAs(litA2));
		// und zur sicherheit: 2 versch Datentyp URIs: nein
		assertFalse(litA11.sameValueAs(litB));
		
		// und nochmal der negativ Test:
		assertTrue(litB.sameValueAs(litB));
		assertFalse(litB.sameValueAs(litA11));
		assertFalse(litB.sameValueAs(litA12));
		assertFalse(litB.sameValueAs(litA2));
		
		// und hier nochmal der Low Level Test:
		// mit GeneralDataType ist nun isEqual()
		// so implementiert, das die URIs der Datentypen verglichen werden und
		// die Werte der Strings
		// und deswegen ist das jetzt hier True
		assertTrue(BDtestA1.isEqual(litA11, litA2));
		
	}
	
	@Test
	public void testOldDataTypesUsedAsIntended() throws Exception {
		
		// laut der jena-dev Mailingliste, sollte man Datentypen so erzeugen:
		// siehe http://groups.yahoo.com/group/jena-dev/message/14052
		
		// String dtURI = tmpProp.getRange().getURI();
		// RDFDatatype dt = TypeMapper.getInstance().getTypeByName(dtURI);
		// Literal tmpLit = tmpModel.createTypedLiteral("123", dt );
		
		// leider f�hrt das dann dazu das "test"^^xsd:funky equal zu "test" ist,
		// da dann xsd:funky ein unknown data type ist und
		// somit "test"^^xsd:funky genau wie ein plain literal behandelt wird.
		
		// die erste DatenTyp URI
		// URI testA = URIUtils.createURI("test://somedata-A");
		String strTestA = new String("test://somedata-A");
		// die zweite DatenTyp URI
		// URI testB = URIUtils.createURI("test://somedata-B");
		String strTestB = new String("test://somedata-B");
		
		// der erste BaseDatatype wird von der ersten DatenTyp URI erzeugt
		RDFDatatype DTtestA1 = TypeMapper.getInstance().getTypeByName(strTestA);
		// der zweite BaseDatatype ebenso
		RDFDatatype DTtestA2 = TypeMapper.getInstance().getTypeByName(strTestA);
		// f�r den dritten BaseDatatype nehmen wir eine neue Datentyp URI
		RDFDatatype DTtestB = TypeMapper.getInstance().getTypeByName(strTestB);
		
		com.hp.hpl.jena.rdf.model.Model model = ModelFactory.createDefaultModel();
		
		Literal litA11 = model.createTypedLiteral("teststring", DTtestA1);
		Literal litA12 = model.createTypedLiteral("teststring", DTtestA1);
		Literal litA2 = model.createTypedLiteral("teststring", DTtestA2);
		@SuppressWarnings("unused")
		Literal litB = model.createTypedLiteral("teststring", DTtestB);
		
		// alle Literals haben den gleichen Wert !
		
		// dann wollen wir mal schauen was passiert:
		
		// reflexivit�t: A == A , passt
		assertTrue(litA11.equals(litA11));
		// gleicher Inhalt, in zwei versch. Objekten, passt auch
		assertTrue(litA11.equals(litA12));
		// zwei Objekte, mit untersch. BaseDatatypes, von der gleichen Datatype
		// URI:
		
		assertTrue(litA11.equals(litA2));
		// und zur sicherheit: 2 versch Datentyp URIs:
		
		// -> das sollte eigentlich nicht sein
		// TODO jena bug assertFalse(litA11.equals(litB));
		
	}
	
	@Test
	public void testDataTypesWithUnknownType() throws Exception {
		
		// siehe dazu com.hp.hpl.jena.graph.test.TestTypedLiterals.java,
		// Funktion testUnknown()
		// das ganze funktioniert sowohl mit Jena2.2 als auch mit dem jena aus
		// dem cvs
		
		// das ganze Problem scheint wohl zu sein das Jena ziemlich abgefahrene
		// Sachen machen kann
		// mit daten typen und der valdierung und solchen advanced topics.
		// die Erwaeaehnte Test Datei zeigt das ziemlich eindrucksvoll.
		
		// Dieser gesamte Test testet direkt die Funktion von Jena, nicht von
		// rdf2go. (SG)
		
		// die erste DatenTyp URI
		String strTestA = new String("test://somedata-A");
		// die zweite DatenTyp URI
		String strTestB = new String("test://somedata-B");
		
		com.hp.hpl.jena.rdf.model.Model model = ModelFactory.createDefaultModel();
		
		// das hier scheint alles zu sein was notwendig ist damit Jena die data
		// typed literals
		// semantisch so behandelt wie wir es wollen
		// Behold !!
		JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = true;
		
		Literal litA1 = model.createTypedLiteral("teststring", strTestA);
		Literal litA2 = model.createTypedLiteral("teststring", strTestA);
		Literal litB = model.createTypedLiteral("teststring", strTestB);
		
		// dann wollen wir mal schauen was passiert:
		
		// reflexivit�t: A == A , passt
		assertTrue(litA1.equals(litA1));
		// gleicher Inhalt, in zwei versch. Objekten, passt auch
		assertTrue(litA1.equals(litA2));
		// und zur sicherheit: 2 versch Datentyp URIs: nein
		assertFalse(litA1.equals(litB));
		
		// und nochmal der negativ Test:
		assertTrue(litB.equals(litB));
		assertFalse(litB.equals(litA1));
		assertFalse(litB.equals(litA2));
		assertEquals("Extract Datatype URI", litA1.getDatatypeURI(), strTestA);
		assertEquals("Extract value", "teststring", litA1.getLexicalForm());
		
		// im jena cvs geht auch folgendes, damit kann man das Object des Daten
		// Typs besser manipulieren
		// assertEquals("Extract value", l1.getValue(), new
		// BaseDatatype.TypedValue("foo", typeURI));
		
	}
	
	@Test
	public void testNewToJenaNode() throws ModelRuntimeException {
		com.hp.hpl.jena.rdf.model.Model model = ModelFactory.createDefaultModel();
		
		DatatypeLiteralImpl l1 = new DatatypeLiteralImpl("test", new URIImpl("test:funky", false));
		DatatypeLiteralImpl l2 = new DatatypeLiteralImpl("test", new URIImpl("test:funky", false));
		
		Node n1 = TypeConversion.toJenaNode(l1, model);
		Node n2 = TypeConversion.toJenaNode(l2, model);
		
		assertTrue(n1.equals(n2));
		
		Object o1 = TypeConversion.toRDF2Go(n1);
		Object o2 = TypeConversion.toRDF2Go(n2);
		
		assertTrue(o1 instanceof DatatypeLiteral);
		assertTrue(o2 instanceof DatatypeLiteral);
		
		DatatypeLiteralImpl new1 = (DatatypeLiteralImpl)o1;
		DatatypeLiteralImpl new2 = (DatatypeLiteralImpl)o2;
		
		assertTrue(new1.getValue().equals("test"));
		assertTrue(new2.getValue().equals("test"));
		assertTrue(new1.getDatatype().equals(new URIImpl("test:funky", false)));
		assertTrue(new2.getDatatype().equals(new URIImpl("test:funky", false)));
	}
	
}
