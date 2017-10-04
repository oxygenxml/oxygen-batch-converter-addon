package com.oxygenxml.resources.batch.converter.persister;

import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;

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
	public void saveState(BatchConverterInteractor convertorInteractor);
	
	/**
	 * Load the content in dialog.
	 * @param convertorInteractor Converter interactor.
	 */
	public void loadState(BatchConverterInteractor convertorInteractor);
}
