package com.oxygenxml.html.convertor.translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Implement internationalization using PluginResourceBundle
 * @author intern4
 *
 */
public class OxygenTranslator implements Translator {

	@Override
	public String getTranslation(String key, String convertorType) {
		return ((StandalonePluginWorkspace)PluginWorkspaceProvider.getPluginWorkspace()).getResourceBundle().getMessage(key + convertorType);
	}
	
}
