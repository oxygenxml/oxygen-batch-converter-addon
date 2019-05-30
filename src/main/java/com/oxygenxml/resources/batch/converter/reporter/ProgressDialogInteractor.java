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
	 * Set the given note in dialog.
	 * @param note The note.
	 */
	public void setNote(File note);
	
	/**
	 * Close the dialog
	 */
	public void close();
}
