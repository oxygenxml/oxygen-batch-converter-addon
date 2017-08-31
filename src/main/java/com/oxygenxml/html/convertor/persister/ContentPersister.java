package com.oxygenxml.html.convertor.persister;

import com.oxygenxml.html.convertor.view.ConvertorInteractor;

/**
 * Used for save and persist  
 * @author intern4
 *
 */
public interface ContentPersister {

	/**
	 * Save content before close the dialog.
	 */
	public void saveState(ConvertorInteractor convertorInteractor);
	
	/**
	 * Load content before start the dialog.
	 */
	public void loadState(ConvertorInteractor convertorInteractor);
}
