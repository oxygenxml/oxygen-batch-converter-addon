package com.oxygenxml.html.convertor.persister;

import com.oxygenxml.html.convertor.ConvertorInteractor;

/**
 * Used for save and persist  
 * @author intern4
 *
 */
public interface ContentPersister {

	/**
	 * Save the content from dialog.
	 * @param convertorInteractor Converter interactor.
	 */
	public void saveState(ConvertorInteractor convertorInteractor);
	
	/**
	 * Load the content in dialog.
	 * @param convertorInteractor Converter interactor.
	 */
	public void loadState(ConvertorInteractor convertorInteractor);
}
