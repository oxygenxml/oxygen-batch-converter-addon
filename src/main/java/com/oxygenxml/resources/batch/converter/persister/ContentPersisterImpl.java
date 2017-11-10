package com.oxygenxml.resources.batch.converter.persister;

import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Use WSOptionStorage for save content from GUI and load saved content
 * 
 * 
 * @author Cosmin Duna
 *
 */
public class ContentPersisterImpl implements ContentPersister {

	/**
	 * Key for openConvertedDocument checkBox.
	 */
	 private static final String OPEN_CONVERTED_DOCUMENT = "open.converted.document.batch.converter";
	
	
	 /**
	  * Save the content from dialog.
	  * @param interactor Interactor with Batch converter dialog.
	  */
	@Override
	public void saveState(BatchConverterInteractor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();

		// save state of openConvertedDocument checkBox
		optionsStorage.setOption(OPEN_CONVERTED_DOCUMENT, String.valueOf(interactor.mustOpenConvertedFiles()));
		
	}


	/**
	 * Load the saved content in dialog.
	 * @param interactor Interactor with Batch converter dialog.
	 */
	@Override
	public void loadState(BatchConverterInteractor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		String value;
	
		//set selected document type
		value = optionsStorage.getOption(OPEN_CONVERTED_DOCUMENT, String.valueOf(false));		
		interactor.setOpenConvertedFiles(Boolean.valueOf(value));
	}
}