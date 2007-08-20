package org.ontoware.semversion;

import java.util.ArrayList;
import java.util.List;


public class Branch {
	
	public Branch() {
		this.fromRootToRecent = new ArrayList<Version>();
	}
	
	public List<Version> fromRootToRecent;

}
