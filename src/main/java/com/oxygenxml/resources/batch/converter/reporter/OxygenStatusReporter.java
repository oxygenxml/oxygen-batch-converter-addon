package com.oxygenxml.resources.batch.converter.reporter;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
/**
 * Report status in Oxygen using PluginWorkspace
 * @author Cosmin Duna
 *
 */
public class OxygenStatusReporter implements StatusReporter {

	/**
	 * Logger
	 */
	 private static final Logger logger = LoggerFactory.getLogger(OxygenStatusReporter.class);
	
	 /**
	  * Report the given message.
	  * @param message Massage to report.
	  */
	@Override
	public void setStatusMessage(final String message) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					PluginWorkspaceProvider.getPluginWorkspace().showStatusMessage(message);
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
	 * Report the finish status that contains the result of conversion.
	 * @param convertedCnt The number of converted file.
	 * @param nuOfFailures  The number of files that aren't converted.
	 */
	@Override
	public void conversionFinished(final int convertedCnt, final int nuOfFailures) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					String message = convertedCnt + " resources converted, " + nuOfFailures + " failures";
					PluginWorkspaceProvider.getPluginWorkspace().showStatusMessage(message);
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
