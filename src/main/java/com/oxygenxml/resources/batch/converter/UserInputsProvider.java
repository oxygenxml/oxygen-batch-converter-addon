package com.oxygenxml.resources.batch.converter;

import com.oxygenxml.batch.converter.core.ConversionInputsProvider;
import com.oxygenxml.batch.converter.core.converters.ConverterDefaults;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Provider for the user inputs like input files, output directory and another options.
 * 
 * @author cosmin_duna
 *
 */
public interface UserInputsProvider extends ConversionInputsProvider {

	/**
	 * Return if the converted file must be opened.
	 * @return <code>true</code> if converted files must be opened, <code>false</code>otherwise.
	 */
	public boolean mustOpenConvertedFiles();
	
  /**
   * Get the maximum heading level for creating DITA Topics when converting to DITA.
   * 
   * @return the maximum heading level for creating DITA Topics when converting to DITA.
   */
  @Override
  default Integer getMaxHeadingLevelForCreatingTopics() {
    int maxHeadingLevel = 0;
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace != null) {
      WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();
      if (optionsStorage != null) {
        maxHeadingLevel = new Integer(optionsStorage.getOption(
            OptionTags.MAX_HEADING_LEVEL_FOR_TOPICS, Integer.toString(ConverterDefaults.DEFAULT_MAX_HEADING_LEVEL_FOR_CREATING_TOPICS)));
      }
    }
    return maxHeadingLevel;
  }
}
