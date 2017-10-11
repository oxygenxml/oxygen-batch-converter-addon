package com.oxygenxml.resources.batch.converter.reporter;
/**
 * Used by checker for report statuses. 
 * @author intern4
 */

public interface StatusReporter {
	
	/**
	 * Report a status
	 * @param message the status
	 */
	public void reportStatus(String message);

	/**
	 * Report the finish status that contains the result of conversion.
	 * @param nuOfConverted The number of converted file.
	 * @param nuOfFailed The number of files that aren't converted.
	 */
	public void reportFinishStatus(int nuOfConverted, int nuOfFailed);
}
