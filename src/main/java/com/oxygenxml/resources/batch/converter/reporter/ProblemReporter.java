package com.oxygenxml.resources.batch.converter.reporter;

import java.io.File;

/**
 * Reporter for problem.
 * @author intern4
 *
 */
public interface ProblemReporter {

	/**
	 * Report the given exception.
	 * @param ex 	The exception.
	 * @param docFile The file with problem.
	 */
	public void reportProblem(Exception ex, File docFile);


	/**
	 * Delete the reported problems
	 */
	public void deleteReportedProblems();
}
