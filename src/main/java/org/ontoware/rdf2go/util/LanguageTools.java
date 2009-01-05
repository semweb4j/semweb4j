/**
 * LICENSE INFORMATION
 * 
 * Copyright 2005-2008 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2008
 * 
 * Further project information at http://semanticweb.org/wiki/RDF2Go 
 */

package org.ontoware.rdf2go.util;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;

public class LanguageTools {

	/**
	 * Deletes all literals except the ones in the desired language.
	 * 
	 * Removes all languatagges literals that DO NOT start with the 'language' string.
	 * 
	 * @param model
	 * @param language
	 * @return a Model that contains only literals in the desired language
	 */
	public static Model selectLanguage( Model model, String language ) {
		Model result = RDF2Go.getModelFactory().createModel();
		result.open();
		ClosableIterator<Statement> it = model.iterator();
		while (it.hasNext()) {
			Statement s = it.next();
			if (s.getObject() instanceof LanguageTagLiteral) {
				LanguageTagLiteral lit = s.getObject().asLanguageTagLiteral();
				if (lit.getLanguageTag().startsWith(language)) {
					result.addStatement(s);
				} 
				// else don't copy
			} else {
				result.addStatement(s);
			}
		}
		it.close();
		return result;
	}
	
}
