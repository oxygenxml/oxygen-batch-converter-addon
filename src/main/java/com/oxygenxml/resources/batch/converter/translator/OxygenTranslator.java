package com.oxygenxml.resources.batch.converter.translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Implement internationalization using PluginResourceBundle
 * @author Cosmin Duna
 *
 */
public class OxygenTranslator implements Translator {

	@Override
	public String getTranslation(String key, String convertorType) {
		String toRet = key;
	  StandalonePluginWorkspace pluginWorkspace = (StandalonePluginWorkspace)PluginWorkspaceProvider.getPluginWorkspace();
	  if(pluginWorkspace != null) {
	    toRet = pluginWorkspace.getResourceBundle().getMessage(key + convertorType);
	  }
	  return toRet;
	}
}
