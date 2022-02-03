package com.oxygenxml.resources.batch.converter.reporter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.results.ResultsManager;
import ro.sync.exml.workspace.api.results.ResultsManager.ResultType;

/**
 * Implementation used for reports problem in Oxygen.
 * @author Cosmin Duna
 *
 */
public class OxygenProblemReporter implements ProblemReporter {
	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(OxygenProblemReporter.class);
	 
	/**
	 * Result manager.
	 */
	private ResultsManager resultManager = PluginWorkspaceProvider.getPluginWorkspace().getResultsManager();

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
						    docFile != null ? docFile.toURI().toURL().toString() : null);
						// add exception in tabKey
						resultManager.addResult(ResultsUtil.BATCH_CONVERTER_RESULTS_TAB_KEY, result, ResultType.PROBLEM, true, true);

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
}
