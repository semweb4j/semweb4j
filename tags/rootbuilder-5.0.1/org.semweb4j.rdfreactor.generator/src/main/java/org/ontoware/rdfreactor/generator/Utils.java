package org.ontoware.rdfreactor.generator;

import java.util.List;

import org.ontoware.rdfreactor.generator.java.JMapped;


public class Utils {
	
	public static String toJavaComment(List<String> rdfComments) {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < rdfComments.size(); i++) {
			buf.append(rdfComments.get(i));
			if(i + 1 < rdfComments.size()) {
				buf.append("\n");
			}
		}
		return buf.toString();
	}
	
	// replaces #macro( mixedcase $name
	// )$name.substring(0,1).toUpperCase()$name.substring(1)#end
	public static String mixedcase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	// replaces #macro( comment $indent $name )#if($name.getComment().length
	// > 0)
	public static String comment(String indent, JMapped jMapped) {
		if(jMapped.getComment().length() > 0) {
			return indent + "* Comment from ontology: " + jMapped.getComment() + "\n" + indent
			        + "*";
		} else
			return "";
	}
	
}
