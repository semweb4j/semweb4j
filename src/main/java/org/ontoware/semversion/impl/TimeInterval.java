/*
 * Created on 12-Jul-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ontoware.semversion.impl;

import java.util.Calendar;
import java.util.Date;

/**
 * @author careng
 * 
 */
public class TimeInterval {

	protected Calendar start;

	protected Calendar end;

	public TimeInterval(Calendar start, Calendar end) {
		this.start = start;
		this.end = end;
	}

	public Calendar getStart() {
		return start;
	}

	public Calendar getEnd() {
		return end;
	}

	public static Date getCurrentTimeStamp() {
		return new java.util.Date();
	}

	/**
	 * This interval should contain the other specified
	 * @param other
	 * @return true (this .... (other...) .... this)
	 */
	public boolean contains(TimeInterval other) {
		return this.getStart().compareTo(other.getStart()) <= 0
				&& this.getEnd().compareTo(other.getEnd()) >= 0;
	}

	public String toString() {
		return "[" + getStart() + " ... " + getEnd() + "]=["
				+ getStart().getTime() + " ... " + getEnd().getTime() + "]";
	}

	/**
	 * This will subtract timeExpr to current timestamp
	 * 
	 * @param timeExpr
	 *            is a date with only time
	 * @return
	 */
	public static Date subtract(Date timeExpr) {
		long dateMillis = new Date().getTime();
		long timeExprMillis = timeExpr.getTime();

		return new Date(dateMillis - timeExprMillis);
	}

	/**
	 * 
	 * @param date
	 * @param timeExpr
	 *            is a date with only time
	 * @return
	 */
	public static Date subtract(Date date, Date timeExpr) {
		long dateMillis = date.getTime();
		long timeExprMillis = timeExpr.getTime();

		return new Date(dateMillis - timeExprMillis);
	}

	

	/**
	 * 
	 * @param date
	 * @param timeExpr
	 *            is a date with only time
	 * @return
	 */
	public static Date add(Date date, Date timeExpr) {
		long dateMillis = date.getTime();
		long timeExprMillis = timeExpr.getTime();

		return new Date(dateMillis + timeExprMillis);
	}

	public boolean equals(Object o) {
		if (o instanceof TimeInterval) {
			TimeInterval ot = (TimeInterval) o;
			return ot.getStart().equals(this.getStart())
					&& ot.getEnd().equals(this.getEnd());
		}
		return false;
	}

}
