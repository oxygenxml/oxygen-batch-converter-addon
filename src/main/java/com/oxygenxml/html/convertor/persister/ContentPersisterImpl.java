package com.oxygenxml.html.convertor.persister;

import com.oxygenxml.html.convertor.view.ConvertorDialogInteractor;

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

	@Override
	public void saveState(ConvertorDialogInteractor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();

		// save state of checkCurrentFile radioButton
		optionsStorage.setOption(OptionKeys.CHECK_CURRENT_RESOURCE, String.valueOf(""));

	}
	
	@Override
	public void loadState(ConvertorDialogInteractor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		String value;

	}
}