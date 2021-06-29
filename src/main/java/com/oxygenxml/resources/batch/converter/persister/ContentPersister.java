package com.oxygenxml.resources.batch.converter.persister;

import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;

/**
 * Used for save and persist  
 * @author Cosmin Duna
 *
 */
public interface ContentPersister {

	/**
	 * Save content before close the dialog.
	 * @param interactor Interactor with batch converter dialog.
	 * @param inputsProvider Inputs provider
	 */
	 void saveState(BatchConverterInteractor interactor, UserInputsProvider inputsProvider);
	
	/**
	 * Load content before start the dialog.
	 * @param interactor  Interactor with batch converter dialog.
	 */
	 void loadState(BatchConverterInteractor interactor);
}
