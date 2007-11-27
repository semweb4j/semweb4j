/*
 * Created on 13-Jul-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ontoware.semversion;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.impl.jena24.ModelImplJena24;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.semversion.impl.SyntacticDiffEngine;

/**
 * @author careng, mvo
 * 
 */
public class SemanticDiffEngine {

	/**
	 * Diff computation powered by Jena.
	 * 
	 * @param a
	 *            Model
	 * @param b
	 *            Model
	 * @return the semantic diff, according to particular ontology language
	 * @throws Exception
	 */
	public static Diff getSemanticDiff_RDFS(Model a, Model b) throws Exception {
		Model inf_a = new ModelImplJena24(Reasoning.rdfs);
		inf_a.open();
		inf_a.addAll(a.iterator());

		Model inf_b = new ModelImplJena24(Reasoning.rdfs);
		inf_b.open();
		inf_b.addAll(b.iterator());

		return SyntacticDiffEngine.getSyntacticDiff(inf_a, inf_b);
	}

}