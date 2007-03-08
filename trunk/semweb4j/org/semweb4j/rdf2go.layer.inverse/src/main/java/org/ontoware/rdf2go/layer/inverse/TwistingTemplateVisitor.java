package org.ontoware.rdf2go.layer.inverse;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import com.hp.hpl.jena.query.core.TemplateGroup;
import com.hp.hpl.jena.query.core.TemplateTriple;
import com.hp.hpl.jena.query.core.TemplateVisitor;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

/**
 * If a property in a triple pattern is a URI and has a defined inverse, the
 * triple pattern (s,p,o) is changed to (o,-p,s).
 * 
 * @author voelkel
 * 
 */
class TwistingTemplateVisitor implements TemplateVisitor {

	private InverseMap inverseMap;

	public TwistingTemplateVisitor( InverseMap inverseMap) {
		this.inverseMap = inverseMap;
	}
	
	public void visit(TemplateTriple template) {

	}

	@SuppressWarnings("unchecked")
	public void visit(TemplateGroup template) {

		for (int i = 0; i < template.getTemplates().size(); i++) {
			TemplateTriple tt = (TemplateTriple) template.getTemplates().get(i);

			// if predicate has inverse: replace

			if (tt.getTriple().getMatchPredicate().isURI()) {
				URI inverse;
				inverse = inverseMap.getInverseProperty(new URIImpl(tt.getTriple().getMatchPredicate()
						.getURI()));
				if (inverse != null) {
					// switch s and o, use inverse of p
					Property inverseProperty = new PropertyImpl(inverse + "");
					TemplateTriple replacement = new TemplateTriple(
							tt.getTriple().getMatchObject(), inverseProperty.asNode(), tt
									.getTriple().getMatchSubject());
					template.getTemplates().set(i, replacement);

				}
			}
		}

	}

}
