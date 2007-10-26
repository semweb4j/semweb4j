/*
 * Created on 14.09.2005
 *
 */
package org.ontoware.semversion;

public class InvalidChildOfSuggestionException extends IllegalStateException
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidChildOfSuggestionException()
	{
		super ("Committing a valid child of a suggestion is not allowed!");
	}

}
