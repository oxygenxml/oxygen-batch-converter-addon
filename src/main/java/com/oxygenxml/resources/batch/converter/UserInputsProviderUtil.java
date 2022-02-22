package com.oxygenxml.resources.batch.converter;

import com.oxygenxml.batch.converter.core.converters.ConverterDefaults;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Utility class for collecting options set by user
 * @author cosmin_duna
 *
 */
public class UserInputsProviderUtil {
  
  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private UserInputsProviderUtil() {
    // Private to avoid instantiations
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
  }
  
  /**
   * Get the maximum heading level for creating DITA Topics when converting to DITA.
   * 
   * @return the maximum heading level for creating DITA Topics when converting to DITA.
   */
  public static Integer getMaxHeadingLevelForCreatingTopicsOption() {
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
