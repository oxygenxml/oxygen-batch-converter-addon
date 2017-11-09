package com.oxygenxml.resources.batch.converter.reporter;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
/**
 * Report status in oxygen using PluginWorkspace
 * @author intern4
 *
 */
public class OxygenStatusReporter implements StatusReporter {

	/**
	 * Logger
	 */
	 private static final Logger logger = Logger.getLogger(OxygenStatusReporter.class);
	
	 /**
	  * Report the given message.
	  * @param message Massage to report.
	  */
	@Override
	public void reportStatus(final String message) {
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
	 * @param nuOfConverted The number of converted file.
	 * @param nuOfFailures  The number of files that aren't converted.
	 */
	@Override
	public void reportFinishStatus(final int nuOfConverted, final int nuOfFailures) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					String message = nuOfConverted + " resources converted, " + nuOfFailures + " failures";
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
