package com.oxygenxml.html.convertor.reporter;

/**
 * Reporter for problem.
 * @author intern4
 *
 */
public interface ProblemReporter {

	/**
	 * Report the given exception.
	 * @param ex 	The exception.
	 * @param docUrl The URL of file with problem.
	 */
	public void reportProblem(Exception ex, String docUrl);
}
