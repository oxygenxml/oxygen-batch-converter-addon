package com.oxygenxml.resources.batch.converter.persister;

import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;

/**
 * Used for save and persist  
 * @author Cosmin Duna
 *
 */
public interface ContentPersister {

	/**
	 * Save content before close the dialog.
	 * @param interactor Interactor with batch converter dialog.
	 */
	 void saveState(BatchConverterInteractor interactor);
	
	/**
	 * Load content before start the dialog.
	 * @param interactor  Interactor with batch converter dialog.
	 */
	 void loadState(BatchConverterInteractor interactor);
}
