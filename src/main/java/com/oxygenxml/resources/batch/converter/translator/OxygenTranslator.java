package com.oxygenxml.resources.batch.converter.translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Implement internationalization using PluginResourceBundle
 * @author Cosmin Duna
 *
 */
public class OxygenTranslator implements Translator {

  /**
   * Get the translation from the given key with the given suffix.
   * 
   * @param key  The key of the message.
   * 
   * @return The message.
   */
  @Override
	public String getTranslation(String key) {
    String toRet = key;
    StandalonePluginWorkspace pluginWorkspace = (StandalonePluginWorkspace)PluginWorkspaceProvider.getPluginWorkspace();
    if(pluginWorkspace != null) {
      toRet = pluginWorkspace.getResourceBundle().getMessage(key);
    }
    return toRet;
  }
}
