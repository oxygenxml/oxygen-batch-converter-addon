package com.oxygenxml.html.convertor.reporter;

public interface ProblemReporter {

	public void reportProblem(Exception ex, String docUrl);
}
