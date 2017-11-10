package tests.utils;

import java.io.File;

import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;

/**
 * Implementation of ProblemReporter for JUnits.
 * @author Cosmin Duna
 *
 */
public class ProblemReporterTestImpl implements ProblemReporter {

	@Override
	public void reportProblem(Exception ex, File docUrl) {
		System.out.println("Problem: "+ex.getMessage());
	}

	@Override
	public void deleteReportedProblems() {
	}

}
