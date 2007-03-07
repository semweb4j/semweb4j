package org.ontoware.jrest.example;

import org.ontoware.jrest.annotation.RestContent;

public interface AdditionRestletInterface {

	public void put(@RestContent
	int i);

	public int get();

	public void post(@RestContent
	int i);

}