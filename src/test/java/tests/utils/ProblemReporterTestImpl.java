package tests.utils;

import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;

public class ProblemReporterTestImpl implements ProblemReporter {

	@Override
	public void reportProblem(Exception ex, String docUrl) {
		System.out.println("Problem: "+ex.getMessage());

	}

}
