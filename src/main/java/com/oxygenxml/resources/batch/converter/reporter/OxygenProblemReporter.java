package com.oxygenxml.resources.batch.converter.reporter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;


import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.results.ResultsManager;
import ro.sync.exml.workspace.api.results.ResultsManager.ResultType;

public class OxygenProblemReporter implements ProblemReporter {
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(OxygenProblemReporter.class);
	 
	/**
	 * Result manager.
	 */
	private ResultsManager resultManager = PluginWorkspaceProvider.getPluginWorkspace().getResultsManager();

	/**
	 * The tab key.
	 */
	private static final String TAB_KEY = "Batch-Converter";
	
	/**
	 * Report a problem using resultManager.
	 * 
	 */
	@Override
	public void reportProblem(final Exception ex, final File docFile) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					// informations that will be added
					DocumentPositionedInfo result;
					try {
						result = new DocumentPositionedInfo(DocumentPositionedInfo.SEVERITY_ERROR, ex.getMessage(),
								docFile.toURI().toURL().toString());
						// add exception in tabKey
						resultManager.addResult(TAB_KEY, result, ResultType.PROBLEM, true, true);

					} catch (MalformedURLException e) {
						logger.debug(e.getMessage(), e);
					}

				}
			});
		} catch (InvocationTargetException e) {
			logger.debug(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.debug(e.getMessage(), e);
			// Restore interrupted state...
	    Thread.currentThread().interrupt();
		}
	}

	/**
	 * Delete the reported problems.
	 */
	@Override
	public void deleteReportedProblems() {
	try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					List<DocumentPositionedInfo> resultsList = resultManager.getAllResults(TAB_KEY);
					int size = resultsList.size();
					for (int i = size - 1; i >= 0; i--) {
						resultManager.removeResult(TAB_KEY, resultsList.get(i));
					}
				}
			});
		} catch (InvocationTargetException e) {
			logger.debug(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.debug(e.getMessage(), e);
			// Restore interrupted state...
	    Thread.currentThread().interrupt();
		}
	}

}
