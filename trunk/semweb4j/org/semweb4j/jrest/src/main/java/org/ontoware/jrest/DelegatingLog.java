package org.ontoware.jrest;

import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.log.Logger;

public class DelegatingLog implements Logger {

	private static final Log log = LogFactory.getLog(DelegatingLog.class);

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public void setDebugEnabled(boolean enabled) {
		throw new UnsupportedOperationException("not (yet) implemented");
	}

	public void info(String msg, Object arg0, Object arg1) {
		log.info(msg + " a = " + arg0 + " b = " + arg1);
	}

	public void debug(String msg, Throwable th) {
		if (th instanceof SocketTimeoutException) {
			// ignore!
		} else
			log.debug(msg, th);
	}

	public void debug(String msg, Object arg0, Object arg1) {
		if (arg0 instanceof org.mortbay.jetty.EofException) {
			// INGORE IT
		} else
			log.debug(msg + " a = " + arg0 + " b = " + arg1);
	}

	public void warn(String msg, Object arg0, Object arg1) {
		log.warn(msg + " a = " + arg0 + " b = " + arg1);
	}

	public void warn(String msg, Throwable th) {
		log.warn(msg, th);
	}

	public Logger getLogger(String name) {
		return this;
	}

}
