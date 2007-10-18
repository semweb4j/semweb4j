/*
 * Created on 14.09.2005
 *
 */
package org.ontoware.semversion;

public class CommitConflictException extends IllegalStateException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommitConflictException()
	{
		super("A child of this version has already been committed!");
	}

}
