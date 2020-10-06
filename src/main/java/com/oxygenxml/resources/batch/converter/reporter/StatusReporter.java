package com.oxygenxml.resources.batch.converter.reporter;
/**
 * Used by checker to report statuses. 
 * @author Cosmin Duna
 */

public interface StatusReporter {
	
	/**
	 * Set the given status message.
	 *  
	 * @param message The status message to set.
	 */
	public void setStatusMessage(String message);

	/**
	 * Report the conversion was finished.
	 * 
	 * @param convertedCnt   The counter of files that was successfully converted.
	 * @param failedCnt      The counter of files that was not converted.  
	 */
	public void conversionFinished(int convertedCnt, int failedCnt);
}
