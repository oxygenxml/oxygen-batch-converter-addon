package com.oxygenxml.resources.batch.converter;

import java.util.Set;

import com.oxygenxml.batch.converter.core.converters.ConverterDefaults;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Interactor with batch converter.
 * @author Cosmin Duna
 */
public interface BatchConverterInteractor extends UserInputsProvider{

	/**
	 * Set the output folder path.
	 * @param text The path of output folder.
	 */
	public void setOutputFolder(String text);
	
	/**
   * Get the output folder path.
   * @return The path of output folder.
   */
	public String getOutputFolderPath();
	
	/**
   * Set the selected value for the given additional option.
   * 
   * @param additionalOptionId The id of the additional option.
   * @param state <code>true</code> to set selected.
   */
  public void setAdditionalOptionValue(String additionalOptionId, boolean state);
	
  /**
   * Get all additional options used in this conversion.
   * 
   * @return All additional options
   */
	public Set<String> getAdditionalOptions();
	
	/**
	 * Set if the converted file must be opened.
	 * @param <code>true</code> if converted files must be opened, <code>false</code>otherwise.
	 */
	public void setOpenConvertedFiles(boolean state);
	
	/**
	 * Set enable/ disable the convert button.
	 * @param state <code>true</code> to set enable, <code>false</code> to set disable.
	 */
	public void setEnableConvert(boolean state);
	
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
