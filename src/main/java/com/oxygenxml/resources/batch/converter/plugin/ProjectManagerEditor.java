package com.oxygenxml.resources.batch.converter.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.batch.converter.core.extensions.ExtensionGetter;
import com.oxygenxml.batch.converter.core.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.proxy.ProjectPopupMenuCustomizerInvocationHandler;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

/**
 * Project manager editor.
 * @author Cosmin Duna
 *
 */
public class ProjectManagerEditor {

	/**
	 * Private constructor.
	 */
  private ProjectManagerEditor() {
    throw new IllegalStateException("Utility class");
  }
	
	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProjectManagerEditor.class);

	/**
	 * For 19.1 oxygen version add a Menu with given actions in contextual menu
	 * of project manager. For older version than 19.1 do nothing.
	 * 
	 * @param pluginWorkspaceAccess
	 *          The StandalonePluginWorkspace.
	 * @param menuToAdd
	 *          The menu to add
	 */
	public static void addPopUpMenuCustomizer(StandalonePluginWorkspace pluginWorkspaceAccess, Menu menuToAdd, Translator translator) {
		// try to get method from 19.1 version
		try {
			// get the getProjectManager method
			Method getProjectManager = pluginWorkspaceAccess.getClass().getMethod("getProjectManager");

			// get the projectManager class
			Class<?> projectManagerClass = getProjectManager.getReturnType();

			// get the projectPopupMenuCustomizer interface
			Class<?> projectPopupMenuCustomizerClass = Class
					.forName("ro.sync.exml.workspace.api.standalone.project.ProjectPopupMenuCustomizer");

			// create a ProxyInstance of projectPopupMenuCustomizer
			Object proxyProjectPopupMenuCustomizerImpl = Proxy.newProxyInstance(
					projectPopupMenuCustomizerClass.getClassLoader(), new Class[] { projectPopupMenuCustomizerClass },
					new ProjectPopupMenuCustomizerInvocationHandler(pluginWorkspaceAccess, menuToAdd));

			// get the project manager object
			Object projectManager = getProjectManager.invoke(pluginWorkspaceAccess);

			// get the addPopUpMenuCustomizer method
			Method addPopUpMenuCustomizerMethod = projectManagerClass.getMethod("addPopUpMenuCustomizer",
					projectPopupMenuCustomizerClass );
			// invoke addPopUpMenuCustomizer method
			addPopUpMenuCustomizerMethod.invoke(projectManager, proxyProjectPopupMenuCustomizerImpl);

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	/**
	 * For 19.1 oxygen version, get the selected files from
	 * projectManager according to converterType.
	 * 
	 * @param pluginWorkspaceAccess
	 * @param converterType The type of converter.
	 * @return If oxygen version is 19.1 return a list with files paths ,
	 *         else return a empty list.
	 */
	public static List<File> getSelectedFiles(String converterType) {
		List<File> toReturn = new ArrayList<File>();
		
		// the input types
		List<String> inputTypes = Arrays.asList(ExtensionGetter.getInputExtension(converterType) );
		
		try {
		  PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
			// get the getProjectManager method
			Method getProjectManager = pluginWorkspace.getClass().getMethod("getProjectManager");

			// get the projectManager class
			Class<?> projectManagerClass = getProjectManager.getReturnType();

			// get the projectManager
			Object projectManager = getProjectManager.invoke(pluginWorkspace);

			// get the getSelectedFiles method
			Method getSelectedFiles = projectManagerClass.getMethod("getSelectedFiles");

			// get the selected files
			File[] selectedFiles = (File[]) getSelectedFiles.invoke(projectManager);

			// iterate over files
			int size = selectedFiles.length;
			for (int i = 0; i < size; i++) {
				toReturn.addAll(ConverterFileUtils.getAllFiles(selectedFiles[i], inputTypes));
			}

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return toReturn;
	}
	
}
