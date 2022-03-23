package com.oxygenxml.resources.batch.converter.persister;

import java.util.Iterator;
import java.util.Set;

import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.view.ConverterAdditionalOptionsProvider;

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
	  * Save the content from dialog.
	  * @param interactor Interactor with Batch converter dialog.
	  */
	@Override
	public void saveState(BatchConverterInteractor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();

		// save state of openConvertedDocument checkBox
		optionsStorage.setOption(OptionTags.OPEN_CONVERTED_DOCUMENT, String.valueOf(interactor.mustOpenConvertedFiles()));
		
		Set<String> additionalOptions = interactor.getAdditionalOptions();
		if(additionalOptions != null) {
		  Iterator<String> additionalOptionsIterator = additionalOptions.iterator();
		  while (additionalOptionsIterator.hasNext()) {
        String option = additionalOptionsIterator.next();
        Boolean additionalOptionValue = interactor.getAdditionalOptionValue(option);
        if(additionalOptionValue != null) {
          optionsStorage.setOption(option, String.valueOf(additionalOptionValue));
        }
      }
		}
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
		value = optionsStorage.getOption(OptionTags.OPEN_CONVERTED_DOCUMENT, String.valueOf(false));		
		interactor.setOpenConvertedFiles(Boolean.valueOf(value));
		
		Set<String> additionalOptions = interactor.getAdditionalOptions();
    if(additionalOptions != null) {
      Iterator<String> additionalOptionsIterator = additionalOptions.iterator();
      while (additionalOptionsIterator.hasNext()) {
        String option = additionalOptionsIterator.next();
        value = optionsStorage.getOption(option,
            String.valueOf(ConverterAdditionalOptionsProvider.getDefaultValueFor(option))); 
        interactor.setAdditionalOptionValue(option, Boolean.valueOf(value));
      }
    }
	}
}