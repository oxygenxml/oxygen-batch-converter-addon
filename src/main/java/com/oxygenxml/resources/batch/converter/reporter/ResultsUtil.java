package com.oxygenxml.resources.batch.converter.reporter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.results.ResultsManager;
/**
 * Utility class to work with the results from Oxygen.
 * 
 * @author cosmin_duna
 */
public class ResultsUtil {

  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(ResultsUtil.class);
  
  /**
   * The key for for problem reported by add-on in Results.
   */
  public static final String BATCH_CONVERTER_RESULTS_TAB_KEY = "Batch-Converter";
  
  /**
   * Private constructor
   */
  private ResultsUtil() {
    // Avoid instantiation
  }
  
  /**
   * Delete the reported problems.
   */
  public static void deleteReportedProblems() {
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace != null) {
      final ResultsManager resultsManager = pluginWorkspace.getResultsManager();
      try {
        SwingUtilities.invokeAndWait(new Runnable() {
          @Override
          public void run() {
            List<DocumentPositionedInfo> resultsList = resultsManager.getAllResults(BATCH_CONVERTER_RESULTS_TAB_KEY);
            int size = resultsList.size();
            for (int i = size - 1; i >= 0; i--) {
              resultsManager.removeResult(BATCH_CONVERTER_RESULTS_TAB_KEY, resultsList.get(i));
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
}
