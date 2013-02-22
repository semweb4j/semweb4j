package org.ontoware.rdf2go.impl.jena;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.graph.impl.LiteralLabel;

public class GeneralDataType extends BaseDatatype {

	public GeneralDataType(String uri) {
		super(uri);
	}

    /**
     * Equalness of DataTypes in Jena is implemented through the class
     * com.hp.hpl.jena.datatypes.BaseDatatype#isEqual .
     * In that implementation Datatypes are Compared on the Object Level.
     * We dont know the reason for that design desicision, but in our case
     * we need DataTypes to be equal if their URIs are equal.
     * 
     * So we compare the URIs of both Data Types and the Values of both literals. 
     * @see com.hp.hpl.jena.datatypes.BaseDatatype#isEqual(com.hp.hpl.jena.graph.impl.LiteralLabel, com.hp.hpl.jena.graph.impl.LiteralLabel)
     */
    @Override
	public boolean isEqual(LiteralLabel a, LiteralLabel b) {

    	// because LiteralLabel.sameValueAs does not check data typed literals itself,
    	// but instead just calls RDFDatatype.isEqual for data typed literals
    	// we have the undesired effect of comparing data types on the object level
    	
    	// what we want for rdf2go is the following:
    	// datatyped literals l1,l2 are equal if and only if:
    	// 		the uri of the datatype of l1 and the uri of the datatype of l2 are equal
    	//		and the value of both literals are equal
    	// We dont concern ourselves with the lexical representation of the
    	// data typed literal, which might be the cause for all this confusion ^_-
    	
    	// the new
    	return a.getDatatype().getURI().equals( b.getDatatype().getURI() )
    		&& a.getValue().equals(b.getValue());
    	
    	// the old
//    	 return a.getDatatype().equals( b.getDatatype() )
//    	       && a.getValue().equals(b.getValue());
    }

}
