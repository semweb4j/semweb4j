/*
 * Created on 14.09.2005
 *
 */
package org.ontoware.semversion;

public class BranchlabelAlreadyUsedException extends IllegalStateException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BranchlabelAlreadyUsedException()
	{
		super("A branch with the same label already exists!");
	}
}
