package com.oxygenxml.html.convertor.reporter;

import java.lang.reflect.InvocationTargetException;

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
	private static final String TAB_KEY = "Convertor";
	
	/**
	 * Report a problem using resultManager.
	 * 
	 */
	@Override
	public void reportProblem(Exception ex, String docUrl) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					// informations that will be added
					DocumentPositionedInfo result = new DocumentPositionedInfo(DocumentPositionedInfo.SEVERITY_ERROR,
							ex.getMessage(), docUrl);

					// add exception in tabKey
					resultManager.addResult(TAB_KEY, result, ResultType.PROBLEM, true, true);

				}
			});
		} catch (InvocationTargetException e) {
			logger.debug(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.debug(e.getMessage(), e);
		}
	}

}
