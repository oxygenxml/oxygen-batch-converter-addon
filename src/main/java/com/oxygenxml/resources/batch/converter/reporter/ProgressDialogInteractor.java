package com.oxygenxml.resources.batch.converter.reporter;

import java.io.File;

/**
 * Progress dialog interactor.
 * @author Cosmin Duna
 *
 */
public interface ProgressDialogInteractor {
	
	/**
	 * Set the dialog visible/invisible.
	 * @param state <code>true</code> to set visible, <code>false</code> to set invisible
	 */
	public void setDialogVisible(boolean state);
	
	/**
	 * The conversion of the given file is in progress.
	 * 
	 * @param file The converted file.
	 */
	public void conversionInProgress(File file);
	
	/**
	 * Close the dialog
	 */
	public void close();
}
