/**
 * 
 */
package org.ontoware.jrest.example;

import org.ontoware.jrest.annotation.RestContent;

/**
 * @author $Author: xamde $
 * @version $Id: AdditionRestlet.java,v 1.2 2006/07/01 15:54:03 xamde Exp $
 * 
 */

public class AdditionRestlet implements AdditionRestletInterface {

	private int number = 0;

	/* (non-Javadoc)
	 * @see org.ontoware.jrest.AdditionRestletInterface#put(int)
	 */
	public void put(@RestContent
	int i) {
		this.number = i;
	}

	/* (non-Javadoc)
	 * @see org.ontoware.jrest.AdditionRestletInterface#get()
	 */
	public int get() {
		return this.number;
	}

	/* (non-Javadoc)
	 * @see org.ontoware.jrest.AdditionRestletInterface#post(int)
	 */
	public void post(@RestContent
	int i) {
		this.number = this.number + i;
	}

}
