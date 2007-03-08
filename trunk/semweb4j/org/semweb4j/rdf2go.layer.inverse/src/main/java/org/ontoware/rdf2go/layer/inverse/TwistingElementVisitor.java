package org.ontoware.rdf2go.layer.inverse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.core.ElementBasicGraphPattern;
import com.hp.hpl.jena.query.core.ElementDataset;
import com.hp.hpl.jena.query.core.ElementExtension;
import com.hp.hpl.jena.query.core.ElementFilter;
import com.hp.hpl.jena.query.core.ElementGroup;
import com.hp.hpl.jena.query.core.ElementNamedGraph;
import com.hp.hpl.jena.query.core.ElementOptional;
import com.hp.hpl.jena.query.core.ElementTriplePattern;
import com.hp.hpl.jena.query.core.ElementUnion;
import com.hp.hpl.jena.query.core.ElementUnsaid;
import com.hp.hpl.jena.query.core.ElementVisitor;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

class TwistingElementVisitor implements ElementVisitor {

	private static final Log log = LogFactory
			.getLog(TwistingElementVisitor.class);

	private InverseMap inverseMap;

	public TwistingElementVisitor(InverseMap inverseMap) {
		this.inverseMap = inverseMap;
	}

	public void visit(ElementTriplePattern el) {
		log.debug("visiting " + el);
	}

	public void visit(ElementFilter el) {
		log.debug("" + el);
	}

	public void visit(ElementUnion el) {
		log.debug("" + el);
	}

	public void visit(ElementOptional el) {
		log.debug("" + el);
	}

	/**
	 * Rewriting patterns:
	 * 
	 * 
	 * <pre>
	 *        (s,p,o) &amp; (p,inv,-p)=&gt; (o,-p,s) 
	 *        
	 *        (s,?x,o) =&gt; UNION{ (s,?x,o) &amp; (o, -?x, s) }
	 *        
	 * </pre>
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void visit(ElementGroup eg) {
		log.debug("" + eg);

		log.debug("EG size " + eg.getElements().size());

		for (int i = 0; i < eg.getElements().size(); i++) {
			// inspect each pattern in the ElementGroup
			ElementBasicGraphPattern ebg = (ElementBasicGraphPattern) eg
					.getElements().get(i);

			log.debug("EBG size " + ebg.getElements().size());

			for (int j = 0; j < ebg.getElements().size(); j++) {
				ElementTriplePattern etp = (ElementTriplePattern) ebg
						.getElements().get(j);

				log.debug("inspecting " + etp);

				Triple triple = etp.getTriple();
				log.debug("triple is " + triple);

				Triple replacement;

				if (triple.getMatchPredicate().isURI()) {
					log.debug("Predicate is a URI");
					URI inverse;
					inverse = inverseMap.getInverseProperty(new URIImpl(triple
							.getMatchPredicate().getURI()));

					if (inverse == null) {
						// ok, no twisting support
						log.debug("property " + etp.getTriple().getPredicate()
								+ " has no inverse");
					} else {
						log.debug("inverse is " + inverse);
						// switch s and o, use inverse of p
						Property inverseProperty = new PropertyImpl(inverse
								+ "");
						replacement = new Triple(triple.getMatchObject(),
								inverseProperty.asNode(), triple
										.getMatchSubject());
						etp.setTriple(replacement);
					}

				} else {
					log
							.debug("property is not a URI -> have to create a UNION block");
					// twisted patterm
					Node pNode = triple.getMatchPredicate();
					String varname = pNode.getName();
					Node pInvNode = Node.createVariable(varname + "Inv");

					ElementTriplePattern triple_direct = new ElementTriplePattern(
							triple);
					ElementTriplePattern triple_twisted = new ElementTriplePattern(
							new Triple(triple.getMatchObject(), pInvNode,
									triple.getMatchSubject()));

					ElementGroup group = new ElementGroup();
					ElementUnion union = new ElementUnion();
					union.addElement(triple_direct);
					ElementGroup invGroup = new ElementGroup();
					invGroup.addElement(triple_twisted);
					ElementGroup linkInverseGroup = new ElementGroup();
					ElementUnion linkInverseUnion = new ElementUnion();
					linkInverseUnion
							.addElement(new ElementTriplePattern(
									new Triple(
											pInvNode,
											Node
													.createURI(InverseMapImpl.HAS_INVERSE_PROPERTY
															.toString()),
											triple.getMatchPredicate())));
					linkInverseUnion
							.addElement(new ElementTriplePattern(
									new Triple(
											triple.getMatchPredicate(),
											Node
													.createURI(InverseMapImpl.HAS_INVERSE_PROPERTY
															.toString()),
											pInvNode)));
					linkInverseGroup.addElement(linkInverseUnion);

					invGroup.addElement(linkInverseGroup);
					union.addElement(invGroup);

					group.addElement(union);
					assert j < ebg.getElements().size();
					ebg.getElements().set(j, group);
				}

			}
		}

	}

	public void visit(ElementNamedGraph el) {
		log.debug("" + el);
	}

	public void visit(ElementUnsaid el) {
		log.debug("" + el);
	}

	public void visit(ElementExtension el) {
		log.debug("" + el);
	}

	public void visit(ElementBasicGraphPattern el) {
		log.debug("" + el);
	}

	public void visit(ElementDataset el) {
		log.debug("" + el);
	}
}
