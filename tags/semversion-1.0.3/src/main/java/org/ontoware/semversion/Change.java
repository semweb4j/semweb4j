package org.ontoware.semversion;

/**
 * what is a change? what to record?
 * 
 * @author mvo
 * 
 */
public class Change {

	/**
	 * every change results in a new version
	 */
	private Version newVersion;

	private String creationCause;

	public Change(Version version, String cause) {
		newVersion = version;
		creationCause = cause;
	}

	public String getCreationCause() {
		return creationCause;
	}

	public Version getNewVersion() {
		return newVersion;
	}
}
