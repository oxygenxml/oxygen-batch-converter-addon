package tests.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;

/**
 * Implementation of ProblemReporter for JUnits.
 * @author Cosmin Duna
 *
 */
public class ProblemReporterTestImpl implements ProblemReporter {

	/**
	 * Reported problems in convert process.
	 */
	List<Exception> reportedProblems = new ArrayList<Exception>();
	
	@Override
	public void reportProblem(Exception ex, File docUrl) {
		reportedProblems.add(ex);
	}

	@Override
	public void deleteReportedProblems() {
	}

	/**
	 * Getter for reported problems in convert process.
	 * @return Reported problems in convert process.
	 */
	public List<Exception> getReportedProblems() {
		return reportedProblems;
	}
}
