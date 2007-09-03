/*
 * Created on 12-Jul-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ontoware.semversion;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.ontoware.semversion.impl.TimeInterval;

/**
 * @author careng
 *
 */
public class TransactionTime extends TimeInterval
{
	/**
     * Infinite value Until Changed 
     */
	public static final Calendar FOREVER = new GregorianCalendar(3000,0,0);
	
    public TransactionTime(Calendar start, Calendar end) {
		super(start, end);
	}

}
