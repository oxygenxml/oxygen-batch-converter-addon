package com.oxygenxml.html.convertor.persister;

import com.oxygenxml.html.convertor.view.ConvertorDialogInteractor;

/**
 * Used for save and persist  
 * @author intern4
 *
 */
public interface ContentPersister {

	/**
	 * Save content before close the dialog.
	 */
	public void saveState(ConvertorDialogInteractor frame);
	
	/**
	 * Load content before start the dialog.
	 */
	public void loadState(ConvertorDialogInteractor frame);
}
