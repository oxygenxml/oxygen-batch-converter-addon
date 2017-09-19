package com.oxygenxml.html.convertor.persister;

import com.oxygenxml.html.convertor.ConvertorInteractor;
import com.oxygenxml.html.convertor.FileType;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Use WSOptionStorage for save content from GUI and load saved content
 * 
 * 
 * @author intern4
 *
 */
public class ContentPersisterImpl implements ContentPersister {
	
	/**
	 * Save the content from dialog.
	 * @param convertorInteractor Converter interactor.
	 */
	@Override
	public void saveState(ConvertorInteractor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		
		//save the output type
		optionsStorage.setOption(OptionKeys.OUTPUT_TYPE, interactor.getOutputType());
	}
	
	/**
	 * Load the content in dialog.
	 * @param convertorInteractor Converter interactor.
	 */
	@Override
	public void loadState(ConvertorInteractor interactor) {
		
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		String value;

		//set the output type
		value = optionsStorage.getOption(OptionKeys.OUTPUT_TYPE, FileType.XHTML_TYPE_AND_EXTENSION);
		interactor.setOutputType(value);
	}
	
	
}