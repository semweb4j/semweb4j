package org.ontoware.aifbcommons.labtools;

/**
 * start and stop to stop time
 * 
 * @author mvo
 * 
 */
public class StopWatch {
	
	private long start, stop;
	
	public void start() {
		start = System.currentTimeMillis();
	}
	
	public void stop() {
		stop = System.currentTimeMillis();
	}
	
	public long stopstart() {
		stop();
		long l = getDuration();
		start();
		return l;
	}

	public long getDuration() {
		return stop - start;
	}
	

}
